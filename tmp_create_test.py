import json
import urllib.request

BASE = 'http://localhost:8080'

def call(method, path, body=None, token=None):
    req = urllib.request.Request(BASE + path, method=method)
    req.add_header('Content-Type', 'application/json; charset=utf-8')
    if token:
        req.add_header('Authorization', 'Bearer ' + token)
    data = json.dumps(body, ensure_ascii=False).encode('utf-8') if body else None
    try:
        with urllib.request.urlopen(req, data=data) as resp:
            return resp.status, json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        return e.code, json.loads(e.read().decode('utf-8'))

# 1. 登录
code, r = call('POST', '/api/user/login', {'account': '13800000002', 'password': 'test123456'})
print('登录:', code, r.get('code'))
token = r['data']['token']

# 2. 创建剧集
code, r = call('POST', '/api/drama', {
    'name': '白夜追凶',
    'genre': '悬疑,犯罪,剧情',
    'releaseDate': '2017-08-30',
    'description': '双胞胎兄弟昼夜交替查案，潘粤明一人分饰两角。',
    'poster': ''
}, token)
print('创建:', code, r)

# 3. 同名再创建
code, r = call('POST', '/api/drama', {'name': '白夜追凶'}, token)
print('重名创建:', code, r)

# 4. 搜索
from urllib.parse import quote
code, r = call('GET', '/api/drama/search?keyword=' + quote('白夜追凶'))
print('搜索:', code, '结果数:', len(r.get('data') or []), '首条:', (r.get('data') or [{}])[0].get('name'))

# 5. 未登录创建
code, r = call('POST', '/api/drama', {'name': '白夜追凶2'})
print('未登录创建:', code, r)
