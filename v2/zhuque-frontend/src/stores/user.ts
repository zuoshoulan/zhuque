import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginResponse } from '@/api/auth'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<LoginResponse['userInfo'] | null>(
    JSON.parse(localStorage.getItem('userInfo') || 'null')
  )

  /**
   * 登录
   */
  const login = async (account: string, password: string) => {
    const data = await loginApi({ account, password })

    token.value = data.accessToken
    userInfo.value = data.userInfo

    localStorage.setItem('token', data.accessToken)
    localStorage.setItem('userInfo', JSON.stringify(data.userInfo))

    return data
  }

  /**
   * 登出
   */
  const logout = () => {
    token.value = ''
    userInfo.value = null

    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  /**
   * 检查是否已登录
   */
  const isLoggedIn = () => {
    return !!token.value
  }

  return {
    token,
    userInfo,
    login,
    logout,
    isLoggedIn
  }
})
