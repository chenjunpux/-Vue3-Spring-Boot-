<template>
  <div class="admin-page">

    <!-- ========== 数据卡片 ========== -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2 flex-wrap">
          <select v-model="statusFilter" class="select select-bordered select-sm w-32"
            @change="page = 1; fetchList()">
            <option :value="-1">全部状态</option>
            <option :value="1">上架中</option>
            <option :value="0">已下架</option>
          </select>
          <input v-model="keyword" class="input input-bordered input-sm w-52"
            placeholder="搜索优惠券名称" @keyup.enter="page = 1; fetchList()" />
          <button class="btn btn-sm" @click="page = 1; fetchList()">
            <MagnifyingGlassIcon class="w-4 h-4" />
          </button>
        </div>
        <button class="btn btn-primary btn-sm" @click="openForm()">
          <PlusIcon class="w-4 h-4" />
          创建优惠券
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center p-12">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- 表格 -->
      <div v-else class="overflow-x-auto">
        <table class="table table-zebra w-full text-sm">
          <thead>
            <tr>
              <th>ID</th>
              <th>优惠券名称</th>
              <th>类型</th>
              <th>优惠内容</th>
              <th>门槛</th>
              <th>发放/剩余</th>
              <th>有效期</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td>
                <div class="font-medium max-w-36 truncate">{{ row.name }}</div>
              </td>
              <td>
                <span :class="row.type === 1 ? 'badge-error' : 'badge-warning'" class="badge badge-sm">
                  {{ row.type === 1 ? '满减券' : '折扣券' }}
                </span>
              </td>
              <td>
                <span class="font-bold" :class="row.type === 1 ? 'text-red-500' : 'text-orange-500'">
                  <template v-if="row.type === 1">¥{{ row.discountValue }}</template>
                  <template v-else>{{ (row.discountValue * 10).toFixed(1) }}折</template>
                </span>
                <div v-if="row.type === 2 && row.maxDiscount" class="text-xs text-neutral/50">
                  最高抵 ¥{{ row.maxDiscount }}
                </div>
              </td>
              <td>
                <span class="text-neutral/60 text-xs">
                  {{ row.minAmount > 0 ? `满¥${row.minAmount}` : '无门槛' }}
                </span>
              </td>
              <td>
                <span class="font-medium">{{ row.totalCount - row.remainCount }}</span>
                <span class="text-neutral/40"> / </span>
                <span>{{ row.totalCount }}</span>
              </td>
              <td>
                <div class="text-xs">
                  <div>{{ formatDate(row.validStart) }}</div>
                  <div class="text-neutral/40">至 {{ formatDate(row.validEnd) }}</div>
                </div>
              </td>
              <td>
                <span :class="getStatusBadge(row)" class="badge badge-sm">
                  {{ getStatusText(row) }}
                </span>
              </td>
              <td>
                <div class="flex gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="openForm(row)">编辑</button>
                  <button class="btn btn-xs" :class="row.status === 1 ? 'btn-warning' : 'btn-success'"
                    @click="toggleStatus(row)">
                    {{ row.status === 1 ? '下架' : '上架' }}
                  </button>
                  <button class="btn btn-error btn-xs" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>

            <tr v-if="tableData.length === 0">
              <td colspan="9" class="text-center py-12 text-neutral/50">
                <div class="flex flex-col items-center gap-2">
                  <TicketIcon class="w-10 h-10 opacity-40" />
                  <span>暂无优惠券</span>
                </div>
              </td>
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

    <!-- ========== 创建/编辑 Modal ========== -->
    <dialog id="coupon_form_modal" class="modal">
      <div class="modal-box max-w-xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-6">{{ isEdit ? '编辑优惠券' : '创建优惠券' }}</h3>

        <div class="space-y-4">
          <!-- 名称 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">优惠券名称 <span class="text-error">*</span></span></div>
            <input v-model="formData.name" class="input input-bordered w-full" placeholder="如：新人专享满减券" maxlength="50" />
          </label>

          <!-- 类型 -->
          <div class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">优惠券类型 <span class="text-error">*</span></span></div>
            <div class="flex gap-4">
              <label class="label cursor-pointer gap-2">
                <input v-model="formData.type" type="radio" :value="1" class="radio radio-sm" />
                <span class="label-text">满减券</span>
              </label>
              <label class="label cursor-pointer gap-2">
                <input v-model="formData.type" type="radio" :value="2" class="radio radio-sm" />
                <span class="label-text">折扣券</span>
              </label>
            </div>
          </div>

          <!-- 优惠面值 -->
          <div class="form-control w-full">
            <div class="label">
              <span class="label-text font-semibold">
                {{ formData.type === 1 ? '优惠金额' : '折扣比例' }} <span class="text-error">*</span>
              </span>
              <span class="label-text-alt">{{ formData.type === 1 ? '元' : '折' }}</span>
            </div>
            <input v-model.number="formData.discountValue" type="number" step="0.1" min="0"
              class="input input-bordered w-full" :placeholder="formData.type === 1 ? '如：10' : '如：0.9'" />
          </div>

          <!-- 使用门槛 -->
          <div class="form-control w-full">
            <div class="label">
              <span class="label-text font-semibold">使用门槛</span>
              <span class="label-text-alt">0 = 无门槛</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-neutral/50">满</span>
              <input v-model.number="formData.minAmount" type="number" min="0" step="1"
                class="input input-bordered w-32" placeholder="0" />
              <span class="text-neutral/50">元可用</span>
            </div>
          </div>

          <!-- 最高优惠（折扣券专用） -->
          <div v-if="formData.type === 2" class="form-control w-full">
            <div class="label">
              <span class="label-text font-semibold">最高优惠</span>
              <span class="label-text-alt">0 = 不限制</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-neutral/50">最高抵扣</span>
              <input v-model.number="formData.maxDiscount" type="number" min="0" step="1"
                class="input input-bordered w-32" placeholder="0" />
              <span class="text-neutral/50">元</span>
            </div>
          </div>

          <!-- 发放数量 & 每人限领 -->
          <div class="grid grid-cols-2 gap-4">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">发放数量 <span class="text-error">*</span></span></div>
              <input v-model.number="formData.totalCount" type="number" min="1" class="input input-bordered w-full" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">每人限领</span></div>
              <input v-model.number="formData.perUserLimit" type="number" min="1" class="input input-bordered w-full" />
            </label>
          </div>

          <!-- 有效期 -->
          <div class="grid grid-cols-2 gap-4">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">开始日期 <span class="text-error">*</span></span></div>
              <input v-model="formData.validStart" type="date" class="input input-bordered w-full" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">结束日期 <span class="text-error">*</span></span></div>
              <input v-model="formData.validEnd" type="date" class="input input-bordered w-full" />
            </label>
          </div>

          <!-- 适用范围 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">适用范围</span></div>
            <select v-model="formData.applicableType" class="select select-bordered w-full">
              <option :value="1">全场通用</option>
              <option :value="2">指定景点</option>
              <option :value="3">指定酒店</option>
            </select>
          </label>

          <!-- 使用说明 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">使用说明</span></div>
            <textarea v-model="formData.description" class="textarea textarea-bordered w-full" rows="2"
              placeholder="描述优惠券的使用条件或注意事项" maxlength="200"></textarea>
            <div class="label"><span class="label-text-alt text-neutral/40">{{ formData.description?.length || 0 }}/200</span></div>
          </label>

          <!-- 状态（仅编辑时显示） -->
          <div v-if="isEdit" class="flex items-center gap-3">
            <span class="font-semibold">状态：</span>
            <input v-model="formData.status" type="checkbox" class="toggle toggle-success toggle-sm"
              :true-value="1" :false-value="0" />
            <span class="text-sm" :class="formData.status === 1 ? 'text-success' : 'text-neutral/40'">
              {{ formData.status === 1 ? '上架中' : '已下架' }}
            </span>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="mt-6 pt-4 border-t border-base-300 flex justify-end gap-3">
          <form method="dialog"><button class="btn btn-ghost">取消</button></form>
          <button class="btn btn-primary" :class="{ 'btn-disabled': submitLoading }" @click="handleSubmit">
            <span v-if="submitLoading" class="loading loading-spinner loading-sm"></span>
            {{ submitLoading ? '保存中...' : (isEdit ? '保存' : '创建') }}
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { couponApi, type Coupon } from '@/api/coupon'
import dayjs from 'dayjs'
import { MagnifyingGlassIcon, PlusIcon, TicketIcon } from '@heroicons/vue/24/outline'

// ===== 状态 =====
const loading = ref(false)
const submitLoading = ref(false)
const keyword = ref('')
const statusFilter = ref(-1)
const tableData = ref<Coupon[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// ===== 表单 =====
const isEdit = ref(false)
const formData = reactive<{
  id?: number
  name: string
  type: number
  discountValue: number
  minAmount: number
  maxDiscount?: number
  totalCount: number
  perUserLimit: number
  applicableType: number
  validStart: string
  validEnd: string
  description: string
  status: number
}>({
  name: '',
  type: 1,
  discountValue: 10,
  minAmount: 0,
  maxDiscount: undefined,
  totalCount: 100,
  perUserLimit: 1,
  applicableType: 1,
  validStart: '',
  validEnd: '',
  description: '',
  status: 1,
})

// ===== 辅助方法 =====
function formatDate(d: string) {
  return d ? dayjs(d).format('YYYY-MM-DD') : '-'
}

function getStatusText(row: Coupon) {
  if (row.status === 0) return '已下架'
  const now = dayjs()
  if (now.isBefore(dayjs(row.validStart))) return '未开始'
  if (now.isAfter(dayjs(row.validEnd))) return '已过期'
  return '进行中'
}

function getStatusBadge(row: Coupon) {
  if (row.status === 0) return 'badge-neutral'
  const now = dayjs()
  if (now.isBefore(dayjs(row.validStart))) return 'badge-info'
  if (now.isAfter(dayjs(row.validEnd))) return 'badge-warning'
  return 'badge-success'
}

function getModal(): HTMLDialogElement {
  return document.getElementById('coupon_form_modal') as HTMLDialogElement
}

// ===== 获取列表 =====
async function fetchList() {
  try {
    loading.value = true
    const res: any = await couponApi.admin.list({
      page: page.value,
      pageSize: pageSize.value,
      status: statusFilter.value === -1 ? undefined : statusFilter.value,
    })
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// ===== 打开表单 =====
function openForm(row?: Coupon) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, {
      id: row.id,
      name: row.name,
      type: row.type,
      discountValue: row.discountValue,
      minAmount: row.minAmount || 0,
      maxDiscount: row.maxDiscount,
      totalCount: row.totalCount,
      perUserLimit: row.perUserLimit || 1,
      applicableType: row.applicableType || 1,
      validStart: row.validStart ? row.validStart.slice(0, 10) : '',
      validEnd: row.validEnd ? row.validEnd.slice(0, 10) : '',
      description: row.description || '',
      status: row.status,
    })
  } else {
    isEdit.value = false
    Object.assign(formData, {
      id: undefined,
      name: '',
      type: 1,
      discountValue: 10,
      minAmount: 0,
      maxDiscount: undefined,
      totalCount: 100,
      perUserLimit: 1,
      applicableType: 1,
      validStart: '',
      validEnd: '',
      description: '',
      status: 1,
    })
  }
  getModal()?.showModal()
}

// ===== 提交表单 =====
async function handleSubmit() {
  if (!formData.name.trim()) {
    alert('请输入优惠券名称')
    return
  }
  if (!formData.validStart || !formData.validEnd) {
    alert('请选择有效期')
    return
  }
  if (formData.discountValue <= 0) {
    alert('请输入有效的优惠面值')
    return
  }
  if (formData.type === 2 && formData.discountValue >= 1) {
    alert('折扣比例必须小于1，如 0.9 表示九折')
    return
  }

  try {
    submitLoading.value = true
    const payload = {
      id: formData.id,
      name: formData.name,
      type: formData.type,
      discountValue: formData.discountValue,
      minAmount: formData.minAmount,
      maxDiscount: formData.maxDiscount || undefined,
      totalCount: formData.totalCount,
      perUserLimit: formData.perUserLimit,
      applicableType: formData.applicableType,
      description: formData.description,
      status: formData.status,
      validStart: formData.validStart + ' 00:00:00',
      validEnd: formData.validEnd + ' 23:59:59',
    }
    if (isEdit.value) {
      await couponApi.admin.update(payload)
    } else {
      await couponApi.admin.create(payload)
    }
    getModal()?.close()
    fetchList()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

// ===== 切换状态 =====
async function toggleStatus(row: Coupon) {
  const action = row.status === 1 ? '下架' : '上架'
  if (!confirm(`确定要${action}优惠券「${row.name}」吗？`)) return
  try {
    await couponApi.admin.toggleStatus(row.id)
    row.status = row.status === 1 ? 0 : 1
  } catch {
    alert('操作失败')
  }
}

// ===== 删除 =====
async function handleDelete(row: Coupon) {
  if (!confirm(`确定要删除优惠券「${row.name}」吗？此操作不可恢复！`)) return
  try {
    await couponApi.admin.delete(row.id)
    fetchList()
  } catch {
    alert('删除失败')
  }
}

onMounted(() => {
  fetchList()
})
</script>
