<template>
  <div class="admin-page">
    <!-- 数据卡片 -->
    <div class="admin-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-input v-model="keyword" placeholder="搜索订单号/用户ID" clearable class="admin-search" @clear="fetchList" @keyup.enter="fetchList">
            <template #prefix><i class="i-mdi-magnify"></i></template>
          </el-input>
          <el-select v-model="statusFilter" placeholder="订单状态" clearable class="admin-search" @change="fetchList">
            <el-option label="待支付" :value="1" />
            <el-option label="已支付" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已退款" :value="4" />
          </el-select>
          <el-select v-model="orderTypeFilter" placeholder="订单类型" clearable class="admin-search" @change="fetchList">
            <el-option label="景点订单" :value="1" />
            <el-option label="酒店订单" :value="2" />
          </el-select>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="admin-table">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column type="index" label="序号" width="70" align="center" />
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="targetName" label="景点/酒店" min-width="150">
            <template #default="{ row }">
              <div class="font-medium">{{ row.targetName }}</div>
              <div class="text-sm text-gray-500">{{ row.orderType === 1 ? '景点' : '酒店' }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="totalAmount" label="金额" width="100">
            <template #default="{ row }">
              <span class="text-green-600 font-medium">¥{{ row.totalAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="payTime" label="支付时间" width="170">
            <template #default="{ row }">{{ row.payTime ? formatDate(row.payTime) : '-' }}</template>
          </el-table-column>
          <el-table-column prop="createdAt" label="下单时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="showDetail(row)">详情</el-button>
              <el-button v-if="row.status === 2" type="warning" text size="small" @click="handleRefund(row)">退款</el-button>
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
          :page-count="pageCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">{{ currentOrder.orderType === 1 ? '景点' : '酒店' }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item label="总金额"><span class="text-green-600 font-medium">¥{{ currentOrder.totalAmount }}</span></el-descriptions-item>
        <el-descriptions-item label="景点/酒店" :span="2">{{ currentOrder.targetName }}</el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">{{ formatDate(currentOrder.createdAt) }}</el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.payTime" label="支付时间" :span="2">{{ formatDate(currentOrder.payTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderList, refundOrder } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<number | ''>('')
const orderTypeFilter = ref<number | ''>('')
const tableData = ref<any[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const pageCount = computed(() => Math.ceil(pagination.total / pagination.pageSize) || 1)

const detailVisible = ref(false)
const currentOrder = ref<any>(null)

const statusMap: Record<number, { text: string; type: string }> = {
  1: { text: '待支付', type: 'warning' },
  2: { text: '已支付', type: 'success' },
  3: { text: '已取消', type: 'info' },
  4: { text: '已退款', type: 'danger' },
}

function getStatusType(status: number) { return statusMap[status]?.type || 'info' }
function getStatusText(status: number) { return statusMap[status]?.text || '未知' }
function formatDate(dateStr: string) { return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminOrderList({
      page: pagination.page, pageSize: pagination.pageSize,
      status: statusFilter.value || undefined,
      orderType: orderTypeFilter.value || undefined
    })
    tableData.value = res.data?.records || res.records || []
    pagination.total = res.data?.total || res.total || 0
  } catch (e) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

function showDetail(row: any) {
  currentOrder.value = row
  detailVisible.value = true
}

async function handleRefund(row: any) {
  try {
    await ElMessageBox.confirm('确定要退款该订单吗？退款将原路返回。', '退款确认', { type: 'warning', confirmButtonText: '确认退款' })
    await refundOrder(row.id)
    ElMessage.success('退款成功')
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('退款失败')
  }
}

onMounted(() => { fetchList() })
</script>

<style scoped lang="scss">
// 使用全局样式，无需额外定义
</style>
