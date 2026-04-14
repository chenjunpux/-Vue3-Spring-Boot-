<template>
  <div class="admin-page">
    <div class="bg-base-100 rounded-lg shadow border border-base-300 overflow-hidden">

      <!-- 操作栏 -->
      <div class="flex items-center flex-wrap gap-3 p-4 border-b border-base-300">
        <select v-model="statusFilter" class="select select-bordered w-36" @change="page = 1; fetchList()">
          <option value="">全部状态</option>
          <option :value="0">待审核</option>
          <option :value="1">已通过</option>
          <option :value="2">已拒绝</option>
        </select>
      </div>

      <!-- 加载 -->
      <div v-if="loading" class="flex items-center justify-center p-8">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <!-- 表格 -->
      <div v-else class="overflow-x-auto">
        <table class="table table-zebra w-full text-sm">
          <thead>
            <tr>
              <th>ID</th><th>序号</th><th>封面</th><th>标题</th><th>作者</th><th>浏览</th><th>点赞</th><th>置顶</th><th>状态</th><th>发布时间</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in tableData" :key="row.id">
              <td class="font-mono">{{ row.id }}</td>
              <td>{{ (page - 1) * pageSize + idx + 1 }}</td>
              <td>
                <div class="avatar"><div class="w-12 rounded">
                    <img v-if="row.coverImage" :src="row.coverImage" :alt="row.title" />
                    <div v-else class="bg-base-300 w-full h-full flex items-center justify-center text-xs">无</div>
                </div></div>
              </td>
              <td>
                <div class="font-medium max-w-xs truncate">{{ row.title }}</div>
                <div class="text-xs opacity-60 max-w-xs truncate">{{ row.summary }}</div>
              </td>
              <td>{{ row.authorName }}</td>
              <td>{{ row.viewCount }}</td>
              <td>{{ row.likeCount }}</td>
              <td>
                <span :class="row.isTop ? 'badge-warning' : 'badge-neutral'" class="badge badge-sm">
                  {{ row.isTop ? '是' : '否' }}
                </span>
              </td>
              <td>
                <span :class="getStatusBadgeClass(row.status)" class="badge badge-sm">
                  {{ getStatusText(row.status) }}
                </span>
              </td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>
                <div class="flex gap-1 flex-wrap">
                  <button class="btn btn-primary btn-xs" @click="showDetail(row)">查看</button>
                  <button v-if="row.status === 0" class="btn btn-success btn-xs" @click="audit(row, 1)">通过</button>
                  <button v-if="row.status === 0" class="btn btn-error btn-xs" @click="audit(row, 2)">拒绝</button>
                  <button :class="row.isTop ? 'btn-warning' : 'btn-primary'" class="btn btn-xs" @click="toggleTop(row)">
                    {{ row.isTop ? '取消置顶' : '置顶' }}
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="tableData.length === 0">
              <td colspan="10" class="text-center py-8 text-neutral/50">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="flex items-center justify-between p-4 border-t border-base-300 flex-wrap gap-2">
        <span class="text-sm text-neutral/60">共 {{ total }} 条</span>
        <div class="join">
          <button class="join-item btn btn-sm" :disabled="page <= 1" @click="page--; fetchList()">«</button>
          <button class="join-item btn btn-sm disabled">第 {{ page }} / {{ totalPages || 1 }} 页</button>
          <button class="join-item btn btn-sm" :disabled="page >= (totalPages || 1)" @click="page++; fetchList()">»</button>
        </div>
      </div>
    </div>

    <!-- 详情 Modal -->
    <dialog id="article_detail_modal" class="modal">
      <div class="modal-box max-w-2xl">
        <form method="dialog">
          <button class="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
        </form>
        <h3 class="font-bold text-lg mb-2">{{ currentArticle?.title }}</h3>
        <div class="flex gap-4 text-sm text-neutral/60 mb-4 flex-wrap">
          <span>作者：{{ currentArticle?.authorName }}</span>
          <span>浏览：{{ currentArticle?.viewCount }}</span>
          <span>点赞：{{ currentArticle?.likeCount }}</span>
          <span>{{ formatDate(currentArticle?.createdAt) }}</span>
        </div>
        <img v-if="currentArticle?.coverImage" :src="currentArticle.coverImage" class="w-full h-48 object-cover rounded-lg mb-4" />
        <div class="prose prose-sm max-w-none">{{ currentArticle?.content || currentArticle?.summary || '暂无内容' }}</div>
      </div>
      <form method="dialog" class="modal-backdrop"><button>close</button></form>
    </dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAdminArticleList, updateArticleStatus, toggleArticleTop } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const statusFilter = ref<number | string>('')
const tableData = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)
const currentArticle = ref<any>(null)

const statusMap: Record<number, { text: string; cls: string }> = {
  0: { text: '待审核', cls: 'badge-warning' },
  1: { text: '已通过', cls: 'badge-success' },
  2: { text: '已拒绝', cls: 'badge-error' },
}

function getStatusBadgeClass(s: number) { return statusMap[s]?.cls || 'badge-neutral' }
function getStatusText(s: number) { return statusMap[s]?.text || '未知' }
function formatDate(d: string) { return d ? dayjs(d).format('YYYY-MM-DD HH:mm') : '-' }

async function fetchList() {
  try {
    loading.value = true
    const res: any = await getAdminArticleList({
      page: page.value, pageSize: pageSize.value,
      status: statusFilter.value !== '' ? Number(statusFilter.value) : undefined,
    })
    tableData.value = res.data?.records || res.records || []
    total.value = res.data?.total || res.total || 0
  } catch { console.error('获取列表失败') } finally { loading.value = false }
}

function showDetail(row: any) {
  currentArticle.value = row
  const modal = document.getElementById('article_detail_modal') as HTMLDialogElement
  modal?.showModal()
}

async function audit(row: any, status: number) {
  if (!confirm(`确定要${status === 1 ? '通过' : '拒绝'}该游记吗？`)) return
  try { await updateArticleStatus(row.id, status); fetchList() }
  catch { window.adminToast('操作失败', 'error') }
}

async function toggleTop(row: any) {
  if (!confirm(`确定要${row.isTop ? '取消置顶' : '置顶'}该游记吗？`)) return
  try { await toggleArticleTop(row.id); fetchList() }
  catch { window.adminToast('操作失败', 'error') }
}

onMounted(() => { fetchList() })
</script>
