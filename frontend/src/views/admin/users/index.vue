<template>
  <div class="admin-users">
    <div class="page-header">
      <h2 class="page-title">👥 用户管理</h2>
      <el-button type="primary" @click="openDialog('create')">+ 新增用户</el-button>
    </div>

    <!-- 筛选 -->
    <div class="admin-filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="用户名/昵称/手机号" clearable style="width:180px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="filterForm.role" clearable placeholder="全部" style="width:120px">
            <el-option label="全部" :value="undefined" />
            <el-option label="普通用户" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" clearable placeholder="全部" style="width:120px">
            <el-option label="全部" :value="undefined" />
            <el-option label="正常" :value="1" />
            <el-option label="封禁" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="filterForm.page = 1; fetchData()">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="admin-content-card admin-table">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="头像" width="60">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="36">{{ row.nickname?.[0] }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="130" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="170" />
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 2 ? 'danger' : 'primary'" size="small">
              {{ row.role === 2 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openDialog('edit', row)">编辑</el-button>
            <el-button size="small" type="warning" plain @click="openPermDialog(row)">菜单权限</el-button>
            <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" plain @click="toggleStatus(row)">
              {{ row.status === 1 ? '封禁' : '解封' }}
            </el-button>
            <el-popconfirm title="确定删除该用户？" @confirm="deleteUser(row.id)">
              <template #reference>
                <el-button size="small" type="danger" plain>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :current-page="filterForm.page"
          :page-size="filterForm.pageSize"
          :page-sizes="[10, 20, 50]"
          @current-change="p => { filterForm.page = p; fetchData() }"
          @size-change="s => { filterForm.pageSize = s; filterForm.page = 1; fetchData() }"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username" v-if="dialogMode === 'create'">
          <el-input v-model="form.username" placeholder="登录用户名" />
        </el-form-item>
        <el-form-item label="密码" :prop="dialogMode === 'create' ? 'password' : ''">
          <el-input v-model="form.password" type="password" :placeholder="dialogMode === 'create' ? '默认123456' : '不修改请留空'" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="显示昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱地址" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio :value="1">普通用户</el-radio>
            <el-radio :value="2">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">封禁</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 菜单权限弹窗 -->
    <el-dialog v-model="permDialogVisible" title="菜单权限分配" width="600px" destroy-on-close>
      <div class="perm-tip">勾选该管理员可访问的菜单模块（留空则拥有全部权限）</div>
      <div v-for="group in menuItems" :key="group.group" class="perm-group-block">
        <div class="perm-group-title">{{ group.group }}</div>
        <el-checkbox-group v-model="selectedPerms" class="perm-group">
          <el-checkbox v-for="item in group.children" :key="item.key" :value="item.key">
            {{ item.label }}
            <span class="perm-desc">{{ item.desc }}</span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPermissions">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { getAdminUserList, createUser, updateUser, deleteUser, updateUserStatus, updateMenuPermissions } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const formRef = ref<FormInstance>()

const filterForm = reactive({
  page: 1, pageSize: 10,
  keyword: '', role: undefined as number | undefined, status: undefined as number | undefined,
})

const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const dialogTitle = computed(() => dialogMode.value === 'create' ? '新增用户' : '编辑用户')

const form = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  gender: 0,
  role: 1,
  status: 1,
})

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
}

// 菜单权限选项（与 AdminLayout 菜单分组一致）
// 管理员管理 = 控制 spots/hotels/orders/articles 的可见性
// 用户管理 = 控制 users 的可见性
const menuItems = [
  {
    group: '管理员管理',
    children: [
      { key: 'spots',    label: '🏛️ 景点管理', desc: '增删改查景点信息' },
      { key: 'hotels',   label: '🏨 酒店管理', desc: '增删改查酒店及房型' },
      { key: 'orders',   label: '📋 订单管理', desc: '查看和处理订单' },
      { key: 'articles', label: '📝 游记管理', desc: '审核和管理用户游记' },
    ],
  },
  {
    group: '用户管理',
    children: [
      { key: 'users', label: '👥 用户列表', desc: '增删改查用户账号' },
    ],
  },
]

const permDialogVisible = ref(false)
const selectedPerms = ref<string[]>([])
const permTargetId = ref<number>()

function formatDate(str?: string) {
  return str ? dayjs(str).format('YYYY-MM-DD HH:mm') : '—'
}

function resetFilter() {
  Object.assign(filterForm, { keyword: '', role: undefined, status: undefined, page: 1 })
  fetchData()
}

async function fetchData() {
  try {
    loading.value = true
    const res: any = await getAdminUserList(filterForm)
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function openDialog(mode: 'create' | 'edit', row?: any) {
  dialogMode.value = mode
  if (mode === 'create') {
    Object.assign(form, { id: undefined, username: '', password: '', nickname: '', phone: '', email: '', gender: 0, role: 1, status: 1 })
  } else {
    Object.assign(form, { id: row.id, username: row.username, password: '', nickname: row.nickname || '', phone: row.phone || '', email: row.email || '', gender: row.gender || 0, role: row.role || 1, status: row.status || 1 })
  }
  dialogVisible.value = true
}

async function submitForm() {
  try {
    await formRef.value?.validate()
    submitting.value = true
    if (dialogMode.value === 'create') {
      await createUser({ ...form })
      ElMessage.success('创建成功')
    } else {
      await updateUser(form.id!, { ...form })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row: any) {
  try {
    await updateUserStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success(row.status === 1 ? '已封禁' : '已解封')
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function deleteUser(id: number) {
  try {
    await deleteUser(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function openPermDialog(row: any) {
  permTargetId.value = row.id
  selectedPerms.value = row.menuPermissions ? row.menuPermissions.split(',').filter(Boolean) : []
  permDialogVisible.value = true
}

async function submitPermissions() {
  try {
    await updateMenuPermissions(permTargetId.value!, selectedPerms.value.join(','))
    ElMessage.success('权限保存成功')
    permDialogVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

fetchData()
</script>

<style scoped lang="scss">
.admin-users {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    .page-title { font-size: 20px; font-weight: 700; color: #1e293b; margin: 0; }
  }
  .filter-card { margin-bottom: 16px; }
  .pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
}

.perm-tip {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 16px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 6px;
}

.perm-group-block {
  margin-bottom: 20px;
  &:last-child { margin-bottom: 0; }

  .perm-group-title {
    font-size: 13px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 10px;
    padding-bottom: 6px;
    border-bottom: 1px solid #e2e8f0;
  }
}

.perm-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  .el-checkbox {
    padding: 10px 14px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    margin: 0;
    transition: all 0.2s;
    &:hover { border-color: #3b82f6; background: #f0f6ff; }
    .perm-desc { color: #94a3b8; font-size: 12px; margin-left: 8px; }
  }
}
</style>
