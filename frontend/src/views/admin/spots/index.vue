<template>
  <div class="admin-spots">
    <div class="page-header">
      <h2 class="page-title">🗺️ 景点管理</h2>
      <el-button type="primary" @click="openDialog('create')">+ 添加景点</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="admin-filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="城市">
          <el-input v-model="filterForm.city" placeholder="城市名称" clearable />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="景点名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="admin-content-card admin-table">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" style="width:60px;height:40px;border-radius:4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="景点名称" min-width="150" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="level" label="等级" width="80">
          <template #default="{ row }">{{ row.level || '-' }}</template>
        </el-table-column>
        <el-table-column prop="ticketPrice" label="门票价" width="100">
          <template #default="{ row }">¥{{ row.ticketPrice }}</template>
        </el-table-column>
        <el-table-column prop="hotScore" label="热度" width="80" />
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog('edit', row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确认删除该景点？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :current-page="filterForm.page"
          :page-size="filterForm.pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="封面图URL" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="图片URL" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="如：北京" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="景区等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择" style="width: 100%">
                <el-option label="5A" value="5A" />
                <el-option label="4A" value="4A" />
                <el-option label="3A" value="3A" />
                <el-option label="其他" value="" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="详细地址" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="门票价格" prop="ticketPrice">
              <el-input-number v-model="form.ticketPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建议时长" prop="suggestedTime">
              <el-input-number v-model="form.suggestedTime" :min="1" :max="72" style="width: 100%">
                <template #append>小时</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开放时间" prop="openTime">
          <el-input v-model="form.openTime" placeholder="如：08:00-18:00" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔，如：历史,文化,博物馆" />
        </el-form-item>
        <el-form-item label="景点描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="景点描述..." />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number v-model="form.longitude" :precision="6" :min="-180" :max="180" style="width: 100%" />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number v-model="form.latitude" :precision="6" :min="-90" :max="90" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminSpotList, createSpot, updateSpot, deleteSpot, updateSpotStatus, type SpotDTO } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref<any[]>([])
const total = ref(0)
const dialogMode = ref<'create' | 'edit'>('create')

const filterForm = reactive({
  page: 1,
  pageSize: 10,
  city: '',
  keyword: '',
  status: undefined as number | undefined,
})

const defaultForm = (): SpotDTO => ({
  name: '',
  coverImage: '',
  city: '',
  address: '',
  longitude: 0,
  latitude: 0,
  description: '',
  ticketPrice: 0,
  openTime: '',
  suggestedTime: 4,
  level: '',
  tags: '',
  status: 1,
})

const form = reactive<SpotDTO>(defaultForm())

const dialogTitle = computed(() => dialogMode.value === 'create' ? '添加景点' : '编辑景点')

const formRules = {
  name: [{ required: true, message: '请输入景点名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
}

async function fetchData() {
  try {
    loading.value = true
    const res: any = await getAdminSpotList(filterForm)
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  filterForm.page = 1
  fetchData()
}

function handleReset() {
  filterForm.city = ''
  filterForm.keyword = ''
  filterForm.status = undefined
  filterForm.page = 1
  fetchData()
}

function handlePageChange(page: number) {
  filterForm.page = page
  fetchData()
}

function openDialog(mode: 'create' | 'edit', row?: any) {
  dialogMode.value = mode
  if (mode === 'edit' && row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      coverImage: row.coverImage || '',
      city: row.city,
      address: row.address || '',
      longitude: row.longitude || 0,
      latitude: row.latitude || 0,
      description: row.description || '',
      ticketPrice: row.ticketPrice || 0,
      openTime: row.openTime || '',
      suggestedTime: row.suggestedTime || 4,
      level: row.level || '',
      tags: row.tags || '',
      status: row.status,
    })
  } else {
    Object.assign(form, defaultForm())
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (dialogMode.value === 'create') {
      await createSpot(form)
      ElMessage.success('添加成功')
    } else {
      await updateSpot(form.id!, form)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e: any) {
    if (e !== false) ElMessage.error(e?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateSpotStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(id: number) {
  try {
    await deleteSpot(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

fetchData()
</script>

<style scoped lang="scss">
.admin-spots {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;

    .page-title {
      font-size: 20px;
      font-weight: 700;
      color: #1e293b;
      margin: 0;
    }
  }

  .filter-card {
    margin-bottom: 16px;
  }

  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}
</style>
