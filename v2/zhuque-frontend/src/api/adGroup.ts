import request from '@/utils/request'

/**
 * 广告组状态枚举
 */
export enum AdGroupStatus {
  DRAFT = 0,      // 草稿
  RUNNING = 1,    // 进行中
  PAUSED = 2      // 暂停
}

/**
 * 广告组状态名称
 */
export const AdGroupStatusName: Record<AdGroupStatus, string> = {
  [AdGroupStatus.DRAFT]: '草稿',
  [AdGroupStatus.RUNNING]: '进行中',
  [AdGroupStatus.PAUSED]: '暂停'
}

/**
 * 出价策略枚举
 */
export enum BidStrategy {
  FIXED_CPM = 1,       // 固定CPM
  SMART_BID = 2,       // 智能出价
  TARGET_CPA = 3,      // 目标CPA
  MAX_WIN = 4          // 最高赢价
}

/**
 * 出价策略名称
 */
export const BidStrategyName: Record<BidStrategy, string> = {
  [BidStrategy.FIXED_CPM]: '固定CPM',
  [BidStrategy.SMART_BID]: '智能出价',
  [BidStrategy.TARGET_CPA]: '目标CPA',
  [BidStrategy.MAX_WIN]: '最高赢价'
}

/**
 * 投放速度枚举
 */
export enum DeliveryMode {
  ACCELERATED = 1,    // 加速
  STANDARD = 2         // 均匀
}

/**
 * 投放速度名称
 */
export const DeliveryModeName: Record<DeliveryMode, string> = {
  [DeliveryMode.ACCELERATED]: '加速',
  [DeliveryMode.STANDARD]: '均匀'
}

/**
 * 投放时段类型枚举
 */
export enum ScheduleType {
  ALL_DAY = 1,         // 全天
  WEEKDAYS = 2,        // 工作日
  CUSTOM = 3           // 自定义
}

/**
 * 投放时段类型名称
 */
export const ScheduleTypeName: Record<ScheduleType, string> = {
  [ScheduleType.ALL_DAY]: '全天',
  [ScheduleType.WEEKDAYS]: '工作日',
  [ScheduleType.CUSTOM]: '自定义'
}

/**
 * 频次周期枚举
 */
export enum FrequencyCapPeriod {
  HOURLY = 1,          // 小时
  DAILY = 2,           // 天
  WEEKLY = 3,          // 周
  MONTHLY = 4          // 月
}

/**
 * 频次周期名称
 */
export const FrequencyCapPeriodName: Record<FrequencyCapPeriod, string> = {
  [FrequencyCapPeriod.HOURLY]: '小时',
  [FrequencyCapPeriod.DAILY]: '天',
  [FrequencyCapPeriod.WEEKLY]: '周',
  [FrequencyCapPeriod.MONTHLY]: '月'
}

/**
 * 品牌安全级别枚举
 */
export enum BrandSafetyLevel {
  LOW = 1,             // 宽松
  MEDIUM = 2,          // 中等
  HIGH = 3             // 严格
}

/**
 * 品牌安全级别名称
 */
export const BrandSafetyLevelName: Record<BrandSafetyLevel, string> = {
  [BrandSafetyLevel.LOW]: '宽松',
  [BrandSafetyLevel.MEDIUM]: '中等',
  [BrandSafetyLevel.HIGH]: '严格'
}

/**
 * 受众类型枚举
 */
export enum AudienceType {
  ALL = 1,             // 全部
  NEW = 2,             // 新客
  RETURNING = 3        // 老客
}

/**
 * 受众类型名称
 */
export const AudienceTypeName: Record<AudienceType, string> = {
  [AudienceType.ALL]: '全部',
  [AudienceType.NEW]: '新客',
  [AudienceType.RETURNING]: '老客'
}

/**
 * 广告组详情
 */
export interface AdGroupDetail {
  id: number
  campaignId: number
  campaignName: string
  advertiserId: number
  name: string
  description?: string

  // 出价设置
  bidStrategy: number
  bidStrategyName: string
  baseBidPrice: number
  maxBid?: number
  bidFloor?: number
  targetCpa?: number
  targetRoas?: number
  bidAdjustments?: string

  // 预算控制
  dailyBudget?: number
  dailyBudgetUsed: number
  dailyUsedPercent?: number
  remainingDailyBudget?: number

  // 投放速度
  deliveryMode?: number
  deliveryModeName?: string
  deliveryPace?: number

  // 定向设置概要
  targetingGeo?: string
  targetingGeoCount?: number
  targetingDevice?: string
  targetingUserSegments?: string
  targetingUserSegmentsCount?: number

  // 时段定向
  scheduleType?: number
  scheduleTypeName?: string
  scheduleConfig?: string

  // 频次控制
  frequencyCap?: number
  frequencyCapPeriod?: number
  frequencyCapPeriodName?: string
  frequencyCapDesc?: string

  // 品牌安全
  brandSafetyLevel?: number
  brandSafetyLevelName?: string

  // 状态和优先级
  status: number
  statusName: string
  displayStatusType: string
  priority: number

  // 统计信息
  adCount: number
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
 * 广告组列表项
 */
export interface AdGroupListItem {
  id: number
  campaignId: number
  campaignName: string
  name: string
  bidStrategy: number
  bidStrategyName: string
  baseBidPrice: number
  dailyBudget?: number
  dailyBudgetUsed: number
  dailyUsedPercent?: number
  status: number
  statusName: string
  displayStatusType: string
  adCount: number
  todayImpressions: number
  todayClicks: number
  todayCtr: number
  todayCost: number
  createTime: string
}

/**
 * 定向设置
 */
export interface TargetingSettings {
  geo?: string[]                    // 地域定向
  geoExclude?: string[]              // 地域排除
  device?: string[]                  // 设备定向
  os?: string[]                      // 操作系统
  osVersion?: Record<string, string> // OS版本
  carrier?: string[]                 // 运营商
  connectionType?: string[]          // 网络类型
  browser?: string[]                 // 浏览器
  keywords?: string[]                // 关键词
  keywordsExclude?: string[]         // 排除关键词
  iabCategories?: string[]           // IAB内容类别
  iabCategoriesExclude?: string[]    // 排除IAB类别
  userSegments?: string[]            // 人群包
  userSegmentsExclude?: string[]     // 排除人群包
  audienceType?: number              // 受众类型
}

/**
 * 时段配置
 */
export interface ScheduleConfig {
  type: ScheduleType
  timeRanges?: string[]              // 时段范围 ["09:00-12:00"]
  weekdays?: number[]                // 星期 [1,2,3,4,5]
}

/**
 * 出价调整
 */
export interface BidAdjustments {
  device?: Record<string, number>    // 设备调整 {"mobile": 1.2}
  geo?: Record<string, number>       // 地域调整 {"CN-11": 1.3}
}

/**
 * 创建广告组请求
 */
export interface AdGroupCreateRequest {
  campaignId: number
  name: string
  description?: string

  // 出价设置
  bidStrategy: number
  baseBidPrice: number
  maxBid?: number
  bidFloor?: number
  targetCpa?: number
  targetRoas?: number
  bidAdjustments?: string

  // 预算控制
  dailyBudget?: number

  // 投放速度
  deliveryMode?: number
  deliveryPace?: number

  // 定向设置
  targetingGeo?: string
  targetingGeoExclude?: string
  targetingDevice?: string
  targetingOs?: string
  targetingOsVersion?: string
  targetingCarrier?: string
  targetingConnectionType?: string
  targetingBrowser?: string
  targetingKeywords?: string
  targetingKeywordsExclude?: string
  targetingIabCategories?: string
  targetingIabCategoriesExclude?: string
  targetingUserSegments?: string
  targetingUserSegmentsExclude?: string
  targetingAudienceType?: number

  // 时段定向
  scheduleType?: number
  scheduleConfig?: string

  // 频次控制
  frequencyCap?: number
  frequencyCapPeriod?: number

  // 品牌安全
  brandSafetyLevel?: number
  brandSafetyCategoriesExclude?: string

  // 优先级
  priority?: number
}

/**
 * 更新广告组请求
 */
export type AdGroupUpdateRequest = Partial<Omit<AdGroupCreateRequest, 'campaignId'>>

/**
 * 广告组查询参数
 */
export interface AdGroupQuery {
  current?: number
  size?: number
  campaignId?: number
  name?: string
  status?: number
  bidStrategy?: number
}

/**
 * 广告组分页响应
 */
export interface AdGroupPageResponse {
  list: AdGroupListItem[]
  total: number
  current: number
  size: number
}

/**
 * 分页查询广告组列表
 */
export const getAdGroupPage = (params: AdGroupQuery) => {
  return request.post<any, AdGroupPageResponse>('/api/ad-groups/list', params)
}

/**
 * 根据ID查询广告组
 */
export const getAdGroupById = (id: number) => {
  return request.get<any, AdGroupDetail>(`/api/ad-groups/${id}`)
}

/**
 * 根据投放活动ID查询广告组列表
 */
export const getAdGroupsByCampaignId = (campaignId: number) => {
  return request.get<any, AdGroupListItem[]>(`/api/ad-groups/campaign/${campaignId}`)
}

/**
 * 创建广告组
 */
export const createAdGroup = (data: AdGroupCreateRequest) => {
  return request.post('/api/ad-groups', data)
}

/**
 * 更新广告组
 */
export const updateAdGroup = (id: number, data: AdGroupUpdateRequest) => {
  return request.put(`/api/ad-groups/${id}`, data)
}

/**
 * 删除广告组
 */
export const deleteAdGroup = (id: number) => {
  return request.delete(`/api/ad-groups/${id}`)
}

/**
 * 更新广告组状态
 */
export const updateAdGroupStatus = (id: number, status: number) => {
  return request.put(`/api/ad-groups/${id}/status`, null, {
    params: { status }
  })
}

/**
 * 启动广告组
 */
export const startAdGroup = (id: number) => {
  return request.post(`/api/ad-groups/${id}/start`)
}

/**
 * 暂停广告组
 */
export const pauseAdGroup = (id: number) => {
  return request.post(`/api/ad-groups/${id}/pause`)
}
