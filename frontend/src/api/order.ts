import request from './axios'

export interface Order {
  id: number
  orderNo: string
  userId: number
  orderType: number
  targetId: number
  targetName: string
  totalAmount: number
  payAmount: number
  status: number
  payTime?: string
  payChannel?: string
  contactName: string
  contactPhone: string
  quantity: number
  visitDate?: string
  createdAt: string
}

// 我的订单
export function getMyOrdersApi(params: { page: number; pageSize: number; status?: number }) {
  return request.get('/order/list', { params })
}

// 订单详情
export function getOrderDetailApi(id: number) {
  return request.get(`/order/${id}`)
}

// 创建景点订单
export function createSpotOrderApi(data: {
  spotId: number
  spotName: string
  totalAmount: number
  quantity: number
  visitDate?: string
  contactName?: string
  contactPhone?: string
}) {
  return request.post('/order/create/spot', data)
}

// 创建酒店订单
export function createHotelOrderApi(data: {
  hotelId: number
  hotelName: string
  totalAmount: number
  contactName?: string
  contactPhone?: string
}) {
  return request.post('/order/create/hotel', data)
}

// 模拟支付
export function payOrderApi(orderNo: string, payChannel = 'wechat') {
  return request.post(`/order/${orderNo}/pay`, null, { params: { payChannel } })
}

// 取消订单
export function cancelOrderApi(id: number) {
  return request.put(`/order/${id}/cancel`)
}
