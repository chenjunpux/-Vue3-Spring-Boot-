import { ref } from 'vue'
import { notificationApi } from '@/api/notification'

const unreadCount = ref(0)

/**
 * 全局通知状态管理
 */
export function useNotification() {
  async function loadUnreadCount() {
    try {
      const res = await notificationApi.unreadCount()
      unreadCount.value = res.data || 0
    } catch {
      unreadCount.value = 0
    }
  }

  async function clearUnread() {
    unreadCount.value = 0
  }

  return {
    unreadCount,
    loadUnreadCount,
    clearUnread,
  }
}
