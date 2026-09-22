# -*- coding: utf-8 -*-
"""智能推荐 Agent 可调用的工具（查询站内剧库与评价）。"""

import db

TOOLS = [
    {
        "type": "function",
        "function": {
            "name": "query_dramas",
            "description": (
                "在剧迷社区剧库中搜索剧集。按剧名关键词或类型模糊匹配，"
                "返回剧集 id、名称、类型、简介、上映日期。关键词可为空以列出全部剧集。"
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "keyword": {"type": "string", "description": "剧名关键词，可为空"},
                    "genre": {"type": "string", "description": "类型关键词，如 悬疑/犯罪/古装，可为空"},
                },
                "required": [],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "query_reviews",
            "description": (
                "查询某部剧集的用户评价（评分和文字），用于了解社区口碑，"
                "为推荐提供依据。参数是剧集 id。"
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "drama_id": {"type": "integer", "description": "剧集 id"},
                },
                "required": ["drama_id"],
            },
        },
    },
]


def execute_tool(name, args):
    """执行工具并返回可 JSON 序列化的结果。"""
    if name == "query_dramas":
        rows = db.query_dramas(
            keyword=(args.get("keyword") or None),
            genre=(args.get("genre") or None),
            limit=10,
        )
        return {"count": len(rows), "dramas": rows}
    if name == "query_reviews":
        rows = db.query_reviews(int(args.get("drama_id", 0)), limit=30)
        return {"count": len(rows), "reviews": rows}
    return {"error": "unknown tool: %s" % name}
