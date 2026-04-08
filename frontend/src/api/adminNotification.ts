import axios from './axios'

export interface AdminNotification {
  id?: number
  title: string
  content: string
  type: number       // 1系统通知 2订单通知 3活动通知
  targetType: number // 1全部用户 2指定用户
  targetUserIds?: string
  status: number     // 0草稿 1已发布
  publishedAt?: string
  createdAt?: string
}

export const adminNotificationApi = {
  /** 分页查询通知列表 */
  list: (params?: { page?: number; pageSize?: number; type?: number; status?: number }) =>
    axios.get('/admin/notification/list', { params }) as Promise<{
      code: number
      msg: string
      data: { records: AdminNotification[]; total: number; size: number; current: number }
    }>,

  /** 获取通知详情 */
  detail: (id: number) =>
    axios.get(`/admin/notification/${id}`) as Promise<{ code: number; data: AdminNotification }>,

  /** 保存/更新通知 */
  save: (data: Partial<AdminNotification>) =>
    axios.post('/admin/notification/save', data) as Promise<{ code: number; msg: string }>,

  /** 发布通知 */
  publish: (id: number) =>
    axios.post(`/admin/notification/${id}/publish`) as Promise<{ code: number; msg: string }>,

  /** 保存草稿 */
  saveDraft: (data: Partial<AdminNotification>) =>
    axios.post('/admin/notification/draft', data) as Promise<{ code: number; msg: string }>,

  /** 删除通知 */
  delete: (id: number) =>
    axios.delete(`/admin/notification/${id}`) as Promise<{ code: number; msg: string }>,
}
