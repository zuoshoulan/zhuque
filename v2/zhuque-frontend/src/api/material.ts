import request from '@/utils/request'

/**
 * 素材信息
 */
export interface MaterialInfo {
  id: number
  materialName: string
  materialType: string
  width: number
  height: number
  fileSize: number
  fileUrl: string
  status: number
  createTime: string
  updateTime: string
}

/**
 * 素材列表项
 */
export interface MaterialListItem {
  id: number
  materialName: string
  materialType: string
  width: number
  height: number
  fileSize: number
  fileUrl: string
  status: number
  createTime: string
}

/**
 * 创建素材请求
 */
export interface MaterialCreateRequest {
  materialName: string
  materialType: string
  fileUrl: string
  width?: number
  height?: number
}

/**
 * 更新素材请求
 */
export interface MaterialUpdateRequest {
  materialName?: string
  materialType?: string
  fileUrl?: string
  width?: number
  height?: number
}

/**
 * 素材查询参数
 */
export interface MaterialQuery {
  page?: number
  size?: number
  keyword?: string
  status?: number
}

/**
 * 素材分页响应
 */
export interface MaterialPageResponse {
  list: MaterialListItem[]
  total: number
  current: number
  size: number
  pages: number
}

/**
 * 分页查询素材列表
 */
export const getMaterialPage = (params: MaterialQuery) => {
  return request.post<any, MaterialPageResponse>('/api/rtb/material/list', params)
}

/**
 * 根据ID查询素材
 */
export const getMaterialById = (id: number) => {
  return request.get<any, MaterialInfo>(`/api/rtb/material/${id}`)
}

/**
 * 创建素材
 */
export const createMaterial = (data: MaterialCreateRequest) => {
  return request.post('/api/rtb/material', data)
}

/**
 * 更新素材
 */
export const updateMaterial = (id: number, data: MaterialUpdateRequest) => {
  return request.put(`/api/rtb/material/${id}`, data)
}

/**
 * 删除素材
 */
export const deleteMaterial = (id: number) => {
  return request.delete(`/api/rtb/material/${id}`)
}

/**
 * 上传素材文件
 */
export const uploadMaterial = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, string>('/api/rtb/material/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新素材状态
 */
export const updateMaterialStatus = (id: number, status: number) => {
  return request.put(`/api/rtb/material/${id}/status`, null, {
    params: { status }
  })
}
