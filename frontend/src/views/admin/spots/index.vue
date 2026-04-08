<template>
  <div class="admin-page admin-spots">
    <!-- 数据卡片 -->
    <div class="data-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-input v-model="keyword" placeholder="搜索景点名称" clearable class="admin-search" @clear="handleSearch"
            @keyup.enter="handleSearch">
            <template #prefix>
              <i class="i-mdi-magnify"></i>
            </template>
          </el-input>
          <el-select v-model="cityFilter" placeholder="城市筛选" clearable class="admin-search" @change="fetchList">
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="广州" value="广州" />
            <el-option label="深圳" value="深圳" />
            <el-option label="杭州" value="杭州" />
            <el-option label="成都" value="成都" />
            <el-option label="西安" value="西安" />
          </el-select>
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable class="admin-search" @change="fetchList">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="openForm()">
            <i class="i-mdi-plus mr-1"></i> 新增景点
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" class="admin-cover"
              :preview-src-list="[row.coverImage]" />
            <span v-else class="text-gray-400">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="景点名称" min-width="150">
          <template #default="{ row }">
            <div class="font-medium">{{ row.name }}</div>
            <div class="text-sm text-gray-500">{{ row.city }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="ticketPrice" label="门票价格" width="100">
          <template #default="{ row }">
            <span class="text-green-600 font-medium">¥{{ row.ticketPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="星级" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.level" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="openTime" label="开放时间" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openForm(row)">
              编辑
            </el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" text size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="admin-pagination">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" :page-count="pageCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList" @current-change="fetchList" />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑景点' : '新增景点'" width="700px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="formData.coverImage" placeholder="请输入封面图片URL" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在城市" prop="city">
              <el-input v-model="formData.city" placeholder="请输入城市" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="门票价格" prop="ticketPrice">
              <el-input-number v-model="formData.ticketPrice" :min="0" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建议游览" prop="suggestedTime">
              <el-input-number v-model="formData.suggestedTime" :min="1" :max="24" />
              <span class="ml-2">小时</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开放时间" prop="openTime">
          <el-input v-model="formData.openTime" placeholder="如：08:00-18:00" />
        </el-form-item>
        <el-form-item label="景点描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入景点描述" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="formData.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminSpotList, createSpot, updateSpot, deleteSpot, updateSpotStatus } from '@/api/admin'
import type { SpotDTO } from '@/api/admin'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const keyword = ref('')
const cityFilter = ref('')
const statusFilter = ref<number | ''>('')
const tableData = ref<any[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})
const pageCount = computed(() => Math.ceil(pagination.total / pagination.pageSize) || 1)

// 表单相关
const formVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<SpotDTO>({
  name: '',
  coverImage: '',
  city: '',
  address: '',
  longitude: 0,
  latitude: 0,
  description: '',
  ticketPrice: 0,
  openTime: '',
  suggestedTime: 1,
  level: '',
  tags: '',
  status: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  ticketPrice: [{ required: true, message: '请输入门票价格', trigger: 'blur' }],
}

// 获取列表
async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminSpotList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: keyword.value || undefined,
      city: cityFilter.value || undefined,
      status: statusFilter.value || undefined
    })
    tableData.value = res.data?.records || res.records || []
    pagination.total = res.data?.total || res.total || 0
  } catch (e) {
    console.error('获取列表失败', e)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchList()
}

// 打开表单
function openForm(row?: any) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    Object.assign(formData, {
      name: '',
      coverImage: '',
      city: '',
      address: '',
      longitude: 0,
      latitude: 0,
      description: '',
      ticketPrice: 0,
      openTime: '',
      suggestedTime: 1,
      level: '',
      tags: '',
      status: 1
    })
  }
  formVisible.value = true
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      await updateSpot(formData.id!, formData)
      ElMessage.success('更新成功')
    } else {
      await createSpot(formData)
      ElMessage.success('创建成功')
    }

    formVisible.value = false
    fetchList()
  } catch (e: any) {
    if (e?.message) {
      ElMessage.error(e.message)
    }
  } finally {
    submitting.value = false
  }
}

// 切换状态
async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'

  try {
    await ElMessageBox.confirm(`确定要${action}该景点吗？`, '提示', { type: 'warning' })
    await updateSpotStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该景点吗？此操作不可恢复。', '警告', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteSpot(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.admin-spots {
  max-width: 1400px;
  margin: 0 auto;
}

/* 数据卡片 */
.data-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.dark .data-card {
  background: #1f2937;
  border-color: #374151;
}

/* 卡片头部 */
.card-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
  gap: 12px;
}

.dark .card-header {
  border-color: #374151;
}


</style>
