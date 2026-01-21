import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    const token = userStore.token

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 如果返回的状态码为200，说明接口请求成功
    if (res.code === 200) {
      // 如果有分页信息，将data和page合并返回
      if (res.page) {
        return {
          list: res.data,
          total: res.page.total,
          current: res.page.current,
          size: res.page.size,
          pages: res.page.pages
        }
      }
      // 普通接口直接返回data
      return res.data
    } else {
      // 显示错误信息
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error) => {
    // 处理403未授权
    if (error.response?.status === 403) {
      ElMessage.error('请先登录')
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    } else if (error.response?.status === 500) {
      ElMessage.error(error.response.data?.message || '服务器错误')
    } else {
      ElMessage.error(error.message || '网络错误')
    }

    return Promise.reject(error)
  }
)

export default request
