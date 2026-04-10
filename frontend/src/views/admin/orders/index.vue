<template>
  <div class="admin-page">

    <!-- 数据卡片 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2 flex-wrap">
          <!-- 搜索 -->
          <div class="join">
            <input v-model="keyword" class="input input-bordered join-item w-64"
              placeholder="搜索订单号/用户ID" @keyup.enter="page = 1; fetchList()" />
            <button class="btn join-item" @click="page = 1; fetchList()">
              <MagnifyingGlassIcon class="w-4 h-4" />
            </button>
          </div>
          <!-- 状态筛选 -->
          <select v-model="statusFilter" class="select select-bordered w-28" @change="page = 1; fetchList()">
            <option value="">全部状态</option>
            <option :value="1">待支付</option>
            <option :value="2">已支付</option>
            <option :value="3">已取消</option>
            <option :value="4">已退款</option>
          </select>
          <!-- 类型筛选 -->
          <select v-model="orderTypeFilter" class="select select-bordered w-28" @change="page = 1; fetchList()">
            <option value="">全部类型</option>
            <option :value="1">景点订单</option>
            <option :value="2">酒店订单</option>
          </select>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center p-8">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- 数据表格 -->
      <div v-else class="overflow-x-auto">
        <table class="table table-zebra w-full text-sm">
          <thead>
            <tr>
              <th>#</th>
              <th>ID</th>
              <th>订单号</th>
              <th>用户ID</th>
              <th>景点/酒店</th>
              <th>数量</th>
              <th>金额</th>
              <th>状态</th>
              <th>支付时间</th>
              <th>下单时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in tableData" :key="row.id">
              <td>{{ idx + 1 }}</td>
              <td class="font-mono">{{ row.id }}</td>
              <td class="font-mono text-xs">{{ row.orderNo }}</td>
              <td>{{ row.userId }}</td>
              <td>
                <div class="font-medium">{{ row.targetName }}</div>
                <div class="text-xs opacity-60">{{ row.orderType === 1 ? '景点' : '酒店' }}</div>
              </td>
              <td>{{ row.quantity }}</td>
              <td class="text-green-600 font-medium">¥{{ row.totalAmount }}</td>
              <td>
                <span :class="getStatusBadgeClass(row.status)" class="badge badge-sm">
                  {{ getStatusText(row.status) }}
                </span>
              </td>
              <td>{{ row.payTime ? formatDate(row.payTime) : '-' }}</td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>
                <div class="flex gap-1">
                  <button class="btn btn-primary btn-xs" @click="showDetail(row)">详情</button>
                  <button v-if="row.status === 2" class="btn btn-warning btn-xs" @click="handleRefund(row)">退款</button>
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

    <!-- 订单详情 Modal -->
    <dialog id="order_detail_modal" class="modal">
      <div class="modal-box max-w-2xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">订单详情</h3>
        <div v-if="currentOrder" class="space-y-2 text-sm">
          <div class="grid grid-cols-2 gap-2">
            <div><span class="font-semibold">订单号：</span><span class="font-mono">{{ currentOrder.orderNo }}</span></div>
            <div><span class="font-semibold">用户ID：</span>{{ currentOrder.userId }}</div>
            <div><span class="font-semibold">订单类型：</span>{{ currentOrder.orderType === 1 ? '景点' : '酒店' }}</div>
            <div>
              <span class="font-semibold">状态：</span>
              <span :class="getStatusBadgeClass(currentOrder.status)" class="badge">{{ getStatusText(currentOrder.status) }}</span>
            </div>
            <div><span class="font-semibold">数量：</span>{{ currentOrder.quantity }}</div>
            <div><span class="font-semibold">总金额：</span><span class="text-green-600 font-bold">¥{{ currentOrder.totalAmount }}</span></div>
          </div>
          <div><span class="font-semibold">景点/酒店：</span>{{ currentOrder.targetName }}</div>
          <div><span class="font-semibold">下单时间：</span>{{ formatDate(currentOrder.createdAt) }}</div>
          <div v-if="currentOrder.payTime"><span class="font-semibold">支付时间：</span>{{ formatDate(currentOrder.payTime) }}</div>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAdminOrderList, refundOrder } from '@/api/admin'
import dayjs from 'dayjs'
import { MagnifyingGlassIcon } from '@heroicons/vue/24/outline'

const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<number | string>('')
const orderTypeFilter = ref<number | string>('')
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const detailVisible = ref(false)
const currentOrder = ref<any>(null)

const statusMap: Record<number, { text: string; cls: string }> = {
  1: { text: '待支付', cls: 'badge-warning' },
  2: { text: '已支付', cls: 'badge-success' },
  3: { text: '已取消', cls: 'badge-neutral' },
  4: { text: '已退款', cls: 'badge-error' },
}

function getStatusBadgeClass(status: number) { return statusMap[status]?.cls || 'badge-neutral' }
function getStatusText(status: number) { return statusMap[status]?.text || '未知' }
function formatDate(dateStr: string) { return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminOrderList({
      page: page.value, pageSize: pageSize.value,
      status: statusFilter.value !== '' ? Number(statusFilter.value) : undefined,
      orderType: orderTypeFilter.value !== '' ? Number(orderTypeFilter.value) : undefined,
    })
    tableData.value = res.data?.records || res.records || []
    total.value = res.data?.total || res.total || 0
  } catch (e) {
    console.error('获取列表失败', e)
  } finally {
    loading.value = false
  }
}

function showDetail(row: any) {
  currentOrder.value = row
  const modal = document.getElementById('order_detail_modal') as HTMLDialogElement
  modal?.showModal()
}

async function handleRefund(row: any) {
  if (!confirm('确定要退款该订单吗？退款将原路返回。')) return
  try {
    await refundOrder(row.id)
    row.status = 4
  } catch (e) {
    alert('退款失败')
  }
}

onMounted(() => { fetchList() })
</script>
