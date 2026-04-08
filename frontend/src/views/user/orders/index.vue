<template>
  <div class="orders-page page-container">
    <div class="orders-header">
      <h2 class="page-title">📦 我的订单</h2>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="1" />
        <el-tab-pane label="已支付" name="2" />
        <el-tab-pane label="已完成" name="5" />
        <el-tab-pane label="已取消" name="3" />
      </el-tabs>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list" v-loading="loading">
      <div v-if="orders.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无订单" />
        <el-button type="primary" @click="$router.push('/spots')">去预订</el-button>
      </div>

      <div v-else class="order-cards">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </div>

          <div class="order-body">
            <div class="order-info">
              <p class="order-type">{{ order.orderType === 1 ? '🎫 景点门票' : '🏨 酒店预订' }}</p>
              <p class="order-name">{{ order.targetName }}</p>
              <p class="order-meta">
                <span v-if="order.quantity">数量: {{ order.quantity }}份</span>
                <span v-if="order.visitDate">游玩日期: {{ order.visitDate }}</span>
                <span>下单时间: {{ formatDate(order.createdAt) }}</span>
              </p>
            </div>
            <div class="order-price">
              <span class="price-label">应付</span>
              <span class="price-value">¥{{ order.payAmount?.toFixed(2) }}</span>
            </div>
          </div>

          <div class="order-actions">
            <!-- 待支付订单 -->
            <template v-if="order.status === 1">
              <el-button type="primary" size="small" @click="goToPay(order.orderNo)">
                立即支付
              </el-button>
              <el-button size="small" @click="handleCancel(order)">取消订单</el-button>
            </template>

            <!-- 已支付订单 -->
            <template v-else-if="order.status === 2">
              <el-tag type="success" size="small">支付成功</el-tag>
              <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
            </template>

            <!-- 已完成订单 -->
            <template v-else-if="order.status === 5">
              <el-tag type="info" size="small">已完成</el-tag>
              <el-button size="small" @click="viewOrderDetail(order)">查看详情</el-button>
            </template>

            <!-- 已取消/已退款 -->
            <template v-else-if="order.status === 3 || order.status === 4">
              <el-tag size="small">{{ order.status === 4 ? '已退款' : '已取消' }}</el-tag>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOrdersApi, cancelOrderApi } from '@/api/order'

const router = useRouter()
const loading = ref(false)
const orders = ref<any[]>([])
const activeTab = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusMap: Record<number, string> = {
  1: '待支付',
  2: '已支付',
  3: '已取消',
  4: '已退款',
  5: '已完成'
}

const getStatusText = (status: number) => statusMap[status] || '未知'

const getStatusClass = (status: number) => {
  const classMap: Record<number, string> = {
    1: 'status-pending',
    2: 'status-paid',
    3: 'status-cancelled',
    4: 'status-refunded',
    5: 'status-completed'
  }
  return classMap[status] || ''
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleTabChange = () => {
  currentPage.value = 1
  fetchOrders()
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value
    }
    if (activeTab.value !== 'all') {
      params.status = parseInt(activeTab.value)
    }

    const res = await getMyOrdersApi(params)
    if (res.code === 200) {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取订单失败:', error)
  } finally {
    loading.value = false
  }
}

const goToPay = (orderNo: string) => {
  router.push(`/pay/${orderNo}`)
}

const viewOrderDetail = (order: any) => {
  ElMessage.info(`订单详情: ${order.orderNo}`)
}

const handleCancel = async (order: any) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await cancelOrderApi(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.orders-header {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.page-title {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 8px;
}

.order-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  transition: box-shadow 0.3s;
}

.order-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.order-no {
  font-size: 13px;
  color: #666;
}

.order-status {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 4px;
}

.status-pending {
  background: #fff7e6;
  color: #fa8c16;
}

.status-paid {
  background: #e6f7e6;
  color: #52c41a;
}

.status-cancelled,
.status-refunded {
  background: #f5f5f5;
  color: #999;
}

.status-completed {
  background: #e6f7ff;
  color: #1890ff;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.order-info {
  flex: 1;
}

.order-type {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.order-name {
  font-size: 15px;
  color: #666;
  margin-bottom: 8px;
}

.order-meta {
  font-size: 12px;
  color: #999;
}

.order-meta span {
  margin-right: 15px;
}

.order-price {
  text-align: right;
}

.price-label {
  font-size: 12px;
  color: #999;
  display: block;
}

.price-value {
  font-size: 20px;
  font-weight: 600;
  color: #ff4d4f;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
