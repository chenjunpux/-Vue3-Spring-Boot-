<template>
  <div class="travel-page">

    <!-- ── Hero ── -->
    <div class="travel-hero" ref="heroRef">
      <div class="hero-bg-overlay" />
      <div class="hero-content">
        <span class="hero-eyebrow" ref="heroEyebrowRef">✈️ 智能行程规划</span>
        <h1 class="hero-title" ref="heroTitleRef">智慧旅行</h1>
        <p class="hero-sub" ref="heroSubRef">AI 驱动，量身定制你的完美旅程</p>

        <!-- 搜索框 -->
        <div class="hero-search" ref="heroSearchRef">
          <div class="search-tabs">
            <span
              v-for="tab in searchTabs"
              :key="tab.value"
              class="tab-btn"
              :class="{ active: activeSearchTab === tab.value }"
              @click="activeSearchTab = tab.value"
            >
              {{ tab.label }}
            </span>
          </div>
          <div class="search-inputs">
            <div class="input-group">
              <span class="input-icon">📍</span>
              <input v-model="searchDest" :placeholder="activeSearchTab === 'itinerary' ? '想去哪里？如：云南、三亚...' : '输入目的地'" />
            </div>
            <div class="input-group">
              <span class="input-icon">📅</span>
              <select v-model="searchDays">
                <option value="">行程天数</option>
                <option value="3">3天2晚</option>
                <option value="5">5天4晚</option>
                <option value="7">7天6晚</option>
                <option value="10">10天以上</option>
              </select>
            </div>
            <div class="input-group">
              <span class="input-icon">💰</span>
              <select v-model="searchBudget">
                <option value="">预算范围</option>
                <option value="low">¥3000以内</option>
                <option value="mid">¥3000-8000</option>
                <option value="high">¥8000-20000</option>
                <option value="luxury">¥20000以上</option>
              </select>
            </div>
            <button class="search-btn" @click="handleSearch">
              <span>🔍</span> 智能规划
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 主题推荐 ── -->
    <div class="section themes-section" ref="themesRef">
      <div class="sec-head">
        <div class="sec-label" ref="themesLabelRef"><span>01</span><span>旅行灵感</span></div>
        <h2 ref="themesTitleRef">精选主题旅行</h2>
        <p ref="themesDescRef">无论你是亲子出行、户外探险还是浪漫蜜月，这里都有为你量身定制的路线</p>
      </div>
      <div class="themes-grid" ref="themesGridRef">
        <div
          v-for="(theme, i) in themes"
          :key="theme.id"
          class="theme-card gsap-theme-card"
          :style="{ '--theme-color': theme.color }"
          @click="selectTheme(theme)"
        >
          <div class="theme-bg" :style="{ backgroundImage: `url(${theme.cover})` }" />
          <div class="theme-overlay" />
          <div class="theme-content">
            <div class="theme-emoji">{{ theme.emoji }}</div>
            <h3>{{ theme.name }}</h3>
            <p>{{ theme.desc }}</p>
            <div class="theme-tags">
              <span v-for="t in theme.tags" :key="t">{{ t }}</span>
            </div>
            <div class="theme-meta">
              <span>{{ theme.days }}天行程</span>
              <span>约 ¥{{ theme.budget }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 精选行程 ── -->
    <div class="section itineraries-section" ref="itinerariesRef">
      <div class="sec-head">
        <div class="sec-label" ref="itinLabelRef"><span>02</span><span>热门路线</span></div>
        <h2 ref="itinTitleRef">热门行程推荐</h2>
        <p ref="itinDescRef">精选 3-7 天行程，覆盖全国热门目的地</p>
      </div>

      <!-- 行程筛选 -->
      <div class="itin-filter" ref="itinFilterRef">
        <span
          v-for="f in itinFilters"
          :key="f.value"
          class="filter-tag"
          :class="{ active: activeItinFilter === f.value }"
          @click="activeItinFilter = f.value"
        >
          {{ f.label }}
        </span>
      </div>

      <div class="itin-list" ref="itinListRef">
        <div
          v-for="(itin, i) in filteredItineraries"
          :key="itin.id"
          class="itin-card gsap-itin-card"
          :class="{ featured: itin.featured }"
          @click="$router.push('/spots')"
        >
          <!-- 封面 -->
          <div class="itin-cover">
            <img :src="itin.cover" :alt="itin.name" />
            <div class="itin-cover-overlay" />
            <div class="itin-days-badge">{{ itin.days }}天{{ itin.nights }}晚</div>
            <div v-if="itin.featured" class="itin-featured-tag">推荐</div>
          </div>

          <!-- 信息 -->
          <div class="itin-info">
            <div class="itin-dest-row">
              <span v-for="(city, ci) in itin.destinations.slice(0, 3)" :key="city" class="dest-chip">
                {{ city }}
                <span v-if="ci < Math.min(itin.destinations.length, 3) - 1" class="dest-arrow">→</span>
              </span>
              <span v-if="itin.destinations.length > 3" class="dest-more">+{{ itin.destinations.length - 3 }}</span>
            </div>

            <h3>{{ itin.name }}</h3>
            <p class="itin-desc">{{ itin.desc }}</p>

            <!-- 亮点标签 -->
            <div class="itin-highlights">
              <span v-for="h in itin.highlights" :key="h" class="highlight-tag">{{ h }}</span>
            </div>

            <!-- 行程概览 -->
            <div class="itin-schedule">
              <div
                v-for="(day, di) in itin.schedule.slice(0, 3)"
                :key="di"
                class="schedule-day"
              >
                <div class="day-num">D{{ di + 1 }}</div>
                <div class="day-content">{{ day }}</div>
              </div>
              <div v-if="itin.schedule.length > 3" class="schedule-more">
                +{{ itin.schedule.length - 3 }}天行程...
              </div>
            </div>

            <!-- 价格和按钮 -->
            <div class="itin-footer">
              <div class="itin-price">
                <span class="price-label">起</span>
                <span class="price-num">¥{{ itin.price.toLocaleString() }}</span>
                <span class="price-unit">/人</span>
              </div>
              <button class="itin-btn">查看详情</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 季节推荐 ── -->
    <div class="section season-section" ref="seasonRef">
      <div class="sec-head">
        <div class="sec-label" ref="seasonLabelRef"><span>03</span><span>当季精选</span></div>
        <h2 ref="seasonTitleRef">{{ currentSeasonTitle }}</h2>
        <p ref="seasonDescRef">{{ currentSeasonDesc }}</p>
      </div>
      <div class="season-grid" ref="seasonGridRef">
        <div
          v-for="spot in seasonSpots"
          :key="spot.id"
          class="season-card gsap-season-card"
          @click="$router.push('/spots')"
        >
          <div class="season-cover">
            <img :src="spot.img" :alt="spot.name" />
            <div class="season-badge">{{ spot.season }}</div>
          </div>
          <div class="season-info">
            <h3>{{ spot.name }}</h3>
            <p>{{ spot.city }}</p>
            <div class="season-tags">
              <span v-for="t in spot.tags" :key="t">{{ t }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 智能预算 ── -->
    <div class="section budget-section" ref="budgetRef">
      <div class="budget-inner">
        <div class="budget-visual" ref="budgetVisualRef">
          <div class="budget-chart">
            <svg viewBox="0 0 200 200" class="donut-chart">
              <circle cx="100" cy="100" r="70" fill="none" stroke="#f1f5f9" stroke-width="20" />
              <circle cx="100" cy="100" r="70" fill="none" stroke="#667eea" stroke-width="20"
                :stroke-dasharray="`${budgetChart.transportation} 440`" stroke-dashoffset="110"
                class="chart-arc" />
              <circle cx="100" cy="100" r="70" fill="none" stroke="#764ba2" stroke-width="20"
                :stroke-dasharray="`${budgetChart.accommodation} 440`" stroke-dashoffset="110"
                class="chart-arc" />
              <circle cx="100" cy="100" r="70" fill="none" stroke="#06b6d4" stroke-width="20"
                :stroke-dasharray="`${budgetChart.food} 440`" stroke-dashoffset="110"
                class="chart-arc" />
              <circle cx="100" cy="100" r="70" fill="none" stroke="#22c55e" stroke-width="20"
                :stroke-dasharray="`${budgetChart.tickets} 440`" stroke-dashoffset="110"
                class="chart-arc" />
            </svg>
            <div class="chart-center">
              <span class="chart-total">¥{{ totalBudget.toLocaleString() }}</span>
              <span class="chart-label">总预算</span>
            </div>
          </div>
          <div class="budget-legend">
            <div v-for="item in budgetItems" :key="item.label" class="legend-item">
              <span class="legend-dot" :style="{ background: item.color }" />
              <span class="legend-label">{{ item.label }}</span>
              <span class="legend-val">¥{{ item.value.toLocaleString() }}</span>
              <span class="legend-pct">{{ item.pct }}%</span>
            </div>
          </div>
        </div>

        <div class="budget-controls">
          <h3 ref="budgetTitleRef">💡 智能预算估算</h3>
          <p class="budget-hint">拖动滑块，调整各项预算占比</p>

          <div v-for="item in budgetItems" :key="item.label" class="budget-slider-row">
            <div class="slider-label-row">
              <span class="slider-dot" :style="{ background: item.color }" />
              <span>{{ item.label }}</span>
              <span class="slider-val">¥{{ Math.round(totalBudget * item.pct / 100).toLocaleString() }}</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              v-model.number="item.pct"
              class="budget-range"
              :style="{ '--range-color': item.color }"
            />
          </div>

          <div class="budget-summary">
            <div class="summary-row">
              <span>行程天数</span>
              <el-input-number v-model="budgetDays" :min="1" :max="30" size="default" />
            </div>
            <div class="summary-row">
              <span>人均预算</span>
              <div class="budget-total-display">¥{{ totalBudget.toLocaleString() }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { splitTextByWord } from '@/composables/useGsapAnimation'
import { ElInputNumber } from 'element-plus'

const router = useRouter()


gsap.registerPlugin(ScrollTrigger)

// ─── Refs ───────────────────────────────────────────────────────────────────
const heroRef = ref<HTMLElement | null>(null)
const heroEyebrowRef = ref<HTMLElement | null>(null)
const heroTitleRef = ref<HTMLElement | null>(null)
const heroSubRef = ref<HTMLElement | null>(null)
const heroSearchRef = ref<HTMLElement | null>(null)
const themesRef = ref<HTMLElement | null>(null)
const themesLabelRef = ref<HTMLElement | null>(null)
const themesTitleRef = ref<HTMLElement | null>(null)
const themesDescRef = ref<HTMLElement | null>(null)
const themesGridRef = ref<HTMLElement | null>(null)
const itinerariesRef = ref<HTMLElement | null>(null)
const itinLabelRef = ref<HTMLElement | null>(null)
const itinTitleRef = ref<HTMLElement | null>(null)
const itinDescRef = ref<HTMLElement | null>(null)
const itinFilterRef = ref<HTMLElement | null>(null)
const itinListRef = ref<HTMLElement | null>(null)
const seasonRef = ref<HTMLElement | null>(null)
const seasonLabelRef = ref<HTMLElement | null>(null)
const seasonTitleRef = ref<HTMLElement | null>(null)
const seasonDescRef = ref<HTMLElement | null>(null)
const seasonGridRef = ref<HTMLElement | null>(null)
const budgetRef = ref<HTMLElement | null>(null)
const budgetVisualRef = ref<HTMLElement | null>(null)
const budgetTitleRef = ref<HTMLElement | null>(null)

// ─── 搜索状态 ───────────────────────────────────────────────────────────────
const activeSearchTab = ref('itinerary')
const searchTabs = [
  { label: '🗺️ 行程规划', value: 'itinerary' },
  { label: '📍 目的地', value: 'destination' },
]
const searchDest = ref('')
const searchDays = ref('')
const searchBudget = ref('')

function handleSearch() {
  const dest = searchDest.value.trim()
  const days = searchDays.value
  const budget = searchBudget.value
  const tab = activeSearchTab.value

  if (tab === 'itinerary' && !dest) {
    return ElMessage.warning('请输入目的地')
  }

  let msg = ''
  if (tab === 'itinerary') {
    msg = '我想去' + (dest || '旅行')
    if (days) msg += '，' + days
    if (budget) {
      const budgetMap: Record<string, string> = { low: '3000以内', mid: '3000-8000', high: '8000-20000', luxury: '20000以上' }
      msg += '，预算' + (budgetMap[budget] || budget)
    }
    msg += '，帮我推荐一下行程'
  } else {
    msg = dest ? '给我介绍' + dest + '这个地方' : '推荐一些热门旅行目的地'
  }

  router.push({ path: '/chat', query: { q: msg } })
}

// ─── 主题数据 ───────────────────────────────────────────────────────────────
const themes = ref([
  { id: 1, name: '浪漫之旅', emoji: '💕', desc: '牵手看日落，享受二人世界', color: '#ec4899', tags: ['蜜月', '情侣', '海岛'], days: 5, budget: '8,000', cover: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80' },
  { id: 2, name: '亲子时光', emoji: '👨‍👩‍👧', desc: '陪孩子探索世界，留下童年回忆', color: '#f59e0b', tags: ['亲子', '乐园', '动物园'], days: 4, budget: '6,000', cover: 'https://images.unsplash.com/photo-1564349683136-77e08dba1ef7?w=800&q=80' },
  { id: 3, name: '户外探险', emoji: '🏔️', desc: '挑战自我，征服高山与大海', color: '#10b981', tags: ['徒步', '登山', '露营'], days: 6, budget: '5,000', cover: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&q=80' },
  { id: 4, name: '文化之旅', emoji: '🏯', desc: '穿越千年，感受历史的厚重', color: '#8b5cf6', tags: ['古镇', '博物馆', '寺庙'], days: 5, budget: '7,000', cover: 'https://images.unsplash.com/photo-1547981609-4b6bfe67ca0b?w=800&q=80' },
  { id: 5, name: '美食寻味', emoji: '🍜', desc: '走遍大街小巷，品味人间烟火', color: '#ef4444', tags: ['夜市', '小吃', '米其林'], days: 4, budget: '4,000', cover: 'https://images.unsplash.com/photo-1555126634-323283e090fa?w=800&q=80' },
  { id: 6, name: '摄影天堂', emoji: '📷', desc: '用镜头记录每一帧绝美风景', color: '#0ea5e9', tags: ['日出', '星空', '自然'], days: 5, budget: '9,000', cover: 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800&q=80' },
])

function selectTheme(theme: any) {
  searchDest.value = theme.name
}

// ─── 精选行程 ───────────────────────────────────────────────────────────────
const activeItinFilter = ref('all')
const itinFilters = [
  { label: '全部', value: 'all' },
  { label: '3天短途', value: 'short' },
  { label: '5天经典', value: 'medium' },
  { label: '7天深度', value: 'long' },
  { label: '户外探险', value: 'outdoor' },
]

const itineraries = ref([
  {
    id: 1, name: '云南大理丽江 5天4晚深度游', featured: true,
    destinations: ['昆明', '大理', '丽江'],
    desc: '环游洱海、登玉龙雪山、漫步丽江古城，体验纳西族风情',
    highlights: ['洱海骑行', '丽江古城', '玉龙雪山', '泸沽湖'],
    schedule: ['昆明接机-滇池', '大理环洱海', '大理-丽江-拉市海', '玉龙雪山-蓝月谷', '丽江古城自由行'],
    days: 5, nights: 4, price: 4588,
    cover: 'https://images.unsplash.com/photo-1528164344705-47542687000d?w=800&q=80',
  },
  {
    id: 2, name: '成都九寨沟 5天4晚川西秘境', featured: true,
    destinations: ['成都', '九寨沟', '黄龙'],
    desc: '探秘九寨沟彩池瑶池，拜水都江堰，感受川西高原的绝美风光',
    highlights: ['九寨沟', '黄龙景区', '都江堰', '川剧变脸'],
    schedule: ['成都接机-宽窄巷子', '成都-黄龙景区', '九寨沟全天', '九寨沟-成都', '成都自由行'],
    days: 5, nights: 4, price: 5288,
    cover: 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=800&q=80',
  },
  {
    id: 3, name: '西北大环线 7天6晚青甘越野', featured: false,
    destinations: ['西宁', '青海湖', '敦煌', '张掖'],
    desc: '穿越柴达木盆地，行走天空之境茶卡盐湖，打卡莫高窟与七彩丹霞',
    highlights: ['青海湖', '茶卡盐湖', '莫高窟', '七彩丹霞'],
    schedule: ['西宁集合', '青海湖环湖', '茶卡盐湖', '翡翠湖-石油小镇', '莫高窟-鸣沙山', '张掖丹霞', '返程'],
    days: 7, nights: 6, price: 7888,
    cover: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&q=80',
  },
  {
    id: 4, name: '西藏拉萨林芝 8天7晚朝圣之旅', featured: false,
    destinations: ['拉萨', '林芝', '羊卓雍措'],
    desc: '朝圣布达拉宫，穿行雅鲁藏布大峡谷，邂逅南迦巴瓦峰日照金山',
    highlights: ['布达拉宫', '林芝桃花', '雅鲁藏布', '羊卓雍措'],
    schedule: ['拉萨接机-休整', '布达拉宫-大昭寺', '纳木措', '林芝-巴松措', '雅鲁藏布大峡谷', '林芝-拉萨', '羊卓雍措', '返程'],
    days: 8, nights: 7, price: 12888,
    cover: 'https://images.unsplash.com/photo-1501555088652-021faa106b9b?w=800&q=80',
  },
  {
    id: 5, name: '三亚5天4晚亲子度假', featured: true,
    destinations: ['三亚'],
    desc: '亚特兰蒂斯水族馆、水世界全天畅玩，沙滩赶海抓蟹，享受热带海岛亲子时光',
    highlights: ['亚特兰蒂斯', '水世界', '免税购物', '温泉SPA'],
    schedule: ['三亚接机-酒店', '亚特兰蒂斯水族馆', '水世界全天', '南山寺-天涯海角', '自由活动-返程'],
    days: 5, nights: 4, price: 6899,
    cover: 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800&q=80',
  },
  {
    id: 6, name: '贵州黄果树 3天2晚避暑之旅', featured: false,
    destinations: ['贵阳', '黄果树', '西江千户苗寨'],
    desc: '打卡亚洲最大瀑布，探访全球最大苗族聚居村寨，感受贵州清凉夏日',
    highlights: ['黄果树瀑布', '西江千户苗寨', '青岩古镇'],
    schedule: ['贵阳接机', '黄果树瀑布全天', '西江千户苗寨-返程'],
    days: 3, nights: 2, price: 2688,
    cover: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&q=80',
  },
  {
    id: 7, name: '呼伦贝尔 6天5晚草原牧歌', featured: false,
    destinations: ['海拉尔', '呼伦贝尔草原', '满洲里'],
    desc: '骑马穿越草原、住蒙古包、看日落、参加篝火晚会，感受蒙古族风情',
    highlights: ['草原骑马', '蒙古包', '满洲里', '边境公路'],
    schedule: ['海拉尔集合', '草原穿越-恩和', '室韦-临江', '黑山头-满洲里', '满洲里-海拉尔', '返程'],
    days: 6, nights: 5, price: 6588,
    cover: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&q=80',
  },
  {
    id: 8, name: '桂林阳朔 4天3晚山水画卷', featured: false,
    destinations: ['桂林', '阳朔'],
    desc: '漓江竹筏漂流、骑行十里画廊、遇龙河畔下午茶，打卡20元人民币背景',
    highlights: ['漓江竹筏', '十里画廊', '银子岩', '印象刘三姐'],
    schedule: ['桂林接机', '漓江竹筏-兴坪', '阳朔骑行-银子岩', '返程'],
    days: 4, nights: 3, price: 3288,
    cover: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&q=80',
  },
])

const filteredItineraries = computed(() => {
  if (activeItinFilter.value === 'all') return itineraries.value
  if (activeItinFilter.value === 'short') return itineraries.value.filter(i => i.days <= 3)
  if (activeItinFilter.value === 'medium') return itineraries.value.filter(i => i.days >= 4 && i.days <= 6)
  if (activeItinFilter.value === 'long') return itineraries.value.filter(i => i.days >= 7)
  if (activeItinFilter.value === 'outdoor') return itineraries.value.filter(i =>
    i.highlights.some((h: string) => ['徒步', '草原', '雪山'].includes(h))
  )
  return itineraries.value
})

// ─── 季节推荐 ───────────────────────────────────────────────────────────────
const seasonTitle = ref('🌸 春季赏花指南')
const seasonDesc = ref('三月江南烟雨，四月桃花盛开，春天是中国最美的季节')

const currentSeasonTitle = computed(() => seasonTitle.value)
const currentSeasonDesc = computed(() => seasonDesc.value)

const seasonSpots = ref([
  { id: 1, name: '武汉大学樱花', city: '武汉', season: '🌸 春季', img: 'https://images.unsplash.com/photo-1522383225653-ed111181a951?w=600&q=80', tags: ['樱花', '大学', '赏花'] },
  { id: 2, name: '西藏林芝桃花', city: '林芝', season: '🌸 春季', img: 'https://images.unsplash.com/photo-1501555088652-021faa106b9b?w=600&q=80', tags: ['桃花', '雪山', '高原'] },
  { id: 3, name: '新疆伊犁杏花', city: '伊犁', season: '🌸 春季', img: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=600&q=80', tags: ['杏花', '草原', '村落'] },
  { id: 4, name: '婺源油菜花', city: '上饶', season: '🌸 春季', img: 'https://images.unsplash.com/photo-1476820865390-c52aeebb9891?w=600&q=80', tags: ['油菜花', '徽派建筑', '古镇'] },
  { id: 5, name: '青海湖鸟岛', city: '青海', season: '🐦 夏季', img: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=600&q=80', tags: ['候鸟', '湖泊', '草原'] },
  { id: 6, name: '长白山天池', city: '吉林', season: '🏔️ 夏季', img: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&q=80', tags: ['天池', '火山', '森林'] },
])

// ─── 预算估算 ───────────────────────────────────────────────────────────────
const budgetDays = ref(5)
const totalBudget = ref(10000)
const budgetItems = ref([
  { label: '交通', color: '#667eea', pct: 20, value: 2000 },
  { label: '住宿', color: '#764ba2', pct: 30, value: 3000 },
  { label: '餐饮', color: '#06b6d4', pct: 20, value: 2000 },
  { label: '门票/玩乐', color: '#22c55e', pct: 30, value: 3000 },
])

const budgetChart = computed(() => {
  const total = 440
  return {
    transportation: Math.round(total * budgetItems.value[0].pct / 100),
    accommodation: Math.round(total * budgetItems.value[1].pct / 100),
    food: Math.round(total * budgetItems.value[2].pct / 100),
    tickets: Math.round(total * budgetItems.value[3].pct / 100),
  }
})

// ─── GSAP 动画 ───────────────────────────────────────────────────────────────
function initGsapAnimations() {
  // Hero
  const heroTl = gsap.timeline({ delay: 0.2 })
  if (heroEyebrowRef.value) {
    heroTl.fromTo(heroEyebrowRef.value,
      { opacity: 0, y: 20, filter: 'blur(8px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.7, ease: 'power3.out' }
    )
  }
  if (heroTitleRef.value) {
    const words = splitTextByWord(heroTitleRef.value)
    heroTl.fromTo(words,
      { yPercent: 120, opacity: 0, filter: 'blur(6px)' },
      { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.04 },
      '-=0.4'
    )
  }
  if (heroSubRef.value) {
    heroTl.fromTo(heroSubRef.value,
      { opacity: 0, y: 20 },
      { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' },
      '-=0.5'
    )
  }
  if (heroSearchRef.value) {
    heroTl.fromTo(heroSearchRef.value,
      { opacity: 0, y: 30, scale: 0.96 },
      { opacity: 1, y: 0, scale: 1, duration: 0.9, ease: 'back.out(1.4)' },
      '-=0.4'
    )
  }

  // 通用 section 头部动画
  function animateSection(labelEl: HTMLElement | null, titleEl: HTMLElement | null, descEl: HTMLElement | null) {
    if (labelEl) {
      gsap.fromTo(labelEl,
        { opacity: 0, x: -40 },
        { opacity: 1, x: 0, duration: 0.8, ease: 'power3.out',
          scrollTrigger: { trigger: labelEl, start: 'top 85%', once: true } }
      )
    }
    if (titleEl) {
      const words = splitTextByWord(titleEl)
      gsap.fromTo(words,
        { yPercent: 100, opacity: 0, filter: 'blur(6px)' },
        { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 0.9, ease: 'power3.out', stagger: 0.04,
          scrollTrigger: { trigger: titleEl, start: 'top 85%', once: true } }
      )
    }
    if (descEl) {
      gsap.fromTo(descEl,
        { opacity: 0, y: 20 },
        { opacity: 1, y: 0, duration: 0.7, ease: 'power3.out',
          scrollTrigger: { trigger: descEl, start: 'top 88%', once: true } }
      )
    }
  }

  animateSection(themesLabelRef.value, themesTitleRef.value, themesDescRef.value)
  animateSection(itinLabelRef.value, itinTitleRef.value, itinDescRef.value)
  animateSection(seasonLabelRef.value, seasonTitleRef.value, seasonDescRef.value)

  // 主题卡片 stagger
  if (themesGridRef.value) {
    const cards = themesGridRef.value.querySelectorAll<HTMLElement>('.gsap-theme-card')
    gsap.fromTo(Array.from(cards),
      { opacity: 0, y: 60, scale: 0.93, rotationY: 15 },
      {
        opacity: 1, y: 0, scale: 1, rotationY: 0,
        duration: 0.85, ease: 'power3.out', stagger: 0.1,
        scrollTrigger: { trigger: themesGridRef.value, start: 'top 80%', once: true },
      }
    )

    // Hover 3D 倾斜
    cards.forEach((card) => {
      card.addEventListener('mousemove', (e) => {
        const rect = card.getBoundingClientRect()
        const x = (e.clientX - rect.left) / rect.width - 0.5
        const y = (e.clientY - rect.top) / rect.height - 0.5
        gsap.to(card, { rotateY: x * 12, rotateX: -y * 12, duration: 0.5, ease: 'power2.out' })
      })
      card.addEventListener('mouseleave', () => {
        gsap.to(card, { rotateY: 0, rotateX: 0, duration: 0.5, ease: 'power2.out' })
      })
    })
  }

  // 行程筛选标签
  if (itinFilterRef.value) {
    gsap.fromTo(itinFilterRef.value.children,
      { opacity: 0, y: 15 },
      { opacity: 1, y: 0, duration: 0.5, ease: 'power3.out', stagger: 0.06,
        scrollTrigger: { trigger: itinFilterRef.value, start: 'top 88%', once: true } }
    )
  }

  // 行程卡片
  animateItineraries()

  // 季节卡片
  if (seasonGridRef.value) {
    const sc = seasonGridRef.value.querySelectorAll<HTMLElement>('.gsap-season-card')
    gsap.fromTo(Array.from(sc),
      { opacity: 0, y: 70, scale: 0.95 },
      {
        opacity: 1, y: 0, scale: 1,
        duration: 0.85, ease: 'power3.out', stagger: 0.1,
        scrollTrigger: { trigger: seasonGridRef.value, start: 'top 82%', once: true },
      }
    )

    sc.forEach((card) => {
      const img = card.querySelector<HTMLElement>('.season-cover img')
      if (!img) return
      card.addEventListener('mousemove', (e) => {
        const rect = card.getBoundingClientRect()
        const x = (e.clientX - rect.left) / rect.width - 0.5
        const y = (e.clientY - rect.top) / rect.height - 0.5
        gsap.to(img, { xPercent: x * 8, yPercent: y * 8, scale: 1.06, duration: 0.5 })
      })
      card.addEventListener('mouseleave', () => {
        gsap.to(img, { xPercent: 0, yPercent: 0, scale: 1, duration: 0.5 })
      })
    })
  }

  // 预算区
  if (budgetVisualRef.value) {
    gsap.fromTo(budgetVisualRef.value,
      { opacity: 0, x: -60 },
      {
        opacity: 1, x: 0, duration: 1, ease: 'power3.out',
        scrollTrigger: { trigger: budgetRef.value, start: 'top 80%', once: true },
      }
    )
  }

  if (budgetTitleRef.value) {
    gsap.fromTo(budgetTitleRef.value,
      { opacity: 0, x: 40 },
      {
        opacity: 1, x: 0, duration: 0.8, ease: 'power3.out',
        scrollTrigger: { trigger: budgetRef.value, start: 'top 80%', once: true },
      }
    )
  }

  // 行程筛选重新触发动画
  if (itinListRef.value) {
    const cards = itinListRef.value.querySelectorAll<HTMLElement>('.gsap-itin-card')
    gsap.fromTo(Array.from(cards),
      { opacity: 0, y: 60, scale: 0.96 },
      {
        opacity: 1, y: 0, scale: 1,
        duration: 0.8, ease: 'power3.out', stagger: 0.08,
      }
    )
  }
}

function animateItineraries() {
  if (!itinListRef.value) return
  const cards = itinListRef.value.querySelectorAll<HTMLElement>('.gsap-itin-card')
  gsap.fromTo(Array.from(cards),
    { opacity: 0, y: 60, scale: 0.96 },
    {
      opacity: 1, y: 0, scale: 1,
      duration: 0.8, ease: 'power3.out', stagger: 0.1,
      scrollTrigger: { trigger: itinListRef.value, start: 'top 80%', once: true },
    }
  )
  cards.forEach((card) => {
    const img = card.querySelector<HTMLElement>('.itin-cover img')
    if (!img) return
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect()
      const x = (e.clientX - rect.left) / rect.width - 0.5
      gsap.to(img, { xPercent: x * 6, scale: 1.04, duration: 0.5 })
    })
    card.addEventListener('mouseleave', () => {
      gsap.to(img, { xPercent: 0, scale: 1, duration: 0.5 })
    })
  })
}

onMounted(() => {
  initGsapAnimations()
})

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped lang="scss">
.travel-page {
  min-height: 100vh;
  background: #f8fafc;
}

/* ── Hero ── */
.travel-hero {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  width: 100%;
  background: linear-gradient(135deg, #0f0f23 0%, #1e1b4b 40%, #312e81 70%, #1e1b4b 100%);
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background:
      radial-gradient(ellipse 80% 50% at 50% 50%, rgba(102,126,234,0.15) 0%, transparent 70%),
      radial-gradient(ellipse 60% 40% at 20% 80%, rgba(118,75,162,0.1) 0%, transparent 60%),
      radial-gradient(ellipse 50% 30% at 80% 20%, rgba(6,182,212,0.08) 0%, transparent 60%);
  }

  .hero-bg-overlay {
    position: absolute;
    inset: 0;
    background: url('https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=1920&q=60') center/cover no-repeat;
    opacity: 0.18;
  }

  .hero-content {
    position: relative;
    display: block;
    z-index: 2;
    text-align: center;
    padding: 0 clamp(16px, 4vw, 48px);
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }

  .hero-eyebrow {
    display: inline-block;
    font-size: 14px;
    color: rgba(255,255,255,0.55);
    letter-spacing: 3px;
    text-transform: uppercase;
    margin-bottom: 20px;
  }

  .hero-title {
    font-size: clamp(60px, 10vw, 120px);
    font-weight: 900;
    color: white;
    margin-bottom: 20px;
  }

  .hero-sub {
    font-size: 20px;
    color: rgba(255,255,255,0.55);
    margin-bottom: 50px;
  }

  /* 搜索框 */
  .hero-search {
    background: rgba(255,255,255,0.06);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255,255,255,0.12);
    border-radius: 24px;
    padding: 24px 28px;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .search-tabs {
      display: flex;
      gap: 8px;
      justify-content: center;

      .tab-btn {
        padding: 6px 20px;
        border-radius: 20px;
        font-size: 13px;
        color: rgba(255,255,255,0.5);
        cursor: pointer;
        transition: all 0.2s;

        &:hover { color: rgba(255,255,255,0.8); }
        &.active {
          color: white;
          background: rgba(255,255,255,0.15);
          font-weight: 700;
        }
      }
    }

    .search-inputs {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
      justify-content: center;
      max-width: 900px;
      margin: 0 auto;

      .input-group {
        display: flex;
        align-items: center;
        gap: 8px;
        background: rgba(255,255,255,0.08);
        border: 1px solid rgba(255,255,255,0.15);
        border-radius: 14px;
        padding: 0 16px;
        height: 48px;
        flex: 1;
        min-width: 160px;
        max-width: 220px;
        transition: border-color 0.2s;

        &:focus-within { border-color: rgba(102,126,234,0.6); }

        .input-icon { font-size: 16px; }

        input, select {
          flex: 1;
          background: none;
          border: none;
          outline: none;
          color: white;
          font-size: 14px;
          cursor: pointer;
          &::placeholder { color: rgba(255,255,255,0.4); }
          option { background: #1e1b4b; color: white; }
        }
      }

      .search-btn {
        height: 48px;
        padding: 0 28px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border: none;
        border-radius: 14px;
        color: white;
        font-size: 15px;
        font-weight: 700;
        cursor: pointer;
        transition: all 0.3s;
        white-space: nowrap;
        display: flex;
        align-items: center;
        gap: 6px;

        &:hover {
          transform: translateY(-2px) scale(1.03);
          box-shadow: 0 10px 30px rgba(102,126,234,0.45);
        }
      }
    }
  }
}

/* ── Section 通用 ── */
.section {
  padding: 100px 24px;
  max-width: 1280px;
  margin: 0 auto;
  border-radius: 0 !important;
}

.sec-head {
  text-align: center;
  margin-bottom: 60px;

  .sec-label {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    font-size: 13px;
    color: #667eea;
    text-transform: uppercase;
    letter-spacing: 3px;
    font-weight: 700;
  }

  h2 {
    font-size: clamp(28px, 4vw, 48px);
    font-weight: 900;
    color: #1e293b;
    margin-bottom: 12px;
  }

  p {
    font-size: 16px;
    color: #94a3b8;
    max-width: 600px;
    margin: 0 auto;
    line-height: 1.7;
  }
}

/* ── 主题卡片 ── */
.themes-section .themes-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;

  @media (max-width: 900px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 600px) { grid-template-columns: 1fr; }

  .theme-card {
    position: relative;
    border-radius: 24px;
    overflow: hidden;
    height: 300px;
    cursor: pointer;
    perspective: 800px;
    will-change: transform;

    &:hover {
      .theme-overlay { opacity: 1; }
      .theme-bg { transform: scale(1.06); }
      .theme-content { transform: translateY(-4px); }
    }

    .theme-bg {
      position: absolute;
      inset: 0;
      background-size: cover;
      background-position: center;
      transition: transform 0.5s;
    }

    .theme-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(to top, rgba(0,0,0,0.85) 0%, rgba(0,0,0,0.2) 60%, transparent 100%);
      opacity: 0.85;
      transition: opacity 0.3s;
    }

    .theme-content {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      padding: 24px;
      color: white;
      transition: transform 0.3s;

      .theme-emoji { font-size: 36px; margin-bottom: 8px; }
      h3 { font-size: 22px; font-weight: 800; margin-bottom: 6px; }
      p { font-size: 13px; color: rgba(255,255,255,0.75); margin-bottom: 12px; }

      .theme-tags {
        display: flex;
        gap: 6px;
        flex-wrap: wrap;
        margin-bottom: 12px;

        span {
          font-size: 11px;
          padding: 3px 10px;
          background: rgba(255,255,255,0.15);
          border: 1px solid rgba(255,255,255,0.2);
          border-radius: 10px;
          backdrop-filter: blur(4px);
        }
      }

      .theme-meta {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: rgba(255,255,255,0.6);
        font-weight: 600;
      }
    }
  }
}

/* ── 行程列表 ── */
.itineraries-section {
  background: #fff;

  .itin-filter {
    display: flex;
    gap: 12px;
    justify-content: center;
    flex-wrap: wrap;
    margin-bottom: 40px;

    .filter-tag {
      padding: 8px 22px;
      border-radius: 20px;
      font-size: 14px;
      color: #64748b;
      background: #f1f5f9;
      cursor: pointer;
      transition: all 0.25s;
      border: 1.5px solid transparent;

      &:hover {
        color: #667eea;
        background: rgba(102,126,234,0.06);
      }

      &.active {
        color: white;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-color: transparent;
        box-shadow: 0 4px 16px rgba(102,126,234,0.3);
        font-weight: 600;
      }
    }
  }

  .itin-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 28px;

    @media (max-width: 800px) { grid-template-columns: 1fr; }

    .itin-card {
      border-radius: 20px;
      overflow: hidden;
      background: white;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06);
      cursor: pointer;
      transition: box-shadow 0.3s, transform 0.3s;
      border: 1.5px solid #f1f5f9;
      will-change: transform, opacity;

      &:hover {
        transform: translateY(-6px);
        box-shadow: 0 20px 60px rgba(102,126,234,0.16);
        border-color: rgba(102,126,234,0.2);

        .itin-cover img { transform: scale(1.05); }
        .itin-btn { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
      }

      &.featured {
        border-color: rgba(102,126,234,0.25);
        background: linear-gradient(135deg, rgba(102,126,234,0.02), rgba(118,75,162,0.02));
      }

      .itin-cover {
        position: relative;
        height: 220px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s;
          will-change: transform;
        }

        .itin-cover-overlay {
          position: absolute;
          inset: 0;
          background: linear-gradient(to top, rgba(0,0,0,0.5) 0%, transparent 60%);
        }

        .itin-days-badge {
          position: absolute;
          top: 12px;
          left: 12px;
          background: rgba(15,15,35,0.75);
          backdrop-filter: blur(8px);
          color: white;
          font-size: 12px;
          font-weight: 700;
          padding: 4px 12px;
          border-radius: 12px;
          border: 1px solid rgba(255,255,255,0.2);
        }

        .itin-featured-tag {
          position: absolute;
          top: 12px;
          right: 12px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          color: white;
          font-size: 11px;
          font-weight: 700;
          padding: 4px 12px;
          border-radius: 12px;
        }
      }

      .itin-info {
        padding: 20px;

        .itin-dest-row {
          display: flex;
          flex-wrap: wrap;
          gap: 4px;
          margin-bottom: 10px;
          align-items: center;

          .dest-chip {
            font-size: 12px;
            font-weight: 600;
            color: #667eea;
            background: rgba(102,126,234,0.1);
            padding: 3px 10px;
            border-radius: 8px;
          }

          .dest-arrow { color: #cbd5e1; margin: 0 2px; }
          .dest-more { font-size: 11px; color: #94a3b8; }
        }

        h3 { font-size: 17px; font-weight: 800; color: #1e293b; margin-bottom: 6px; }

        .itin-desc {
          font-size: 13px;
          color: #94a3b8;
          line-height: 1.6;
          margin-bottom: 12px;
        }

        .itin-highlights {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;
          margin-bottom: 16px;

          .highlight-tag {
            font-size: 11px;
            color: #64748b;
            background: #f1f5f9;
            padding: 2px 8px;
            border-radius: 6px;
          }
        }

        .itin-schedule {
          border: 1px solid #f1f5f9;
          border-radius: 12px;
          overflow: hidden;
          margin-bottom: 16px;

          .schedule-day {
            display: flex;
            gap: 12px;
            padding: 8px 12px;
            border-bottom: 1px solid #f1f5f9;
            align-items: center;

            &:last-child { border-bottom: none; }

            .day-num {
              font-size: 11px;
              font-weight: 800;
              color: #667eea;
              background: rgba(102,126,234,0.1);
              padding: 2px 8px;
              border-radius: 6px;
              flex-shrink: 0;
            }

            .day-content {
              font-size: 12px;
              color: #64748b;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .schedule-more {
            padding: 6px 12px;
            font-size: 12px;
            color: #94a3b8;
            text-align: center;
          }
        }

        .itin-footer {
          display: flex;
          align-items: center;
          justify-content: space-between;

          .itin-price {
            display: flex;
            align-items: baseline;
            gap: 2px;

            .price-label { font-size: 12px; color: #94a3b8; }
            .price-num { font-size: 22px; font-weight: 900; color: #ef4444; }
            .price-unit { font-size: 12px; color: #94a3b8; }
          }

          .itin-btn {
            padding: 8px 20px;
            border-radius: 20px;
            border: 1.5px solid #667eea;
            color: #667eea;
            font-size: 13px;
            font-weight: 600;
            background: none;
            cursor: pointer;
            transition: all 0.2s;
          }
        }
      }
    }
  }
}

/* ── 季节推荐 ── */
.season-section {
  background: #f8fafc;

  .season-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;

    @media (max-width: 800px) { grid-template-columns: repeat(2, 1fr); }
    @media (max-width: 500px) { grid-template-columns: 1fr; }

    .season-card {
      border-radius: 20px;
      overflow: hidden;
      background: white;
      box-shadow: 0 4px 20px rgba(0,0,0,0.06);
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;
      will-change: transform, opacity;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 20px 60px rgba(102,126,234,0.16);

        .season-cover img { transform: scale(1.07); }
      }

      .season-cover {
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

        .season-badge {
          position: absolute;
          top: 10px;
          right: 10px;
          background: rgba(255,255,255,0.95);
          backdrop-filter: blur(8px);
          font-size: 12px;
          font-weight: 700;
          padding: 4px 12px;
          border-radius: 12px;
          color: #1e293b;
        }
      }

      .season-info {
        padding: 18px;

        h3 { font-size: 16px; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
        p { font-size: 13px; color: #94a3b8; margin-bottom: 10px; }

        .season-tags {
          display: flex;
          gap: 6px;
          flex-wrap: wrap;

          span {
            font-size: 11px;
            color: #667eea;
            background: rgba(102,126,234,0.08);
            padding: 2px 8px;
            border-radius: 6px;
          }
        }
      }
    }
  }
}

/* ── 预算估算 ── */
.budget-section {
  background: linear-gradient(135deg, #0f0f23 0%, #1e1b4b 100%);
  max-width: 100%;
  padding: 100px 24px;

  .budget-inner {
    max-width: 1100px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 80px;
    align-items: center;

    @media (max-width: 800px) { grid-template-columns: 1fr; }

    /* 图表侧 */
    .budget-visual {
      display: flex;
      flex-direction: column;
      gap: 40px;
      align-items: center;
    }

    .budget-chart {
      position: relative;
      width: 240px;
      height: 240px;

      .donut-chart {
        width: 100%;
        height: 100%;
        transform: rotate(-90deg);
      }

      .chart-arc { transition: stroke-dasharray 0.5s; }

      .chart-center {
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        .chart-total { font-size: 28px; font-weight: 900; color: white; }
        .chart-label { font-size: 13px; color: rgba(255,255,255,0.5); }
      }
    }

    .budget-legend {
      display: flex;
      flex-direction: column;
      gap: 12px;
      width: 100%;

      .legend-item {
        display: flex;
        align-items: center;
        gap: 10px;

        .legend-dot { width: 12px; height: 12px; border-radius: 50%; flex-shrink: 0; }
        .legend-label { font-size: 14px; color: rgba(255,255,255,0.7); flex: 1; }
        .legend-val { font-size: 14px; font-weight: 700; color: white; }
        .legend-pct { font-size: 12px; color: rgba(255,255,255,0.4); width: 40px; text-align: right; }
      }
    }

    /* 控制侧 */
    .budget-controls {
      color: white;

      h3 {
        font-size: 28px;
        font-weight: 900;
        margin-bottom: 8px;
      }

      .budget-hint {
        font-size: 14px;
        color: rgba(255,255,255,0.5);
        margin-bottom: 32px;
      }

      .budget-slider-row {
        margin-bottom: 24px;

        .slider-label-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;

          .slider-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; }
          span { font-size: 14px; color: rgba(255,255,255,0.7); flex: 1; }
          .slider-val { font-size: 14px; font-weight: 700; color: white; }
        }

        .budget-range {
          width: 100%;
          -webkit-appearance: none;
          height: 4px;
          border-radius: 2px;
          background: rgba(255,255,255,0.15);
          outline: none;
          cursor: pointer;

          &::-webkit-slider-thumb {
            -webkit-appearance: none;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: var(--range-color, #667eea);
            cursor: pointer;
            box-shadow: 0 2px 8px rgba(0,0,0,0.3);
          }
        }
      }

      .budget-summary {
        background: rgba(255,255,255,0.06);
        border: 1px solid rgba(255,255,255,0.1);
        border-radius: 16px;
        padding: 20px;
        margin-top: 16px;
        display: flex;
        flex-direction: column;
        gap: 16px;

        .summary-row {
          display: flex;
          align-items: center;
          justify-content: space-between;

          span { font-size: 14px; color: rgba(255,255,255,0.6); }

          .budget-total-display {
            font-size: 28px;
            font-weight: 900;
            color: white;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
          }
        }
      }
    }
  }
}
</style>