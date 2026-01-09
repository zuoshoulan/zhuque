import request from '@/utils/request'

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string | null
  status: number
  forceChangePassword: boolean
  createTime: string
  updateTime: string
}

/**
 * 用户查询参数
 */
export interface UserQuery {
  page?: number
  size?: number
  keyword?: string
  status?: number
}

/**
 * 用户分页响应
 */
export interface UserPageResponse {
  records: UserInfo[]
  total: number
  current: number
  size: number
}

/**
 * 分页查询用户列表
 */
export const getUserPage = (params: UserQuery) => {
  return request.get<any, UserPageResponse>('/api/user/page', { params })
}

/**
 * 根据ID查询用户
 */
export const getUserById = (id: number) => {
  return request.get<any, UserInfo>(`/api/user/${id}`)
}

/**
 * 创建用户
 */
export const createUser = (data: Partial<UserInfo>) => {
  return request.post('/api/user', data)
}

/**
 * 更新用户
 */
export const updateUser = (id: number, data: Partial<UserInfo>) => {
  return request.put(`/api/user/${id}`, data)
}

/**
 * 删除用户
 */
export const deleteUser = (id: number) => {
  return request.delete(`/api/user/${id}`)
}

/**
 * 修改用户状态
 */
export const updateUserStatus = (id: number, status: number) => {
  return request.put(`/api/user/${id}/status`, { status })
}

/**
 * 重置密码请求
 */
export interface ResetPasswordRequest {
  newPassword?: string
}

/**
 * 重置密码响应
 */
export interface ResetPasswordResponse {
  userId: number
  username: string
  phone: string
  password: string
  passwordType: 'custom' | 'default'
}

/**
 * 重置用户密码
 */
export const resetUserPassword = (userId: number, data: ResetPasswordRequest) => {
  return request.post<any, ResetPasswordResponse>(`/api/user/${userId}/reset-password`, data)
}
