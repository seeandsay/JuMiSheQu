# -*- coding: utf-8 -*-
from openai import OpenAI

import config


def get_text_client():
    return OpenAI(api_key=config.DEEPSEEK_API_KEY, base_url=config.DEEPSEEK_BASE_URL)


def get_image_client():
    return OpenAI(api_key=config.SILICONFLOW_API_KEY, base_url=config.SILICONFLOW_BASE_URL)


def chat(messages, temperature=0.7, max_tokens=800, json_mode=False):
    """单次文本对话（DeepSeek）。json_mode=True 时要求严格输出 JSON。"""
    client = get_text_client()
    kwargs = dict(
        model=config.DEEPSEEK_MODEL,
        messages=messages,
        temperature=temperature,
        max_tokens=max_tokens,
    )
    if json_mode:
        kwargs["response_format"] = {"type": "json_object"}
    resp = client.chat.completions.create(**kwargs)
    return resp.choices[0].message.content or ""
