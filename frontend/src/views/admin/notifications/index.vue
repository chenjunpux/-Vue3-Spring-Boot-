<template>
  <div class="admin-page">
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2">
          <select v-model="typeFilter" class="select select-bordered w-36" @change="page = 1; fetchList()">
            <option value="">全部类型</option>
            <option :value="1">系统通知</option>
            <option :value="2">订单通知</option>
            <option :value="3">活动通知</option>
          </select>
          <select v-model="statusFilter" class="select select-bordered w-28" @change="page = 1; fetchList()">
            <option value="">全部状态</option>
            <option :value="0">草稿</option>
            <option :value="1">已发布</option>
          </select>
        </div>
        <button class="btn btn-primary btn-sm" @click="openForm()">
          <PlusIcon class="w-4 h-4 mr-1" /> 发送通知
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
              <th>ID</th><th>标题</th><th>内容</th><th>类型</th><th>发送范围</th><th>状态</th><th>发布时间</th><th>创建时间</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td class="font-medium max-w-xs truncate">{{ row.title }}</td>
              <td class="max-w-xs truncate">{{ row.content }}</td>
              <td>
                <span :class="getTypeBadgeClass(row.type)" class="badge badge-sm">{{ getTypeText(row.type) }}</span>
              </td>
              <td>{{ row.targetType === 1 ? '全部用户' : '指定用户' }}</td>
              <td>
                <span :class="row.status === 1 ? 'badge-success' : 'badge-neutral'" class="badge badge-sm">
                  {{ row.status === 1 ? '已发布' : '草稿' }}
                </span>
              </td>
              <td>{{ formatDate(row.publishedAt) }}</td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>
                <div class="flex gap-1 flex-wrap">
                  <button v-if="row.status === 0" class="btn btn-success btn-xs" @click="handlePublish(row)">发布</button>
                  <button class="btn btn-primary btn-xs" @click="openForm(row)">编辑</button>
                  <button class="btn btn-error btn-xs" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="9" class="text-center py-8 text-neutral/50">暂无数据</td>
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

    <!-- 通知表单 Modal -->
    <dialog id="notif_form_modal" class="modal">
      <div class="modal-box">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">{{ isEdit ? '编辑通知' : '发送通知' }}</h3>
        <div class="space-y-3">
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">通知标题 *</span><span class="label-text-alt">{{ formData.title.length }}/100</span></div>
            <input v-model="formData.title" class="input input-bordered w-full" placeholder="通知标题" maxLength="100" />
          </label>
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">通知内容 *</span><span class="label-text-alt">{{ formData.content.length }}/500</span></div>
            <textarea v-model="formData.content" class="textarea textarea-bordered w-full" rows="4"
              placeholder="通知内容" maxLength="500"></textarea>
          </label>
          <div>
            <div class="label"><span class="label-text font-semibold">通知类型</span></div>
            <div class="flex gap-4 flex-wrap">
              <label class="label cursor-pointer gap-2"><input v-model="formData.type" type="radio" :value="1" class="radio radio-sm" />系统通知</label>
              <label class="label cursor-pointer gap-2"><input v-model="formData.type" type="radio" :value="2" class="radio radio-sm" />订单通知</label>
              <label class="label cursor-pointer gap-2"><input v-model="formData.type" type="radio" :value="3" class="radio radio-sm" />活动通知</label>
            </div>
          </div>
          <div>
            <div class="label"><span class="label-text font-semibold">发送范围</span></div>
            <div class="flex gap-4 flex-wrap">
              <label class="label cursor-pointer gap-2"><input v-model="formData.targetType" type="radio" :value="1" class="radio radio-sm" />全部用户</label>
              <label class="label cursor-pointer gap-2"><input v-model="formData.targetType" type="radio" :value="2" class="radio radio-sm" />指定用户</label>
            </div>
          </div>
          <label v-if="formData.targetType === 2" class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">用户ID</span></div>
            <input v-model="formData.targetUserIds" class="input input-bordered w-full"
              placeholder="多个用逗号分隔，如: 1,2,3" />
          </label>
        </div>
        <div class="modal-action">
          <form method="dialog"><button class="btn">取消</button></form>
          <button class="btn btn-ghost" :disabled="saving" @click.prevent="saveDraft">
            <span v-if="saving" class="loading loading-spinner loading-xs"></span>保存草稿
          </button>
          <button class="btn btn-primary" :disabled="saving" @click.prevent="sendNotification">
            <span v-if="saving" class="loading loading-spinner loading-xs"></span>立即发送
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { adminNotificationApi, type AdminNotification } from '@/api/adminNotification'
import { PlusIcon } from '@heroicons/vue/24/outline'

const loading = ref(false)
const typeFilter = ref<number | string>('')
const statusFilter = ref<number | string>('')
const tableData = ref<AdminNotification[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const formVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)

const formData = reactive({
  id: undefined as number | undefined,
  title: '', content: '', type: 1, targetType: 1, targetUserIds: '', status: 0
})

function getTypeText(type: number) {
  const m: Record<number, string> = { 1: '系统通知', 2: '订单通知', 3: '活动通知' }
  return m[type] || '未知'
}
function getTypeBadgeClass(type: number) {
  const m: Record<number, string> = { 1: 'badge-primary', 2: 'badge-warning', 3: 'badge-success' }
  return m[type] || 'badge-neutral'
}
function formatDate(d?: string) { return d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    const res = await adminNotificationApi.list({
      page: page.value, pageSize: pageSize.value,
      type: typeFilter.value !== '' ? Number(typeFilter.value) : undefined,
      status: statusFilter.value !== '' ? Number(statusFilter.value) : undefined,
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch { console.error('获取列表失败') } finally { loading.value = false }
}

function openForm(row?: AdminNotification) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, { id: row.id, title: row.title, content: row.content, type: row.type, targetType: row.targetType, targetUserIds: row.targetUserIds || '', status: row.status })
  } else {
    isEdit.value = false
    Object.assign(formData, { id: undefined, title: '', content: '', type: 1, targetType: 1, targetUserIds: '', status: 0 })
  }
  const modal = document.getElementById('notif_form_modal') as HTMLDialogElement
  modal?.showModal()
}

async function saveDraft() {
  if (!formData.title) { alert('请输入标题'); return }
  try {
    saving.value = true
    const data: Partial<AdminNotification> = { ...formData, status: 0 }
    if (isEdit.value && formData.id) data.id = formData.id
    const res = await adminNotificationApi.save(data)
    if (res.code === 200) {
      document.getElementById('notif_form_modal')?.dispatchEvent(new Event('close'))
      fetchList()
    } else alert(res.msg || '保存失败')
  } catch { alert('保存失败') } finally { saving.value = false }
}

async function sendNotification() {
  if (!formData.title || !formData.content) { alert('请填写完整信息'); return }
  try {
    saving.value = true
    if (!isEdit.value || !formData.id) {
      const saveRes = await adminNotificationApi.save({ ...formData, status: 1 } as AdminNotification)
      if (saveRes.code !== 200) { alert(saveRes.msg || '保存失败'); return }
    } else {
      const res = await adminNotificationApi.publish(formData.id)
      if (res.code !== 200) { alert(res.msg || '发布失败'); return }
    }
    document.getElementById('notif_form_modal')?.dispatchEvent(new Event('close'))
    fetchList()
  } catch { alert('操作失败') } finally { saving.value = false }
}

async function handlePublish(row: AdminNotification) {
  if (!confirm('确定要发布该通知吗？发布后将立即推送给用户。')) return
  try {
    const res = await adminNotificationApi.publish(row.id!)
    if (res.code === 200) fetchList()
    else alert(res.msg || '发布失败')
  } catch { alert('发布失败') }
}

async function handleDelete(row: AdminNotification) {
  if (!confirm('确定要删除该通知吗？')) return
  try {
    const res = await adminNotificationApi.delete(row.id!)
    if (res.code === 200) fetchList()
    else alert(res.msg || '删除失败')
  } catch { alert('删除失败') }
}

onMounted(() => { fetchList() })
</script>
