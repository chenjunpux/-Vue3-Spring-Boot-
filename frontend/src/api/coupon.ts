import axios from './axios'

export interface Coupon {
  id: number
  name: string
  description: string
  type: number
  discountValue: number
  minAmount: number
  maxDiscount?: number
  totalCount: number
  remainCount: number
  perUserLimit: number
  applicableType: number
  validStart: string
  validEnd: string
  status: number
}

export interface UserCoupon {
  id: number
  userId: number
  couponId: number
  couponName: string
  couponType: number
  discountValue: number
  minAmount: number
  maxDiscount?: number
  status: number
  receiveTime: string
  useTime?: string
  expireTime: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export const couponApi = {
  // ========== 用户端 API ==========
  available: (params?: { page?: number; pageSize?: number }) =>
    axios.get('/coupon/available', { params }) as Promise<{ code: number; msg: string; data: PageResult<Coupon> }>,

  receive: (couponId: number) =>
    axios.post(`/coupon/receive/${couponId}`) as Promise<{ code: number; msg: string; data: UserCoupon }>,

  myCoupons: (status?: number) =>
    axios.get('/coupon/my', { params: { status } }) as Promise<{ code: number; msg: string; data: UserCoupon[] }>,

  calculate: (couponId: number, orderAmount: number) =>
    axios.post('/coupon/calculate', { couponId, orderAmount }) as Promise<{ code: number; msg: string; data: { canUse: boolean; discountAmount: number } }>,

  canUse: (couponId: number, orderAmount: number) =>
    axios.get(`/coupon/canUse/${couponId}`, { params: { orderAmount } }) as Promise<{ code: number; msg: string; data: { canUse: boolean } }>,

  // ========== 管理端 API ==========
  admin: {
    list: (params?: { page?: number; pageSize?: number; status?: number }) =>
      axios.get('/coupon/admin/list', { params }) as Promise<{ code: number; msg: string; data: PageResult<Coupon> }>,

    detail: (id: number) =>
      axios.get(`/coupon/admin/${id}`) as Promise<{ code: number; msg: string; data: Coupon }>,

    create: (data: Partial<Coupon>) =>
      axios.post('/coupon/admin/create', data) as Promise<{ code: number; msg: string }>,

    update: (data: Partial<Coupon>) =>
      axios.put('/coupon/admin/update', data) as Promise<{ code: number; msg: string }>,

    delete: (id: number) =>
      axios.delete(`/coupon/admin/${id}`) as Promise<{ code: number; msg: string }>,

    toggleStatus: (id: number) =>
      axios.put(`/coupon/admin/toggle/${id}`) as Promise<{ code: number; msg: string }>,
  },
}
