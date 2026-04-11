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
              placeholder="搜索管理员ID/昵称" @keyup.enter="page = 1; fetchList()" />
            <button class="btn join-item" @click="page = 1; fetchList()">
              <MagnifyingGlassIcon class="w-4 h-4" />
            </button>
          </div>
          <!-- 状态筛选 -->
          <select v-model="statusFilter" class="select select-bordered w-36"
            @change="page = 1; fetchList()">
            <option :value="-1">全部状态</option>
            <option :value="1">正常</option>
            <option :value="0">已禁用</option>
          </select>
        </div>
        <button class="btn btn-primary btn-sm" @click="openForm()">
          <PlusIcon class="w-4 h-4" />
          添加管理员
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex items-center justify-center p-8">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- 表格 -->
      <div v-else class="overflow-x-auto">
        <table class="table table-zebra w-full text-sm">
          <thead>
            <tr>
              <th>ID</th>
              <th>头像</th>
              <th>昵称</th>
              <th>手机号</th>
              <th>权限</th>
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
                    <img :src="row.avatar || `https://placehold.co/40x40/3b82f6/ffffff?text=${row.nickname?.[0] || 'A'}`"
                      :alt="row.nickname" />
                  </div>
                </div>
              </td>
              <td>
                <div class="font-medium">{{ row.nickname || '-' }}</div>
                <div class="text-xs text-neutral/50">{{ row.username || '-' }}</div>
              </td>
              <td class="text-neutral/60">{{ row.phone || '-' }}</td>
              <td>
                <div class="flex flex-wrap gap-1 max-w-48">
                  <span v-for="perm in getPermissionBadges(row.menuPermissions)" :key="perm"
                    class="badge badge-info badge-sm">{{ perm }}</span>
                  <span v-if="!row.menuPermissions" class="text-xs text-neutral/40">无</span>
                </div>
              </td>
              <td>
                <span :class="row.status === 1 ? 'badge-success' : 'badge-error'" class="badge badge-sm">
                  {{ row.status === 1 ? '正常' : '禁用' }}
                </span>
              </td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>
                <div class="flex items-center gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="openForm(row)">编辑</button>
                  <button class="btn btn-warning btn-xs" @click="openPermissions(row)">权限</button>
                  <button class="btn btn-info btn-xs" @click="handleResetPassword(row)">重置密码</button>
                  <button :class="row.status === 1 ? 'btn-error' : 'btn-success'" class="btn btn-xs"
                    @click="toggleStatus(row)">
                    {{ row.status === 1 ? '禁用' : '启用' }}
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="8" class="text-center py-12 text-neutral/50">
                <div class="flex flex-col items-center gap-2">
                  <ShieldCheckIcon class="w-10 h-10 opacity-40" />
                  <span>暂无管理员</span>
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
    <dialog id="admin_form_modal" class="modal">
      <div class="modal-box max-w-lg">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-6">{{ isEdit ? '编辑管理员' : '添加管理员' }}</h3>

        <div class="space-y-4">
          <!-- 用户名 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">用户名 <span class="text-error">*</span></span></div>
            <input v-model="formData.username" class="input input-bordered w-full" placeholder="登录用户名"
              :disabled="isEdit" />
          </label>

          <!-- 昵称 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">昵称 <span class="text-error">*</span></span></div>
            <input v-model="formData.nickname" class="input input-bordered w-full" placeholder="显示名称" />
          </label>

          <!-- 手机号 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">手机号</span></div>
            <input v-model="formData.phone" class="input input-bordered w-full" placeholder="手机号码" />
          </label>

          <!-- 邮箱 -->
          <label class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">邮箱</span></div>
            <input v-model="formData.email" class="input input-bordered w-full" type="email" placeholder="邮箱" />
          </label>

          <!-- 密码（仅创建时） -->
          <label v-if="!isEdit" class="form-control w-full">
            <div class="label"><span class="label-text font-semibold">初始密码</span></div>
            <input v-model="formData.password" class="input input-bordered w-full" type="password"
              placeholder="默认 123456" />
            <div class="label"><span class="label-text-alt text-neutral/40">留空则默认 123456</span></div>
          </label>

          <!-- 状态 -->
          <div class="flex items-center gap-3">
            <span class="font-semibold">状态：</span>
            <input v-model="formData.status" type="checkbox" class="toggle toggle-success toggle-sm"
              :true-value="1" :false-value="0" />
            <span class="text-sm" :class="formData.status === 1 ? 'text-success' : 'text-neutral/40'">
              {{ formData.status === 1 ? '正常' : '禁用' }}
            </span>
          </div>
        </div>

        <!-- 底部按钮 -->
        <div class="mt-6 pt-4 border-t border-base-300 flex justify-end gap-3">
          <form method="dialog"><button class="btn btn-ghost">取消</button></form>
          <button class="btn btn-primary" :class="{ 'btn-disabled': submitLoading }" @click="handleSubmit">
            <span v-if="submitLoading" class="loading loading-spinner loading-sm"></span>
            {{ submitLoading ? '保存中...' : (isEdit ? '保存' : '添加') }}
          </button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>

    <!-- ========== 权限配置 Modal ========== -->
    <dialog id="permission_modal" class="modal">
      <div class="modal-box max-w-md">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-2">菜单权限配置</h3>
        <p class="text-sm text-neutral/60 mb-4">勾选该管理员可访问的菜单模块</p>

        <div v-if="permTarget" class="mb-2 p-3 bg-base-200 rounded-lg text-sm">
          <span class="font-medium">{{ permTarget.nickname || permTarget.username }}</span>
          <span class="text-neutral/50 ml-2">ID: {{ permTarget.id }}</span>
        </div>

        <div class="space-y-2 max-h-80 overflow-y-auto">
          <label v-for="item in allPermissions" :key="item.key"
            class="flex items-center gap-3 p-2 hover:bg-base-200 rounded-lg cursor-pointer">
            <input v-model="permSelections" :value="item.key" type="checkbox" class="checkbox checkbox-sm" />
            <div>
              <div class="font-medium text-sm">{{ item.label }}</div>
              <div class="text-xs text-neutral/50">{{ item.desc }}</div>
            </div>
          </label>
        </div>

        <div class="mt-4 pt-4 border-t border-base-300 flex justify-end gap-3">
          <form method="dialog"><button class="btn btn-ghost">取消</button></form>
          <button class="btn btn-primary btn-sm" @click="savePermissions">保存权限</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>

    <!-- ========== 详情 Modal ========== -->
    <dialog id="admin_detail_modal" class="modal">
      <div class="modal-box">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-4">管理员详情</h3>
        <div v-if="currentAdmin" class="space-y-2 text-sm">
          <div class="flex gap-2"><span class="font-semibold w-20">ID：</span>{{ currentAdmin.id }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">用户名：</span>{{ currentAdmin.username || '-' }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">昵称：</span>{{ currentAdmin.nickname || '-' }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">手机号：</span>{{ currentAdmin.phone || '-' }}</div>
          <div class="flex gap-2"><span class="font-semibold w-20">邮箱：</span>{{ currentAdmin.email || '-' }}</div>
          <div class="flex gap-2">
            <span class="font-semibold w-20">权限：</span>
            <span class="flex flex-wrap gap-1">
              <span v-for="p in getPermissionBadges(currentAdmin.menuPermissions)" :key="p"
                class="badge badge-info badge-sm">{{ p }}</span>
              <span v-if="!currentAdmin.menuPermissions" class="text-neutral/50">无</span>
            </span>
          </div>
          <div class="flex gap-2">
            <span class="font-semibold w-20">状态：</span>
            <span :class="currentAdmin.status === 1 ? 'badge-success' : 'badge-error'"
              class="badge badge-sm">{{ currentAdmin.status === 1 ? '正常' : '禁用' }}</span>
          </div>
          <div class="flex gap-2"><span class="font-semibold w-20">注册时间：</span>{{ formatDate(currentAdmin.createdAt) }}</div>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>

    <!-- ========== 重置密码确认 Modal ========== -->
    <dialog id="reset_pwd_modal" class="modal">
      <div class="modal-box max-w-sm">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <div class="flex flex-col items-center text-center pt-4">
          <div class="text-warning mb-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 mx-auto" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 7a2 2 0 012 2m4 0a5 5 0 01-7.646 7.646l-1.041-1.041A3 3 0 1110 11H3a3 3 0 013-3z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M3 7V5a2 2 0 012-2h2M3 7h2a2 2 0 012 2v2a2 2 0 01-2 2H3M3 9v6a2 2 0 002 2h2" />
            </svg>
          </div>
          <h3 class="font-bold text-lg mb-2">确认重置密码</h3>
          <p class="text-sm text-neutral/70 mb-1">
            确定要将管理员
          </p>
          <p class="font-semibold text-base mb-1">
            「{{ resetPwdTarget?.nickname || resetPwdTarget?.username }}」
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

    <!-- ========== 切换状态确认 Modal ========== -->
    <dialog id="toggle_status_modal" class="modal">
      <div class="modal-box max-w-sm">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <div class="flex flex-col items-center text-center pt-4">
          <div :class="toggleTarget?.status === 1 ? 'text-error' : 'text-success'" class="mb-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 mx-auto" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path v-if="toggleTarget?.status === 1" stroke-linecap="round" stroke-linejoin="round" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
              <path v-else stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="font-bold text-lg mb-2">
            {{ toggleTarget?.status === 1 ? '确认禁用' : '确认启用' }}管理员
          </h3>
          <p class="font-semibold text-base mb-5">
            「{{ toggleTarget?.nickname || toggleTarget?.username }}」
          </p>
          <p class="text-sm text-neutral/70 mb-5">
            {{ toggleTarget?.status === 1 ? '禁用后该管理员将无法登录系统' : '启用后该管理员可正常登录系统' }}
          </p>
          <div class="flex gap-3 w-full">
            <form method="dialog" class="flex-1"><button class="btn btn-ghost w-full">取消</button></form>
            <button class="btn flex-1" :class="[toggleTarget?.status === 1 ? 'btn-error' : 'btn-success', { 'btn-disabled': toggling }]" @click="confirmToggleStatus">
              <span v-if="toggling" class="loading loading-spinner loading-xs"></span>
              {{ toggling ? '处理中...' : (toggleTarget?.status === 1 ? '确认禁用' : '确认启用') }}
            </button>
          </div>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getAdminUserList, updateUserStatus, resetUserPassword, updateMenuPermissions, createUser, updateUser } from '@/api/admin'
import dayjs from 'dayjs'
import { MagnifyingGlassIcon, PlusIcon, ShieldCheckIcon } from '@heroicons/vue/24/outline'

// ===== 状态 =====
const loading = ref(false)
const submitLoading = ref(false)
const keyword = ref('')
const statusFilter = ref(-1)
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

// ===== 权限配置 =====
const permTarget = ref<any>(null)
const permSelections = ref<string[]>([])

const allPermissions = [
  { key: 'dashboard', label: 'Dashboard', desc: '数据大屏' },
  { key: 'spots', label: '景点管理', desc: '景点增删改查' },
  { key: 'hotels', label: '酒店管理', desc: '酒店及房型管理' },
  { key: 'orders', label: '订单管理', desc: '所有订单及退款' },
  { key: 'users', label: '用户管理', desc: '查看用户列表' },
  { key: 'articles', label: '游记管理', desc: '游记审核与置顶' },
  { key: 'payments', label: '支付管理', desc: '支付流水与退款' },
  { key: 'coupons', label: '优惠券管理', desc: '优惠券增删改查' },
  { key: 'notifications', label: '通知管理', desc: '系统消息发布' },
  { key: 'settings', label: '系统设置', desc: '基础配置、主题、安全' },
]

// ===== 表单 =====
const isEdit = ref(false)
const formData = reactive({
  id: undefined as number | undefined,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  password: '',
  status: 1,
  role: 2, // 管理员角色
})

// ===== 详情 =====
const currentAdmin = ref<any>(null)
const resetPwdTarget = ref<any>(null)
const resetting = ref(false)
const toggleTarget = ref<any>(null)
const toggling = ref(false)

// ===== 辅助方法 =====
function formatDate(d: string) {
  return d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-'
}

function getPermissionBadges(perms: string | null | undefined) {
  if (!perms) return []
  return perms.split(',').filter(Boolean)
}

function getModal(id: string): HTMLDialogElement {
  return document.getElementById(id) as HTMLDialogElement
}

// ===== 获取列表 =====
async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminUserList({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value || undefined,
      status: statusFilter.value === -1 ? undefined : statusFilter.value,
    })
    const allUsers = res.data?.records || res.records || []
    // 只显示管理员（role === 2）
    tableData.value = allUsers.filter((u: any) => u.role === 2)
    total.value = tableData.value.length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// ===== 打开表单 =====
function openForm(row?: any) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, {
      id: row.id,
      username: row.username || '',
      nickname: row.nickname || '',
      phone: row.phone || '',
      email: row.email || '',
      password: '',
      status: row.status || 1,
      role: 2,
    })
  } else {
    isEdit.value = false
    Object.assign(formData, {
      id: undefined,
      username: '',
      nickname: '',
      phone: '',
      email: '',
      password: '',
      status: 1,
      role: 2,
    })
  }
  getModal('admin_form_modal')?.showModal()
}

// ===== 提交表单 =====
async function handleSubmit() {
  if (!formData.username.trim()) {
    window.adminToast('请输入用户名', 'error')
    return
  }
  if (!formData.nickname.trim()) {
    window.adminToast('请输入昵称', 'error')
    return
  }
  try {
    submitLoading.value = true
    const payload: any = {
      username: formData.username,
      nickname: formData.nickname,
      phone: formData.phone || undefined,
      email: formData.email || undefined,
      status: formData.status,
      role: 2,
    }
    if (!isEdit.value) {
      payload.password = formData.password || '123456'
      await createUser(payload)
    } else {
      await updateUser(formData.id!, payload)
    }
    getModal('admin_form_modal')?.close()
    fetchList()
  } catch (e) {
    console.error(e)
    window.adminToast('操作失败', 'error')
  } finally {
    submitLoading.value = false
  }
}

// ===== 权限配置 =====
function openPermissions(row: any) {
  permTarget.value = row
  permSelections.value = row.menuPermissions ? row.menuPermissions.split(',').filter(Boolean) : []
  getModal('permission_modal')?.showModal()
}

async function savePermissions() {
  if (!permTarget.value) return
  try {
    await updateMenuPermissions(permTarget.value.id, permSelections.value.join(','))
    permTarget.value.menuPermissions = permSelections.value.join(',')
    getModal('permission_modal')?.close()
    window.adminToast('权限已保存', 'success')
  } catch {
    window.adminToast('保存失败', 'error')
  }
}

// ===== 重置密码 =====
function handleResetPassword(row: any) {
  resetPwdTarget.value = row
  resetting.value = false
  getModal('reset_pwd_modal')?.showModal()
}

async function confirmResetPwd() {
  if (!resetPwdTarget.value) return
  try {
    resetting.value = true
    await resetUserPassword(resetPwdTarget.value.id)
    getModal('reset_pwd_modal')?.close()
    window.adminToast('密码已重置为 123456', 'success')
  } catch {
    window.adminToast('重置失败', 'error')
  } finally {
    resetting.value = false
  }
}

// ===== 切换状态 =====
function toggleStatus(row: any) {
  toggleTarget.value = row
  toggling.value = false
  getModal('toggle_status_modal')?.showModal()
}

async function confirmToggleStatus() {
  if (!toggleTarget.value) return
  try {
    toggling.value = true
    const newStatus = toggleTarget.value.status === 1 ? 0 : 1
    await updateUserStatus(toggleTarget.value.id, newStatus)
    toggleTarget.value.status = newStatus
    getModal('toggle_status_modal')?.close()
  } catch {
    window.adminToast('操作失败', 'error')
  } finally {
    toggling.value = false
  }
}

// ===== 详情 =====
function showDetail(row: any) {
  currentAdmin.value = row
  getModal('admin_detail_modal')?.showModal()
}

onMounted(() => {
  fetchList()
})
</script>
