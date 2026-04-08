<template>
  <div class="hotels-page">

    <!-- Hero -->
    <div class="hotels-hero" ref="hotelsHeroRef">
      <div class="hero-bg-overlay" />
      <div class="hero-content">
        <span class="hero-eyebrow" ref="hotelsEyebrowRef">🏨 精选住宿</span>
        <h1 class="hero-title-gsap" ref="hotelsTitleRef">精品酒店</h1>
        <p class="hero-sub" ref="hotelsSubRef">高品质酒店，为每一次旅途加分</p>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar" ref="filterBarRef">
      <el-input
        v-model="filters.keyword"
        placeholder="搜索酒店名称"
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
      <el-select v-model="filters.type" placeholder="酒店类型" clearable size="large" @change="handleFilter" style="width: 160px;">
        <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
      </el-select>
      <el-select v-model="filters.sortBy" size="large" @change="handleFilter" style="width: 160px;">
        <el-option label="推荐排序" value="" />
        <el-option label="评分最高" value="stars" />
        <el-option label="价格最低" value="price" />
      </el-select>
    </div>

    <!-- 加载 -->
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 酒店网格 -->
    <div v-else-if="filteredList.length > 0" class="hotels-grid" ref="hotelsGridRef">
      <div
        v-for="hotel in filteredList"
        :key="hotel.id"
        class="hotel-card gsap-hotel-card"
        @click="$router.push(`/hotels/${hotel.id}`)"
      >
        <!-- 封面 -->
        <div class="hotel-cover">
          <img :src="hotel.img" :alt="hotel.name" />
          <div class="hotel-glass-overlay">
            <span class="glass-badge">{{ hotel.badge }}</span>
          </div>
          <!-- 评分角标 -->
          <div class="hotel-score-tag">
            <span>⭐</span> {{ hotel.score }}
          </div>
        </div>

        <div class="hotel-info">
          <div class="hotel-name-row">
            <h3>{{ hotel.name }}</h3>
            <span class="hotel-type-tag">{{ hotel.type }}</span>
          </div>
          <p class="hotel-location">📍 {{ hotel.city }}</p>
          <div class="hotel-stars">{{ hotel.stars }}</div>

          <!-- 设施标签 -->
          <div class="hotel-facilities">
            <span v-for="f in hotel.facilities" :key="f" class="facility-tag">{{ f }}</span>
          </div>

          <div class="hotel-price-row">
            <div class="price-block">
              <span class="price-main">¥<b>{{ hotel.price }}</b></span>
              <span class="price-unit">/晚</span>
            </div>
            <span class="hotel-book-btn">查看详情</span>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无酒店数据" />

  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { splitTextByWord } from '@/composables/useGsapAnimation'
import { getHotelListApi } from '@/api/hotel'

gsap.registerPlugin(ScrollTrigger)

const loading = ref(false)
const hotelsGridRef = ref<HTMLElement | null>(null)
const hotelsHeroRef = ref<HTMLElement | null>(null)
const hotelsEyebrowRef = ref<HTMLElement | null>(null)
const hotelsTitleRef = ref<HTMLElement | null>(null)
const hotelsSubRef = ref<HTMLElement | null>(null)
const filterBarRef = ref<HTMLElement | null>(null)

const cityOptions = ['北京', '上海', '杭州', '成都', '厦门', '广州', '深圳', '三亚']
const typeOptions = ['豪华五星', '度假别墅', '设计酒店', '海滨度假', '商务酒店', '精品民宿']

const filters = reactive({
  keyword: '',
  city: '',
  type: '',
  sortBy: '',
})

// 从后端API获取
const allHotels = ref<any[]>([])

const filteredList = computed(() => {
  let list = allHotels.value
  if (filters.keyword) {
    list = list.filter(h => h.name.includes(filters.keyword))
  }
  if (filters.city) {
    list = list.filter(h => h.city === filters.city)
  }
  if (filters.sortBy === 'stars') {
    list = [...list].sort((a, b) => (b.starLevel || 0) - (a.starLevel || 0))
  } else if (filters.sortBy === 'price') {
    list = [...list].sort((a, b) => (a.minPrice || 0) - (b.minPrice || 0))
  }
  return list
})

async function fetchHotels() {
  try {
    loading.value = true
    const res: any = await getHotelListApi({ page: 1, pageSize: 50 })
    const data = res.data || res
    allHotels.value = (data.records || []).map((h: any) => ({
      id: h.id,
      name: h.name,
      city: h.city,
      type: h.starLevel ? `${h.starLevel}星` : '酒店',
      score: '4.8',
      stars: '⭐'.repeat(h.starLevel || 3),
      price: 0,
      minPrice: 0,
      badge: '推荐',
      img: h.coverImage || `https://picsum.photos/seed/hotel${h.id}/500/400`,
      facilities: h.facilities ? h.facilities.split(',') : [],
    }))
  } catch (e) {
    console.error('加载酒店列表失败', e)
  } finally {
    loading.value = false
  }
}

let debounceTimer: ReturnType<typeof setTimeout>
function handleFilter() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    nextTick(() => animateHotelsGrid())
  }, 400)
}

function initGsapAnimations() {
  const tl = gsap.timeline({ delay: 0.2 })
  if (hotelsEyebrowRef.value) {
    tl.fromTo(hotelsEyebrowRef.value,
      { opacity: 0, y: 20, filter: 'blur(8px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.7, ease: 'power3.out' }
    )
  }
  if (hotelsTitleRef.value) {
    const words = splitTextByWord(hotelsTitleRef.value)
    tl.fromTo(words,
      { yPercent: 120, opacity: 0, filter: 'blur(6px)' },
      { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.04 },
      '-=0.4'
    )
  }
  if (hotelsSubRef.value) {
    tl.fromTo(hotelsSubRef.value,
      { opacity: 0, y: 20 },
      { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' },
      '-=0.5'
    )
  }

  if (filterBarRef.value) {
    gsap.fromTo(filterBarRef.value,
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.7, ease: 'power3.out', delay: 0.2 }
    )
  }

  animateHotelsGrid()
}

function animateHotelsGrid() {
  if (!hotelsGridRef.value) return
  const cards = hotelsGridRef.value.querySelectorAll<HTMLElement>('.gsap-hotel-card')
  if (!cards.length) return

  gsap.fromTo(Array.from(cards),
    { opacity: 0, y: 80, scale: 0.93 },
    {
      opacity: 1, y: 0, scale: 1,
      duration: 0.9, ease: 'power3.out', stagger: 0.09,
      scrollTrigger: { trigger: hotelsGridRef.value, start: 'top 82%', once: true },
    }
  )

  // Hover 视差
  cards.forEach((card) => {
    const img = card.querySelector<HTMLElement>('.hotel-cover img')
    if (!img) return
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect()
      const x = (e.clientX - rect.left) / rect.width - 0.5
      const y = (e.clientY - rect.top) / rect.height - 0.5
      gsap.to(img, { xPercent: x * 10, yPercent: y * 10, duration: 0.5, ease: 'power2.out' })
    })
    card.addEventListener('mouseleave', () => {
      gsap.to(img, { xPercent: 0, yPercent: 0, duration: 0.5, ease: 'power2.out' })
    })
  })
}

onMounted(() => {
  fetchHotels()
  initGsapAnimations()
})

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped lang="scss">
.hotels-page {
  min-height: 100vh;
  background: #f8fafc;

  /* ── Hero ── */
  .hotels-hero {
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
      background: url('https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1920&q=60') center/cover no-repeat;
      opacity: 0.2;
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

  /* ── 筛选 ── */
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
  .hotels-grid {
    max-width: 1280px;
    margin: 24px auto 32px;
    padding: 0 24px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;

    .hotel-card {
      border-radius: 20px;
      overflow: hidden;
      background: white;
      cursor: pointer;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06);
      transition: box-shadow 0.3s, transform 0.3s;
      will-change: transform, opacity;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 24px 70px rgba(102,126,234,0.2);

        .hotel-cover img { transform: scale(1.07); }
        .hotel-glass-overlay { opacity: 1; }
      }

      .hotel-cover {
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

        .hotel-glass-overlay {
          position: absolute;
          inset: 0;
          background: rgba(15,15,35,0.4);
          backdrop-filter: blur(4px);
          display: flex;
          align-items: flex-start;
          justify-content: flex-end;
          padding: 10px;
          opacity: 0;
          transition: opacity 0.3s;

          .glass-badge {
            font-size: 11px;
            font-weight: 700;
            color: white;
            background: rgba(102,126,234,0.85);
            backdrop-filter: blur(8px);
            padding: 4px 10px;
            border-radius: 12px;
          }
        }

        .hotel-score-tag {
          position: absolute;
          bottom: 10px;
          right: 10px;
          background: rgba(255,255,255,0.95);
          backdrop-filter: blur(8px);
          border-radius: 10px;
          padding: 3px 10px;
          font-size: 13px;
          font-weight: 700;
          color: #1e293b;
          border: 1px solid rgba(255,255,255,0.5);
        }
      }

      .hotel-info {
        padding: 18px;

        .hotel-name-row {
          display: flex;
          align-items: flex-start;
          justify-content: space-between;
          gap: 8px;
          margin-bottom: 6px;

          h3 {
            font-size: 15px;
            font-weight: 700;
            color: #1e293b;
            line-height: 1.3;
            flex: 1;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .hotel-type-tag {
            flex-shrink: 0;
            font-size: 10px;
            font-weight: 600;
            color: #667eea;
            background: rgba(102,126,234,0.1);
            border: 1px solid rgba(102,126,234,0.2);
            padding: 2px 8px;
            border-radius: 8px;
            white-space: nowrap;
          }
        }

        .hotel-location {
          font-size: 12px;
          color: #94a3b8;
          margin-bottom: 6px;
        }

        .hotel-stars {
          font-size: 12px;
          margin-bottom: 10px;
        }

        .hotel-facilities {
          display: flex;
          flex-wrap: wrap;
          gap: 5px;
          margin-bottom: 14px;

          .facility-tag {
            font-size: 11px;
            color: #64748b;
            background: #f1f5f9;
            padding: 2px 8px;
            border-radius: 8px;
          }
        }

        .hotel-price-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          border-top: 1px solid #f1f5f9;
          padding-top: 12px;

          .price-block {
            display: flex;
            align-items: baseline;
            gap: 2px;

            .price-main {
              font-size: 20px;
              font-weight: 900;
              color: #ef4444;
            }

            .price-unit {
              font-size: 12px;
              color: #94a3b8;
            }
          }

          .hotel-book-btn {
            font-size: 12px;
            font-weight: 600;
            color: #667eea;
            background: rgba(102,126,234,0.1);
            padding: 5px 14px;
            border-radius: 20px;
            transition: all 0.2s;
          }

          &:hover .hotel-book-btn {
            background: #667eea;
            color: white;
          }
        }
      }
    }
  }
}
</style>
