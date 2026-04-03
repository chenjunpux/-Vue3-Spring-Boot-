import axios, { type AxiosInstance, type AxiosError, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@stores/user'

// ==================== 创建 Axios 实例 ====================
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
  },
})

// ==================== 请求拦截器 ====================
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加 Token
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加时间戳防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now(),
      }
    }

    return config
  },
  (error: AxiosError) => {
    console.error('❌ 请求错误:', error)
    return Promise.reject(error)
  }
)

// ==================== 响应拦截器 ====================
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 根据后端统一响应格式判断
    if (res.code !== undefined) {
      if (res.code === 200) {
        return res
      }

      // 业务错误
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }

    // 非标准格式直接返回
    return res
  },

  async (error: AxiosError<{ code: number; msg: string }>) => {
    const status = error.response?.status
    const data = error.response?.data
    const userStore = useUserStore()

    switch (status) {
      case 400:
        ElMessage.error(data?.msg || '参数错误')
        break

      case 401: {
        // Token 过期，尝试刷新
        if (!error.config?.headers?.['X-Retry']) {
          try {
            const newToken = await userStore.doRefreshToken()
            if (newToken && error.config) {
              error.config.headers.Authorization = `Bearer ${newToken}`
              error.config.headers['X-Retry'] = 'true'
              return service(error.config)
            }
          } catch {
            userStore.logout()
            ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
              confirmButtonText: '去登录',
              cancelButtonText: '留在本页',
              type: 'warning',
            }).then(() => {
              router.push('/login')
            })
          }
        } else {
          userStore.logout()
          router.push('/login')
        }
        break
      }

      case 403:
        ElMessage.error(data?.msg || '无权限访问')
        break

      case 404:
        ElMessage.error(data?.msg || '请求资源不存在')
        break

      case 500:
        ElMessage.error('服务器异常，请稍后重试')
        break

      case 502:
      case 503:
        ElMessage.error('服务不可用，请稍后重试')
        break

      case 504:
        ElMessage.error('请求超时，请检查网络')
        break

      default:
        if (!error.config?.url?.includes('/auth/login')) {
          ElMessage.error(data?.msg || error.message || '网络错误')
        }
    }

    return Promise.reject(error)
  }
)

// ==================== 导出 ====================
export default service
