<template>
  <div class="home">

    <!-- ── Hero ── -->
    <section class="hero">
      <div class="hero-bg" ref="heroBgRef">
        <img
          v-for="(src, i) in heroImages"
          :key="i"
          :src="src"
          class="hero-img"
          :class="{ on: heroIdx === i }"
          ref="heroImgsRef"
        />
        <div class="hero-mask" />
      </div>
      <div class="hero-body" ref="heroBodyRef">
        <div class="hero-badge" ref="heroBadgeRef">
          <span class="badge-dot" />
          探索世界，发现美好
        </div>
        <h1 class="hero-title" ref="heroTitleRef">发现绝美风景</h1>
        <p class="hero-sub" ref="heroSubRef">百万旅行者的选择，开启你的下一段旅程</p>
        <div class="hero-search" ref="heroSearchRef">
          <input v-model="kw" placeholder="搜索目的地、酒店、景点..." @keyup.enter="goSearch" />
          <button @click="goSearch">搜索</button>
          <button class="ai-btn" @click="goAIChat" title="AI智能规划">
            🤖 智慧规划
          </button>
        </div>
        <div class="hero-tags" ref="heroTagsRef">
          <span v-for="t in ['北京','上海','杭州','成都','厦门']" :key="t" @click="kw = t">{{ t }}</span>
        </div>
      </div>
      <div class="hero-dots">
        <b v-for="(_, i) in heroImages" :key="i" :class="{ active: heroIdx === i }" @click="heroIdx = i" />
      </div>
      <!-- 向下滚动提示 -->
      <div class="scroll-hint" ref="scrollHintRef">
        <div class="scroll-line" />
        <span>向下滚动</span>
      </div>
    </section>

    <!-- ── Stats ── -->
    <section class="stats" ref="statsRef">
      <div class="stats-row">
        <div v-for="s in stats" :key="s.label" class="stat-cell" :ref="el => el && statCellsRef.push(el as HTMLElement)">
          <div class="stat-n" :ref="el => el && statNumRefs.push(el as HTMLElement)" :data-target="s.v.replace(/,/g,'')" :data-suffix="s.s">{{ s.v }}<small>{{ s.s }}</small></div>
          <div class="stat-l">{{ s.label }}</div>
        </div>
      </div>
    </section>

    <!-- ── Spots ── -->
    <section class="spots" ref="spotsRef">
      <div class="sec-head">
        <div class="sec-label" ref="spotsLabelRef"><span>01</span><span>精选景点</span></div>
        <h2 ref="spotsTitleRef">发现绝美风景</h2>
        <p ref="spotsDescRef">热门景点，等你来探索</p>
      </div>
      <div class="spots-row" ref="spotsRowRef">
        <div v-for="s in spots" :key="s.id" class="spot-card gsap-card" @click="$router.push(`/spots/${s.id}`)">
          <div class="spot-img"><img :src="s.img" :alt="s.name" loading="lazy" /><span class="spot-badge">{{ s.level }}</span></div>
          <div class="spot-info">
            <div class="spot-meta"><span>📍 {{ s.city }}</span><span>⭐ {{ s.score }}</span></div>
            <div class="spot-name">{{ s.name }}</div>
            <div class="spot-price">{{ s.price > 0 ? '¥' + s.price : '免费' }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Hotels ── -->
    <section class="hotels" ref="hotelsRef">
      <div class="sec-head">
        <div class="sec-label" ref="hotelsLabelRef"><span>02</span><span>精品酒店</span></div>
        <h2 ref="hotelsTitleRef">奢华住宿体验</h2>
        <p ref="hotelsDescRef">精选高品质酒店，享受非凡入住体验</p>
      </div>
      <div class="hotels-row" ref="hotelsRowRef">
        <div v-for="h in hotels" :key="h.id" class="hotel-card gsap-card" @click="$router.push(`/hotels/${h.id}`)">
          <div class="hotel-img"><img :src="h.img" :alt="h.name" loading="lazy" /><span class="hotel-badge">{{ h.badge }}</span></div>
          <div class="hotel-info">
            <div class="hotel-name">{{ h.name }}</div>
            <div class="hotel-loc">{{ h.city }} · {{ h.type }}</div>
            <div class="hotel-stars">{{ h.stars }}</div>
            <div class="hotel-price">¥{{ h.price }}<small>/晚</small></div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── Experiences ── -->
    <section class="exps" ref="expsRef">
      <div class="sec-head">
        <div class="sec-label"><span>03</span><span>特色体验</span></div>
        <h2 ref="expsTitleRef">与众不同的旅行方式</h2>
      </div>
      <div class="exp-list">
        <div
          v-for="(e, i) in exps"
          :key="e.title"
          class="exp-item"
          :class="{ rev: i % 2 !== 0 }"
          ref="expItemsRef"
        >
          <div class="exp-pic" ref="expPicsRef"><img :src="e.img" :alt="e.title" loading="lazy" /><b class="exp-n">{{ String(i + 1).padStart(2, '0') }}</b></div>
          <div class="exp-desc">
            <span class="exp-tag">{{ e.tag }}</span>
            <h3 class="exp-title-gsap">{{ e.title }}</h3>
            <p>{{ e.desc }}</p>
            <button @click="$router.push('/spots')">体验一下 →</button>
          </div>
        </div>
      </div>
    </section>

    <!-- ── 全局地图 ── -->
    <section class="home-map" ref="homeMapRef">
      <div class="map-head">
        <div class="sec-label"><span>03</span><span>地图导览</span></div>
        <h2>景点 & 酒店分布</h2>
        <p>探索热门景点与精品酒店的位置</p>
      </div>
      <div class="map-legend">
        <span class="legend-item"><span class="dot spot-dot"></span>景点</span>
        <span class="legend-item"><span class="dot hotel-dot"></span>酒店</span>
      </div>
      <div class="home-map-wrap">
        <div id="home-amap" class="home-amap"></div>
      </div>
    </section>

    <!-- ── CTA ── -->
    <section class="cta" ref="ctaRef">
      <div class="cta-inner">
        <h2 ref="ctaTitleRef">准备好开始你的旅行了吗？</h2>
        <p ref="ctaDescRef">加入我们，发现全球最美风景</p>
        <div class="cta-btns" ref="ctaBtnsRef">
          <router-link to="/register" class="btn-pri">立即注册</router-link>
          <router-link to="/spots" class="btn-out">探索景点</router-link>
        </div>
      </div>
    </section>

    <!-- ── Footer ── -->
    <div class="page-foot">
      <div class="foot-logo">🧳 TRAVEL</div>
      <p>© 2026 智慧旅游管理系统 · 毕业设计作品</p>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { splitTextByWord } from '@/composables/useGsapAnimation'
import { useAmap } from '@/composables/useAmap'
import { getHotSpotsApi } from '@/api/spot'
import { getHotHotelsApi } from '@/api/hotel'
import { getDashboardData } from '@/api/admin'

gsap.registerPlugin(ScrollTrigger)

const router = useRouter()
const kw = ref('')
const heroIdx = ref(0)
let heroTimer: ReturnType<typeof setInterval>

// ─── Refs ───────────────────────────────────────────────────────────────────
const heroBgRef = ref<HTMLElement | null>(null)
const heroBodyRef = ref<HTMLElement | null>(null)
const heroBadgeRef = ref<HTMLElement | null>(null)
const heroTitleRef = ref<HTMLElement | null>(null)
const heroSubRef = ref<HTMLElement | null>(null)
const heroSearchRef = ref<HTMLElement | null>(null)
const heroTagsRef = ref<HTMLElement | null>(null)
const scrollHintRef = ref<HTMLElement | null>(null)
const heroImgsRef = ref<HTMLElement[]>([])
const statsRef = ref<HTMLElement | null>(null)
const statCellsRef = ref<HTMLElement[]>([])
const statNumRefs: HTMLElement[] = []
const spotsRef = ref<HTMLElement | null>(null)
const spotsLabelRef = ref<HTMLElement | null>(null)
const spotsTitleRef = ref<HTMLElement | null>(null)
const spotsDescRef = ref<HTMLElement | null>(null)
const spotsRowRef = ref<HTMLElement | null>(null)
const hotelsRef = ref<HTMLElement | null>(null)
const hotelsLabelRef = ref<HTMLElement | null>(null)
const hotelsTitleRef = ref<HTMLElement | null>(null)
const hotelsDescRef = ref<HTMLElement | null>(null)
const hotelsRowRef = ref<HTMLElement | null>(null)
const expsRef = ref<HTMLElement | null>(null)
const expsTitleRef = ref<HTMLElement | null>(null)
const expItemsRef = ref<HTMLElement[]>([])
const expPicsRef = ref<HTMLElement[]>([])
const ctaRef = ref<HTMLElement | null>(null)
const ctaTitleRef = ref<HTMLElement | null>(null)
const ctaDescRef = ref<HTMLElement | null>(null)
const ctaBtnsRef = ref<HTMLElement | null>(null)
const homeMapRef = ref<HTMLElement | null>(null)

const heroImages = [
  'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=80',
  'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=1920&q=80',
  'https://images.unsplash.com/photo-1426604966848-d7adac402bff?w=1920&q=80',
]

// 从后端API获取数据
const stats = ref([
  { label: '精选景点', v: '—', s: '+' },
  { label: '注册用户', v: '—', s: '+' },
  { label: '合作酒店', v: '—', s: '+' },
  { label: '总订单数', v: '—', s: '' },
])

const spots = ref<any[]>([])
const hotels = ref<any[]>([])

const exps = [
  { title: '直升机俯瞰之旅', tag: '特色体验', desc: '乘坐直升机从空中俯瞰城市全景，极限视野，极致震撼。', img: 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=700&q=80' },
  { title: '海岛潜水探秘', tag: '户外探索', desc: '潜入蔚蓝深海，与海洋生物零距离接触，探索珊瑚礁的神奇世界。', img: 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=700&q=80' },
  { title: '热气球浪漫飞行', tag: '浪漫之旅', desc: '日出时分乘热气球缓缓升空，在晨光中俯瞰大地，体验最浪漫的天空之旅。', img: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=700&q=80' },
]

function goSearch() {
  if (!kw.value.trim()) return ElMessage.warning('请输入关键词')
  router.push({ path: '/spots', query: { keyword: kw.value } })
}

function goAIChat() {
  const query = kw.value.trim()
  if (query) {
    router.push({ path: '/chat', query: { q: query } })
  } else {
    router.push('/chat')
  }
}

// ─── GSAP 动画 ───────────────────────────────────────────────────────────────
function initGsapAnimations() {
  // Hero 视差
  if (heroBgRef.value) {
    gsap.to('.hero-img.on', {
      yPercent: 25,
      ease: 'none',
      scrollTrigger: {
        trigger: '.hero',
        start: 'top top',
        end: 'bottom top',
        scrub: true,
      },
    })
  }

  // Hero 进场动画（逐词 reveal + Velvet Pour 风格）
  const heroTl = gsap.timeline({ delay: 0.3 })

  if (heroBadgeRef.value) {
    heroTl.fromTo(heroBadgeRef.value,
      { opacity: 0, y: 30, filter: 'blur(10px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.8, ease: 'power3.out' }
    )
  }

  if (heroTitleRef.value) {
    const chars = splitTextByWord(heroTitleRef.value)
    heroTl.fromTo(chars,
      { yPercent: 110, opacity: 0, filter: 'blur(8px)' },
      { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.05 },
      '-=0.3'
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
      { opacity: 0, y: 20, scale: 0.95 },
      { opacity: 1, y: 0, scale: 1, duration: 0.8, ease: 'back.out(1.7)' },
      '-=0.5'
    )
  }

  if (heroTagsRef.value) {
    heroTl.fromTo(heroTagsRef.value.querySelectorAll('span'),
      { opacity: 0, y: 15 },
      { opacity: 1, y: 0, duration: 0.6, ease: 'power3.out', stagger: 0.06 },
      '-=0.4'
    )
  }

  if (scrollHintRef.value) {
    heroTl.fromTo(scrollHintRef.value,
      { opacity: 0 },
      { opacity: 1, duration: 0.6 },
      '-=0.2'
    )
  }

  // 滚动提示动画
  gsap.to('.scroll-line', {
    scaleY: 0,
    transformOrigin: 'top center',
    repeat: -1,
    yoyo: true,
    duration: 1.2,
    ease: 'power2.inOut',
    delay: 2,
  })

  // ─── Stats ──────────────────────────────────────────────────────────────
  if (statsRef.value) {
    gsap.fromTo(statCellsRef.value,
      { opacity: 0, y: 60 },
      {
        opacity: 1, y: 0, duration: 0.9, ease: 'power3.out', stagger: 0.12,
        scrollTrigger: { trigger: statsRef.value, start: 'top 80%', once: true },
      }
    )
  }

  // 数字跳动
  statNumRefs.forEach((el) => {
    if (!el) return
    const targetStr = el.dataset.target || '0'
    const suffix = el.dataset.suffix || ''
    const hasDecimal = targetStr.includes('.')
    const isDecimal = hasDecimal && targetStr.split('.')[1].length > 0
    const target = parseFloat(targetStr)
    const obj = { val: hasDecimal && !isDecimal ? 0 : 0 }

    ScrollTrigger.create({
      trigger: el,
      start: 'top 85%',
      once: true,
      onEnter: () => {
        gsap.to(obj, {
          val: target,
          duration: 2.2,
          ease: 'power2.out',
          onUpdate() {
            el.textContent = (hasDecimal && !isDecimal
              ? obj.val.toFixed(targetStr.split('.')[1]?.length ?? 1)
              : Math.round(obj.val).toLocaleString()) + suffix
          },
          onComplete() {
            el.textContent = targetStr + suffix
          },
        })
      },
    })
  })

  // ─── Spots ───────────────────────────────────────────────────────────────
  function animateSection(labelEl: HTMLElement | null, titleEl: HTMLElement | null, descEl: HTMLElement | null, rowEl: HTMLElement | null) {
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
    if (rowEl) {
      const cards = rowEl.querySelectorAll<HTMLElement>('.gsap-card')
      gsap.fromTo(cards,
        { opacity: 0, y: 80, scale: 0.94 },
        { opacity: 1, y: 0, scale: 1, duration: 0.85, ease: 'power3.out', stagger: 0.1,
          scrollTrigger: { trigger: rowEl, start: 'top 82%', once: true } }
      )
    }
  }

  animateSection(spotsLabelRef.value, spotsTitleRef.value, spotsDescRef.value, spotsRowRef.value)
  animateSection(hotelsLabelRef.value, hotelsTitleRef.value, hotelsDescRef.value, hotelsRowRef.value)

  // ─── Experiences ────────────────────────────────────────────────────────
  if (expsTitleRef.value) {
    const words = splitTextByWord(expsTitleRef.value)
    gsap.fromTo(words,
      { yPercent: 110, opacity: 0 },
      { yPercent: 0, opacity: 1, duration: 1, ease: 'power3.out', stagger: 0.06,
        scrollTrigger: { trigger: expsTitleRef.value, start: 'top 85%', once: true } }
    )
  }

  expItemsRef.value.forEach((item, i) => {
    if (!item) return
    const pics = item.querySelectorAll<HTMLElement>('.exp-pic img')
    const texts = item.querySelectorAll<HTMLElement>('.exp-desc .exp-tag, .exp-desc .exp-title-gsap, .exp-desc p, .exp-desc button')

    gsap.fromTo(pics,
      { clipPath: 'inset(100% 0 0 0)', scale: 1.08 },
      {
        clipPath: 'inset(0% 0 0 0)', scale: 1, duration: 1.3, ease: 'power4.out',
        scrollTrigger: { trigger: item, start: 'top 78%', once: true },
      }
    )

    gsap.fromTo(texts,
      { opacity: 0, x: i % 2 === 0 ? 50 : -50 },
      {
        opacity: 1, x: 0, duration: 0.9, ease: 'power3.out', stagger: 0.1,
        scrollTrigger: { trigger: item, start: 'top 75%', once: true },
      }
    )
  })

  // ─── CTA ────────────────────────────────────────────────────────────────
  if (ctaRef.value) {
    const ctaTl = gsap.timeline({
      scrollTrigger: { trigger: ctaRef.value, start: 'top 75%', once: true },
    })
    if (ctaTitleRef.value) {
      const words = splitTextByWord(ctaTitleRef.value)
      ctaTl.fromTo(words,
        { yPercent: 100, opacity: 0, filter: 'blur(8px)' },
        { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.05 }
      )
    }
    if (ctaDescRef.value) {
      ctaTl.fromTo(ctaDescRef.value, { opacity: 0, y: 20 }, { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' }, '-=0.5')
    }
    if (ctaBtnsRef.value) {
      ctaTl.fromTo(ctaBtnsRef.value.children,
        { opacity: 0, y: 30, scale: 0.9 },
        { opacity: 1, y: 0, scale: 1, duration: 0.7, ease: 'back.out(1.4)', stagger: 0.12 },
        '-=0.4'
      )
    }
  }
}

async function fetchHomeData() {
  try {
    const [statsRes, spotsRes, hotelsRes] = await Promise.all([
      getDashboardData(),
      getHotSpotsApi(4),
      getHotHotelsApi(4),
    ])

    // 填充统计数据
    const d = (statsRes as any).data || statsRes
    stats.value = [
      { label: '精选景点', v: String(d.totalSpots || 0), s: '+' },
      { label: '注册用户', v: String(d.totalUsers || 0), s: '+' },
      { label: '合作酒店', v: String(d.totalHotels || 0), s: '+' },
      { label: '今日订单', v: String(d.todayOrders || 0), s: '' },
    ]

    // 填充景点数据
    const spotRecords = (spotsRes as any).data?.records || (spotsRes as any).data || []
    spots.value = spotRecords.slice(0, 4).map((s: any) => ({
      id: s.id,
      name: s.name,
      city: s.city,
      level: s.level || '景点',
      price: s.ticketPrice,
      score: '4.8',
      img: s.coverImage || `https://picsum.photos/seed/spot${s.id}/600/400`,
      longitude: s.longitude ? Number(s.longitude) : 116.397428,
      latitude: s.latitude ? Number(s.latitude) : 39.90923,
    }))

    // 填充酒店数据
    const hotelRecords = (hotelsRes as any).data?.records || (hotelsRes as any).data || []
    hotels.value = hotelRecords.slice(0, 4).map((h: any, i: number) => ({
      id: h.id,
      name: h.name,
      city: h.city,
      type: h.starLevel ? `${h.starLevel}星` : '酒店',
      stars: '⭐'.repeat(h.starLevel || 3),
      price: 0,
      badge: ['人气之选', '网红打卡', '奢华体验', '最佳口碑'][i] || '推荐',
      img: h.coverImage || `https://picsum.photos/seed/hotel${h.id}/500/400`,
      longitude: h.longitude ? Number(h.longitude) : 116.397428,
      latitude: h.latitude ? Number(h.latitude) : 39.90923,
    }))

    // 数据加载完后重新初始化动画
    nextTick(() => {
      initGsapAnimations()
      // 初始化首页聚合地图
      const allMarkers = [
        ...spots.value.map((s: any) => ({ lng: s.longitude, lat: s.latitude, title: s.name, type: 'spot' as const })),
        ...hotels.value.map((h: any) => ({ lng: h.longitude, lat: h.latitude, title: h.name, type: 'hotel' as const })),
      ]
      if (allMarkers.length > 0) {
        const { initMap } = useAmap('home-amap', {
          longitude: 116.397428,
          latitude: 39.90923,
          zoom: 10,
          markers: allMarkers,
        })
        setTimeout(initMap, 500)
      }
    })
  } catch (e) {
    console.error('加载首页数据失败', e)
    // 失败时也初始化动画，避免界面卡住
    initGsapAnimations()
  }
}

onMounted(() => {
  heroTimer = setInterval(() => {
    heroIdx.value = (heroIdx.value + 1) % heroImages.length
  }, 5000)
  fetchHomeData()
})

onUnmounted(() => {
  clearInterval(heroTimer)
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped>
/* ── Reset ── */
.home { width: 100%; overflow-x: hidden; }

/* GSAP 动画初始状态 */
.gsap-card {
  will-change: transform, opacity;
}

/* 体验区标题初始透明（GSAP 接管） */
.exp-title-gsap {
  will-change: transform, opacity;
}

/* ── Hero ── */
.hero {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.hero-bg {
  position: absolute;
  inset: 0;
}
.hero-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 1.8s;
}
.hero-img.on { opacity: 1; }
.hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(160deg, rgba(8,8,28,0.7), rgba(20,20,55,0.5) 50%, rgba(0,0,0,0.65));
}
.hero-body {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 0 24px;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 30px;
  font-size: 13px;
  color: rgba(255,255,255,0.9);
  backdrop-filter: blur(12px);
  margin-bottom: 32px;
}
.badge-dot {
  width: 6px; height: 6px; background: #22c55e; border-radius: 50%;
  animation: blink 2s infinite;
}
@keyframes blink { 0%,100%{opacity:1} 50%{opacity:0.3} }
.hero-title {
  font-size: clamp(52px, 9vw, 110px);
  font-weight: 900;
  color: white;
  margin-bottom: 20px;
}
.hero-sub { font-size: 18px; color: rgba(255,255,255,0.65); margin-bottom: 40px; }
.hero-search {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}
.hero-search input {
  max-width: 360px; width: calc(100% - 120px); min-width: 200px; height: 52px; padding: 0 20px;
  background: rgba(255,255,255,0.12); border: 1.5px solid rgba(255,255,255,0.2);
  border-radius: 26px; color: white; font-size: 15px; outline: none;
  backdrop-filter: blur(20px); transition: all 0.3s;
}
.hero-search input::placeholder { color: rgba(255,255,255,0.45); }
.hero-search input:focus { background: rgba(255,255,255,0.18); border-color: rgba(255,255,255,0.5); }
.hero-search button {
  height: 52px; padding: 0 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none; border-radius: 26px; color: white; font-size: 15px; font-weight: 600;
  cursor: pointer; transition: all 0.3s;
}
.hero-search button:hover { transform: scale(1.04); box-shadow: 0 8px 30px rgba(102,126,234,0.5); }
.hero-search .ai-btn {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  display: flex; align-items: center; gap: 6px;
}
.hero-search .ai-btn:hover { box-shadow: 0 8px 30px rgba(240,147,251,0.5); }
.hero-tags { display: flex; justify-content: center; flex-wrap: wrap; gap: 10px; }
.hero-tags span {
  padding: 8px 18px; background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.15);
  border-radius: 20px; font-size: 13px; color: rgba(255,255,255,0.8); cursor: pointer; transition: all 0.3s;
}
.hero-tags span:hover { background: rgba(255,255,255,0.18); transform: translateY(-2px); }
.hero-dots {
  position: absolute; bottom: 100px; left: 50%; transform: translateX(-50%);
  display: flex; gap: 8px; z-index: 2;
}
.hero-dots b {
  width: 8px; height: 8px; border-radius: 50%; border: 1.5px solid rgba(255,255,255,0.5); cursor: pointer;
  transition: all 0.3s; background: transparent;
}
.hero-dots b.active { background: white; border-color: white; transform: scale(1.3); }

/* 向下滚动提示 */
.scroll-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  z-index: 3;
  opacity: 0;
}
.scroll-line {
  width: 1px;
  height: 50px;
  background: linear-gradient(to bottom, rgba(255,255,255,0.8), rgba(255,255,255,0));
  transform-origin: top;
}
.scroll-hint span {
  font-size: 11px;
  color: rgba(255,255,255,0.5);
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* ── Stats ── */
.stats { background: #0f0f23; overflow: hidden; }
.stats-row {
  max-width: 1200px; margin: 0 auto;
  display: flex; justify-content: space-around; gap: clamp(16px, 3vw, 40px);
  flex-wrap: wrap;
}
.stat-cell { text-align: center; will-change: transform, opacity; flex: 1; min-width: 160px; }
.stat-n { font-size: clamp(32px, 5vw, 56px); font-weight: 900; color: white; line-height: 1.1; }
.stat-n small { font-size: clamp(16px, 2.5vw, 28px); color: #667eea; }
.stat-l { font-size: clamp(11px, 1.5vw, 14px); color: rgba(255,255,255,0.5); text-transform: uppercase; letter-spacing: 2px; margin-top: 8px; word-break: break-all; }

/* ── Stats Responsive ── */
@media (max-width: 768px) {
  .stats-row { flex-direction: column; align-items: center; gap: 24px; }
  .stat-cell { width: 100%; max-width: 280px; }
  .stat-n { font-size: 36px; }
}
@media (max-width: 480px) {
  .stat-cell { max-width: 100%; }
}

/* ── Section Common ── */
.sec-head { text-align: center; margin-bottom: clamp(32px, 5vw, 60px); padding: 0 16px; }
.sec-label { display: inline-flex; align-items: center; gap: 12px; margin-bottom: 16px; font-size: clamp(11px, 1.5vw, 13px); color: #667eea; text-transform: uppercase; letter-spacing: 3px; font-weight: 700; }
.sec-head h2 { font-size: clamp(24px, 4vw, 44px); font-weight: 900; color: #1e293b; margin-bottom: 12px; }
.sec-head p { font-size: clamp(13px, 1.8vw, 16px); color: #94a3b8; }

/* ── Spots ── */
.spots { padding: clamp(60px, 8vw, 100px) clamp(16px, 4vw, 60px); background: #f8fafc; }
.spots-row {
  max-width: 1280px; margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: clamp(16px, 2vw, 24px);
}
.spot-card {
  border-radius: 20px; overflow: hidden; background: white;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08); cursor: pointer; transition: transform 0.3s, box-shadow 0.3s;
}
.spot-card:hover { transform: translateY(-8px); box-shadow: 0 20px 60px rgba(0,0,0,0.14); }
.spot-img { position: relative; height: clamp(140px, 20vw, 200px); overflow: hidden; }
.spot-img img { width: 100%; height: clamp(140px, 20vw, 200px); object-fit: cover; display: block; transition: transform 0.5s; }
.spot-card:hover .spot-img img { transform: scale(1.08); }
.spot-badge { position: absolute; top: 12px; right: 12px; background: #ef4444; color: white; padding: 2px 10px; border-radius: 10px; font-size: 11px; font-weight: 600; }
.spot-info { padding: 20px; }
.spot-meta { display: flex; justify-content: space-between; font-size: 12px; color: #94a3b8; margin-bottom: 8px; }
.spot-name { font-size: 16px; font-weight: 700; color: #1e293b; margin-bottom: 12px; }
.spot-price { font-size: 18px; font-weight: 700; color: #ef4444; }

/* ── Hotels ── */
.hotels { padding: clamp(60px, 8vw, 100px) clamp(16px, 4vw, 60px); background: #ffffff; }
.hotels-row {
  max-width: 1280px; margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: clamp(16px, 2vw, 24px);
}
.hotel-card {
  border-radius: 20px; overflow: hidden; background: white;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06); cursor: pointer; transition: transform 0.3s, box-shadow 0.3s;
}
.hotel-card:hover { transform: translateY(-8px); box-shadow: 0 20px 60px rgba(0,0,0,0.12); }
.hotel-img { position: relative; height: clamp(140px, 20vw, 200px); overflow: hidden; flex-shrink: 0; }
.hotel-img img { width: 100%; height: clamp(140px, 20vw, 200px); object-fit: cover; display: block; transition: transform 0.5s; }
.hotel-card:hover .hotel-img img { transform: scale(1.05); }
.hotel-badge { position: absolute; top: 12px; left: 12px; background: rgba(102,126,234,0.9); color: white; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.hotel-info { padding: 20px; }
.hotel-name { font-size: 15px; font-weight: 700; color: #1e293b; margin-bottom: 6px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.hotel-loc { font-size: 12px; color: #94a3b8; margin-bottom: 6px; }
.hotel-stars { font-size: 12px; margin-bottom: 8px; }
.hotel-price { font-size: 20px; font-weight: 700; color: #ef4444; }
.hotel-price small { font-size: 12px; color: #94a3b8; }

/* ── Experiences ── */
.exps { padding: clamp(60px, 8vw, 100px) clamp(16px, 4vw, 60px); background: #f8fafc; }
.exp-list { max-width: 1280px; margin: 0 auto; display: flex; flex-direction: column; gap: clamp(48px, 8vw, 80px); }
.exp-item {
  display: grid; grid-template-columns: 1fr 1fr; gap: clamp(32px, 6vw, 80px); align-items: center;
}
@media (max-width: 768px) {
  .exp-item, .exp-item.rev { grid-template-columns: 1fr; direction: ltr; gap: 24px; }
  .exp-pic { height: 240px; }
  .exp-pic img { height: 240px; }
  .exp-desc h3 { font-size: clamp(22px, 4vw, 32px); }
}
.exp-item.rev { direction: rtl; }
.exp-item.rev .exp-desc { direction: ltr; }
.exp-pic { position: relative; border-radius: 24px; overflow: hidden; height: 400px; }
.exp-pic img { width: 100%; height: 400px; object-fit: cover; display: block; }
.exp-n { position: absolute; top: 20px; left: 20px; font-size: 80px; font-weight: 900; color: rgba(255,255,255,0.15); line-height: 1; }
.exp-desc { padding: 20px; }
.exp-tag { display: inline-block; padding: 4px 16px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; border-radius: 20px; font-size: 12px; margin-bottom: 20px; }
.exp-desc h3 { font-size: clamp(22px, 3.5vw, 32px); font-weight: 800; color: #1e293b; margin-bottom: 16px; }
.exp-desc p { font-size: clamp(13px, 1.8vw, 16px); color: #64748b; line-height: 1.8; margin-bottom: clamp(20px, 3vw, 32px); }
.exp-desc button { padding: clamp(10px, 1.5vw, 12px) clamp(20px, 3vw, 28px); background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; border-radius: 30px; font-size: clamp(12px, 1.5vw, 14px); font-weight: 600; cursor: pointer; transition: all 0.3s; }
.exp-desc button:hover { transform: translateX(4px); box-shadow: 0 8px 24px rgba(102,126,234,0.4); }

/* ── CTA ── */
.cta {
  padding: clamp(60px, 8vw, 100px) clamp(16px, 4vw, 60px);
  background: linear-gradient(135deg, #0f0f23 0%, #1e1b4b 50%, #312e81 100%);
  text-align: center;
}
.cta-inner h2 { font-size: clamp(26px, 5vw, 48px); font-weight: 900; color: white; margin-bottom: 16px; }
.cta-inner p { font-size: clamp(14px, 2vw, 18px); color: rgba(255,255,255,0.6); margin-bottom: clamp(24px, 4vw, 40px); }
.cta-btns { display: flex; justify-content: center; gap: 16px; flex-wrap: wrap; }
.btn-pri { padding: 16px 40px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; border-radius: 40px; font-size: 16px; font-weight: 700; text-decoration: none; transition: all 0.3s; }
.btn-pri:hover { transform: translateY(-3px); box-shadow: 0 12px 40px rgba(102,126,234,0.5); }
.btn-out { padding: 16px 40px; background: transparent; color: white; border: 2px solid rgba(255,255,255,0.4); border-radius: 40px; font-size: 16px; font-weight: 600; text-decoration: none; transition: all 0.3s; }
.btn-out:hover { background: rgba(255,255,255,0.1); border-color: rgba(255,255,255,0.7); }

/* ── Footer ── */
.page-foot { padding: clamp(32px, 5vw, 48px) clamp(16px, 4vw, 60px); background: #0a0a1a; text-align: center; }
.foot-logo { font-size: 20px; font-weight: 900; color: white; letter-spacing: 4px; margin-bottom: 12px; }
.page-foot p { font-size: 13px; color: rgba(255,255,255,0.3); }

/* ── 首页地图 ── */
.home-map {
  background: #f8fafc;
  padding: 80px 24px;
  text-align: center;

  .map-head {
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
      color: #64748b;
      margin-bottom: 24px;
    }
  }

  .map-legend {
    display: flex;
    justify-content: center;
    gap: 24px;
    margin-bottom: 24px;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #64748b;
      font-weight: 600;

      .dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
      }

      .spot-dot { background: #3b82f6; }
      .hotel-dot { background: #f97316; }
    }
  }

  .home-map-wrap {
    max-width: 1100px;
    margin: 0 auto;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 8px 40px rgba(0, 0, 0, 0.12);
    border: 1px solid #f1f5f9;
  }

  .home-amap {
    width: 100%;
    height: 450px;
    background: #e8f4f8;
  }
}
</style>
