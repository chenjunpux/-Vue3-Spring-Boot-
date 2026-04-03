<template>
  <div class="admin-articles">
    <h2 class="page-title">📝 游记管理</h2>

    <div class="admin-filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" clearable style="width: 130px">
            <el-option label="待审核" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="filterForm.page = 1; fetchData()">搜索</el-button>
          <el-button @click="Object.assign(filterForm, { status: undefined, page: 1 }); fetchData()">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="admin-content-card admin-table">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" style="width:60px;height:40px;border-radius:4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="userId" label="作者ID" width="100" />
        <el-table-column prop="tags" label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-for="tag in (row.tags || '').split(',').filter(Boolean)" :key="tag" size="small" style="margin-right: 4px">{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" size="small">{{ statusMap[row.status]?.text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isTop === 1 ? 'warning' : 'info'" size="small">{{ row.isTop === 1 ? '置顶' : '普通' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">查看</el-button>
            <el-button size="small" v-if="row.status === 0" type="success" @click="handleAudit(row, 1)">通过</el-button>
            <el-button size="small" v-if="row.status !== 2" type="danger" plain @click="handleAudit(row, 2)">下架</el-button>
            <el-button size="small" type="warning" plain @click="handleTop(row)">{{ row.isTop === 1 ? '取消置顶' : '置顶' }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background layout="total, prev, pager, next"
          :total="total" :current-page="filterForm.page" :page-size="filterForm.pageSize"
          @current-change="p => { filterForm.page = p; fetchData() }"
        />
      </div>
    </div>

    <!-- 游记详情对话框 -->
    <el-dialog v-model="detailVisible" title="游记详情" width="800px">
      <div v-if="currentArticle" class="article-detail">
        <div class="detail-header">
          <h3>{{ currentArticle.title }}</h3>
          <div class="meta">
            <span>作者ID: {{ currentArticle.userId }}</span>
            <span>浏览: {{ currentArticle.viewCount }}</span>
            <span>点赞: {{ currentArticle.likeCount }}</span>
            <span>评论: {{ currentArticle.commentCount }}</span>
            <span>{{ formatDate(currentArticle.createdAt) }}</span>
          </div>
        </div>
        <div v-if="currentArticle.coverImage" style="margin: 16px 0">
          <el-image :src="currentArticle.coverImage" fit="cover" style="width:100%;max-height:300px;border-radius:8px;" />
        </div>
        <div class="article-content" v-html="currentArticle.content"></div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentArticle && currentArticle.status === 0" type="success" @click="handleAudit(currentArticle, 1); detailVisible = false">通过审核</el-button>
        <el-button v-if="currentArticle && currentArticle.status !== 2" type="danger" @click="handleAudit(currentArticle, 2); detailVisible = false">下架</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminArticleList, updateArticleStatus, toggleArticleTop } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentArticle = ref<any>(null)
const filterForm = reactive({ page: 1, pageSize: 10, status: undefined as number | undefined })

const statusMap: Record<number, { text: string; type: string }> = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '已发布', type: 'success' },
  2: { text: '已下架', type: 'info' },
}

function formatDate(str?: string) {
  return str ? dayjs(str).format('YYYY-MM-DD HH:mm:ss') : '—'
}

async function fetchData() {
  try {
    loading.value = true
    const res: any = await getAdminArticleList(filterForm)
    const data = res.data || res
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function openDetail(row: any) {
  currentArticle.value = row
  detailVisible.value = true
}

async function handleAudit(row: any, status: number) {
  try {
    await updateArticleStatus(row.id, status)
    const statusText = status === 1 ? '审核通过' : status === 2 ? '已下架' : '已发布'
    ElMessage.success(statusText)
    fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleTop(row: any) {
  try {
    await toggleArticleTop(row.id)
    ElMessage.success(row.isTop === 1 ? '已取消置顶' : '已置顶')
    fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

fetchData()
</script>

<style scoped lang="scss">
.admin-articles {
  .page-title { font-size: 20px; font-weight: 700; color: #1e293b; margin-bottom: 20px; }
  .filter-card { margin-bottom: 16px; }
  .pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
  .article-detail {
    .detail-header {
      h3 { font-size: 18px; margin-bottom: 12px; }
      .meta { display: flex; gap: 16px; color: #666; font-size: 13px; margin-bottom: 16px; }
    }
    .article-content { line-height: 1.8; color: #333; }
  }
}
</style>
