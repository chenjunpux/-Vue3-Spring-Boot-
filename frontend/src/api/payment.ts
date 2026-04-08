import axios from './axios'

export interface PaymentData {
  paymentNo: string
  orderNo: string
  amount: number
  payChannel: string
  qrCode?: string
  payUrl?: string
  expireTime?: string
}

export const paymentApi = {
  create: (orderNo: string, payChannel = 'wechat') =>
    axios.post<any, any>('/payment/create', { orderNo, payChannel }),

  status: (orderNo: string) =>
    axios.get<any, any>(`/payment/status/${orderNo}`),

  callback: (paymentNo: string, transactionId?: string) =>
    axios.post<any, any>(`/payment/${paymentNo}/callback`, { transactionId }),
}
