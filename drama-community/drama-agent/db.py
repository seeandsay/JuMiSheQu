import pymysql
from pymysql.cursors import DictCursor

import config


def get_conn():
    return pymysql.connect(
        host=config.DB_HOST,
        port=config.DB_PORT,
        user=config.DB_USER,
        password=config.DB_PASSWORD,
        database=config.DB_NAME,
        charset="utf8mb4",
        cursorclass=DictCursor,
    )


def query_dramas(keyword=None, genre=None, limit=10):
    """按剧名关键词/类型模糊查询剧集，供 Agent 工具调用。"""
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            sql = "SELECT id, name, genre, description, release_date, poster FROM drama WHERE 1=1"
            args = []
            if keyword:
                sql += " AND name LIKE %s"
                args.append("%" + keyword + "%")
            if genre:
                sql += " AND genre LIKE %s"
                args.append("%" + genre + "%")
            sql += " ORDER BY create_time DESC LIMIT %s"
            args.append(limit)
            cur.execute(sql, args)
            return cur.fetchall()
    finally:
        conn.close()


def query_reviews(drama_id, limit=50):
    """查询某剧集最近的用户评价（评分 + 文字），供口碑速览和 Agent 工具使用。"""
    conn = get_conn()
    try:
        with conn.cursor() as cur:
            cur.execute(
                "SELECT rating, content, create_time FROM review "
                "WHERE drama_id=%s ORDER BY create_time DESC LIMIT %s",
                (drama_id, limit),
            )
            return cur.fetchall()
    finally:
        conn.close()
