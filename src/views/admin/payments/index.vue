<template>
  <div class="admin-page">

    <!-- 统计卡片 -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
      <div class="stats shadow border border-base-300 w-full">
        <div class="stat">
          <div class="stat-figure text-primary"><i class="i-mdi-currency-cny text-3xl"></i></div>
          <div class="stat-title">总收入</div>
          <div class="stat-value text-primary">¥{{ formatAmount(stats.totalIncome) }}</div>
          <div class="stat-desc">成功交易 {{ stats.successCount || 0 }} 笔</div>
        </div>
      </div>
      <div class="stats shadow border border-base-300 w-full">
        <div class="stat">
          <div class="stat-figure text-success"><i class="i-mdi-check-circle text-3xl"></i></div>
          <div class="stat-title">成功交易</div>
          <div class="stat-value text-success">{{ stats.successCount || 0 }}</div>
          <div class="stat-desc">已支付订单</div>
        </div>
      </div>
      <div class="stats shadow border border-base-300 w-full">
        <div class="stat">
          <div class="stat-figure text-error"><i class="i-mdi-minus-circle text-3xl"></i></div>
          <div class="stat-title">退款笔数</div>
          <div class="stat-value text-error">{{ stats.refundCount || 0 }}</div>. 
          <div class="stat-desc">已退款订单</div>
        </div>
      </div>
      <div class="stats shadow border border-base-300 w-full">
        <div class="stat">
          <div class="stat-figure text-warning"><i class="i-mdi-clock-outline text-3xl"></i></div>
          <div class="stat-title">退款金额</div>
          <div class="stat-value text-warning">¥{{ formatAmount(stats.refundAmount) }}</div>
          <div class="stat-desc">已退款总额</div>
        </div>
      </div>
    </div>

    <!-- 数据卡片 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2 flex-wrap">
          <div class="join">
            <input v-model="keyword" class="input input-bordered join-item w-52"
              placeholder="搜索用户/订单号" @keyup.enter="handleSearch" />
            <button class="btn join-item" @click="handleSearch"><MagnifyingGlassIcon class="w-4 h-4" /></button>
          </div>
          <select v-model="typeFilter" class="select select-bordered w-28" @change="page = 1; fetchList()">
            <option :value="0">全部类型</option>
            <option :value="1">景点门票</option>
            <option :value="2">酒店预订</option>
          </select>
          <select v-model="statusFilter" class="select select-bordered w-28" @change="page = 1; fetchList()">
            <option :value="0">全部状态</option>
            <option :value="1">待支付</option>
            <option :value="2">已支付</option>
            <option :value="3">已退款</option>
          </select>
          <!-- 日期范围 -->
          <input v-model="startDate" type="date" class="input input-bordered w-36" placeholder="开始日期"
            @change="page = 1; fetchList()" />
          <span class="opacity-50">至</span>
          <input v-model="endDate" type="date" class="input input-bordered w-36" placeholder="结束日期"
            @change="page = 1; fetchList()" />
        </div>
        <div class="flex gap-2">
          <button class="btn btn-outline btn-sm" @click="refreshData">刷新</button>
          <button class="btn btn-primary btn-sm" @click="exportData">导出</button>
        </div>
      </div>

      <!-- 加载 -->
      <div v-if="loading" class="flex items-center justify-center p-8">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- 表格 -->
      <div v-else class="overflow-x-auto">
        <table class="table table-zebra w-full text-sm">
          <thead>
            <tr>
              <th>ID</th><th>支付单号</th><th>关联订单</th><th>用户信息</th><th>类型</th>
              <th>消费内容</th><th>金额</th><th>支付方式</th><th>状态</th><th>时间</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td class="font-mono text-xs max-w-32 truncate">{{ row.paymentNo }}</td>
              <td class="font-mono text-xs max-w-32 truncate">{{ row.orderNo }}</td>
              <td>
                <div class="flex items-center gap-2">
                  <div class="avatar"><div class="w-8 rounded-full bg-primary text-primary-content text-xs">
                      {{ (row.userName || 'U').slice(0, 1) }}
                    </div></div>
                  <div>
                    <div class="text-sm font-medium">{{ row.userName || '-' }}</div>
                    <div class="text-xs opacity-60">ID: {{ row.userId }}</div>
                  </div>
                </div>
              </td>
              <td>
                <span :class="row.orderType === 1 ? 'badge-primary' : 'badge-warning'" class="badge badge-sm">
                  {{ row.orderType === 1 ? '景点' : '酒店' }}
                </span>
              </td>
              <td class="max-w-xs truncate">{{ row.targetName || '-' }}</td>
              <td class="text-green-600 font-bold">¥{{ formatAmount(row.amount) }}</td>
              <td>
                <span class="badge badge-outline badge-sm">{{ getPayChannelText(row.payChannel) }}</span>
              </td>
              <td>
                <span :class="getStatusBadgeClass(row.payStatus)" class="badge badge-sm">
                  {{ getStatusText(row.payStatus) }}
                </span>
              </td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>
                <div class="flex gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="showDetail(row)">详情</button>
                  <button v-if="row.payStatus === 2" class="btn btn-warning btn-xs" @click="handleRefund(row)">退款</button>
                </div>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="11" class="text-center py-8 text-neutral/50">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="flex items-center justify-between p-4 border-t border-base-300 flex-wrap gap-2">
        <span class="text-sm text-neutral/60">共 {{ total }} 条</span>
        <div class="join">
          <button class="join-item btn btn-sm" :disabled="page <= 1" @click="page--; fetchList()">«</button>
          <button class="join-item btn btn-sm disabled">第 {{ page }} / {{ totalPages || 1 }} 页</button>
          <button class="join-item btn btn-sm" :disabled="page >= (totalPages || 1)" @click="page++; fetchList()">»</button>
        </div>
      </div>
    </div>

    <!-- 详情 Modal -->
    <dialog id="payment_detail_modal" class="modal">
      <div class="modal-box max-w-xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">支付详情</h3>
        <div v-if="currentPayment" class="space-y-2 text-sm">
          <div class="grid grid-cols-2 gap-x-4 gap-y-1">
            <div><span class="font-semibold">支付单号：</span><span class="font-mono text-xs">{{ currentPayment.paymentNo }}</span></div>
            <div><span class="font-semibold">订单号：</span><span class="font-mono text-xs">{{ currentPayment.orderNo }}</span></div>
            <div><span class="font-semibold">用户ID：</span>{{ currentPayment.userId }}</div>
            <div><span class="font-semibold">用户昵称：</span>{{ currentPayment.userName || '-' }}</div>
            <div>
              <span class="font-semibold">订单类型：</span>
              <span :class="currentPayment.orderType === 1 ? 'badge-primary' : 'badge-warning'" class="badge badge-sm">
                {{ currentPayment.orderType === 1 ? '景点门票' : '酒店预订' }}
              </span>
            </div>
            <div>
              <span class="font-semibold">支付状态：</span>
              <span :class="getStatusBadgeClass(currentPayment.payStatus)" class="badge badge-sm">
                {{ getStatusText(currentPayment.payStatus) }}
              </span>
            </div>
            <div class="col-span-2"><span class="font-semibold">消费内容：</span>{{ currentPayment.targetName || '-' }}</div>
            <div class="text-green-600 font-bold text-base"><span class="font-semibold">支付金额：</span>¥{{ formatAmount(currentPayment.amount) }}</div>
            <div><span class="font-semibold">支付方式：</span>{{ getPayChannelText(currentPayment.payChannel) }}</div>
            <div><span class="font-semibold">创建时间：</span>{{ formatDate(currentPayment.createdAt) }}</div>
            <div><span class="font-semibold">支付时间：</span>{{ currentPayment.payTime ? formatDate(currentPayment.payTime) : '-' }}</div>
            <div v-if="currentPayment.transactionId" class="col-span-2"><span class="font-semibold">交易流水号：</span><span class="font-mono text-xs">{{ currentPayment.transactionId }}</span></div>
            <div v-if="currentPayment.refundTime" class="col-span-2"><span class="font-semibold">退款时间：</span>{{ formatDate(currentPayment.refundTime) }}</div>
          </div>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getPaymentList, refundPayment, type PaymentVO, type PaymentStats } from '@/api/admin'
import dayjs from 'dayjs'
import { MagnifyingGlassIcon } from '@heroicons/vue/24/outline'

const loading = ref(false)
const keyword = ref('')
const typeFilter = ref(0)
const statusFilter = ref(0)
const startDate = ref('')
const endDate = ref('')
const tableData = ref<PaymentVO[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)
const stats = reactive<PaymentStats>({ totalIncome: 0, refundAmount: 0, successCount: 0, refundCount: 0 })

const currentPayment = ref<PaymentVO | null>(null)

function formatAmount(amount: number) { return amount ? amount.toFixed(2) : '0.00' }

function getPayChannelText(channel: any) {
  const map: Record<string, string> = { wechat: '微信', alipay: '支付宝', bank: '银行卡', balance: '余额' }
  return map[channel?.toLowerCase()] || channel || '-'
}

const statusMap: Record<number, { text: string; cls: string }> = {
  0: { text: '待支付', cls: 'badge-warning' },
  1: { text: '已支付', cls: 'badge-success' },
  2: { text: '已取消', cls: 'badge-neutral' },
  3: { text: '已退款', cls: 'badge-error' },
}
function getStatusBadgeClass(s: number) { return statusMap[s]?.cls || 'badge-neutral' }
function getStatusText(s: number) { return statusMap[s]?.text || '未知' }
function formatDate(d: string) { return d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getPaymentList({
      page: page.value, pageSize: pageSize.value,
      status: statusFilter.value || undefined,
      orderType: typeFilter.value || undefined,
      keyword: keyword.value || undefined,
      startDate: startDate.value || undefined,
      endDate: endDate.value || undefined,
    })
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
    if (data.stats) Object.assign(stats, data.stats)
  } catch (e) { console.error(e) } finally { loading.value = false }
}

function handleSearch() { page.value = 1; fetchList() }
function refreshData() { fetchList() }

function showDetail(row: PaymentVO) {
  currentPayment.value = row
  const modal = document.getElementById('payment_detail_modal') as HTMLDialogElement
  modal?.showModal()
}

async function handleRefund(row: PaymentVO) {
  if (!confirm(`确定要为用户【${row.userName || 'ID:' + row.userId}】退款 ¥${formatAmount(row.amount)} 吗？`)) return
  try {
    await refundPayment(row.id)
    row.payStatus = 3
  } catch { window.adminToast('退款失败', 'error') }
}

async function exportData() {
  try {
    const params = new URLSearchParams()
    if (statusFilter.value) params.append('status', String(statusFilter.value))
    if (typeFilter.value) params.append('orderType', String(typeFilter.value))
    if (keyword.value) params.append('keyword', keyword.value)
    if (startDate.value) params.append('startDate', startDate.value)
    if (endDate.value) params.append('endDate', endDate.value)
    const token = localStorage.getItem('token') || localStorage.getItem('access_token')
    const response = await fetch(`/api/v1/admin/payment/export?${params.toString()}`, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    })
    if (!response.ok) throw new Error('导出失败')
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `支付记录_${new Date().toISOString().slice(0, 10)}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch { window.adminToast('导出失败', 'error') }
}

onMounted(() => { fetchList() })
</script>
