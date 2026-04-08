<template>
  <div class="admin-page admin-hotels">
    <!-- 数据卡片 -->
    <div class="data-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-input v-model="keyword" placeholder="搜索酒店名称" clearable class="admin-search" @clear="handleSearch"
            @keyup.enter="handleSearch">
            <template #prefix>
              <i class="i-mdi-magnify"></i>
            </template>
          </el-input>
          <el-select v-model="statusFilter" placeholder="状态筛选" clearable class="admin-search" @change="fetchList">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </div>
        <div class="admin-header-right">
          <el-button type="primary" @click="openForm()">
            <i class="i-mdi-plus mr-1"></i> 新增酒店
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" class="admin-cover" />
            <span v-else class="text-gray-400">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="酒店名称" min-width="150">
          <template #default="{ row }">
            <div class="font-medium">{{ row.name }}</div>
            <div class="text-sm text-gray-500">{{ row.city }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="starLevel" label="星级" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.starLevel" disabled text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openRooms(row)">
              房型管理
            </el-button>
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
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑酒店' : '新增酒店'" width="700px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="酒店名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入酒店名称" />
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
            <el-form-item label="酒店星级" prop="starLevel">
              <el-rate v-model="formData.starLevel" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="酒店设施" prop="facilities">
          <el-input v-model="formData.facilities" type="textarea" :rows="3" placeholder="如：WiFi,停车场,游泳池" />
        </el-form-item>
        <el-form-item label="酒店描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入酒店描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 房型管理弹窗 -->
    <el-dialog v-model="roomsVisible" title="房型管理" width="900px">
      <div class="room-toolbar">
        <el-button type="primary" size="small" @click="openRoomForm()">
          <i class="i-mdi-plus mr-1"></i> 新增房型
        </el-button>
      </div>
      <el-table :data="roomsData" stripe size="small">
        <el-table-column prop="name" label="房型名称" />
        <el-table-column prop="bedType" label="床型" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="text-green-600 font-medium">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxGuest" label="可住人数" width="100" />
        <el-table-column prop="totalRooms" label="房间数" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openRoomForm(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="deleteRoom(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增/编辑房型 -->
      <el-dialog v-model="roomFormVisible" :title="isRoomEdit ? '编辑房型' : '新增房型'" width="500px" append-to-body>
        <el-form ref="roomFormRef" :model="roomForm" label-width="100px">
          <el-form-item label="房型名称" required>
            <el-input v-model="roomForm.name" placeholder="如：豪华大床房" />
          </el-form-item>
          <el-form-item label="床型" required>
            <el-input v-model="roomForm.bedType" placeholder="如：大床1.8m" />
          </el-form-item>
          <el-form-item label="价格/晚" required>
            <el-input-number v-model="roomForm.price" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="可住人数" required>
            <el-input-number v-model="roomForm.maxGuest" :min="1" :max="10" />
          </el-form-item>
          <el-form-item label="房间数量" required>
            <el-input-number v-model="roomForm.totalRooms" :min="1" />
          </el-form-item>
          <el-form-item label="设施">
            <el-input v-model="roomForm.amenities" placeholder="如：WiFi,空调,电视" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="roomFormVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoomSubmit">确定</el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminHotelList, createHotel, updateHotel, deleteHotel, updateHotelStatus,
  getHotelRooms, createRoomType, updateRoomType, deleteRoomType
} from '@/api/admin'
import type { HotelDTO, RoomTypeDTO } from '@/api/admin'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<number | ''>('')
const tableData = ref<any[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const pageCount = computed(() => Math.ceil(pagination.total / pagination.pageSize) || 1)

// 酒店表单
const formVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<HotelDTO>({
  name: '', coverImage: '', city: '', address: '',
  longitude: 0, latitude: 0, description: '', starLevel: 3, facilities: '', status: 1
})
const rules: FormRules = {
  name: [{ required: true, message: '请输入酒店名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }]
}

// 房型
const roomsVisible = ref(false)
const roomsData = ref<any[]>([])
const currentHotelId = ref<number>()
const roomFormVisible = ref(false)
const isRoomEdit = ref(false)
const roomFormRef = ref<FormInstance>()
const roomForm = reactive<RoomTypeDTO>({
  hotelId: 0, name: '', price: 0, bedType: '', maxGuest: 2, totalRooms: 10, amenities: '', images: ''
})

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminHotelList({
      page: pagination.page, pageSize: pagination.pageSize,
      keyword: keyword.value || undefined, status: statusFilter.value || undefined
    })
    tableData.value = res.data?.records || res.records || []
    pagination.total = res.data?.total || res.total || 0
  } catch (e) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.page = 1; fetchList() }

function openForm(row?: any) {
  if (row) {
    isEdit.value = true
    Object.assign(formData, row)
  } else {
    isEdit.value = false
    Object.assign(formData, { name: '', coverImage: '', city: '', address: '', longitude: 0, latitude: 0, description: '', starLevel: 3, facilities: '', status: 1 })
  }
  formVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await updateHotel(formData.id!, formData)
      ElMessage.success('更新成功')
    } else {
      await createHotel(formData)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    fetchList()
  } catch (e: any) {
    if (e?.message) ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(`确定要${newStatus === 1 ? '上架' : '下架'}该酒店吗？`, '提示', { type: 'warning' })
    await updateHotelStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该酒店吗？', '警告', { type: 'warning', confirmButtonText: '删除' })
    await deleteHotel(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

async function openRooms(row: any) {
  currentHotelId.value = row.id
  roomsVisible.value = true
  await fetchRooms()
}

async function fetchRooms() {
  if (!currentHotelId.value) return
  try {
    const res: any = await getHotelRooms(currentHotelId.value)
    roomsData.value = res.data || res || []
  } catch (e) {
    ElMessage.error('获取房型失败')
  }
}

function openRoomForm(row?: any) {
  if (row) {
    isRoomEdit.value = true
    Object.assign(roomForm, row)
  } else {
    isRoomEdit.value = false
    Object.assign(roomForm, { hotelId: currentHotelId.value, name: '', price: 0, bedType: '', maxGuest: 2, totalRooms: 10, amenities: '', images: '' })
  }
  roomFormVisible.value = true
}

async function handleRoomSubmit() {
  try {
    roomForm.hotelId = currentHotelId.value!
    if (isRoomEdit.value) {
      await updateRoomType(roomForm.id!, roomForm)
    } else {
      await createRoomType(roomForm)
    }
    ElMessage.success('操作成功')
    roomFormVisible.value = false
    fetchRooms()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function deleteRoom(row: any) {
  try {
    await ElMessageBox.confirm('确定要删除该房型吗？', '警告', { type: 'warning' })
    await deleteRoomType(row.id)
    ElMessage.success('删除成功')
    fetchRooms()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => { fetchList() })
</script>

<style scoped lang="scss">
.admin-hotels {
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
