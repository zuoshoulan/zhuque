import request from '@/utils/request'

/**
 * 广告状态枚举
 */
export enum AdStatus {
  DRAFT = 0,      // 草稿
  RUNNING = 1,    // 进行中
  PAUSED = 2      // 暂停
}

/**
 * 广告状态名称
 */
export const AdStatusName: Record<AdStatus, string> = {
  [AdStatus.DRAFT]: '草稿',
  [AdStatus.RUNNING]: '进行中',
  [AdStatus.PAUSED]: '暂停'
}

/**
 * 广告详情
 */
export interface AdDetail {
  id: number
  campaignId: number
  campaignName: string
  adGroupId: number
  adGroupName: string
  advertiserId: number
  creativeId: number
  creativeName: string
  creativeType: string
  name: string
  landingPageUrl?: string
  displayUrl?: string
  trackingParams?: string
  weight: number
  status: number
  statusName: string
  displayStatusType: string

  // 统计信息
  todayImpressions: number
  todayClicks: number
  todayConversions: number
  todayCtr: number
  todayCvr: number
  todayCost: number
  totalImpressions: number
  totalClicks: number
  totalConversions: number
  totalCtr: number
  totalCvr: number
  totalCost: number

  createTime: string
  updateTime: string
}

/**
 * 广告列表项
 */
export interface AdListItem {
  id: number
  campaignId: number
  adGroupId: number
  adGroupName: string
  advertiserId: number
  creativeId: number
  creativeName: string
  name: string
  landingPageUrl?: string
  weight: number
  status: number
  statusName: string
  displayStatusType: string
  todayImpressions: number
  todayClicks: number
  todayCtr: number
  todayCost: number
  createTime: string
}

/**
 * 创建广告请求
 */
export interface AdCreateRequest {
  adGroupId: number
  creativeId: number
  name: string
  landingPageUrl?: string
  displayUrl?: string
  trackingParams?: string
  weight?: number
  status?: number
}

/**
 * 更新广告请求
 */
export interface AdUpdateRequest {
  name: string
  creativeId?: number
  landingPageUrl?: string
  displayUrl?: string
  trackingParams?: string
  weight?: number
  status?: number
}

/**
 * 广告查询参数
 */
export interface AdQuery {
  current?: number
  size?: number
  campaignId?: number
  adGroupId?: number
  creativeId?: number
  name?: string
  status?: number
}

/**
 * 广告分页响应
 */
export interface AdPageResponse {
  list: AdListItem[]
  total: number
  current: number
  size: number
}

/**
 * 分页查询广告列表
 */
export const getAdPage = (params: AdQuery) => {
  return request.post<any, AdPageResponse>('/api/ads/list', params)
}

/**
 * 根据ID查询广告
 */
export const getAdById = (id: number) => {
  return request.get<any, AdDetail>(`/api/ads/${id}`)
}

/**
 * 根据广告组ID查询广告列表
 */
export const getAdsByAdGroupId = (adGroupId: number) => {
  return request.get<any, AdListItem[]>(`/api/ads/ad-group/${adGroupId}`)
}

/**
 * 创建广告
 */
export const createAd = (data: AdCreateRequest) => {
  return request.post('/api/ads', data)
}

/**
 * 更新广告
 */
export const updateAd = (id: number, data: AdUpdateRequest) => {
  return request.put(`/api/ads/${id}`, data)
}

/**
 * 删除广告
 */
export const deleteAd = (id: number) => {
  return request.delete(`/api/ads/${id}`)
}

/**
 * 更新广告状态
 */
export const updateAdStatus = (id: number, status: number) => {
  return request.put(`/api/ads/${id}/status`, null, {
    params: { status }
  })
}

/**
 * 启动广告
 */
export const startAd = (id: number) => {
  return request.post(`/api/ads/${id}/start`)
}

/**
 * 暂停广告
 */
export const pauseAd = (id: number) => {
  return request.post(`/api/ads/${id}/pause`)
}
