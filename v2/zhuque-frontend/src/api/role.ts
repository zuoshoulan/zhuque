import request from '@/utils/request'

/**
 * 角色信息
 */
export interface RoleInfo {
  id: number
  roleName: string
  roleCode: string
  description: string
  status: number
  createTime: string
  updateTime: string
}

/**
 * 角色查询参数
 */
export interface RoleQuery {
  page?: number
  size?: number
  keyword?: string
  status?: number
}

/**
 * 角色分页响应
 */
export interface RolePageResponse {
  records: RoleInfo[]
  total: number
  current: number
  size: number
}

/**
 * 分页查询角色列表
 */
export const getRolePage = (params: RoleQuery) => {
  return request.get<any, RolePageResponse>('/api/role/page', { params })
}

/**
 * 根据ID查询角色
 */
export const getRoleById = (id: number) => {
  return request.get<any, RoleInfo>(`/api/role/${id}`)
}

/**
 * 创建角色
 */
export const createRole = (data: Partial<RoleInfo>) => {
  return request.post('/api/role', data)
}

/**
 * 更新角色
 */
export const updateRole = (id: number, data: Partial<RoleInfo>) => {
  return request.put(`/api/role/${id}`, data)
}

/**
 * 删除角色
 */
export const deleteRole = (id: number) => {
  return request.delete(`/api/role/${id}`)
}

/**
 * 修改角色状态
 */
export const updateRoleStatus = (id: number, status: number) => {
  return request.put(`/api/role/${id}/status`, { status })
}
