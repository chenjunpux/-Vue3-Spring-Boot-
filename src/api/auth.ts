import request from './axios'
import type { LoginParams, UserInfo } from '@/types/user'

export interface LoginRes {
  accessToken: string
  refreshToken: string
  user: UserInfo
}

// 登录
export function loginApi(data: LoginParams) {
  return request.post('/auth/login', data) as Promise<{ code: number; msg: string; data: LoginRes }>
}

// 注册
export function registerApi(data: { username: string; password: string; nickname?: string; phone?: string }) {
  return request.post('/auth/register', data) as Promise<{ code: number; msg: string }>
}

// 退出登录
export function logoutApi() {
  return request.post('/auth/logout') as Promise<{ code: number; msg: string }>
}

// 获取当前用户信息
export function getUserInfoApi() {
  return request.get('/auth/me') as Promise<{ code: number; msg: string; data: UserInfo }>
}

// 刷新 Token
export function refreshTokenApi(data: { refreshToken: string }) {
  return request.post('/auth/refresh', data) as Promise<{ code: number; msg: string; data: { accessToken: string } }>
}

// 修改密码
export function updatePasswordApi(data: { oldPassword: string; newPassword: string }) {
  return request.put('/user/password', data) as Promise<{ code: number; msg: string }>
}
