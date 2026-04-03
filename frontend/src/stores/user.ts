import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, getUserInfoApi, refreshTokenApi } from '@api/auth'
import type { LoginParams, UserInfo } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  // ------------------- State -------------------
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshTokenVal = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(null)

  // ------------------- Getters -------------------
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 2)
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '用户')

  // 菜单权限列表（逗号分隔）
  const menuPermissions = computed(() => {
    const perms = userInfo.value?.menuPermissions
    if (!perms) return null  // null 表示拥有全部权限
    return perms.split(',').filter(Boolean)
  })

  // ------------------- Actions -------------------
  async function login(params: LoginParams) {
    const res = await loginApi(params)
    const { accessToken, refreshToken: refresh, user } = res.data

    token.value = accessToken
    refreshTokenVal.value = refresh
    userInfo.value = user

    localStorage.setItem('token', accessToken)
    localStorage.setItem('refreshToken', refresh)

    return res
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfoApi()
      userInfo.value = res.data
    } catch {
      logout()
      throw new Error('获取用户信息失败')
    }
  }

  async function doRefreshToken() {
    try {
      const res = await refreshTokenApi({ refreshToken: refreshTokenVal.value })
      const { accessToken } = res.data
      token.value = accessToken
      localStorage.setItem('token', accessToken)
      return accessToken
    } catch {
      logout()
      throw new Error('Token 刷新失败')
    }
  }

  function logout() {
    token.value = ''
    refreshTokenVal.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  return {
    token,
    refreshTokenVal,
    userInfo,
    isLoggedIn,
    isAdmin,
    nickname,
    login,
    fetchUserInfo,
    doRefreshToken,
    logout,
  }
})
