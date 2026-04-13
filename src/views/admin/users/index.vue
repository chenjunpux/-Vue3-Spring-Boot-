<template>
  <div class="admin-page">

    <!-- 数据卡片 -->
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center justify-between flex-wrap gap-3 p-4 border-b border-base-300">
        <div class="flex items-center gap-2 flex-wrap">
          <!-- 搜索 -->
          <div class="join">
            <input
              v-model="keyword"
              class="input input-bordered join-item w-64"
              placeholder="搜索用户ID/昵称"
              @keyup.enter="page = 1; fetchList()"
            />
            <button class="btn join-item" @click="page = 1; fetchList()">
              <MagnifyingGlassIcon class="w-4 h-4" />
            </button>
          </div>
          <!-- 状态筛选 -->
          <select v-model="statusFilter" class="select select-bordered w-36" @change="page = 1; fetchList()">
            <option value="">全部状态</option>
            <option :value="1">正常</option>
            <option :value="0">已封禁</option>
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
              <th>ID</th>
              <th>头像</th>
              <th>昵称</th>
              <th>手机号</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>状态</th>
              <th>注册时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td>
                <div class="avatar">
                  <div class="w-10 rounded-full">
                    <img :src="row.avatar || `https://placehold.co/40x40/3b82f6/ffffff?text=${row.nickname?.[0] || 'U'}`" :alt="row.nickname" />
                  </div>
                </div>
              </td>
              <td>{{ row.nickname || '-' }}</td>
              <td>{{ row.phone || '-' }}</td>
              <td>{{ row.email || '-' }}</td>
              <td>
                <span :class="row.role === 2 ? 'badge-error' : 'badge-primary'" class="badge badge-sm">
                  {{ row.role === 2 ? '管理员' : '普通用户' }}
                </span>
              </td>
              <td>
                <span :class="row.status === 1 ? 'badge-success' : 'badge-error'" class="badge badge-sm">
                  {{ row.status === 1 ? '正常' : '封禁' }}
                </span>
              </td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>
                <div class="flex items-center gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="showDetail(row)">详情</button>
                  <button class="btn btn-warning btn-xs" @click="handleResetPassword(row)">重置密码</button>
                  <button
                    :class="row.status === 1 ? 'btn-error' : 'btn-success'"
                    class="btn btn-xs"
                    @click="toggleStatus(row)"
                  >
                    {{ row.status === 1 ? '封禁' : '解封' }}
                  </button>
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

    <!-- 用户详情 Modal -->
    <dialog id="user_detail_modal" class="modal">
      <div class="modal-box">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">用户详情</h3>
        <div v-if="currentUser" class="space-y-2 text-sm">
          <div class="flex gap-2"><span class="font-semibold w-20">用户ID：</span>{{ currentUser.id }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">昵称：</span>{{ currentUser.nickname || '-' }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">手机号：</span>{{ currentUser.phone || '-' }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">邮箱：</span>{{ currentUser.email || '-' }}</div>
          <div class="flex gap-2">
            <span class="font-semibold w-20">角色：</span>
            <span :class="currentUser.role === 2 ? 'badge-error' : 'badge-primary'" class="badge badge-sm">
              {{ currentUser.role === 2 ? '管理员' : '普通用户' }}
            </span>
          </div>
          <div class="flex gap-2">
            <span class="font-semibold w-20">状态：</span>
            <span :class="currentUser.status === 1 ? 'badge-success' : 'badge-error'" class="badge badge-sm">
              {{ currentUser.status === 1 ? '正常' : '封禁' }}
            </span>
          </div>
          <div class="flex gap-2"><span class="font-semibold w-20">注册时间：</span>{{ formatDate(currentUser.createdAt) }}</div>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>

    <!-- 重置密码确认 Modal -->
    <dialog id="reset_pwd_modal" class="modal">
      <div class="modal-box max-w-sm">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <div class="flex flex-col items-center text-center pt-4">
          <div class="text-warning mb-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 mx-auto" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
          </div>
          <h3 class="font-bold text-lg mb-2">确认重置密码</h3>
          <p class="text-sm text-neutral/70 mb-1">
            确定要将用户
          </p>
          <p class="font-semibold text-base mb-1">
            「{{ resetPwdTarget?.nickname || resetPwdTarget?.id }}」
          </p>
          <p class="text-sm text-neutral/70 mb-5">
            的密码重置为 <span class="font-bold text-error">123456</span> 吗？
          </p>
          <div class="flex gap-3 w-full">
            <form method="dialog" class="flex-1"><button class="btn btn-ghost w-full">取消</button></form>
            <button class="btn btn-warning flex-1" :class="{ 'btn-disabled': resetting }" @click="confirmResetPwd">
              <span v-if="resetting" class="loading loading-spinner loading-xs"></span>
              {{ resetting ? '重置中...' : '确认重置' }}
            </button>
          </div>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAdminUserList, updateUserStatus, resetUserPassword } from '@/api/admin'
import dayjs from 'dayjs'
import { MagnifyingGlassIcon } from '@heroicons/vue/24/outline'

const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<number | string>('')
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

const currentUser = ref<any>(null)
const resetPwdTarget = ref<any>(null)
const resetting = ref(false)

function formatDate(dateStr: string) {
  return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '-'
}

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminUserList({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: statusFilter.value !== '' ? Number(statusFilter.value) : undefined,
    })
    const allUsers = res.data?.records || res.records || []
    // 只显示普通用户（role !== 2）
    tableData.value = allUsers.filter((u: any) => u.role !== 2)
    total.value = tableData.value.length
  } catch (e) {
    console.error('获取列表失败', e)
  } finally {
    loading.value = false
  }
}

function handleResetPassword(row: any) {
  resetPwdTarget.value = row
  resetting.value = false
  const modal = document.getElementById('reset_pwd_modal') as HTMLDialogElement
  modal?.showModal()
}

async function confirmResetPwd() {
  if (!resetPwdTarget.value) return
  try {
    resetting.value = true
    await resetUserPassword(resetPwdTarget.value.id)
    const modal = document.getElementById('reset_pwd_modal') as HTMLDialogElement
    modal?.close()
    window.adminToast('密码已重置为 123456', 'success')
  } catch {
    window.adminToast('重置失败', 'error')
  } finally {
    resetting.value = false
  }
}

function showDetail(row: any) {
  currentUser.value = row
  const modal = document.getElementById('user_detail_modal') as HTMLDialogElement
  modal?.showModal()
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '解封' : '封禁'
  if (!confirm(`确定要${action}该用户（${row.nickname || row.id}）吗？`)) return
  try {
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
  } catch (e) {
    window.adminToast('操作失败', 'error')
  }
}

onMounted(() => {
  fetchList()
})
</script>
