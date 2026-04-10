<template>
  <div class="admin-page">

    <!-- 数据卡片 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2 flex-wrap">
          <!-- 搜索 -->
          <div class="join">
            <input v-model="keyword" class="input input-bordered join-item w-60"
              placeholder="搜索景点名称" @keyup.enter="handleSearch" />
            <button class="btn join-item" @click="handleSearch">
              <MagnifyingGlassIcon class="w-4 h-4" />
            </button>
          </div>
          <!-- 城市筛选 -->
          <select v-model="cityFilter" class="select select-bordered w-32" @change="fetchList">
            <option value="">全部城市</option>
            <option>北京</option><option>上海</option><option>广州</option>
            <option>深圳</option><option>杭州</option><option>成都</option><option>西安</option>
          </select>
          <!-- 状态筛选 -->
          <select v-model="statusFilter" class="select select-bordered w-28" @change="fetchList">
            <option value="">全部状态</option>
            <option :value="1">上架</option>
            <option :value="0">下架</option>
          </select>
        </div>
        <button class="btn btn-primary" @click="openForm()">
          <PlusIcon class="w-4 h-4 mr-1" /> 新增景点
        </button>
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
              <th>ID</th>
              <th>封面</th>
              <th>景点名称</th>
              <th>门票价格</th>
              <th>星级</th>
              <th>开放时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td>
                <div class="avatar">
                  <div class="w-12 rounded">
                    <img v-if="row.coverImage" :src="row.coverImage" :alt="row.name" />
                    <div v-else class="bg-base-300 w-full h-full flex items-center justify-center text-xs">无</div>
                  </div>
                </div>
              </td>
              <td>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-xs opacity-60">{{ row.city }}</div>
              </td>
              <td class="text-green-600 font-medium">¥{{ row.ticketPrice }}</td>
              <td>
                <div class="rating rating-sm">
                  <input type="radio" class="mask mask-star-2 bg-orange-400" :checked="row.level >= 1" disabled />
                  <input type="radio" class="mask mask-star-2 bg-orange-400" :checked="row.level >= 2" disabled />
                  <input type="radio" class="mask mask-star-2 bg-orange-400" :checked="row.level >= 3" disabled />
                  <input type="radio" class="mask mask-star-2 bg-orange-400" :checked="row.level >= 4" disabled />
                  <input type="radio" class="mask mask-star-2 bg-orange-400" :checked="row.level >= 5" disabled />
                </div>
              </td>
              <td>{{ row.openTime || '-' }}</td>
              <td>
                <span :class="row.status === 1 ? 'badge-success' : 'badge-neutral'" class="badge badge-sm">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </span>
              </td>
              <td>
                <div class="flex gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="openForm(row)">编辑</button>
                  <button :class="row.status === 1 ? 'btn-warning' : 'btn-success'" class="btn btn-xs" @click="toggleStatus(row)">
                    {{ row.status === 1 ? '下架' : '上架' }}
                  </button>
                  <button class="btn btn-error btn-xs" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="8" class="text-center py-8 text-neutral/50">暂无数据</td>
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

    <!-- 新增/编辑 Modal -->
    <dialog id="spot_form_modal" class="modal">
      <div class="modal-box max-w-2xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">{{ isEdit ? '编辑景点' : '新增景点' }}</h3>

        <div class="space-y-3">
          <!-- 景点名称 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">景点名称 *</span></div>
            <input v-model="formData.name" class="input input-bordered w-full" placeholder="请输入景点名称" />
          </label>

          <!-- 封面图片 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">封面图片URL</span></div>
            <input v-model="formData.coverImage" class="input input-bordered w-full" placeholder="请输入封面图片URL" />
          </label>

          <!-- 城市 + 地址 -->
          <div class="grid grid-cols-2 gap-3">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">所在城市 *</span></div>
              <input v-model="formData.city" class="input input-bordered w-full" placeholder="城市" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">详细地址</span></div>
              <input v-model="formData.address" class="input input-bordered w-full" placeholder="地址" />
            </label>
          </div>

          <!-- 价格 + 游览时长 -->
          <div class="grid grid-cols-2 gap-3">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">门票价格 *</span></div>
              <input v-model.number="formData.ticketPrice" type="number" min="0" step="0.01"
                class="input input-bordered w-full" placeholder="0.00" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">建议游览（小时）</span></div>
              <input v-model.number="formData.suggestedTime" type="number" min="1" max="24"
                class="input input-bordered w-full" />
            </label>
          </div>

          <!-- 开放时间 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">开放时间</span></div>
            <input v-model="formData.openTime" class="input input-bordered w-full" placeholder="如：08:00-18:00" />
          </label>

          <!-- 描述 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">景点描述</span></div>
            <textarea v-model="formData.description" class="textarea textarea-bordered w-full" rows="3"
              placeholder="请输入景点描述"></textarea>
          </label>

          <!-- 标签 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">标签</span></div>
            <input v-model="formData.tags" class="input input-bordered w-full" placeholder="多个标签用逗号分隔" />
          </label>
        </div>

        <div class="modal-action">
          <form method="dialog">
            <button class="btn">取消</button>
          </form>
          <button class="btn btn-primary" :disabled="submitting" @click="handleSubmit">
            <span v-if="submitting" class="loading loading-spinner loading-xs"></span>
            {{ isEdit ? '更新' : '创建' }}
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getAdminSpotList, createSpot, updateSpot, deleteSpot, updateSpotStatus } from '@/api/admin'
import type { SpotDTO } from '@/api/admin'
import { MagnifyingGlassIcon, PlusIcon } from '@heroicons/vue/24/outline'

const loading = ref(false)
const keyword = ref('')
const cityFilter = ref('')
const statusFilter = ref<number | string>('')
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// 表单相关
const formVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)

const defaultForm: SpotDTO = {
  name: '', coverImage: '', city: '', address: '',
  longitude: 0, latitude: 0, description: '',
  ticketPrice: 0, openTime: '', suggestedTime: 1,
  level: '', tags: '', status: 1
}
const formData = reactive<SpotDTO>({ ...defaultForm })

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminSpotList({
      page: page.value, pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      city: cityFilter.value || undefined,
      status: statusFilter.value !== '' ? Number(statusFilter.value) : undefined,
    })
    tableData.value = res.data?.records || res.records || []
    total.value = res.data?.total || res.total || 0
  } catch (e) {
    console.error('获取列表失败', e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchList()
}

function openForm(row?: any) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    Object.assign(formData, { ...defaultForm })
  }
  const modal = document.getElementById('spot_form_modal') as HTMLDialogElement
  modal?.showModal()
}

async function handleSubmit() {
  if (!formData.name || !formData.city) {
    alert('请填写必填项')
    return
  }
  try {
    submitting.value = true
    if (isEdit.value) {
      await updateSpot(formData.id!, formData)
    } else {
      await createSpot(formData)
    }
    const modal = document.getElementById('spot_form_modal') as HTMLDialogElement
    modal?.close()
    fetchList()
  } catch (e) {
    alert('操作失败')
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  if (!confirm(`确定要${action}该景点吗？`)) return
  try {
    await updateSpotStatus(row.id, newStatus)
    row.status = newStatus
  } catch (e) {
    alert('操作失败')
  }
}

async function handleDelete(row: any) {
  if (!confirm(`确定要删除景点「${row.name}」吗？此操作不可恢复。`)) return
  try {
    await deleteSpot(row.id)
    fetchList()
  } catch (e) {
    alert('删除失败')
  }
}

onMounted(() => { fetchList() })
</script>
