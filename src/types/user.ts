// ==================== 用户类型 ====================
export interface UserInfo {
  id: number
  username: string
  nickname?: string
  avatar?: string
  phone?: string
  email?: string
  gender: 0 | 1 | 2
  birthday?: string
  role: 1 | 2  // 1=普通用户, 2=管理员
  status: 0 | 1
  lastLogin?: string
  createdAt: string
}

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  nickname?: string
  phone?: string
  code?: string  // 验证码
}
