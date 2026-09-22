# -*- coding: utf-8 -*-
import base64
import json
import urllib.request
import uuid
from pathlib import Path
from typing import List, Optional

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

import config
import db
import llm
import prompts
from tools import TOOLS, execute_tool

app = FastAPI(title="drama-agent")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


def parse_json(text):
    """宽容解析 LLM 输出的 JSON，失败返回 None。"""
    try:
        return json.loads(text)
    except Exception:
        start = text.find("{")
        end = text.rfind("}")
        if start >= 0 and end > start:
            try:
                return json.loads(text[start:end + 1])
            except Exception:
                return None
        return None


class WriteReviewRequest(BaseModel):
    dramaName: str
    keywords: str


class SummaryRequest(BaseModel):
    dramaId: int
    dramaName: str


class ChatMessage(BaseModel):
    role: str
    content: str


class ChatRequest(BaseModel):
    messages: List[ChatMessage]


class PosterRequest(BaseModel):
    name: str
    description: Optional[str] = ""


@app.get("/agent/health")
def health():
    return {
        "status": "ok",
        "deepseekConfigured": bool(config.DEEPSEEK_API_KEY),
        "siliconflowConfigured": bool(config.SILICONFLOW_API_KEY),
    }


# A. 写评价助手
@app.post("/agent/write-review")
def write_review(req: WriteReviewRequest):
    if not config.DEEPSEEK_API_KEY:
        raise HTTPException(500, "未配置 DEEPSEEK_API_KEY，请在 drama-agent/.env 中填写")
    messages = [
        {"role": "system", "content": prompts.WRITE_REVIEW_SYSTEM},
        {"role": "user", "content": "剧名：%s\n我的感受/关键词：%s" % (req.dramaName, req.keywords)},
    ]
    content = llm.chat(messages, temperature=0.8, max_tokens=700)
    return {"content": content}


# C. 口碑速览
@app.post("/agent/summary")
def summary(req: SummaryRequest):
    if not config.DEEPSEEK_API_KEY:
        raise HTTPException(500, "未配置 DEEPSEEK_API_KEY，请在 drama-agent/.env 中填写")
    reviews = db.query_reviews(req.dramaId, limit=50)
    if not reviews:
        return {"message": "暂无评价，快去写第一条吧"}
    review_text = "\n".join("[%s分] %s" % (r["rating"], r["content"]) for r in reviews)
    messages = [
        {"role": "system", "content": prompts.SUMMARY_SYSTEM},
        {"role": "user", "content": "剧名：%s\n用户评价列表：\n%s" % (req.dramaName, review_text)},
    ]
    raw = llm.chat(messages, temperature=0.4, max_tokens=600, json_mode=True)
    data = parse_json(raw)
    if data is None:
        raise HTTPException(500, "AI 返回格式异常，请重试")
    return {"data": data}


# B. 智能推荐 Agent（tool calling 循环）
@app.post("/agent/chat")
def chat(req: ChatRequest):
    if not config.DEEPSEEK_API_KEY:
        raise HTTPException(500, "未配置 DEEPSEEK_API_KEY，请在 drama-agent/.env 中填写")
    messages = [{"role": "system", "content": prompts.CHAT_SYSTEM}]
    for m in req.messages:
        if m.role in ("user", "assistant"):
            messages.append({"role": m.role, "content": m.content})

    client = llm.get_text_client()
    for _ in range(5):
        resp = client.chat.completions.create(
            model=config.DEEPSEEK_MODEL,
            messages=messages,
            tools=TOOLS,
            temperature=0.6,
        )
        msg = resp.choices[0].message
        tool_calls = getattr(msg, "tool_calls", None)

        if tool_calls:
            # 记录 assistant 的工具调用意图
            messages.append({
                "role": "assistant",
                "content": msg.content or "",
                "tool_calls": [
                    {
                        "id": tc.id,
                        "type": "function",
                        "function": {"name": tc.function.name, "arguments": tc.function.arguments},
                    }
                    for tc in tool_calls
                ],
            })
            # 执行工具并把结果回填
            for tc in tool_calls:
                args = json.loads(tc.function.arguments or "{}")
                result = execute_tool(tc.function.name, args)
                messages.append({
                    "role": "tool",
                    "tool_call_id": tc.id,
                    "content": json.dumps(result, ensure_ascii=False, default=str),
                })
            continue

        content = msg.content or ""
        parsed = parse_json(content)
        if isinstance(parsed, dict):
            return {
                "reply": parsed.get("reply", ""),
                "recommendations": parsed.get("recommendations", []),
            }
        return {"reply": content, "recommendations": []}

    raise HTTPException(500, "AI 思考轮次超限，请重新提问")


# H. AI 生成海报
@app.post("/agent/poster")
def poster(req: PosterRequest):
    if not config.SILICONFLOW_API_KEY:
        raise HTTPException(500, "未配置 SILICONFLOW_API_KEY，请在 drama-agent/.env 中填写")
    client = llm.get_image_client()
    prompt = prompts.POSTER_PROMPT.format(
        name=req.name, description=req.description or "暂无简介"
    )
    resp = client.images.generate(
        model=config.SILICONFLOW_IMAGE_MODEL,
        prompt=prompt,
        size=config.SILICONFLOW_IMAGE_SIZE,
    )

    # 兼容 url / b64_json 两种返回
    img_bytes = None
    url = None
    data = resp.data[0]
    if getattr(data, "url", None):
        url = data.url
    elif getattr(data, "b64_json", None):
        img_bytes = base64.b64decode(data.b64_json)
    else:
        raise HTTPException(500, "图像生成接口未返回图片")

    save_dir = Path(config.POSTER_SAVE_DIR)
    save_dir.mkdir(parents=True, exist_ok=True)
    filename = uuid.uuid4().hex + ".png"
    dest = save_dir / filename
    if url:
        urllib.request.urlretrieve(url, str(dest))
    else:
        dest.write_bytes(img_bytes)
    return {"posterUrl": "/images/ai-posters/" + filename}
