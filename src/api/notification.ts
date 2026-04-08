import axios from './axios'

export interface Notification {
  id: number
  userId: number
  type: string
  title: string
  content: string
  relatedId?: number
  isRead: number
  readTime?: string
  createdAt: string
}

export const notificationApi = {
  list: (params?: { page?: number; pageSize?: number; isRead?: number }) =>
    axios.get<any, any>('/notification/list', { params }),

  unreadCount: () =>
    axios.get<any, any>('/notification/unread/count'),

  markAsRead: (id: number) =>
    axios.put<any, any>(`/notification/${id}/read`),

  markAllAsRead: () =>
    axios.put<any, any>('/notification/read/all'),

  delete: (id: number) =>
    axios.delete<any, any>(`/notification/${id}`),
}
