# -*- coding: utf-8 -*-
import json
import urllib.request

def call(path, body):
    req = urllib.request.Request('http://localhost:8000' + path, method='POST')
    req.add_header('Content-Type', 'application/json; charset=utf-8')
    data = json.dumps(body, ensure_ascii=False).encode('utf-8')
    with urllib.request.urlopen(req, data=data) as r:
        return json.loads(r.read().decode('utf-8'))

# 模拟真实用户场景：剧名 + 几个关键词
d = call('/agent/write-review', {
    'dramaName': '狂飙',
    'keywords': '张译张颂文演技炸裂 剧情节奏紧凑 结局有点意难平 高启强人物塑造深刻'
})
content = d['content']
print('生成内容长度:', len(content))
print('是否超500字:', len(content) > 500)
print('---内容预览---')
print(content[:200])
