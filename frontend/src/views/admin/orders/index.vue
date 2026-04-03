<template>
  <div class="admin-orders">
    <h2 class="page-title">📦 订单管理</h2>

    <div class="admin-filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="订单类型">
          <el-select v-model="filterForm.orderType" clearable style="width: 120px">
            <el-option label="景点订单" :value="1" />
            <el-option label="酒店订单" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" clearable style="width: 120px">
            <el-option label="待支付" :value="1" />
            <el-option label="已支付" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="已退款" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="filterForm.page = 1; fetchData()">搜索</el-button>
          <el-button @click="Object.assign(filterForm, { orderType: undefined, status: undefined, page: 1 }); fetchData()">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="admin-content-card admin-table">
      <el-table :data="tableData" v-loading="loading" size="small">
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="orderType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.orderType === 1 ? 'primary' : 'warning'" size="small">
              {{ row.orderType === 1 ? '景点' : '酒店' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="景点/酒店" min-width="150" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付" width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payChannel" label="支付渠道" width="100">
          <template #default="{ row }">{{ row.payChannel || '-' }}</template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="visitDate" label="日期" width="110">
          <template #default="{ row }">{{ row.visitDate || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="下单时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 2" size="small" type="danger" @click="handleRefund(row)">退款</el-button>
            <span v-else style="color: #999;">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background layout="total, prev, pager, next"
          :total="total" :current-page="filterForm.page" :page-size="filterForm.pageSize"
          @current-change="p => { filterForm.page = p; fetchData() }"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderList, refundOrder } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const filterForm = reactive({ page: 1, pageSize: 10, orderType: undefined as number | undefined, status: undefined as number | undefined })

const statusMap: Record<number, { text: string; type: string }> = {
  1: { text: '待支付', type: 'warning' },
  2: { text: '已支付', type: 'success' },
  3: { text: '已取消', type: 'info' },
  4: { text: '已退款', type: 'danger' },
}

function formatDate(str: string) {
  return str ? dayjs(str).format('YYYY-MM-DD HH:mm:ss') : '—'
}

async function fetchData() {
  try {
    loading.value = true
    const res: any = await getAdminOrderList(filterForm)
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

async function handleRefund(row: any) {
  await ElMessageBox.confirm(`确认退款订单 ${row.orderNo}？退款金额：¥${row.payAmount}`, '确认退款', { type: 'warning' })
  try {
    await refundOrder(row.id)
    ElMessage.success('退款成功')
    fetchData()
  } catch (e) { ElMessage.error('退款失败') }
}

fetchData()
</script>

<style scoped lang="scss">
.admin-orders {
  .page-title { font-size: 20px; font-weight: 700; color: #1e293b; margin-bottom: 20px; }
  .filter-card { margin-bottom: 16px; }
  .pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
}
</style>
