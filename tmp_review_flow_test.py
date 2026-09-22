# -*- coding: utf-8 -*-
import json
import urllib.request

def call(base, path, body=None, token=None, method='POST'):
    req = urllib.request.Request(base + path, method=method)
    req.add_header('Content-Type', 'application/json; charset=utf-8')
    if token:
        req.add_header('Authorization', 'Bearer ' + token)
    data = json.dumps(body, ensure_ascii=False).encode('utf-8') if body else None
    try:
        with urllib.request.urlopen(req, data=data) as r:
            return r.status, json.loads(r.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        raw = e.read().decode('utf-8', errors='replace')
        try:
            return e.code, json.loads(raw)
        except Exception:
            return e.code, {'raw': raw[:200]}

# 1. 登录
_, login = call('http://localhost:8080', '/api/user/login', {'account': '13800000002', 'password': 'test123456'})
token = login['data']['token']
print('登录 ok')

# 2. AI 生成评价草稿（跑 5 次，看长度分布）
max_len = 0
content = ''
for i in range(5):
    _, d = call('http://localhost:8000', '/agent/write-review', {
        'dramaName': '狂飙',
        'keywords': '张译张颂文演技炸裂 剧情节奏紧凑 结局有点意难平 高启强人物塑造深刻'
    })
    L = len(d['content'])
    print('第%d次生成长度: %d' % (i + 1, L))
    if L > max_len:
        max_len = L
        content = d['content']

print('最长草稿:', max_len, '字')

# 3. 用最长草稿提交评价（dramaId=4 狂飙）
code, resp = call('http://localhost:8080', '/api/review', {
    'dramaId': 4,
    'rating': 9,
    'content': content
}, token)
print('提交评价 status:', code, 'resp:', json.dumps(resp, ensure_ascii=False)[:200])

# 4. 对照组：500字整的内容
code2, resp2 = call('http://localhost:8080', '/api/review', {
    'dramaId': 4,
    'rating': 9,
    'content': '好' * 500
}, token)
print('500字提交 status:', code2, 'resp:', json.dumps(resp2, ensure_ascii=False)[:200])
