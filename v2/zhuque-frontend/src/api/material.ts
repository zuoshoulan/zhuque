import request from '@/utils/request'

/**
 * Banner素材扩展属性
 */
export interface BannerExt {
  pos?: number        // 广告位置:1=首屏/2=次屏
  btype?: number[]    // 横幅类型列表
  wmode?: number      // 窗口模式:1=正常/2=全屏
  ext?: string        // 扩展字段，JSON格式
}

/**
 * Video素材扩展属性
 */
export interface VideoExt {
  linearity?: number          // 播放方式:1=线性播放/2=非线性播放
  sequence?: number           // 视频序列号,从1开始
  minDuration?: number        // 最小视频时长(秒)
  maxDuration?: number        // 最大视频时长(秒)
  startdelay?: number         // 前贴片:0,中贴片:-1,后贴片:>0
  skip?: number               // 跳过按钮:0=不可跳过/1=可跳过
  skipmin?: number            // 最少播放多少秒后可跳过
  skipafter?: number          // 多少秒后显示跳过按钮
  placement?: number          // 1=流内/2=插屏/3=悬停
  playbackend?: number        // 播放方法:1=自动播放有声/2=自动播放静音/3=点击播放/4=鼠标悬停
  playableafter?: number      // 可播放的最长秒数
  podid?: string              // 广告组ID
  podsize?: number            // 广告组大小
  podseq?: number             // 广告组序列号
  mincpmpersec?: number       // 每秒最低CPM
  maxseq?: number             // 最大序列号
  render?: number             // 渲染方式
  api?: number[]              // 支持的API框架列表
  ext?: string                // 扩展字段，JSON格式
}

/**
 * Audio素材扩展属性
 */
export interface AudioExt {
  sequence?: number      // 音频序列号,从1开始
  minDuration?: number    // 最小音频时长(秒)
  maxDuration?: number    // 最大音频时长(秒)
  startdelay?: number     // 开始延迟
  api?: number[]          // 支持的API框架列表
  ext?: string            // 扩展字段，JSON格式
}

/**
 * Native素材扩展属性
 */
export interface NativeExt {
  requestJson?: string   // 原生广告请求JSON字符串
  ver?: string           // 原生API版本
  ext?: string           // 扩展字段，JSON格式
}

/**
 * 素材信息
 */
export interface MaterialInfo {
  id: number
  materialId: string
  creativeId: number
  creativeName: string
  name: string
  format: number
  formatName: string
  width: number
  height: number
  fileSize: number
  fileType: string
  fileUrl: string
  thumbnailUrl?: string
  mimes?: string[]
  dur?: number
  bannerExt?: BannerExt
  videoExt?: VideoExt
  audioExt?: AudioExt
  nativeExt?: NativeExt
  createTime: string
  updateTime: string
}

/**
 * 素材列表项
 */
export interface MaterialListItem {
  id: number
  materialId: string
  creativeId: number
  creativeName: string
  name: string
  format: number
  formatName: string
  width: number
  height: number
  fileUrl: string
  createTime: string
}

/**
 * 创建素材请求
 */
export interface MaterialCreateRequest {
  // 主表字段
  creativeId: number
  format: number
  name: string
  width: number
  height: number
  fileId: number
  mimes?: string[]
  dur?: number

  // 扩展表字段（根据format使用对应的扩展对象）
  bannerExt?: BannerExt    // format=1时使用
  videoExt?: VideoExt      // format=2时使用
  audioExt?: AudioExt      // format=3时使用
  nativeExt?: NativeExt    // format=4时使用
}

/**
 * 更新素材请求
 */
export interface MaterialUpdateRequest {
  id: number
  // 主表字段
  name?: string
  width?: number
  height?: number
  fileId?: number
  mimes?: string[]
  dur?: number

  // 扩展表字段
  bannerExt?: BannerExt
  videoExt?: VideoExt
  audioExt?: AudioExt
  nativeExt?: NativeExt
}

/**
 * 素材查询参数
 */
export interface MaterialQuery {
  current?: number
  size?: number
  creativeId?: number
  format?: number
  width?: number
  height?: number
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
