import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fileApi } from '@/api/file'

/**
 * 文件上传组合式函数
 * 用于在组件中便捷地使用文件上传功能
 */
export function useFileUpload() {
  const uploading = ref(false)

  /**
   * 上传单个文件
   */
  async function uploadFile(
    file: File,
    bizType = 'common',
    bizId?: number
  ): Promise<{ id: number; fileUrl: string; fileName: string } | null> {
    uploading.value = true
    try {
      const res = await fileApi.upload(file, bizType, bizId)
      if (res.code === 200) {
        return res.data
      }
      ElMessage.error(res.msg || '上传失败')
      return null
    } catch (e: any) {
      ElMessage.error(e.message || '上传失败')
      return null
    } finally {
      uploading.value = false
    }
  }

  /**
   * 上传多个文件
   */
  async function uploadFiles(
    files: File[],
    bizType = 'common',
    bizId?: number
  ): Promise<Array<{ id: number; fileUrl: string; fileName: string }>> {
    uploading.value = true
    try {
      const res = await fileApi.uploadMultiple(files, bizType, bizId)
      if (res.code === 200) {
        return res.data || []
      }
      ElMessage.error(res.msg || '上传失败')
      return []
    } catch (e: any) {
      ElMessage.error(e.message || '上传失败')
      return []
    } finally {
      uploading.value = false
    }
  }

  /**
   * 上传图片（带预览）
   */
  async function uploadImage(file: File, bizType = 'article'): Promise<string | null> {
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      ElMessage.warning('请上传图片文件')
      return null
    }
    // 检查文件大小（5MB）
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过 5MB')
      return null
    }
    const result = await uploadFile(file, bizType)
    return result?.fileUrl || null
  }

  /**
   * 上传头像
   */
  async function uploadAvatar(file: File, userId?: number): Promise<string | null> {
    return uploadImage(file, 'avatar')
  }

  return {
    uploading,
    uploadFile,
    uploadFiles,
    uploadImage,
    uploadAvatar,
  }
}
