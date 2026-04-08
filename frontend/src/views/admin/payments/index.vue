<template>
  <div class="admin-page">
    <!-- 统计卡片 -->
    <div class="admin-stats">
      <div class="admin-stat">
        <div class="admin-stat-icon" style="background: rgba(59, 130, 246, 0.1); color: #3b82f6;">
          <i class="i-mdi-currency-cny"></i>
        </div>
        <div class="admin-stat-content">
          <div class="admin-stat-value">¥{{ formatAmount(stats.totalIncome) }}</div>
          <div class="admin-stat-label">总收入</div>
        </div>
      </div>
      <div class="admin-stat">
        <div class="admin-stat-icon" style="background: rgba(16, 185, 129, 0.1); color: #10b981;">
          <i class="i-mdi-check-circle"></i>
        </div>
        <div class="admin-stat-content">
          <div class="admin-stat-value">{{ stats.successCount || 0 }}</div>
          <div class="admin-stat-label">成功交易</div>
        </div>
      </div>
      <div class="admin-stat">
        <div class="admin-stat-icon" style="background: rgba(239, 68, 68, 0.1); color: #ef4444;">
          <i class="i-mdi-minus-circle"></i>
        </div>
        <div class="admin-stat-content">
          <div class="admin-stat-value">{{ stats.refundCount || 0 }}</div>
          <div class="admin-stat-label">退款笔数</div>
        </div>
      </div>
      <div class="admin-stat">
        <div class="admin-stat-icon" style="background: rgba(245, 158, 11, 0.1); color: #f59e0b;">
          <i class="i-mdi-clock-outline"></i>
        </div>
        <div class="admin-stat-content">
          <div class="admin-stat-value">¥{{ formatAmount(stats.refundAmount) }}</div>
          <div class="admin-stat-label">退款金额</div>
        </div>
      </div>
    </div>

    <!-- 数据卡片 -->
    <div class="admin-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-input v-model="keyword" placeholder="搜索用户/订单号" clearable class="admin-search" @clear="handleSearch" @keyup.enter="handleSearch">
            <template #prefix><i class="i-mdi-magnify"></i></template>
          </el-input>
          <el-select v-model="typeFilter" placeholder="订单类型" clearable class="admin-search" @change="fetchList">
            <el-option label="全部" :value="0" />
            <el-option label="景点门票" :value="1" />
            <el-option label="酒店预订" :value="2" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="交易状态" clearable class="admin-search" @change="fetchList">
            <el-option label="全部" :value="0" />
            <el-option label="待支付" :value="1" />
            <el-option label="已支付" :value="2" />
            <el-option label="已退款" :value="3" />
          </el-select>
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" class="admin-search" @change="fetchList" />
        </div>
        <div class="admin-header-right">
          <el-button @click="refreshData">
            <i class="i-mdi-refresh mr-1"></i> 刷新
          </el-button>
          <el-button type="primary" @click="exportData">
            <i class="i-mdi-download mr-1"></i> 导出
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="admin-table">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="paymentNo" label="支付单号" width="200" show-overflow-tooltip />
          <el-table-column prop="orderNo" label="关联订单" width="180" show-overflow-tooltip />
          <el-table-column label="用户信息" width="200">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <el-avatar :size="32" :src="row.userAvatar">{{ (row.userName || 'U').slice(0, 1) }}</el-avatar>
                <div>
                  <div class="font-medium text-sm">{{ row.userName || '-' }}</div>
                  <div class="text-xs text-gray-400">ID: {{ row.userId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="orderType" label="订单类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.orderType === 1 ? 'primary' : 'warning'" size="small">
                {{ row.orderType === 1 ? '景点门票' : '酒店预订' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="targetName" label="消费内容" min-width="150" show-overflow-tooltip />
          <el-table-column prop="amount" label="支付金额" width="120">
            <template #default="{ row }">
              <span class="text-green-600 font-bold">¥{{ formatAmount(row.amount) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="payChannel" label="支付方式" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.payChannel" size="small">{{ getPayChannelText(row.payChannel) }}</el-tag>
              <span v-else class="text-gray-400">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="payStatus" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.payStatus)" size="small">
                {{ getStatusText(row.payStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="showDetail(row)">详情</el-button>
              <el-button v-if="row.payStatus === 1" type="warning" text size="small" @click="handleRefund(row)">退款</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="admin-pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </div>

    <!-- 支付详情弹窗 -->
    <el-dialog v-model="detailVisible" title="支付详情" width="600px">
      <el-descriptions v-if="currentPayment" :column="2" border>
        <el-descriptions-item label="支付单号" :span="2">{{ currentPayment.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号" :span="2">{{ currentPayment.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentPayment.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ currentPayment.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">
          <el-tag :type="currentPayment.orderType === 1 ? 'primary' : 'warning'" size="small">
            {{ currentPayment.orderType === 1 ? '景点门票' : '酒店预订' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="getStatusType(currentPayment.payStatus)">{{ getStatusText(currentPayment.payStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消费内容" :span="2">{{ currentPayment.targetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">
          <span class="text-green-600 font-bold">¥{{ formatAmount(currentPayment.amount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ getPayChannelText(currentPayment.payChannel) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentPayment.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentPayment.payTime ? formatDate(currentPayment.payTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="currentPayment.transactionId" label="交易流水号" :span="2">{{ currentPayment.transactionId }}</el-descriptions-item>
        <el-descriptions-item v-if="currentPayment.refundTime" label="退款时间" :span="2">{{ formatDate(currentPayment.refundTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPaymentList, refundPayment, type PaymentVO, type PaymentStats } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const keyword = ref('')
const typeFilter = ref<number>(0)
const statusFilter = ref<number>(0)
const dateRange = ref<string[]>([])
const tableData = ref<PaymentVO[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const stats = reactive<PaymentStats>({
  totalIncome: 0,
  refundAmount: 0,
  successCount: 0,
  refundCount: 0
})

const detailVisible = ref(false)
const currentPayment = ref<PaymentVO | null>(null)

// 格式化金额（分转元）
function formatAmount(amount: number) {
  if (!amount) return '0.00'
  return (amount).toFixed(2)
}

// 支付方式映射
function getPayChannelText(channel: string) {
  const map: Record<string, string> = {
    wechat: '微信支付',
    alipay: '支付宝',
    bank: '银行卡',
    balance: '余额',
  }
  return map[channel?.toLowerCase()] || channel || '-'
}

// 状态映射
function getStatusType(status: number) {
  const map: Record<number, string> = {
    0: 'info',      // 待支付
    1: 'success',   // 已支付
    2: 'warning',   // 已取消
    3: 'danger',    // 已退款
  }
  return map[status] || 'info'
}

function getStatusText(status: number) {
  const map: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款',
  }
  return map[status] || '未知'
}

function formatDate(dateStr: string) {
  return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss') : '-'
}

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getPaymentList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: statusFilter.value || undefined,
      orderType: typeFilter.value || undefined,
      keyword: keyword.value || undefined,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })

    const data = res.data || res
    tableData.value = data.records || []
    pagination.total = data.total || 0

    // 更新统计数据
    if (data.stats) {
      stats.totalIncome = data.stats.totalIncome || 0
      stats.refundAmount = data.stats.refundAmount || 0
      stats.successCount = data.stats.successCount || 0
      stats.refundCount = data.stats.refundCount || 0
    }
  } catch (e) {
    console.error('获取支付列表失败', e)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchList()
}

function refreshData() {
  fetchList()
  ElMessage.success('已刷新')
}

function showDetail(row: PaymentVO) {
  currentPayment.value = row
  detailVisible.value = true
}

async function handleRefund(row: PaymentVO) {
  try {
    await ElMessageBox.confirm(
      `确定要为用户【${row.userName || 'ID:' + row.userId}】退款 ¥${formatAmount(row.amount)} 吗？`,
      '退款确认',
      { type: 'warning', confirmButtonText: '确认退款' }
    )
    await refundPayment(row.id)
    ElMessage.success('退款成功')
    detailVisible.value = false
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error('退款失败')
    }
  }
}

async function exportData() {
  try {
    ElMessage.warning('正在生成导出文件，请稍候...')
    
    // 构建查询参数
    const params = new URLSearchParams()
    if (statusFilter.value) params.append('status', String(statusFilter.value))
    if (typeFilter.value) params.append('orderType', String(typeFilter.value))
    if (keyword.value) params.append('keyword', keyword.value)
    if (dateRange.value?.[0]) params.append('startDate', dateRange.value[0])
    if (dateRange.value?.[1]) params.append('endDate', dateRange.value[1])
    
    // 获取 token
    const token = localStorage.getItem('token') || localStorage.getItem('access_token')
    
    // 调用后端导出接口
    const response = await fetch(`/api/v1/admin/payment/export?${params.toString()}`, {
      method: 'GET',
      headers: {
        'Authorization': token ? `Bearer ${token}` : ''
      }
    })
    
    if (!response.ok) {
      throw new Error('导出失败')
    }
    
    // 获取文件名
    const contentDisposition = response.headers.get('Content-Disposition')
    let fileName = `支付记录_${new Date().toISOString().slice(0, 10).replace(/-/g, '')}.xlsx`
    if (contentDisposition) {
      const match = contentDisposition.match(/filename\*=?['"]?([^;'\n]+)/)
      if (match) {
        fileName = decodeURIComponent(match[1])
      }
    }
    
    // 下载文件
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (e: any) {
    console.error('导出失败', e)
    ElMessage.error(e.message || '导出失败，请重试')
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
</style>
