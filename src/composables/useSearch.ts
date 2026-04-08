import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { searchApi } from '@/api/search'

/**
 * 全局搜索组合式函数
 */
export function useSearch() {
  const router = useRouter()
  const searching = ref(false)
  const results = ref<any>(null)

  async function search(keyword: string) {
    if (!keyword.trim()) return
    searching.value = true
    try {
      const res = await searchApi.global(keyword, 1, 20)
      results.value = res.data
      return res.data
    } catch {
      results.value = null
      return null
    } finally {
      searching.value = false
    }
  }

  function goToSpot(spot: any) {
    router.push(`/spots/${spot.id}`)
  }

  function goToHotel(hotel: any) {
    router.push(`/hotels/${hotel.id}`)
  }

  function goToArticle(article: any) {
    router.push(`/articles/${article.id}`)
  }

  return {
    searching,
    results,
    search,
    goToSpot,
    goToHotel,
    goToArticle,
  }
}
