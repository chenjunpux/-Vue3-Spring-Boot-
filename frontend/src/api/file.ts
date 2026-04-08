import axios from './axios'

export interface UploadResult {
  id: number
  fileName: string
  fileUrl: string
  fileSize: number
}

export const fileApi = {
  upload: (file: File, bizType = 'common', bizId?: number) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('bizType', bizType)
    if (bizId) formData.append('bizId', String(bizId))
    return axios.post<any, any>('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  uploadMultiple: (files: File[], bizType = 'common', bizId?: number) => {
    const formData = new FormData()
    files.forEach(f => formData.append('files', f))
    formData.append('bizType', bizType)
    if (bizId) formData.append('bizId', String(bizId))
    return axios.post<any, any>('/file/upload/multiple', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  delete: (id: number) =>
    axios.delete<any, any>(`/file/${id}`),
}
