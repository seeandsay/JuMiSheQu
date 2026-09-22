import axios from 'axios'

// Agent 服务独立实例：baseURL /agent，不走 /api 的 JWT 拦截
const agent = axios.create({
  baseURL: '/agent',
  timeout: 60000 // LLM 生成较慢，放宽超时
})

agent.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
)

export function writeReview(dramaName, keywords) {
  return agent.post('/write-review', { dramaName, keywords })
}

export function chat(messages) {
  return agent.post('/chat', { messages })
}

export function getSummary(dramaId, dramaName) {
  return agent.post('/summary', { dramaId, dramaName })
}

export function generatePoster(name, description) {
  return agent.post('/poster', { name, description })
}
