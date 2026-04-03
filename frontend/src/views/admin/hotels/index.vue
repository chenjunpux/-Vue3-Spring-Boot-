<template>
  <div class="admin-hotels">
    <div class="page-header">
      <h2 class="page-title">🏨 酒店管理</h2>
      <el-button type="primary" @click="openHotelDialog('create')">+ 添加酒店</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="admin-filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="城市">
          <el-input v-model="filterForm.city" placeholder="城市名称" clearable />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="filterForm.keyword" placeholder="酒店名称" clearable />
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
        <el-table-column prop="name" label="酒店名称" min-width="150" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="starLevel" label="星级" width="80">
          <template #default="{ row }">{{ '⭐'.repeat(row.starLevel || 3) }}</template>
        </el-table-column>
        <el-table-column prop="hotScore" label="热度" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openHotelDialog('edit', row)">编辑</el-button>
            <el-button size="small" @click="openRoomDialog(row)">房型管理</el-button>
            <el-button size="small" type="danger" plain @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确认删除该酒店？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 酒店对话框 -->
    <el-dialog v-model="hotelDialogVisible" :title="hotelDialogTitle" width="680px" destroy-on-close>
      <el-form :model="hotelForm" :rules="hotelRules" ref="hotelFormRef" label-width="100px">
        <el-form-item label="酒店名称" prop="name">
          <el-input v-model="hotelForm.name" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-form-item label="封面图URL" prop="coverImage">
          <el-input v-model="hotelForm.coverImage" placeholder="图片URL" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="城市" prop="city">
              <el-input v-model="hotelForm.city" placeholder="如：北京" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="星级" prop="starLevel">
              <el-rate v-model="hotelForm.starLevel" :max="5" show-text :texts="['一星','二星','三星','四星','五星']" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="hotelForm.address" placeholder="详细地址" />
        </el-form-item>
        <el-form-item label="酒店设施">
          <el-input v-model="hotelForm.facilities" placeholder="多个设施用逗号分隔，如：WiFi,游泳池,健身房" />
        </el-form-item>
        <el-form-item label="酒店描述">
          <el-input v-model="hotelForm.description" type="textarea" :rows="3" placeholder="酒店描述..." />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input-number v-model="hotelForm.longitude" :precision="6" :min="-180" :max="180" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number v-model="hotelForm.latitude" :precision="6" :min="-90" :max="90" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="hotelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleHotelSubmit" :loading="submitLoading">确认</el-button>
      </template>
    </el-dialog>

    <!-- 房型管理对话框 -->
    <el-dialog v-model="roomDialogVisible" title="房型管理" width="900px" destroy-on-close>
      <div class="room-header">
        <span>{{ currentHotel?.name }} - 房型列表</span>
        <el-button size="small" type="primary" @click="openRoomEditDialog('create')">+ 添加房型</el-button>
      </div>
      <el-table :data="roomList" size="small" style="margin-top: 12px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="房型名称" min-width="120" />
        <el-table-column prop="bedType" label="床型" width="100" />
        <el-table-column prop="price" label="价格/晚" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="maxGuest" label="最大入住" width="100" />
        <el-table-column prop="totalRooms" label="房间数" width="80" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openRoomEditDialog('edit', row)">编辑</el-button>
            <el-popconfirm title="确认删除该房型？" @confirm="deleteRoom(row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 房型编辑对话框 -->
      <el-dialog v-model="roomEditVisible" :title="roomEditTitle" width="500px" append-to-body destroy-on-close>
        <el-form :model="roomForm" ref="roomFormRef" label-width="100px">
          <el-form-item label="房型名称" prop="name">
            <el-input v-model="roomForm.name" placeholder="如：豪华大床房" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="价格/晚">
                <el-input-number v-model="roomForm.price" :min="0" :precision="2" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="床型">
                <el-input v-model="roomForm.bedType" placeholder="如：大床1.8m" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="最大入住">
                <el-input-number v-model="roomForm.maxGuest" :min="1" :max="10" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="房间数量">
                <el-input-number v-model="roomForm.totalRooms" :min="1" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="设施服务">
            <el-input v-model="roomForm.amenities" placeholder="多个设施用逗号分隔" />
          </el-form-item>
          <el-form-item label="房型图片">
            <el-input v-model="roomForm.images" placeholder="图片URL，多个用逗号分隔" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="roomEditVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRoomSubmit" :loading="submitLoading">确认</el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminHotelList, createHotel, updateHotel, deleteHotel, updateHotelStatus,
  getHotelRooms, createRoomType, updateRoomType, deleteRoomType,
  type HotelDTO, type RoomTypeDTO } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)

// 酒店相关
const hotelDialogVisible = ref(false)
const hotelDialogMode = ref<'create' | 'edit'>('create')
const currentHotel = ref<any>(null)
const tableData = ref<any[]>([])
const total = ref(0)

const filterForm = reactive({ page: 1, pageSize: 10, city: '', keyword: '', status: undefined as number | undefined })

const defaultHotelForm = (): HotelDTO => ({
  name: '', coverImage: '', city: '', address: '',
  longitude: 0, latitude: 0, description: '',
  starLevel: 3, facilities: '', status: 1,
})
const hotelForm = reactive<HotelDTO>(defaultHotelForm())
const hotelDialogTitle = computed(() => hotelDialogMode.value === 'create' ? '添加酒店' : '编辑酒店')
const hotelRules = {
  name: [{ required: true, message: '请输入酒店名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
}
const hotelFormRef = ref()

// 房型相关
const roomDialogVisible = ref(false)
const roomEditVisible = ref(false)
const roomEditMode = ref<'create' | 'edit'>('create')
const roomList = ref<any[]>([])
const roomDialogTitle = computed(() => `${currentHotel.value?.name} - 房型管理`)
const roomEditTitle = computed(() => roomEditMode.value === 'create' ? '添加房型' : '编辑房型')

const defaultRoomForm = (hotelId: number): RoomTypeDTO => ({
  hotelId, name: '', price: 0, bedType: '', maxGuest: 2, totalRooms: 10, amenities: '', images: '',
})
const roomForm = reactive<RoomTypeDTO>(defaultRoomForm(0))
const roomFormRef = ref()

async function fetchData() {
  try {
    loading.value = true
    const res: any = await getAdminHotelList(filterForm)
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function handleSearch() { filterForm.page = 1; fetchData() }
function handleReset() { Object.assign(filterForm, { city: '', keyword: '', status: undefined, page: 1 }); fetchData() }
function handlePageChange(page: number) { filterForm.page = page; fetchData() }

function openHotelDialog(mode: 'create' | 'edit', row?: any) {
  hotelDialogMode.value = mode
  if (mode === 'edit' && row) {
    Object.assign(hotelForm, { id: row.id, name: row.name, coverImage: row.coverImage || '', city: row.city, address: row.address || '', longitude: row.longitude || 0, latitude: row.latitude || 0, description: row.description || '', starLevel: row.starLevel || 3, facilities: row.facilities || '', status: row.status })
  } else {
    Object.assign(hotelForm, defaultHotelForm())
  }
  hotelDialogVisible.value = true
}

async function handleHotelSubmit() {
  try {
    await hotelFormRef.value.validate()
    submitLoading.value = true
    if (hotelDialogMode.value === 'create') { await createHotel(hotelForm); ElMessage.success('添加成功') }
    else { await updateHotel(hotelForm.id!, hotelForm); ElMessage.success('更新成功') }
    hotelDialogVisible.value = false
    fetchData()
  } catch (e: any) { if (e !== false) ElMessage.error(e?.message || '操作失败') }
  finally { submitLoading.value = false }
}

async function toggleStatus(row: any) {
  try {
    await updateHotelStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success(row.status === 1 ? '已下架' : '已上架')
    fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(id: number) {
  try { await deleteHotel(id); ElMessage.success('删除成功'); fetchData() } catch (e) { ElMessage.error('删除失败') }
}

async function openRoomDialog(row: any) {
  currentHotel.value = row
  roomDialogVisible.value = true
  await loadRooms(row.id)
}

async function loadRooms(hotelId: number) {
  try {
    const res: any = await getHotelRooms(hotelId)
    roomList.value = (res.data || res) || []
  } catch (e) { console.error(e) }
}

function openRoomEditDialog(mode: 'create' | 'edit', row?: any) {
  roomEditMode.value = mode
  if (mode === 'edit' && row) {
    Object.assign(roomForm, { id: row.id, hotelId: currentHotel.value.id, name: row.name, price: row.price, bedType: row.bedType || '', maxGuest: row.maxGuest || 2, totalRooms: row.totalRooms || 10, amenities: row.amenities || '', images: row.images || '' })
  } else {
    Object.assign(roomForm, defaultRoomForm(currentHotel.value.id))
  }
  roomEditVisible.value = true
}

async function handleRoomSubmit() {
  try {
    submitLoading.value = true
    if (roomEditMode.value === 'create') { await createRoomType(roomForm); ElMessage.success('添加成功') }
    else { await updateRoomType(roomForm.id!, roomForm); ElMessage.success('更新成功') }
    roomEditVisible.value = false
    await loadRooms(currentHotel.value.id)
  } catch (e: any) { ElMessage.error(e?.message || '操作失败') }
  finally { submitLoading.value = false }
}

async function deleteRoom(id: number) {
  try { await deleteRoomType(id); ElMessage.success('删除成功'); await loadRooms(currentHotel.value.id) }
  catch (e) { ElMessage.error('删除失败') }
}

fetchData()
</script>

<style scoped lang="scss">
.admin-hotels {
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    .page-title { font-size: 20px; font-weight: 700; color: #1e293b; margin: 0; }
  }
  .filter-card { margin-bottom: 16px; }
  .pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
  .room-header { display: flex; align-items: center; justify-content: space-between; }
}
</style>
