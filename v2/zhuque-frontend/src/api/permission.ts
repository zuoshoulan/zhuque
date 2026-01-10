import request from '@/utils/request'

/**
 * 权限信息
 */
export interface PermissionInfo {
  id: number
  parentId: number
  permissionCode: string
  permissionName: string
  permissionType: string
  path?: string
  method?: string
  icon?: string
  sortOrder?: number
  status: number
  createTime: string
  children?: PermissionInfo[]
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
