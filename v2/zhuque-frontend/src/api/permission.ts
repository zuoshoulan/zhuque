import request from '@/utils/request'

/**
 * 权限信息
 */
export interface PermissionInfo {
  id: number
  parentId: number
  permissionCode: string
  permissionName: string
  permissionType: number
  path?: string
  method?: string
  icon?: string
  sortOrder?: number
  status: number
  createTime: string
  updateTime?: string
  children?: PermissionInfo[]
}

/**
 * 创建权限请求
 */
export interface CreatePermissionRequest {
  parentId: number
  permissionCode: string
  permissionName: string
  permissionType: number
  path?: string
  method?: string
  icon?: string
  sortOrder?: number
}

/**
 * 获取权限树
 */
export const getPermissionTree = () => {
  return request.get<any, PermissionInfo[]>('/api/permissions/tree')
}

/**
 * 获取权限列表（平铺）
 */
export const getPermissionList = () => {
  return request.get<any, PermissionInfo[]>('/api/permissions/list')
}

/**
 * 获取权限详情
 */
export const getPermissionDetail = (id: number) => {
  return request.get<any, PermissionInfo>(`/api/permissions/${id}`)
}

/**
 * 创建权限
 */
export const createPermission = (data: CreatePermissionRequest) => {
  return request.post<any, number>('/api/permissions', data)
}

/**
 * 更新权限
 */
export const updatePermission = (id: number, data: CreatePermissionRequest) => {
  return request.put<any, void>(`/api/permissions/${id}`, data)
}

/**
 * 删除权限
 */
export const deletePermission = (id: number) => {
  return request.delete<any, void>(`/api/permissions/${id}`)
}

/**
 * 权限类型枚举
 */
export enum PermissionType {
  ROUTE = 1, // 路由
  BUTTON = 2, // 按钮
  API = 3 // 接口
}

/**
 * 权限类型映射
 */
export const PermissionTypeMap: Record<number, string> = {
  [PermissionType.ROUTE]: '路由',
  [PermissionType.BUTTON]: '按钮',
  [PermissionType.API]: '接口'
}

/**
 * 权限类型标签类型
 */
export const PermissionTypeTagMap: Record<number, string> = {
  [PermissionType.ROUTE]: 'success',
  [PermissionType.BUTTON]: 'warning',
  [PermissionType.API]: 'info'
}
