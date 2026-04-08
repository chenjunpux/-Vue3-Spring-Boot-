<template>
  <div class="admin-page">
    <!-- 数据卡片 -->
    <div class="admin-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-select v-model="typeFilter" placeholder="通知类型" clearable class="admin-search" @change="fetchList">
            <el-option label="系统通知" :value="1" />
            <el-option label="订单通知" :value="2" />
            <el-option label="活动通知" :value="3" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="状态" clearable class="admin-search" @change="fetchList">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </div>
        <div class="admin-header-right">
          <el-button type="primary" @click="openForm()">
            <i class="i-mdi-plus mr-1"></i> 发送通知
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="admin-table">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <div class="font-medium">{{ row.title }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
          <el-table-column prop="type" label="类型" width="110">
            <template #default="{ row }">
              <el-tag size="small" :type="getTypeTagType(row.type)">{{ getTypeText(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="targetType" label="发送范围" width="110">
            <template #default="{ row }">{{ row.targetType === 1 ? '全部用户' : '指定用户' }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="publishedAt" label="发布时间" width="170">
            <template #default="{ row }">{{ formatDate(row.publishedAt) }}</template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" type="success" text size="small" @click="handlePublish(row)">发布</el-button>
              <el-button type="primary" text size="small" @click="openForm(row)">编辑</el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="admin-pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          :page-count="pageCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </div>

    <!-- 发送通知弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑通知' : '发送通知'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入通知标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="4" placeholder="请输入通知内容" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="通知类型" prop="type">
          <el-radio-group v-model="formData.type">
            <el-radio :label="1">系统通知</el-radio>
            <el-radio :label="2">订单通知</el-radio>
            <el-radio :label="3">活动通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发送范围" prop="targetType">
          <el-radio-group v-model="formData.targetType">
            <el-radio :label="1">全部用户</el-radio>
            <el-radio :label="2">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.targetType === 2" label="用户ID" prop="targetUserIds">
          <el-input v-model="formData.targetUserIds" placeholder="请输入用户ID，多个用逗号分隔，如: 1,2,3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button @click="saveDraft" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="sendNotification" :loading="saving">立即发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import dayjs from 'dayjs'
import { adminNotificationApi, type AdminNotification } from '@/api/adminNotification'

const loading = ref(false)
const typeFilter = ref<number | ''>('')
const statusFilter = ref<number | ''>('')
const tableData = ref<AdminNotification[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const pageCount = computed(() => Math.ceil(pagination.total / pagination.pageSize) || 1)

const formVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<{
  id?: number
  title: string
  content: string
  type: number
  targetType: number
  targetUserIds: string
  status: number
}>({
  title: '',
  content: '',
  type: 1,
  targetType: 1,
  targetUserIds: '',
  status: 0,
})

const formRules: FormRules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
}

function getTypeText(type: number) {
  const map: Record<number, string> = { 1: '系统通知', 2: '订单通知', 3: '活动通知' }
  return map[type] || '未知'
}

function getTypeTagType(type: number) {
  const map: Record<number, string> = { 1: 'primary', 2: 'warning', 3: 'success' }
  return map[type] || 'info'
}

function formatDate(dateStr: string | undefined) {
  return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '-'
}

async function fetchList() {
  try {
    loading.value = true
    const res = await adminNotificationApi.list({
      page: pagination.page,
      pageSize: pagination.pageSize,
      type: typeFilter.value || undefined,
      status: statusFilter.value || undefined,
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

function openForm(row?: AdminNotification) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, {
      id: row.id,
      title: row.title,
      content: row.content,
      type: row.type,
      targetType: row.targetType,
      targetUserIds: row.targetUserIds || '',
      status: row.status,
    })
  } else {
    isEdit.value = false
    Object.assign(formData, {
      id: undefined,
      title: '',
      content: '',
      type: 1,
      targetType: 1,
      targetUserIds: '',
      status: 0,
    })
  }
  formVisible.value = true
}

async function saveDraft() {
  if (!formData.title) return ElMessage.warning('请输入标题')
  try {
    saving.value = true
    const data: Partial<AdminNotification> = {
      ...formData,
      status: 0,
    }
    if (isEdit.value && formData.id) {
      data.id = formData.id
    }
    const res = await adminNotificationApi.save(data)
    if (res.code === 200) {
      ElMessage.success('草稿保存成功')
      formVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function sendNotification() {
  if (!formData.title || !formData.content) return ElMessage.warning('请填写完整信息')
  try {
    saving.value = true
    // 如果是新建，先保存再发布
    if (!isEdit.value || !formData.id) {
      const saveRes = await adminNotificationApi.save({ ...formData, status: 1 } as AdminNotification)
      if (saveRes.code !== 200) {
        ElMessage.error(saveRes.msg || '保存失败')
        return
      }
      ElMessage.success('发送成功')
    } else {
      // 已存在：直接发布
      const publishRes = await adminNotificationApi.publish(formData.id)
      if (publishRes.code !== 200) {
        ElMessage.error(publishRes.msg || '发布失败')
        return
      }
      ElMessage.success('发布成功')
    }
    formVisible.value = false
    fetchList()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function handlePublish(row: AdminNotification) {
  try {
    await ElMessageBox.confirm('确定要发布该通知吗？发布后将立即推送给用户。', '发布确认', {
      type: 'warning',
      confirmButtonText: '发布',
    })
    const res = await adminNotificationApi.publish(row.id!)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('发布失败')
  }
}

async function handleDelete(row: AdminNotification) {
  try {
    await ElMessageBox.confirm('确定要删除该通知吗？', '警告', { type: 'warning', confirmButtonText: '删除' })
    const res = await adminNotificationApi.delete(row.id!)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => { fetchList() })
</script>

<style scoped lang="scss">
</style>
