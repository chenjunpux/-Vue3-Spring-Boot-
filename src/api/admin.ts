import request from './axios'
import type { Spot } from './spot'

// ==================== 景点管理 ====================
export interface SpotDTO {
  id?: number
  name: string
  coverImage: string
  city: string
  address: string
  longitude: number
  latitude: number
  description: string
  ticketPrice: number
  openTime: string
  suggestedTime: number
  level: string
  tags: string
  status: number
}

export function getAdminSpotList(params: {
  page: number
  pageSize: number
  city?: string
  keyword?: string
  status?: number
}) {
  return request.get<{ records: Spot[]; total: number }>('/spot/list', { params })
}

export function createSpot(data: SpotDTO) {
  return request.post('/spot', data)
}

export function updateSpot(id: number, data: SpotDTO) {
  return request.put(`/spot/${id}`, data)
}

export function deleteSpot(id: number) {
  return request.delete(`/spot/${id}`)
}

export function updateSpotStatus(id: number, status: number) {
  return request.put(`/spot/${id}/status`, { status })
}

// ==================== 酒店管理 ====================
export interface HotelDTO {
  id?: number
  name: string
  coverImage: string
  city: string
  address: string
  longitude: number
  latitude: number
  description: string
  starLevel: number
  facilities: string
  status: number
}

export interface RoomTypeDTO {
  id?: number
  hotelId: number
  name: string
  price: number
  bedType: string
  maxGuest: number
  totalRooms: number
  amenities: string
  images: string
}

export function getAdminHotelList(params: {
  page: number
  pageSize: number
  city?: string
  keyword?: string
  status?: number
}) {
  return request.get('/hotel/admin/list', { params })
}

export function createHotel(data: HotelDTO) {
  return request.post('/hotel', data)
}

export function updateHotel(id: number, data: HotelDTO) {
  return request.put(`/hotel/${id}`, data)
}

export function deleteHotel(id: number) {
  return request.delete(`/hotel/${id}`)
}

export function updateHotelStatus(id: number, status: number) {
  return request.put(`/hotel/${id}/status`, { status })
}

export function getHotelRooms(hotelId: number) {
  return request.get(`/hotel/${hotelId}/rooms`)
}

export function createRoomType(data: RoomTypeDTO) {
  return request.post('/hotel/rooms', data)
}

export function updateRoomType(id: number, data: RoomTypeDTO) {
  return request.put(`/hotel/rooms/${id}`, data)
}

export function deleteRoomType(id: number) {
  return request.delete(`/hotel/rooms/${id}`)
}

// ==================== 订单管理 ====================
export function getAdminOrderList(params: {
  page: number
  pageSize: number
  status?: number
  orderType?: number
}) {
  return request.get('/order/admin/list', { params })
}

export function refundOrder(id: number) {
  return request.put(`/order/${id}/refund`)
}

// ==================== 用户管理 ====================
export function getAdminUserList(params: {
  page: number
  pageSize: number
  keyword?: string
  status?: number
}) {
  return request.get('/user/admin/list', { params })
}

export function updateUserStatus(id: number, status: number) {
  return request.put(`/user/admin/${id}/status`, { status })
}

// ==================== 游记管理 ====================
export function getAdminArticleList(params: {
  page: number
  pageSize: number
  status?: number
}) {
  return request.get('/article/admin/list', { params })
}

export function updateArticleStatus(id: number, status: number) {
  return request.put(`/article/${id}/status`, { status })
}

export function toggleArticleTop(id: number) {
  return request.put(`/article/${id}/top`)
}

// ==================== 统计 ====================
export function getDashboardData() {
  return request.get('/statistics/dashboard')
}

export function getOrderTrend(days?: string) {
  return request.get('/statistics/orderTrend', { params: { days } })
}

export function getUserGrowth(days?: string) {
  return request.get('/statistics/userGrowth', { params: { days } })
}

export function getMonthlyStats(year?: string) {
  return request.get('/statistics/monthly', { params: { year } })
}

// ==================== 用户管理 CRUD ====================
export function createUser(data: any) {
  return request.post('/user/admin', data)
}

export function updateUser(id: number, data: any) {
  return request.put(`/user/admin/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete(`/user/admin/${id}`)
}

export function updateMenuPermissions(id: number, permissions: string) {
  return request.put(`/user/admin/${id}/menu-permissions`, { permissions })
}

export function resetUserPassword(id: number) {
  return request.put(`/user/admin/${id}/reset-password`)
}

// ==================== 支付管理 ====================
export interface PaymentVO {
  id: number
  paymentNo: string
  orderNo: string
  userId: number
  userName?: string
  userAvatar?: string
  orderType?: number
  targetName?: string
  amount: number
  payChannel?: string
  payStatus: number
  payTime?: string
  transactionId?: string
  refundAmount?: number
  refundTime?: string
  createdAt: string
}

export interface PaymentStats {
  totalIncome: number
  refundAmount: number
  successCount: number
  refundCount: number
}

export function getPaymentList(params: {
  page: number
  pageSize: number
  status?: number
  orderType?: number
  keyword?: string
  startDate?: string
  endDate?: string
}) {
  return request.get<{ records: PaymentVO[]; total: number; stats: PaymentStats }>('/admin/payment/list', { params })
}

export function getPaymentDetail(id: number) {
  return request.get<PaymentVO>(`/admin/payment/${id}`)
}

export function refundPayment(id: number, reason?: string) {
  return request.post(`/admin/payment/${id}/refund`, { reason })
}

// ==================== 通知管理 ====================
export interface NotificationDTO {
  id?: number
  title: string
  content: string
  type: number
  targetType: number
  status: number
}

export function getNotificationList(params: {
  page: number
  pageSize: number
  type?: number
}) {
  return request.get('/notification/list', { params })
}

export function createNotification(data: NotificationDTO) {
  return request.post('/notification', data)
}

export function updateNotification(id: number, data: NotificationDTO) {
  return request.put(`/notification/${id}`, data)
}

export function deleteNotification(id: number) {
  return request.delete(`/notification/${id}`)
}

export function sendNotification(id: number) {
  return request.put(`/notification/${id}/send`)
}

// ==================== 优惠券管理 ====================
export interface CouponDTO {
  id?: number
  name: string
  type: number
  value: number
  minAmount: number
  totalCount: number
  startTime: string
  endTime: string
  description: string
  status: number
}

export function getCouponList(params: {
  page: number
  pageSize: number
  status?: number
}) {
  return request.get('/coupon/list', { params })
}

export function createCoupon(data: CouponDTO) {
  return request.post('/coupon', data)
}

export function updateCoupon(id: number, data: CouponDTO) {
  return request.put(`/coupon/${id}`, data)
}

export function deleteCoupon(id: number) {
  return request.delete(`/coupon/${id}`)
}

export function grantCoupon(id: number, userIds: number[]) {
  return request.post(`/coupon/${id}/grant`, { userIds })
}

export function updateCouponStatus(id: number, status: number) {
  return request.put(`/coupon/${id}/status`, { status })
}
