<template>
  <div class="notifications-page page-container">
    <div class="header-row">
      <h2 class="page-title">🔔 我的消息</h2>
      <div class="header-actions">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-button text @click="markAllRead">全部已读</el-button>
        </el-badge>
      </div>
    </div>

    <div v-if="loading" class="loading-wrap">
      <el-icon class="is-loading spin"><Loading /></el-icon>
    </div>

    <div v-else-if="notifications.length === 0" class="empty-wrap">
      <el-empty description="暂无通知消息" />
    </div>

    <div v-else class="notification-list">
      <div
        v-for="n in notifications"
        :key="n.id"
        class="notification-item"
        :class="{ unread: n.isRead === 0 }"
        @click="handleRead(n)"
      >
        <div class="notif-icon">{{ getTypeIcon(n.type) }}</div>
        <div class="notif-body">
          <div class="notif-header">
            <span class="notif-title">{{ n.title }}</span>
            <el-tag size="small" :type="getTypeTag(n.type)">{{ getTypeName(n.type) }}</el-tag>
          </div>
          <div class="notif-content">{{ n.content }}</div>
          <div class="notif-time">{{ formatTime(n.createdAt) }}</div>
        </div>
        <div class="notif-action">
          <el-icon @click.stop="deleteNotif(n.id)"><Delete /></el-icon>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadNotifications"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Delete } from '@element-plus/icons-vue'
import { notificationApi } from '@/api/notification'

const loading = ref(false)
const notifications = ref<any[]>([])
const unreadCount = ref(0)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const typeMap: Record<string, string> = {
  order_pay: '订单支付',
  order_cancel: '订单取消',
  refund: '退款通知',
  article_comment: '游记评论',
  article_like: '游记点赞',
  system: '系统通知',
}

const typeIconMap: Record<string, string> = {
  order_pay: '💰',
  order_cancel: '📦',
  refund: '💸',
  article_comment: '💬',
  article_like: '❤️',
  system: '🔔',
}

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})

async function loadNotifications() {
  loading.value = true
  try {
    const res = await notificationApi.list({ page: page.value, pageSize: pageSize.value })
    notifications.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    notifications.value = []
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  try {
    const res = await notificationApi.unreadCount()
    unreadCount.value = res.data || 0
  } catch { unreadCount.value = 0 }
}

async function handleRead(n: any) {
  if (n.isRead === 0) {
    await notificationApi.markAsRead(n.id)
    n.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

async function markAllRead() {
  try {
    await notificationApi.markAllAsRead()
    unreadCount.value = 0
    notifications.value.forEach(n => n.isRead = 1)
    ElMessage.success('已全部标记为已读')
  } catch { ElMessage.error('操作失败') }
}

async function deleteNotif(id: number) {
  await ElMessageBox.confirm('确定删除此通知？', '提示')
  try {
    await notificationApi.delete(id)
    notifications.value = notifications.value.filter(n => n.id !== id)
    ElMessage.success('删除成功')
  } catch { ElMessage.error('删除失败') }
}

function getTypeIcon(type: string) {
  return typeIconMap[type] || '📢'
}
function getTypeName(type: string) {
  return typeMap[type] || type
}
function getTypeTag(type: string) {
  if (type === 'order_pay') return 'success'
  if (type === 'order_cancel' || type === 'refund') return 'warning'
  if (type === 'article_like') return 'danger'
  return 'info'
}
function formatTime(time: string) {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.notifications-page { padding: 40px 20px; }
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
}
.page-title { font-size: 28px; margin: 0; }
.loading-wrap, .empty-wrap { padding: 60px; text-align: center; }
.spin { font-size: 32px; color: #667eea; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.notification-list { display: flex; flex-direction: column; gap: 12px; }
.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.2s;
}
.notification-item:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.notification-item.unread {
  background: linear-gradient(135deg, #f0eeff 0%, #fff 100%);
  border-left: 4px solid #667eea;
}
.notif-icon { font-size: 28px; margin-top: 4px; }
.notif-body { flex: 1; }
.notif-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.notif-title { font-size: 16px; font-weight: bold; color: #333; }
.notif-content { font-size: 14px; color: #666; margin-bottom: 6px; line-height: 1.6; }
.notif-time { font-size: 12px; color: #aaa; }
.notif-action { color: #ccc; padding-top: 4px; }
.notif-action:hover { color: #f56c6c; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
