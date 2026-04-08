import request from './axios'

export interface Spot {
  id: number
  name: string
  coverImage: string
  city: string
  address: string
  longitude: number
  latitude: number
  description: string
  ticketPrice: number
  openTime: string
  suggestedTime: number
  level: string
  tags: string[]
  hotScore: number
  viewCount: number
  status: number
  createdAt: string
  images?: string[]
}

export interface SpotListParams {
  page: number
  pageSize: number
  city?: string
  keyword?: string
  tags?: string
  minPrice?: number
  maxPrice?: number
  sortBy?: 'hotScore' | 'ticketPrice' | 'createdAt'
  sortOrder?: 'asc' | 'desc'
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 景点列表
export function getSpotListApi(params: SpotListParams) {
  return request.get('/spot/list', { params }) as Promise<{ code: number; msg: string; data: PageResult<Spot> }>
}

// 热门景点
export function getHotSpotsApi(limit = 10) {
  return request.get('/spot/hot', { params: { limit } }) as Promise<{ code: number; msg: string; data: Spot[] }>
}

// 景点详情
export function getSpotDetailApi(id: number) {
  return request.get(`/spot/${id}`) as Promise<{ code: number; msg: string; data: Spot }>
}

// 附近景点
export function getNearbySpotsApi(params: { longitude: number; latitude: number; radius?: number }) {
  return request.get('/spot/nearby', { params }) as Promise<{ code: number; msg: string; data: Spot[] }>
}

// 收藏景点
export function favoriteSpotApi(id: number) {
  return request.post(`/spot/${id}/favorite`) as Promise<{ code: number; msg: string }>
}

// 取消收藏
export function unfavoriteSpotApi(id: number) {
  return request.delete(`/spot/${id}/favorite`) as Promise<{ code: number; msg: string }>
}

// 我的收藏
export function getMyFavoritesApi(params: { page: number; pageSize: number }) {
  return request.get('/spot/favorites', { params }) as Promise<{ code: number; msg: string; data: PageResult<Spot> }>
}

// 景点评论
export interface SpotComment {
  id: number
  userId: number
  userAvatar: string
  userNickname: string
  content: string
  rating: number
  likeCount: number
  createdAt: string
  replies?: SpotComment[]
}

export function getSpotCommentsApi(spotId: number, params: { page: number; pageSize: number }) {
  return request.get(`/spot/${spotId}/comments`, { params }) as Promise<{ code: number; msg: string; data: PageResult<SpotComment> }>
}

export function commentSpotApi(spotId: number, data: { content: string; rating: number }) {
  return request.post(`/spot/${spotId}/comment`, data) as Promise<{ code: number; msg: string }>
}
