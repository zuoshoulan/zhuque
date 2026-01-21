import request from '@/utils/request'

/**
 * 登录接口
 */
export interface LoginRequest {
  account: string
  password: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  accessToken: string
  tokenType: string
  userInfo: {
    id: number
    username: string
    nickname: string
    email: string
    phone: string
    avatar: string | null
    forceChangePassword: boolean
    isSuperAdmin: boolean
    themePreference?: 'light' | 'dark' | 'auto'
  }
}

/**
 * 用户登录
 */
export const login = (data: LoginRequest) => {
  return request.post<any, LoginResponse>('/api/auth/login', data)
}

/**
 * 用户登出
 */
export const logout = () => {
  return request.post('/api/auth/logout')
}
