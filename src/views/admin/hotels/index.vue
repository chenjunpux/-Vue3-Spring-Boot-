<template>
  <div class="admin-page">

    <!-- 数据卡片 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2 flex-wrap">
          <div class="join">
            <input v-model="keyword" class="input input-bordered join-item w-60" placeholder="搜索酒店名称"
              @keyup.enter="handleSearch" />
            <button class="btn join-item" @click="handleSearch"><MagnifyingGlassIcon class="w-4 h-4" /></button>
          </div>
          <select v-model="statusFilter" class="select select-bordered w-28" @change="fetchList">
            <option value="">全部状态</option>
            <option :value="1">上架</option>
            <option :value="0">下架</option>
          </select>
        </div>
        <button class="btn btn-primary" @click="openForm()">
          <PlusIcon class="w-4 h-4 mr-1" /> 新增酒店
        </button>
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
              <th>ID</th><th>序号</th><th>封面</th><th>酒店名称</th><th>星级</th>
              <th>地址</th><th>状态</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
              <td>
                <div class="avatar"><div class="w-12 rounded">
                    <img v-if="row.coverImage" :src="row.coverImage" :alt="row.name" />
                    <div v-else class="bg-base-300 w-full h-full flex items-center justify-center text-xs">无</div>
                </div></div>
              </td>
              <td>
                <div class="font-medium">{{ row.name }}</div>
                <div class="text-xs opacity-60">{{ row.city }}</div>
              </td>
              <td>
                <div class="rating rating-sm">
                  <input v-for="n in 5" :key="n" type="radio" class="mask mask-star-2 bg-orange-400"
                    :checked="n <= row.starLevel" disabled />
                </div>
              </td>
              <td class="max-w-xs truncate">{{ row.address || '-' }}</td>
              <td>
                <span :class="row.status === 1 ? 'badge-success' : 'badge-neutral'" class="badge badge-sm">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </span>
              </td>
              <td>
                <div class="flex gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="openRooms(row)">房型</button>
                  <button class="btn btn-primary btn-xs" @click="openForm(row)">编辑</button>
                  <button :class="row.status === 1 ? 'btn-warning' : 'btn-success'" class="btn btn-xs" @click="toggleStatus(row)">
                    {{ row.status === 1 ? '下架' : '上架' }}
                  </button>
                  <button class="btn btn-error btn-xs" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="7" class="text-center py-8 text-neutral/50">暂无数据</td>
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

    <!-- 酒店表单 Modal -->
    <dialog id="hotel_form_modal" class="modal">
      <div class="modal-box max-w-2xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">{{ isEdit ? '编辑酒店' : '新增酒店' }}</h3>
        <div class="space-y-3">
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">酒店名称 *</span></div>
            <input v-model="formData.name" class="input input-bordered w-full" placeholder="请输入酒店名称" />
          </label>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">封面图片URL</span></div>
            <input v-model="formData.coverImage" class="input input-bordered w-full" placeholder="图片URL" />
          </label>
          <div class="grid grid-cols-2 gap-3">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">所在城市 *</span></div>
              <input v-model="formData.city" class="input input-bordered w-full" placeholder="城市" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">酒店星级</span></div>
              <select v-model="formData.starLevel" class="select select-bordered">
                <option :value="1">一星级</option>
                <option :value="2">二星级</option>
                <option :value="3">三星级</option>
                <option :value="4">四星级</option>
                <option :value="5">五星级</option>
              </select>
            </label>
          </div>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">详细地址</span></div>
            <input v-model="formData.address" class="input input-bordered w-full" placeholder="详细地址" />
          </label>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">酒店设施</span></div>
            <textarea v-model="formData.facilities" class="textarea textarea-bordered w-full" rows="2"
              placeholder="如：WiFi,停车场,游泳池"></textarea>
          </label>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">酒店描述</span></div>
            <textarea v-model="formData.description" class="textarea textarea-bordered w-full" rows="3"
              placeholder="酒店描述"></textarea>
          </label>
        </div>
        <div class="modal-action">
          <form method="dialog"><button class="btn">取消</button></form>
          <button class="btn btn-primary" :disabled="submitting" @click="handleSubmit">
            <span v-if="submitting" class="loading loading-spinner loading-xs"></span>
            {{ isEdit ? '更新' : '创建' }}
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>

    <!-- 房型管理 Modal -->
    <dialog id="rooms_modal" class="modal">
      <div class="modal-box max-w-3xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">房型管理</h3>

        <div class="mb-3">
          <button class="btn btn-primary btn-sm" @click.prevent="openRoomForm()">
            <PlusIcon class="w-4 h-4 mr-1" /> 新增房型
          </button>
        </div>

        <div class="overflow-x-auto">
          <table class="table table-zebra w-full text-sm">
            <thead><tr><th>房型名称</th><th>床型</th><th>价格/晚</th><th>可住人数</th><th>房间数</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="r in roomsData" :key="r.id">
                <td>{{ r.name }}</td><td>{{ r.bedType }}</td>
                <td class="text-green-600 font-medium">¥{{ r.price }}</td>
                <td>{{ r.maxGuest }}人</td><td>{{ r.totalRooms }}</td>
                <td>
                  <div class="flex gap-1">
                    <button class="btn btn-primary btn-xs" @click.prevent="openRoomForm(r)">编辑</button>
                    <button class="btn btn-error btn-xs" @click.prevent="deleteRoom(r)">删除</button>
                  </div>
                </td>
              </tr>
              <tr v-if="roomsData.length === 0">
                <td colspan="6" class="text-center py-6 text-neutral/50">暂无房型</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>

    <!-- 房型表单 Modal -->
    <dialog id="room_form_modal" class="modal">
      <div class="modal-box">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">{{ isRoomEdit ? '编辑房型' : '新增房型' }}</h3>
        <div class="space-y-3">
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">房型名称 *</span></div>
            <input v-model="roomForm.name" class="input input-bordered w-full" placeholder="如：豪华大床房" />
          </label>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">床型 *</span></div>
            <input v-model="roomForm.bedType" class="input input-bordered w-full" placeholder="如：大床1.8m" />
          </label>
          <div class="grid grid-cols-2 gap-3">
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">价格/晚 *</span></div>
              <input v-model.number="roomForm.price" type="number" min="0" step="0.01"
                class="input input-bordered w-full" />
            </label>
            <label class="form-control w-full">
              <div class="label"><span class="label-text font-semibold">可住人数 *</span></div>
              <input v-model.number="roomForm.maxGuest" type="number" min="1" max="10"
                class="input input-bordered w-full" />
            </label>
          </div>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">房间数量 *</span></div>
            <input v-model.number="roomForm.totalRooms" type="number" min="1"
              class="input input-bordered w-full" />
          </label>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">设施</span></div>
            <input v-model="roomForm.amenities" class="input input-bordered w-full"
              placeholder="如：WiFi,空调,电视" />
          </label>
        </div>
        <div class="modal-action">
          <form method="dialog"><button class="btn">取消</button></form>
          <button class="btn btn-primary" @click.prevent="handleRoomSubmit">确定</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import {
  getAdminHotelList, createHotel, updateHotel, deleteHotel, updateHotelStatus,
  getHotelRooms, createRoomType, updateRoomType, deleteRoomType
} from '@/api/admin'
import type { HotelDTO, RoomTypeDTO } from '@/api/admin'
import { MagnifyingGlassIcon, PlusIcon } from '@heroicons/vue/24/outline'

const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<number | string>('')
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const formVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formData = reactive<HotelDTO>({
  name: '', coverImage: '', city: '', address: '',
  longitude: 0, latitude: 0, description: '', starLevel: 3, facilities: '', status: 1
})

// 房型
const roomsVisible = ref(false)
const roomsData = ref<any[]>([])
const currentHotelId = ref<number>()
const roomFormVisible = ref(false)
const isRoomEdit = ref(false)
const roomForm = reactive<RoomTypeDTO>({
  hotelId: 0, name: '', price: 0, bedType: '', maxGuest: 2, totalRooms: 10, amenities: '', images: ''
})

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminHotelList({
      page: page.value, pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: statusFilter.value !== '' ? Number(statusFilter.value) : undefined,
    })
    tableData.value = res.data?.records || res.records || []
    total.value = res.data?.total || res.total || 0
  } catch (e) { console.error(e) } finally { loading.value = false }
}

function handleSearch() { page.value = 1; fetchList() }

function openForm(row?: any) {
  if (row) { isEdit.value = true; Object.assign(formData, row) }
  else { isEdit.value = false; Object.assign(formData, { name: '', coverImage: '', city: '', address: '', longitude: 0, latitude: 0, description: '', starLevel: 3, facilities: '', status: 1 }) }
  const modal = document.getElementById('hotel_form_modal') as HTMLDialogElement
  modal?.showModal()
}

async function handleSubmit() {
  if (!formData.name || !formData.city) { window.adminToast('请填写必填项', 'error'); return }
  try {
    submitting.value = true
    if (isEdit.value) await updateHotel(formData.id!, formData)
    else await createHotel(formData)
    document.getElementById('hotel_form_modal')?.dispatchEvent(new Event('close'))
    fetchList()
  } catch { window.adminToast('操作失败', 'error') } finally { submitting.value = false }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  if (!confirm(`确定要${newStatus === 1 ? '上架' : '下架'}该酒店吗？`)) return
  try { await updateHotelStatus(row.id, newStatus); row.status = newStatus }
  catch { window.adminToast('操作失败', 'error') }
}

async function handleDelete(row: any) {
  if (!confirm(`确定要删除酒店「${row.name}」吗？此操作不可恢复。`)) return
  try { await deleteHotel(row.id); fetchList() }
  catch { window.adminToast('删除失败', 'error') }
}

async function openRooms(row: any) {
  currentHotelId.value = row.id
  const modal = document.getElementById('rooms_modal') as HTMLDialogElement
  modal?.showModal()
  await fetchRooms()
}

async function fetchRooms() {
  if (!currentHotelId.value) return
  try {
    const res: any = await getHotelRooms(currentHotelId.value)
    roomsData.value = res.data || res || []
  } catch { window.adminToast('获取房型失败', 'error') }
}

function openRoomForm(row?: any) {
  if (row) { isRoomEdit.value = true; Object.assign(roomForm, row) }
  else { isRoomEdit.value = false; Object.assign(roomForm, { hotelId: currentHotelId.value, name: '', price: 0, bedType: '', maxGuest: 2, totalRooms: 10, amenities: '', images: '' }) }
  const modal = document.getElementById('room_form_modal') as HTMLDialogElement
  modal?.showModal()
}

async function handleRoomSubmit() {
  try {
    roomForm.hotelId = currentHotelId.value!
    if (isRoomEdit.value) await updateRoomType(roomForm.id!, roomForm)
    else await createRoomType(roomForm)
    document.getElementById('room_form_modal')?.dispatchEvent(new Event('close'))
    fetchRooms()
  } catch { window.adminToast('操作失败', 'error') }
}

async function deleteRoom(row: any) {
  if (!confirm('确定要删除该房型吗？')) return
  try { await deleteRoomType(row.id); fetchRooms() }
  catch { window.adminToast('删除失败', 'error') }
}

onMounted(() => { fetchList() })
</script>
