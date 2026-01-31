import request from '@/utils/request'

/**
 * 投放活动状态枚举（仅用于查询参数）
 * 实际显示状态由后端计算返回
 */
export enum CampaignStatus {
  DRAFT = 0,      // 草稿
  RUNNING = 1,    // 进行中
  PAUSED = 2      // 暂停
}

/**
 * 投放活动状态名称
 */
export const CampaignStatusName: Record<CampaignStatus, string> = {
  [CampaignStatus.DRAFT]: '草稿',
  [CampaignStatus.RUNNING]: '进行中',
  [CampaignStatus.PAUSED]: '暂停'
}

/**
 * 营销目标枚举
 */
export enum CampaignObjective {
  BRAND_AWARENESS = 1,  // 品牌曝光
  TRAFFIC = 2,           // 流量
  CONVERSION = 3,        // 转化
  ROI = 4                // ROI
}

/**
 * 营销目标名称
 */
export const CampaignObjectiveName: Record<CampaignObjective, string> = {
  [CampaignObjective.BRAND_AWARENESS]: '品牌曝光',
  [CampaignObjective.TRAFFIC]: '流量',
  [CampaignObjective.CONVERSION]: '转化',
  [CampaignObjective.ROI]: 'ROI'
}

/**
 * 投放活动详情
 */
export interface CampaignDetail {
  id: number
  advertiserId: number
  name: string
  description?: string
  campaignObjective: number
  campaignObjectiveName: string
  campaignGoalType?: number
  campaignGoalTypeName?: string
  campaignGoalValue?: number
  lifetimeBudget: number
  lifetimeBudgetUsed: number
  usedPercent?: number
  remainingBudget?: number
  startTime: string
  endTime: string
  durationDays?: number
  status: number
  statusName: string
  adGroupCount?: number
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
}

/**
 * 投放活动列表项
 */
export interface CampaignListItem {
  id: number
  name: string
  campaignObjective: number
  campaignObjectiveName: string
  lifetimeBudget: number
  lifetimeBudgetUsed: number
  usedPercent?: number
  status: number
  statusName: string
  displayStatusName: string      // 后端计算的显示状态名称
  displayStatusType: string      // 后端计算的显示状态类型
  startTime: string
  endTime: string
  timeRange: string
  createTime: string
}

/**
 * 创建投放活动请求
 */
export interface CampaignCreateRequest {
  name: string
  description?: string
  campaignObjective: number
  campaignGoalType?: number
  campaignGoalValue?: number
  lifetimeBudget: number
  startTime: string
  endTime: string
}

/**
 * 更新投放活动请求
 */
export interface CampaignUpdateRequest {
  name?: string
  description?: string
  campaignObjective?: number
  campaignGoalType?: number
  campaignGoalValue?: number
  lifetimeBudget?: number
  startTime?: string
  endTime?: string
}

/**
 * 投放活动查询参数
 */
export interface CampaignQuery {
  current?: number
  size?: number
  name?: string
  campaignObjective?: number
  status?: number
}

/**
 * 投放活动分页响应
 */
export interface CampaignPageResponse {
  list: CampaignListItem[]
  total: number
  current: number
  size: number
}

/**
 * 分页查询投放活动列表
 */
export const getCampaignPage = (params: CampaignQuery) => {
  return request.post<any, CampaignPageResponse>('/api/campaigns/list', params)
}

/**
 * 根据ID查询投放活动
 */
export const getCampaignById = (id: number) => {
  return request.get<any, CampaignDetail>(`/api/campaigns/${id}`)
}

/**
 * 创建投放活动
 */
export const createCampaign = (data: CampaignCreateRequest) => {
  return request.post('/api/campaigns', data)
}

/**
 * 更新投放活动
 */
export const updateCampaign = (id: number, data: CampaignUpdateRequest) => {
  return request.put(`/api/campaigns/${id}`, data)
}

/**
 * 删除投放活动
 */
export const deleteCampaign = (id: number) => {
  return request.delete(`/api/campaigns/${id}`)
}

/**
 * 更新投放活动状态
 */
export const updateCampaignStatus = (id: number, status: number) => {
  return request.put(`/api/campaigns/${id}/status`, null, {
    params: { status }
  })
}

/**
 * 启动投放活动
 */
export const startCampaign = (id: number) => {
  return request.post(`/api/campaigns/${id}/start`)
}

/**
 * 暂停投放活动
 */
export const pauseCampaign = (id: number) => {
  return request.post(`/api/campaigns/${id}/pause`)
}
