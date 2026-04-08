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
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ getTypeText(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="targetType" label="发送范围" width="100">
            <template #default="{ row }">{{ row.targetType === 1 ? '全部用户' : '指定用户' }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '已发送' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
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
          layout="total, sizes, prev, pager, next"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </div>

    <!-- 发送通知弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑通知' : '发送通知'" width="600px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="通知标题" required>
          <el-input v-model="formData.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知内容" required>
          <el-input v-model="formData.content" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="formData.type">
            <el-option label="系统通知" :value="1" />
            <el-option label="订单通知" :value="2" />
            <el-option label="活动通知" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送范围">
          <el-radio-group v-model="formData.targetType">
            <el-radio :label="1">全部用户</el-radio>
            <el-radio :label="2">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button @click="saveDraft">保存草稿</el-button>
        <el-button type="primary" @click="sendNotification">立即发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const typeFilter = ref<number | ''>('')
const tableData = ref<any[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const formVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({ title: '', content: '', type: 1, targetType: 1, status: 0 })

function getTypeText(type: number) {
  const map: Record<number, string> = { 1: '系统通知', 2: '订单通知', 3: '活动通知' }
  return map[type] || '未知'
}
function formatDate(dateStr: string) { return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    tableData.value = []
    ElMessage.info('通知管理功能开发中...')
  } catch (e) { ElMessage.error('获取列表失败') } finally { loading.value = false }
}

function openForm(row?: any) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    Object.assign(formData, { title: '', content: '', type: 1, targetType: 1, status: 0 })
  }
  formVisible.value = true
}

async function saveDraft() {
  if (!formData.title) return ElMessage.warning('请输入标题')
  ElMessage.success('草稿保存成功')
  formVisible.value = false
  fetchList()
}

async function sendNotification() {
  if (!formData.title || !formData.content) return ElMessage.warning('请填写完整信息')
  ElMessage.success('发送成功')
  formVisible.value = false
  fetchList()
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该通知吗？', '警告', { type: 'warning', confirmButtonText: '删除' })
    ElMessage.success('删除成功')
    fetchList()
  } catch (e: any) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(() => { fetchList() })
</script>

<style scoped lang="scss">
</style>
