<template>
  <div class="spot-detail-page">

    <!-- ── 顶部 Hero ── -->
    <div class="spot-hero" ref="heroRef">
      <img :src="currentSpot.cover" class="hero-img" ref="heroImgRef" alt="" />
      <div class="hero-overlay" />
      <div class="hero-content" ref="heroContentRef">
        <span class="hero-badge" ref="heroBadgeRef">{{ currentSpot.level }}</span>
        <h1 class="hero-title" ref="heroTitleRef">{{ currentSpot.name }}</h1>
        <p class="hero-sub" ref="heroSubRef">📍 {{ currentSpot.city }} · {{ currentSpot.address }}</p>
        <div class="hero-meta" ref="heroMetaRef">
          <span class="meta-item">⭐ {{ currentSpot.score }} 分</span>
          <span class="meta-divider">|</span>
          <span class="meta-item">🔥 {{ currentSpot.hotScore }} 热度</span>
          <span class="meta-divider">|</span>
          <span class="meta-item">🕐 {{ currentSpot.openTime }}</span>
        </div>
        <div class="hero-tags" ref="heroTagsRef">
          <span v-for="tag in currentSpot.tags" :key="tag">{{ tag }}</span>
        </div>
      </div>
      <!-- 背景模糊叠加 -->
      <div class="hero-bg-blur" :style="{ backgroundImage: `url(${currentSpot.cover})` }" />
    </div>

    <!-- ── 景区简介（可滚动图文区）── -->
    <div class="intro-scroll-wrap">
      <div class="intro-scroll-inner">
        <!-- 景区名+文字介绍（首屏展示） -->
        <div class="intro-banner">
          <div class="intro-label">景区简介</div>
          <h2 ref="introTitleRef">{{ currentSpot.name }}</h2>
          <p class="intro-text" ref="introTextRef">{{ currentSpot.description }}</p>
          <div class="intro-meta">
            <span v-if="currentSpot.score">⭐ {{ currentSpot.score }} 分</span>
            <span v-if="currentSpot.level">🏅 {{ currentSpot.level }}</span>
            <span v-if="currentSpot.city">📍 {{ currentSpot.city }}</span>
          </div>
        </div>

        <!-- ── 图文交替区块（与首页「与众不同」风格一致）── -->
        <div class="intro-sec-head">
          <div class="sec-label"><span>01</span><span>特色体验</span></div>
          <h2 ref="expHeadingRef">与众不同的旅行方式</h2>
        </div>
        <div class="intro-panels">
          <div
            v-for="(section, i) in currentSpot.sections"
            :key="section.title"
            class="intro-panel"
            :class="{ rev: i % 2 !== 0 }"
          >
            <div class="panel-pic">
              <img :src="section.img" :alt="section.title" />
              <div class="panel-num">{{ String(i + 1).padStart(2, '0') }}</div>
            </div>
            <div class="panel-desc">
              <div class="panel-tag">{{ section.tag }}</div>
              <h3>{{ section.title }}</h3>
              <p>{{ section.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── 实用信息 ── -->
    <div class="info-section" ref="infoSectionRef">
      <div class="info-inner">
        <div class="info-label">实用信息</div>
        <h2 ref="infoTitleRef">游览指南</h2>

        <div class="info-grid" ref="infoGridRef">
          <div v-for="info in currentSpot.practicalInfo" :key="info.title" class="info-card">
            <div class="info-icon">{{ info.icon }}</div>
            <div class="info-title">{{ info.title }}</div>
            <div class="info-value">{{ info.value }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- ── CTA ── -->
    <div class="detail-cta" ref="ctaSectionRef">
      <div class="cta-content">
        <h2 ref="ctaTitleRef">想要亲身感受{{ currentSpot.name }}的魅力吗？</h2>
        <p ref="ctaDescRef">立即预订，开启你的完美旅程</p>
        <div class="cta-price">
          <span class="price-from">起</span>
          <span class="price-num">¥{{ currentSpot.ticketPrice }}</span>
          <span class="price-unit">/人</span>
        </div>
        <div class="cta-btns">
          <button class="btn-book" @click="$router.push(`/book/spot/${currentSpot.id || $route.params.id}`)">立即预订</button>
          <button class="btn-back" @click="$router.back()">返回列表</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getSpotDetailApi } from '@/api/spot'
import { splitTextByWord } from '@/composables/useGsapAnimation'

gsap.registerPlugin(ScrollTrigger)

const route = useRoute()
const spotId = computed(() => route.params.id as string)


const spotData = ref<any>(null)

const currentSpot = computed(() => {
  if (spotData.value) {
    const s = spotData.value

    // 构建 sections（章节内容）
    const sections = []
    for (let i = 1; i <= 3; i++) {
      const title = s[`section${i}Title`]
      const content = s[`section${i}Content`]
      const image = s[`section${i}Image`]
      if (title) {
        sections.push({
          tag: '推荐',
          title,
          desc: content || '',
          img: image || `https://picsum.photos/seed/${s.id}_sec${i}/800/500`,
          highlights: []
        })
      }
    }

    // 构建 practicalInfo（实用信息）
    const practicalInfo = []
    if (s.openTime) practicalInfo.push({ title: '开放时间', icon: '🕘', value: s.openTime })
    if (s.ticketPrice != null) practicalInfo.push({ title: '门票价格', icon: '🎫', value: `¥${Number(s.ticketPrice).toFixed(0)}/人` })
    if (s.suggestedTime) practicalInfo.push({ title: '建议游览', icon: '⏱', value: `约${s.suggestedTime}小时` })
    if (s.traffic) practicalInfo.push({ title: '交通指南', icon: '🚌', value: s.traffic })
    if (s.tips) practicalInfo.push({ title: '游览贴士', icon: '💡', value: s.tips })

    return {
      id: s.id,
      name: s.name,
      level: s.level || '景点',
      city: s.city,
      address: s.address || '',
      cover: s.coverImage || '',
      score: s.score || '4.8',
      hotScore: s.hotScore || 0,
      openTime: s.open_time || '',
      tags: s.tags ? s.tags.split(',') : [],
      ticketPrice: Number(s.ticketPrice) || 0,
      description: s.description || '',
      introStats: [],
      sections,
      practicalInfo,
    }
  }
  return { name: '', level: '', city: '', address: '', cover: '', score: '0', hotScore: 0, openTime: '', tags: [], ticketPrice: 0, description: '', introStats: [], sections: [], practicalInfo: [] }
})

// ─── Refs ───────────────────────────────────────────────────────────────────
const heroRef = ref<HTMLElement | null>(null)
const heroImgRef = ref<HTMLElement | null>(null)
const heroContentRef = ref<HTMLElement | null>(null)
const heroBadgeRef = ref<HTMLElement | null>(null)
const heroTitleRef = ref<HTMLElement | null>(null)
const heroSubRef = ref<HTMLElement | null>(null)
const heroMetaRef = ref<HTMLElement | null>(null)
const heroTagsRef = ref<HTMLElement | null>(null)
const introRef = ref<HTMLElement | null>(null)
const introTitleRef = ref<HTMLElement | null>(null)
const introTextRef = ref<HTMLElement | null>(null)
const introStatsRef = ref<HTMLElement | null>(null)
const expHeadingRef = ref<HTMLElement | null>(null)
const infoSectionRef = ref<HTMLElement | null>(null)
const infoTitleRef = ref<HTMLElement | null>(null)
const infoGridRef = ref<HTMLElement | null>(null)
const ctaSectionRef = ref<HTMLElement | null>(null)
const ctaTitleRef = ref<HTMLElement | null>(null)
const ctaDescRef = ref<HTMLElement | null>(null)


// ─── GSAP 动画 ───────────────────────────────────────────────────────────────
function initGsapAnimations() {
  // Hero 入场
  const heroTl = gsap.timeline({ delay: 0.2 })
  if (heroBadgeRef.value) {
    heroTl.fromTo(heroBadgeRef.value,
      { opacity: 0, y: 20, filter: 'blur(8px)' },
      { opacity: 1, y: 0, filter: 'blur(0px)', duration: 0.6, ease: 'power3.out' }
    )
  }
  if (heroTitleRef.value) {
    const words = splitTextByWord(heroTitleRef.value)
    heroTl.fromTo(words,
      { yPercent: 110, opacity: 0, filter: 'blur(6px)' },
      { yPercent: 0, opacity: 1, filter: 'blur(0px)', duration: 1, ease: 'power4.out', stagger: 0.04 },
      '-=0.4'
    )
  }
  if (heroSubRef.value) {
    heroTl.fromTo(heroSubRef.value, { opacity: 0, y: 20 }, { opacity: 1, y: 0, duration: 0.7, ease: 'power3.out' }, '-=0.5')
  }
  if (heroMetaRef.value) {
    heroTl.fromTo(heroMetaRef.value, { opacity: 0, y: 15 }, { opacity: 1, y: 0, duration: 0.6, ease: 'power3.out' }, '-=0.3')
  }
  if (heroTagsRef.value) {
    heroTl.fromTo(heroTagsRef.value.children,
      { opacity: 0, scale: 0.8 },
      { opacity: 1, scale: 1, duration: 0.5, ease: 'back.out(1.7)', stagger: 0.06 },
      '-=0.3'
    )
  }
  if (heroImgRef.value) {
    gsap.fromTo(heroImgRef.value,
      { scale: 1.1 },
      { scale: 1, duration: 1.5, ease: 'power2.out', delay: 0.1 }
    )
  }

  // Hero 视差
  if (heroImgRef.value) {
    gsap.to(heroImgRef.value, {
      yPercent: 20,
      ease: 'none',
      scrollTrigger: {
        trigger: heroRef.value,
        start: 'top top',
        end: 'bottom top',
        scrub: true,
      },
    })
  }

  // 简介
  if (introRef.value) {
    const introTl = gsap.timeline({
      scrollTrigger: { trigger: introRef.value, start: 'top 80%', once: true },
    })
    if (introTitleRef.value) {
      introTl.fromTo(introTitleRef.value,
        { opacity: 0, y: 30 },
        { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' }
      )
    }
    if (introTextRef.value) {
      introTl.fromTo(introTextRef.value,
        { opacity: 0, y: 20 },
        { opacity: 1, y: 0, duration: 0.7, ease: 'power3.out' },
        '-=0.4'
      )
    }
    if (introStatsRef.value) {
      introTl.fromTo(introStatsRef.value.children,
        { opacity: 0, y: 30, scale: 0.9 },
        { opacity: 1, y: 0, scale: 1, duration: 0.6, ease: 'back.out(1.4)', stagger: 0.1 },
        '-=0.3'
      )
    }
  }

  // ── 特色体验区块动画（与首页「与众不同」一致）──
  // 标题逐字 reveal
  if (expHeadingRef.value) {
    const words = splitTextByWord(expHeadingRef.value)
    gsap.fromTo(words,
      { yPercent: 110, opacity: 0 },
      {
        yPercent: 0, opacity: 1, duration: 1, ease: 'power3.out', stagger: 0.06,
        scrollTrigger: { trigger: expHeadingRef.value, start: 'top 85%', once: true },
      }
    )
  }

  // 图文块动画（直接查 DOM，避免 v-for ref 时机问题）
  const panelItems = document.querySelectorAll<HTMLElement>('.intro-panel')
  panelItems.forEach((item, i) => {
    const pics = item.querySelectorAll<HTMLElement>('.panel-pic img')
    const texts = item.querySelectorAll<HTMLElement>('.panel-tag, h3, p')

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

  // 实用信息
  if (infoSectionRef.value) {
    const infoTl = gsap.timeline({
      scrollTrigger: { trigger: infoSectionRef.value, start: 'top 80%', once: true },
    })
    if (infoTitleRef.value) {
      infoTl.fromTo(infoTitleRef.value,
        { opacity: 0, y: 30 },
        { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out' }
      )
    }
    if (infoGridRef.value) {
      infoTl.fromTo(infoGridRef.value.children,
        { opacity: 0, y: 30, scale: 0.92 },
        { opacity: 1, y: 0, scale: 1, duration: 0.6, ease: 'back.out(1.5)', stagger: 0.08 },
        '-=0.4'
      )
    }
  }

  // CTA
  if (ctaSectionRef.value) {
    const ctaTl = gsap.timeline({
      scrollTrigger: { trigger: ctaSectionRef.value, start: 'top 80%', once: true },
    })
    if (ctaTitleRef.value) {
      const words = splitTextByWord(ctaTitleRef.value)
      ctaTl.fromTo(words,
        { yPercent: 100, opacity: 0 },
        { yPercent: 0, opacity: 1, duration: 0.8, ease: 'power3.out', stagger: 0.04 }
      )
    }
    if (ctaDescRef.value) {
      ctaTl.fromTo(ctaDescRef.value, { opacity: 0, y: 20 }, { opacity: 1, y: 0, duration: 0.6, ease: 'power3.out' }, '-=0.4')
    }
  }
}

async function fetchSpot() {
  try {
    const res: any = await getSpotDetailApi(Number(spotId.value))
    spotData.value = (res.data || res) || null
  } catch (e) {
    console.error('加载景点详情失败', e)
  }
}

onMounted(() => {
  fetchSpot().then(() => {
    nextTick(() => initGsapAnimations())
  })
})

onUnmounted(() => {
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped lang="scss">
.spot-detail-page {
  background: #f8fafc;
  min-height: 100vh;
}

/* ── Hero ── */
.spot-hero {
  position: relative;
  height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .hero-bg-blur {
    position: absolute;
    inset: -20px;
    background-size: cover;
    background-position: center;
    filter: blur(30px) brightness(0.4);
    transform: scale(1.1);
    z-index: 0;
  }

  .hero-img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    z-index: 1;
  }

  .hero-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(
      to bottom,
      rgba(0,0,0,0.3) 0%,
      rgba(0,0,0,0.2) 40%,
      rgba(0,0,0,0.6) 80%,
      rgba(0,0,0,0.85) 100%
    );
    z-index: 2;
  }

  .hero-content {
    position: relative;
    z-index: 3;
    text-align: center;
    padding: 0 24px;
    max-width: 900px;
  }

  .hero-badge {
    display: inline-block;
    padding: 6px 20px;
    background: rgba(102,126,234,0.8);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255,255,255,0.2);
    border-radius: 30px;
    color: white;
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 1px;
    margin-bottom: 20px;
  }

  .hero-title {
    font-size: clamp(48px, 8vw, 96px);
    font-weight: 900;
    color: white;
    margin-bottom: 16px;
    text-shadow: 0 4px 30px rgba(0,0,0,0.5);
  }

  .hero-sub {
    font-size: 18px;
    color: rgba(255,255,255,0.75);
    margin-bottom: 20px;
  }

  .hero-meta {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-bottom: 24px;
    flex-wrap: wrap;

    .meta-item { font-size: 14px; color: rgba(255,255,255,0.7); }
    .meta-divider { color: rgba(255,255,255,0.3); }
  }

  .hero-tags {
    display: flex;
    justify-content: center;
    gap: 10px;
    flex-wrap: wrap;

    span {
      padding: 6px 16px;
      background: rgba(255,255,255,0.1);
      backdrop-filter: blur(8px);
      border: 1px solid rgba(255,255,255,0.15);
      border-radius: 20px;
      color: white;
      font-size: 13px;
      font-weight: 500;
    }
  }
}

/* ── 景区简介（可滚动图文区）── */
.intro-scroll-wrap {
  background: #f8fafc;
  padding: 80px 24px 40px;
}

.intro-scroll-inner {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
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
  }
}

.intro-banner {
  text-align: center;
  margin-bottom: 40px;

  .intro-label {
    font-size: 13px;
    color: #667eea;
    text-transform: uppercase;
    letter-spacing: 3px;
    font-weight: 700;
    margin-bottom: 16px;
  }

  h2 {
    font-size: clamp(32px, 5vw, 52px);
    font-weight: 900;
    color: #1e293b;
    margin-bottom: 20px;
  }

  .intro-text {
    font-size: 16px;
    color: #64748b;
    line-height: 1.9;
    max-width: 760px;
    margin: 0 auto 24px;
  }

  .intro-meta {
    display: flex;
    justify-content: center;
    gap: 24px;
    flex-wrap: wrap;

    span {
      font-size: 14px;
      color: #94a3b8;
      background: #f8fafc;
      padding: 6px 16px;
      border-radius: 20px;
      border: 1px solid #f1f5f9;
    }
  }
}

/* 图文交替区块 */
.intro-sec-head {
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
  }
}

.intro-panels {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 80px;
}

.intro-panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  border-radius: 24px;
  overflow: hidden;

  &.rev {
    direction: rtl;
    .panel-desc { direction: ltr; }
  }

  .panel-pic {
    position: relative;
    overflow: hidden;
    border-radius: 24px;

    img {
      width: 100%;
      height: 400px;
      object-fit: cover;
      display: block;
      transition: transform 0.6s ease;
    }

    &:hover img { transform: scale(1.05); }

    .panel-num {
      position: absolute;
      top: 20px;
      left: 20px;
      font-size: 72px;
      font-weight: 900;
      color: rgba(255,255,255,0.15);
      line-height: 1;
      pointer-events: none;
    }
  }

  .panel-desc {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 32px 40px;
    background: #f8fafc;

    .panel-tag {
      font-size: 12px;
      font-weight: 700;
      color: #667eea;
      letter-spacing: 2px;
      text-transform: uppercase;
      margin-bottom: 12px;
    }

    h3 {
      font-size: clamp(24px, 2.5vw, 32px);
      font-weight: 800;
      color: #1e293b;
      margin-bottom: 16px;
      line-height: 1.2;
    }

    p {
      font-size: 15px;
      color: #64748b;
      line-height: 1.9;
    }
  }
}

/* 可滚动图文面板容器 */
.exp-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 80px;
}

.exp-item {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 400px;

  &.rev { direction: rtl; }
  &.rev .exp-desc { direction: ltr; }
}

.exp-pic {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  height: 400px;

  img {
    width: 100%;
    height: 400px;
    object-fit: cover;
    display: block;
  }

  .exp-n {
    position: absolute;
    top: 20px;
    left: 20px;
    font-size: 72px;
    font-weight: 900;
    color: rgba(255,255,255,0.15);
    line-height: 1;
    pointer-events: none;
  }
}

.exp-desc {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;

  .exp-tag {
    font-size: 12px;
    font-weight: 700;
    color: #667eea;
    letter-spacing: 2px;
    text-transform: uppercase;
    margin-bottom: 12px;
  }

  h3 {
    font-size: 32px;
    font-weight: 800;
    color: #1e293b;
    margin-bottom: 16px;
  }

  p {
    font-size: 16px;
    color: #64748b;
    line-height: 1.8;
    margin-bottom: 16px;
  }

  button {
    align-self: flex-start;
    padding: 12px 28px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    border-radius: 30px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateX(4px);
      box-shadow: 0 8px 24px rgba(102,126,234,0.4);
    }
  }
}

/* ── 详情交替区块 ── */
.detail-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 600px;
  overflow: hidden;

  @media (max-width: 800px) {
    grid-template-columns: 1fr;
  }

  &.rev {
    direction: rtl;
    .section-text { direction: ltr; }

    @media (max-width: 800px) { direction: ltr; }
  }

  .section-pic {
    position: relative;
    overflow: hidden;
    will-change: clip-path, transform;
    min-height: 500px;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    .pic-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(135deg, rgba(0,0,0,0.15), transparent);
    }

    .pic-num {
      position: absolute;
      top: 24px;
      left: 24px;
      font-size: 80px;
      font-weight: 900;
      color: rgba(255,255,255,0.12);
      line-height: 1;
      pointer-events: none;
    }
  }

  .section-text {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 60px 60px;
    background: #fff;
    will-change: transform, opacity;

    @media (max-width: 1100px) { padding: 40px; }

    .section-tag {
      font-size: 12px;
      font-weight: 700;
      color: #667eea;
      letter-spacing: 2px;
      text-transform: uppercase;
      margin-bottom: 16px;
    }

    .section-title {
      font-size: clamp(24px, 3vw, 38px);
      font-weight: 900;
      color: #1e293b;
      margin-bottom: 20px;
      line-height: 1.2;
    }

    .section-desc {
      font-size: 15px;
      color: #64748b;
      line-height: 1.9;
      margin-bottom: 32px;
    }

    .section-highlights {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;

      .highlight-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 16px;
        background: #f8fafc;
        border-radius: 12px;
        border: 1px solid #f1f5f9;

        .highlight-icon { font-size: 24px; flex-shrink: 0; }

        .highlight-label {
          font-size: 11px;
          color: #94a3b8;
          text-transform: uppercase;
          letter-spacing: 1px;
        }

        .highlight-value {
          font-size: 15px;
          font-weight: 700;
          color: #1e293b;
        }
      }
    }
  }
}

/* ── 实用信息 ── */
.info-section {
  background: #f8fafc;
  padding: 80px 24px;

  .info-inner {
    max-width: 1100px;
    margin: 0 auto;
    text-align: center;
  }

  .info-label {
    font-size: 13px;
    color: #667eea;
    text-transform: uppercase;
    letter-spacing: 3px;
    font-weight: 700;
    margin-bottom: 16px;
  }

  h2 {
    font-size: clamp(28px, 4vw, 48px);
    font-weight: 900;
    color: #1e293b;
    margin-bottom: 48px;
  }

  .info-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;

    @media (max-width: 800px) { grid-template-columns: repeat(2, 1fr); }
    @media (max-width: 500px) { grid-template-columns: 1fr; }

    .info-card {
      padding: 28px 20px;
      background: white;
      border-radius: 20px;
      border: 1px solid #f1f5f9;
      text-align: center;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 40px rgba(102,126,234,0.12);
        border-color: rgba(102,126,234,0.2);
      }

      .info-icon { font-size: 36px; margin-bottom: 12px; }
      .info-title { font-size: 14px; color: #94a3b8; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 8px; }
      .info-value { font-size: 15px; color: #1e293b; font-weight: 600; line-height: 1.5; }
    }
  }
}

/* ── CTA ── */
.detail-cta {
  background: linear-gradient(135deg, #0f0f23 0%, #1e1b4b 50%, #312e81 100%);
  padding: 100px 24px;
  text-align: center;

  .cta-content {
    max-width: 700px;
    margin: 0 auto;
  }

  h2 {
    font-size: clamp(28px, 4vw, 48px);
    font-weight: 900;
    color: white;
    margin-bottom: 12px;
  }

  p {
    font-size: 18px;
    color: rgba(255,255,255,0.55);
    margin-bottom: 32px;
  }

  .cta-price {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 4px;
    margin-bottom: 32px;

    .price-from { font-size: 16px; color: rgba(255,255,255,0.5); }
    .price-num { font-size: 56px; font-weight: 900; color: #ef4444; }
    .price-unit { font-size: 16px; color: rgba(255,255,255,0.5); }
  }

  .cta-btns {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;

    .btn-book {
      padding: 14px 40px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      border: none;
      border-radius: 40px;
      font-size: 16px;
      font-weight: 700;
      cursor: pointer;
      transition: all 0.3s;
      &:hover { transform: translateY(-3px); box-shadow: 0 12px 40px rgba(102,126,234,0.5); }
    }

    .btn-back {
      padding: 14px 40px;
      background: transparent;
      color: rgba(255,255,255,0.7);
      border: 2px solid rgba(255,255,255,0.3);
      border-radius: 40px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;
      &:hover { background: rgba(255,255,255,0.1); color: white; }
    }
  }
}
</style>
