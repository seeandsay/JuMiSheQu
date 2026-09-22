import axios from 'axios'
import store from '@/store'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：携带 JWT
request.interceptors.request.use(
  config => {
    const token = store.state.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      const { status } = error.response
      // 401 未认证 / 403 令牌过期或无效 → 清除登录状态跳登录页
      if (status === 401 || status === 403) {
        store.commit('SET_TOKEN', '')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default request
