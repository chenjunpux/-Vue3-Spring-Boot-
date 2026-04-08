<template>
  <div class="admin-page">
    <!-- 数据卡片 -->
    <div class="admin-card">
      <!-- 操作栏 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-select v-model="statusFilter" placeholder="审核状态" clearable class="admin-search" @change="fetchList">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="admin-table">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="封面" width="100">
            <template #default="{ row }">
              <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" class="admin-cover" />
              <span v-else class="text-gray-400">暂无</span>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <div class="font-medium">{{ row.title }}</div>
              <div class="text-sm text-gray-500 truncate max-w-xs">{{ row.summary }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column prop="isTop" label="置顶" width="80">
            <template #default="{ row }">
              <el-tag :type="row.isTop ? 'warning' : 'info'" size="small">{{ row.isTop ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="170">
            <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="showDetail(row)">查看</el-button>
              <el-button v-if="row.status === 0" type="success" text size="small" @click="audit(row, 1)">通过</el-button>
              <el-button v-if="row.status === 0" type="danger" text size="small" @click="audit(row, 2)">拒绝</el-button>
              <el-button :type="row.isTop ? 'warning' : 'primary'" text size="small" @click="toggleTop(row)">
                {{ row.isTop ? '取消置顶' : '置顶' }}
              </el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="游记详情" width="700px">
      <div v-if="currentArticle" class="article-detail">
        <h2>{{ currentArticle.title }}</h2>
        <div class="meta">
          <span>作者：{{ currentArticle.authorName }}</span>
          <span>发布时间：{{ formatDate(currentArticle.createdAt) }}</span>
          <span>浏览：{{ currentArticle.viewCount }}</span>
          <span>点赞：{{ currentArticle.likeCount }}</span>
        </div>
        <el-image v-if="currentArticle.coverImage" :src="currentArticle.coverImage" class="detail-cover" />
        <div class="content">{{ currentArticle.content || currentArticle.summary || '暂无内容' }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminArticleList, updateArticleStatus, toggleArticleTop } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const statusFilter = ref<number | ''>('')
const tableData = ref<any[]>([])
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })

const detailVisible = ref(false)
const currentArticle = ref<any>(null)

const statusMap: Record<number, { text: string; type: string }> = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已通过', type: 'success' },
  2: { text: '已拒绝', type: 'danger' },
}

function getStatusType(status: number) { return statusMap[status]?.type || 'info' }
function getStatusText(status: number) { return statusMap[status]?.text || '未知' }
function formatDate(dateStr: string) { return dateStr ? dayjs(dateStr).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminArticleList({
      page: pagination.page, pageSize: pagination.pageSize,
      status: statusFilter.value || undefined
    })
    tableData.value = res.data?.records || res.records || []
    pagination.total = res.data?.total || res.total || 0
  } catch (e) { ElMessage.error('获取列表失败') } finally { loading.value = false }
}

function showDetail(row: any) { currentArticle.value = row; detailVisible.value = true }

async function audit(row: any, status: number) {
  try {
    await ElMessageBox.confirm(`确定要${status === 1 ? '通过' : '拒绝'}该游记吗？`, '审核确认', { type: 'warning' })
    await updateArticleStatus(row.id, status)
    ElMessage.success('操作成功')
    fetchList()
  } catch (e: any) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

async function toggleTop(row: any) {
  try {
    await ElMessageBox.confirm(`确定要${row.isTop ? '取消置顶' : '置顶'}该游记吗？`, '提示', { type: 'warning' })
    await toggleArticleTop(row.id)
    ElMessage.success('操作成功')
    fetchList()
  } catch (e: any) { if (e !== 'cancel') ElMessage.error('操作失败') }
}

onMounted(() => { fetchList() })
</script>

<style scoped lang="scss">
.article-detail {
  h2 { font-size: 20px; font-weight: 600; margin-bottom: 12px; }
  .meta { display: flex; gap: 16px; color: #6b7280; font-size: 14px; margin-bottom: 16px; }
  .detail-cover { width: 100%; max-height: 300px; object-fit: cover; border-radius: 8px; margin-bottom: 16px; }
  .content { line-height: 1.8; color: #374151; }
}
</style>
