import request from './axios'

export interface Hotel {
  minPrice: number
  id: number
  name: string
  coverImage: string
  city: string
  address: string
  longitude: number
  latitude: number
  description: string
  starLevel: number
  facilities: string
  hotScore: number
  status: number
  createdAt: string
}

export interface RoomType {
  id: number
  hotelId: number
  name: string
  price: number
  bedType: string
  maxGuest: number
  totalRooms: number
  amenities: string
  images: string
  createdAt: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

// 酒店列表
export function getHotelListApi(params: { page: number; pageSize: number; city?: string; keyword?: string; status?: number }) {
  return request.get('/hotel/list', { params }) as Promise<{ code: number; msg: string; data: PageResult<Hotel> }>
}

// 热门酒店
export function getHotHotelsApi(limit = 10) {
  return request.get('/hotel/hot', { params: { limit } }) as Promise<{ code: number; msg: string; data: Hotel[] }>
}

// 酒店详情
export function getHotelDetailApi(id: number) {
  return request.get(`/hotel/${id}`) as Promise<{ code: number; msg: string; data: Hotel }>
}

// 酒店房型
export function getHotelRoomsApi(id: number) {
  return request.get(`/hotel/${id}/rooms`) as Promise<{ code: number; msg: string; data: RoomType[] }>
}
