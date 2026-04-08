<template>
  <div class="admin-page">
    <div class="admin-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="header-left">
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable class="status-select" @change="fetchList">
            <el-option label="全部" :value="undefined" />
            <el-option label="进行中" :value="1" />
            <el-option label="已下架" :value="0" />
          </el-select>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="openForm()">
            <i class="i-mdi-plus mr-1"></i> 创建优惠券
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="admin-table">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="优惠券名称" min-width="150">
            <template #default="{ row }">
              <div class="coupon-name-cell">
                <span class="name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 1 ? 'danger' : 'warning'" size="small">
                {{ row.type === 1 ? '满减券' : '折扣券' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="优惠内容" width="120">
            <template #default="{ row }">
              <span class="discount-value" :class="{ 'type-2': row.type === 2 }">
                <template v-if="row.type === 1">¥{{ row.discountValue }}</template>
                <template v-else>{{ (row.discountValue * 10).toFixed(1) }}折</template>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="使用门槛" width="100">
            <template #default="{ row }">
              <span class="text-muted">{{ row.minAmount > 0 ? `满¥${row.minAmount}` : '无门槛' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalCount" label="发放数量" width="90" />
          <el-table-column label="已使用" width="90">
            <template #default="{ row }">
              <span class="text-muted">{{ getUsedCount(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="剩余" width="90">
            <template #default="{ row }">
              <span class="text-success">{{ row.remainCount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="有效期" width="200">
            <template #default="{ row }">
              <div class="date-cell">
                <span>{{ formatDate(row.validStart) }}</span>
                <span class="text-muted"> ~ </span>
                <span>{{ formatDate(row.validEnd) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row)" size="small">
                {{ getStatusText(row) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="openForm(row)">编辑</el-button>
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                text size="small" 
                @click="toggleStatus(row)"
              >
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
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

    <!-- 创建/编辑弹窗 -->
    <el-dialog 
      v-model="formVisible" 
      :title="isEdit ? '编辑优惠券' : '创建优惠券'" 
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="formData.name" placeholder="如：新人专享满减券" maxlength="50" />
        </el-form-item>
        <el-form-item label="优惠券类型" prop="type">
          <el-radio-group v-model="formData.type" @change="handleTypeChange">
            <el-radio :label="1">满减券</el-radio>
            <el-radio :label="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠面值" prop="discountValue">
          <el-input-number 
            v-model="formData.discountValue" 
            :min="1" 
            :precision="formData.type === 1 ? 0 : 1" 
            :max="formData.type === 1 ? 1000 : 9.9"
          />
          <span class="form-tip">{{ formData.type === 1 ? '元' : '折' }}</span>
        </el-form-item>
        <el-form-item label="使用门槛" prop="minAmount">
          <el-input-number v-model="formData.minAmount" :min="0" :precision="2" :step="10" />
          <span class="form-tip">元（0表示无门槛）</span>
        </el-form-item>
        <el-form-item label="最高优惠" v-if="formData.type === 2">
          <el-input-number v-model="formData.maxDiscount" :min="0" :precision="2" />
          <span class="form-tip">元（0表示不限制）</span>
        </el-form-item>
        <el-form-item label="发放数量" prop="totalCount">
          <el-input-number v-model="formData.totalCount" :min="1" :max="100000" />
        </el-form-item>
        <el-form-item label="每人限领" prop="perUserLimit">
          <el-input-number v-model="formData.perUserLimit" :min="1" :max="99" />
          <span class="form-tip">张</span>
        </el-form-item>
        <el-form-item label="有效期" prop="validRange">
          <el-date-picker
            v-model="formData.validRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            :shortcuts="dateShortcuts"
          />
        </el-form-item>
        <el-form-item label="适用范围" prop="applicableType">
          <el-select v-model="formData.applicableType" placeholder="选择适用范围">
            <el-option :label="'全场通用'" :value="1" />
            <el-option :label="'指定景点'" :value="2" />
            <el-option :label="'指定酒店'" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="使用说明" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="3" 
            placeholder="描述优惠券的使用条件或注意事项"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" v-if="isEdit">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
          <span class="form-tip ml-2">{{ formData.status === 1 ? '上架中' : '已下架' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { couponApi } from '@/api/coupon'
import dayjs from 'dayjs'

// 状态
const loading = ref(false)
const submitLoading = ref(false)
const statusFilter = ref<number | undefined>(undefined)
const tableData = ref<any[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const pageCount = computed(() => Math.ceil(pagination.total / pagination.pageSize) || 1)

// 表单
const formVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive({
  id: undefined as number | undefined,
  name: '',
  type: 1,
  discountValue: 10,
  minAmount: 0,
  maxDiscount: undefined as number | undefined,
  totalCount: 100,
  perUserLimit: 1,
  applicableType: 1,
  validRange: [] as string[],
  validStart: '',
  validEnd: '',
  description: '',
  status: 1,
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择优惠券类型', trigger: 'change' }],
  discountValue: [{ required: true, message: '请输入优惠面值', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入发放数量', trigger: 'blur' }],
  validRange: [{ required: true, message: '请选择有效期', trigger: 'change' }],
}

// 日期快捷选项
const dateShortcuts = [
  {
    text: '7天内',
    value: () => {
      const start = new Date()
      const end = new Date()
      end.setTime(start.getTime() + 7 * 24 * 60 * 60 * 1000)
      return [start, end]
    },
  },
  {
    text: '30天内',
    value: () => {
      const start = new Date()
      const end = new Date()
      end.setTime(start.getTime() + 30 * 24 * 60 * 60 * 1000)
      return [start, end]
    },
  },
  {
    text: '90天内',
    value: () => {
      const start = new Date()
      const end = new Date()
      end.setTime(start.getTime() + 90 * 24 * 60 * 60 * 1000)
      return [start, end]
    },
  },
]

// 辅助方法
function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD')
}

function getStatusType(row: any) {
  if (row.status === 0) return 'info'
  const now = dayjs()
  const endTime = dayjs(row.validEnd)
  return endTime.isAfter(now) ? 'success' : 'warning'
}

function getStatusText(row: any) {
  if (row.status === 0) return '已下架'
  const now = dayjs()
  const endTime = dayjs(row.validEnd)
  return endTime.isAfter(now) ? '进行中' : '已过期'
}

function getUsedCount(row: any) {
  return row.totalCount - row.remainCount
}

function handleTypeChange() {
  // 切换类型时重置优惠值
  if (formData.type === 1) {
    formData.discountValue = 10
  } else {
    formData.discountValue = 0.9
  }
}

// 获取列表
async function fetchList() {
  try {
    loading.value = true
    const res = await couponApi.admin.list({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: statusFilter.value,
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e: any) {
    ElMessage.error(e.message || '获取列表失败')
  } finally {
    loading.value = false
  }
}

// 打开表单
function openForm(row?: any) {
  if (row) {
    isEdit.value = true
    formData.id = row.id
    formData.name = row.name
    formData.type = row.type
    formData.discountValue = row.discountValue
    formData.minAmount = row.minAmount || 0
    formData.maxDiscount = row.maxDiscount
    formData.totalCount = row.totalCount
    formData.perUserLimit = row.perUserLimit || 1
    formData.applicableType = row.applicableType || 1
    formData.description = row.description || ''
    formData.status = row.status
    formData.validRange = [row.validStart, row.validEnd]
  } else {
    isEdit.value = false
    Object.assign(formData, {
      id: undefined,
      name: '',
      type: 1,
      discountValue: 10,
      minAmount: 0,
      maxDiscount: undefined,
      totalCount: 100,
      perUserLimit: 1,
      applicableType: 1,
      description: '',
      status: 1,
      validRange: [],
    })
  }
  formVisible.value = true
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value?.validate()
    
    // 验证日期
    if (!formData.validRange || formData.validRange.length !== 2) {
      return ElMessage.warning('请选择有效期')
    }
    
    submitLoading.value = true
    
    const data = {
      id: formData.id,
      name: formData.name,
      type: formData.type,
      discountValue: formData.discountValue,
      minAmount: formData.minAmount,
      maxDiscount: formData.maxDiscount,
      totalCount: formData.totalCount,
      perUserLimit: formData.perUserLimit,
      applicableType: formData.applicableType,
      description: formData.description,
      status: formData.status,
      validStart: formData.validRange[0],
      validEnd: formData.validRange[1],
    }
    
    if (isEdit.value) {
      await couponApi.admin.update(data)
      ElMessage.success('更新成功')
    } else {
      await couponApi.admin.create(data)
      ElMessage.success('创建成功')
    }
    
    formVisible.value = false
    fetchList()
  } catch (e: any) {
    if (e !== false) {
      ElMessage.error(e.message || '操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 切换状态
async function toggleStatus(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 1 ? '下架' : '上架'}优惠券「${row.name}」吗？`,
      '提示',
      { type: 'warning' }
    )
    await couponApi.admin.toggleStatus(row.id)
    ElMessage.success('操作成功')
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

// 删除
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除优惠券「${row.name}」吗？此操作不可恢复！`,
      '危险操作',
      { type: 'error', confirmButtonText: '确认删除' }
    )
    await couponApi.admin.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-lighter, #eee);
}

.header-left {
  display: flex;
  gap: 12px;
}

.status-select {
  width: 150px;
}

.coupon-name-cell .name {
  font-weight: 500;
}

.discount-value {
  color: #f56c6c;
  font-weight: 600;
  
  &.type-2 {
    color: #e6a23c;
  }
}

.text-muted {
  color: #909399;
}

.text-success {
  color: #67c23a;
}

.date-cell {
  font-size: 13px;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 13px;
}

.admin-pagination {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(--el-border-color-lighter, #eee);
}
</style>
