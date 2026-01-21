import request from '@/utils/request'

/**
 * 创意信息
 */
export interface CreativeInfo {
  id: number
  creativeName: string
  creativeType: string
  materialId: number
  status: number
  createTime: string
  updateTime: string
}

/**
 * 创意列表项
 */
export interface CreativeListItem {
  id: number
  creativeId: string
  advertiserId: number
  name: string
  status: number
  statusName: string
  materialCount: number
  createTime: string
}

/**
 * 创建创意请求
 */
export interface CreativeCreateRequest {
  creativeName: string
  creativeType: string
  materialId: number
}

/**
 * 更新创意请求
 */
export interface CreativeUpdateRequest {
  creativeName?: string
  creativeType?: string
  materialId?: number
}

/**
 * 创意查询参数
 */
export interface CreativeQuery {
  page?: number
  size?: number
  keyword?: string
  status?: number
}

/**
 * 创意分页响应
 */
export interface CreativePageResponse {
  list: CreativeListItem[]
  total: number
  current: number
  size: number
  pages: number
}

/**
 * 分页查询创意列表
 */
export const getCreativePage = (params: CreativeQuery) => {
  return request.post<any, CreativePageResponse>('/api/rtb/creative/list', params)
}

/**
 * 根据ID查询创意
 */
export const getCreativeById = (id: number) => {
  return request.get<any, CreativeInfo>(`/api/rtb/creative/${id}`)
}

/**
 * 创建创意
 */
export const createCreative = (data: CreativeCreateRequest) => {
  return request.post('/api/rtb/creative', data)
}

/**
 * 更新创意
 */
export const updateCreative = (id: number, data: CreativeUpdateRequest) => {
  return request.put(`/api/rtb/creative/${id}`, data)
}

/**
 * 删除创意
 */
export const deleteCreative = (id: number) => {
  return request.delete(`/api/rtb/creative/${id}`)
}

/**
 * 更新创意状态
 */
export const updateCreativeStatus = (id: number, status: number) => {
  return request.put(`/api/rtb/creative/${id}/status`, null, {
    params: { status }
  })
}
