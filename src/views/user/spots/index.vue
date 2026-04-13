<template>
  <div class="spots-page">

    <!-- 顶部英雄区 -->
    <div class="spots-hero" ref="spotsHeroRef">
      <div class="hero-bg-overlay" />
      <div class="hero-content">
        <span class="hero-eyebrow" ref="spotsEyebrowRef">🗺️ 发现风景</span>
        <h1 class="hero-title-gsap" ref="spotsTitleRef">精选景点</h1>
        <p class="hero-sub" ref="spotsSubRef">覆盖全国热门目的地，带你发现绝美风景</p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar" ref="filterBarRef">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索景点名称"
        size="large"
        clearable
        @input="handleFilter"
        style="width: 260px;"
      >
        <template #prefix><span i="ep-search" /></template>
      </el-input>

      <el-select v-model="filters.city" placeholder="目的地" clearable size="large" @change="handleFilter" style="width: 160px;">
        <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
      </el-select>

      <el-select v-model="filters.sortBy" size="large" @change="handleFilter" style="width: 160px;">
        <el-option label="综合排序" value="" />
        <el-option label="热度最高" value="hotScore" />
        <el-option label="价格最低" value="ticketPrice" />
      </el-select>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 景点列表 -->
    <div v-else-if="list.length > 0" class="spots-grid" ref="spotsGridRef">
      <div
        v-for="spot in list"
        :key="spot.id"
        class="spot-card gsap-spot-card"
        @click="$router.push(`/spots/${spot.id}`)"
      >
        <!-- 玻璃拟态封面 -->
        <div class="spot-cover">
          <img :src="spot.coverImage || 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80'" :alt="spot.name" />
          <!-- 玻璃拟态悬浮层 -->
          <div class="spot-glass-overlay">
            <span class="glass-price">¥{{ spot.ticketPrice }}</span>
            <span class="glass-level">{{ spot.level || '热门景点' }}</span>
          </div>
        </div>

        <div class="spot-info">
          <h3>{{ spot.name }}</h3>
          <p class="spot-address">📍 {{ spot.city }} · {{ spot.address }}</p>
          <div class="spot-tags">
            <span v-for="tag in (spot.tags || '').slice(0, 3)" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="spot-footer">
            <span class="hot-score">🔥 {{ spot.hotScore || 0 }}</span>
            <span class="open-time">{{ spot.openTime || '全天开放' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-else description="暂无景点数据" />

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadList"
        background
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { splitTextByWord } from '@/composables/useGsapAnimation'
import { getSpotListApi } from '@/api/spot'

gsap.registerPlugin(ScrollTrigger)

const route = useRoute()
const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 16

// Refs
const spotsHeroRef = ref<HTMLElement | null>(null)
const spotsEyebrowRef = ref<HTMLElement | null>(null)
const spotsTitleRef = ref<HTMLElement | null>(null)
const spotsSubRef = ref<HTMLElement | null>(null)
const filterBarRef = ref<HTMLElement | null>(null)
const spotsGridRef = ref<HTMLElement | null>(null)

const cityOptions = ['北京', '上海', '杭州', '成都', '西安', '厦门', '广州', '深圳', '张家界', '桂林', '阿坝', '湘西', '拉萨', '甘孜', '黄山', '苏州', '泰安']

const filters = reactive({
  keyword: (route.query.keyword as string) || '',
  city: (route.query.city as string) || '',
  sortBy: '',
})

// 从后端API获取
const allSpots = ref<any[]>([])

async function loadList() {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize,
      keyword: filters.keyword || undefined,
      city: filters.city || undefined,
    }
    if (filters.sortBy) {
      params.sortBy = filters.sortBy
      params.sortOrder = 'desc'
    }

    const res: any = await getSpotListApi(params)
    const data = res.data || res
    allSpots.value = data.records || []
    total.value = data.total || 0
    list.value = allSpots.value

    nextTick(() => {
      setTimeout(() => animateSpotsGrid(), 100)
    })
  } catch (e) {
    console.error('加载景点列表失败', e)
  } finally {
    loading.value = false
  }
}

let debounceTimer: ReturnType<typeof setTimeout>
function handleFilter() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    currentPage.value = 1
    loadList()
  }, 400)
}

function initGsapAnimations() {
  // Hero 区入场
  const tl = gsap.timeline({ delay: 0.2 })
  if (spotsEyebrowRef.value) {
    tl.fromTo(spotsEyebrowRef.value,
      { opacity: 0, y: 20, filter: 'blur(8px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.7, ease: 'power3.out' }
    )
  }
  if (spotsTitleRef.value) {
    const words = splitTextByWord(spotsTitleRef.value)
    tl.fromTo(words,
      { yPercent: 120, opacity: 0, filter: 'blur(6px)' },
      { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.04 },
      '-=0.4'
    )
  }
  if (spotsSubRef.value) {
    tl.fromTo(spotsSubRef.value,
      { opacity: 0, y: 20 },
      { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' },
      '-=0.5'
    )
  }

  // 筛选栏滑入
  if (filterBarRef.value) {
    gsap.fromTo(filterBarRef.value,
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.7, ease: 'power3.out', delay: 0.3,
        scrollTrigger: { trigger: filterBarRef.value, start: 'top 90%', once: true } }
    )
  }

  // 卡片动画（初始调用，加载后重新触发）
  animateSpotsGrid()
}

function animateSpotsGrid() {
  if (!spotsGridRef.value) return
  const cards = spotsGridRef.value.querySelectorAll<HTMLElement>('.gsap-spot-card')
  if (!cards.length) return

  // 入场：从底部交错滑入
  gsap.fromTo(Array.from(cards),
    { opacity: 0, y: 70, scale: 0.93 },
    {
      opacity: 1, y: 0, scale: 1,
      duration: 0.85, ease: 'power3.out', stagger: 0.08,
      scrollTrigger: { trigger: spotsGridRef.value, start: 'top 82%', once: true },
    }
  )

  // 每个卡片的 hover 视差
  cards.forEach((card) => {
    const img = card.querySelector<HTMLElement>('.spot-cover img')
    if (!img) return
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect()
      const x = (e.clientX - rect.left) / rect.width - 0.5
      const y = (e.clientY - rect.top) / rect.height - 0.5
      gsap.to(img, { xPercent: x * 8, yPercent: y * 8, duration: 0.6, ease: 'power2.out' })
    })
    card.addEventListener('mouseleave', () => {
      gsap.to(img, { xPercent: 0, yPercent: 0, duration: 0.6, ease: 'power2.out' })
    })
  })
}

onMounted(() => {
  loadList()
  initGsapAnimations()
})

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped lang="scss">
.spots-page {
  min-height: 100vh;
  background: #f8fafc;

  /* ── Hero ── */
  .spots-hero {
    position: relative;
    height: 340px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0f0f23 0%, #1e1b4b 50%, #312e81 100%);
    overflow: hidden;

    .hero-bg-overlay {
      position: absolute;
      inset: 0;
      background: url('https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=60') center/cover no-repeat;
      opacity: 0.25;
    }

    .hero-content {
      position: relative;
      z-index: 2;
      text-align: center;
      padding: 0 24px;
    }

    .hero-eyebrow {
      display: inline-block;
      font-size: 14px;
      color: rgba(255,255,255,0.6);
      letter-spacing: 3px;
      text-transform: uppercase;
      margin-bottom: 16px;
    }

    .hero-title-gsap {
      font-size: clamp(48px, 7vw, 90px);
      font-weight: 900;
      color: white;
      margin-bottom: 16px;
    }

    .hero-sub {
      font-size: 18px;
      color: rgba(255,255,255,0.55);
    }
  }

  /* ── 筛选栏 ── */
  .filter-bar {
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px 0;
    display: flex;
    gap: 16px;
    align-items: center;
    flex-wrap: wrap;
  }

  /* ── Grid ── */
  .spots-grid {
    max-width: 1280px;
    margin: 24px auto 32px;
    padding: 0 24px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;

    .spot-card {
      border-radius: 20px;
      overflow: hidden;
      background: white;
      cursor: pointer;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06);
      transition: box-shadow 0.3s;
      will-change: transform, opacity;

      &:hover {
        box-shadow: 0 20px 60px rgba(102,126,234,0.18);

        .spot-cover img { transform: scale(1.06); }
        .spot-glass-overlay { opacity: 1; }
      }

      .spot-cover {
        position: relative;
        height: 200px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s;
          will-change: transform;
        }

        /* 玻璃拟态悬浮层 */
        .spot-glass-overlay {
          position: absolute;
          inset: 0;
          background: rgba(15,15,35,0.45);
          backdrop-filter: blur(6px);
          display: flex;
          align-items: flex-end;
          justify-content: space-between;
          padding: 12px 14px;
          opacity: 0;
          transition: opacity 0.3s;
          border-radius: 0;

          .glass-price {
            font-size: 20px;
            font-weight: 900;
            color: white;
          }

          .glass-level {
            font-size: 12px;
            font-weight: 600;
            color: rgba(255,255,255,0.9);
            background: rgba(255,255,255,0.15);
            backdrop-filter: blur(8px);
            padding: 4px 10px;
            border-radius: 12px;
            border: 1px solid rgba(255,255,255,0.2);
          }
        }
      }

      .spot-info {
        padding: 18px;

        h3 {
          font-size: 16px;
          font-weight: 700;
          margin-bottom: 6px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          color: #1e293b;
        }

        .spot-address {
          font-size: 12px;
          color: #94a3b8;
          margin-bottom: 10px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }

        .spot-tags {
          display: flex;
          gap: 6px;
          margin-bottom: 14px;
          flex-wrap: wrap;

          .tag {
            padding: 3px 10px;
            background: linear-gradient(135deg, #667eea22, #764ba222);
            border: 1px solid rgba(102,126,234,0.2);
            border-radius: 10px;
            font-size: 11px;
            color: #667eea;
          }
        }

        .spot-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 12px;
          color: #94a3b8;
          border-top: 1px solid #f1f5f9;
          padding-top: 12px;

          .hot-score { color: #ef4444; font-weight: 600; }
        }
      }
    }
  }

  .pagination {
    display: flex;
    justify-content: center;
    padding: 16px 24px 40px;
  }
}
</style>
