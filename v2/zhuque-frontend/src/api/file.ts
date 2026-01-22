import request from '@/utils/request'

/**
 * 文件上传响应
 */
export interface FileUploadResponse {
  fileId: number
  fileName: string
  fileSize: number
  fileType: string
  width?: number
  height?: number
}

/**
 * 上传文件
 * @param file 文件对象
 * @returns 文件信息（含尺寸）
 */
export const uploadFile = (file: File): Promise<FileUploadResponse> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, FileUploadResponse>('/api/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 根据文件ID下载文件
 * @param fileId 文件ID
 * @returns 文件Blob
 */
export const downloadFile = (fileId: string) => {
  return request.get<any, Blob>(`/api/file/${fileId}`, {
    responseType: 'blob'
  })
}

/**
 * 获取文件信息
 * @param fileId 文件ID
 */
export const getFileInfo = (fileId: string) => {
  return request.get<any, any>(`/api/file/${fileId}/info`)
}
