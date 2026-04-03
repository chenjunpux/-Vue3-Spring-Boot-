import request from './axios'

export interface Article {
  id: number
  userId: number
  title: string
  coverImage: string
  content: string
  tags: string
  viewCount: number
  likeCount: number
  collectCount: number
  commentCount: number
  status: number
  isTop: number
  createdAt: string
}

// 游记列表
export function getArticleListApi(params: { page: number; pageSize: number; status?: number }) {
  return request.get('/article/list', { params })
}

// 热门游记
export function getHotArticlesApi(limit = 10) {
  return request.get('/article/hot', { params: { limit } })
}

// 游记详情
export function getArticleDetailApi(id: number) {
  return request.get(`/article/${id}`)
}

// 发布游记
export function createArticleApi(data: Partial<Article>) {
  return request.post('/article', data)
}

// 我的游记
export function getMyArticlesApi(params: { page: number; pageSize: number }) {
  return request.get('/article/my', { params })
}

// 点赞游记
export function likeArticleApi(id: number) {
  return request.post(`/article/${id}/like`)
}
