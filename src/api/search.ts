import axios from './axios'

export interface SearchResult {
  spots: any[]
  hotels: any[]
  articles: any[]
}

export const searchApi = {
  global: (keyword: string, page = 1, pageSize = 20) =>
    axios.get<any, any>('/search', { params: { keyword, page, pageSize } }),

  spots: (keyword: string, limit = 10) =>
    axios.get<any, any>('/search/spots', { params: { keyword, limit } }),

  hotels: (keyword: string, limit = 10) =>
    axios.get<any, any>('/search/hotels', { params: { keyword, limit } }),

  articles: (keyword: string, limit = 10) =>
    axios.get<any, any>('/search/articles', { params: { keyword, limit } }),
}
