<template>
  <div class="hotel-detail-page">
    <div class="carousel-section" ref="carouselRef">
      <div class="carousel-wrapper" @mouseenter="pauseDanmaku" @mouseleave="resumeDanmaku">
        <div v-for="(img, i) in currentHotel.images" :key="i" class="carousel-item" :class="{ active: carouselIdx === i }">
          <img :src="img" :alt="currentHotel.name" />
          <div class="carousel-overlay"></div>
        </div>
        <div class="danmaku-layer" ref="danmakuLayerRef">
          <div v-for="dm in visibleDanmaku" :key="dm.id" class="danmaku-item" :style="{ top: dm.top + 'px', animationDuration: dm.duration + 's', animationDelay: dm.delay + 's', '--dm-color': dm.color }">
            <span class="dm-avatar">{{ dm.avatar }}</span>
            <span class="dm-text">{{ dm.text }}</span>
          </div>
        </div>
        <div class="carousel-controls">
          <button class="ctrl-btn" @click="prevImage">‹</button>
          <div class="carousel-dots">
            <span v-for="(_, i) in currentHotel.images" :key="i" class="dot" :class="{ active: carouselIdx === i }" @click="carouselIdx = i"></span>
          </div>
          <button class="ctrl-btn" @click="nextImage">›</button>
        </div>
        <div class="carousel-info">
          <div class="info-left">
            <div class="hotel-score">
              <span class="score-num">{{ currentHotel.score }}</span>
              <span class="score-label">分</span>
            </div>
            <div class="hotel-reviews">{{ currentHotel.commentCount }}条评价</div>
          </div>
          <div class="info-right">
            <h1 class="hotel-name">{{ currentHotel.name }}</h1>
            <div class="hotel-tags">
              <span v-for="t in currentHotel.tags" :key="t">{{ t }}</span>
            </div>
            <div class="hotel-address">📍 {{ currentHotel.city }} · {{ currentHotel.address }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="booking-section" ref="bookingRef">
      <div class="booking-inner">
        <div class="room-list" ref="roomListRef">
          <div class="room-list-title">选择房型</div>
          <div v-for="room in currentHotel.rooms" :key="room.id" class="room-card" :class="{ selected: selectedRoom?.id === room.id }" @click="selectedRoom = room">
            <div class="room-img">
              <img :src="room.img" :alt="room.name" />
              <div v-if="room.popular" class="room-popular-tag">人气爆款</div>
            </div>
            <div class="room-info">
              <div class="room-name">{{ room.name }}</div>
              <div class="room-facilities">
                <span v-for="f in room.facilities.slice(0, 3)" :key="f">{{ f }}</span>
              </div>
              <div class="room-price">
                <span class="price-num">¥{{ room.price }}</span>
                <span class="price-unit">/晚</span>
              </div>
            </div>
            <div class="room-select-indicator">
              <span v-if="selectedRoom?.id === room.id">✓ 已选</span>
              <span v-else>选择</span>
            </div>
          </div>
        </div>
        <div class="booking-form" ref="bookingFormRef">
          <div class="booking-card">
            <div class="booking-price-header">
              <div class="selected-room-name">{{ selectedRoom?.name || '请先选择房型' }}</div>
              <div class="price-display">
                <span class="big-price">¥{{ selectedRoom?.price || 0 }}</span>
                <span class="price-unit">/晚</span>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">入住日期</label>
              <el-date-picker v-model="checkInDate" type="date" placeholder="选择入住日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled-date="disabledCheckInDate" size="large" style="width: 100%;"></el-date-picker>
            </div>
            <div class="form-group">
              <label class="form-label">退房日期</label>
              <el-date-picker v-model="checkOutDate" type="date" placeholder="选择退房日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled-date="disabledCheckOutDate" size="large" style="width: 100%;"></el-date-picker>
            </div>
            <div v-if="nightCount > 0" class="night-info">共 <b>{{ nightCount }}</b> 晚</div>
            <div class="form-group">
              <label class="form-label">入住人数</label>
              <el-input-number v-model="guestCount" :min="1" :max="selectedRoom?.maxGuest || 10" size="large"></el-input-number>
            </div>
            <div v-if="nightCount > 0 && selectedRoom" class="price-breakdown">
              <div class="breakdown-row">
                <span>¥{{ selectedRoom.price }} × {{ nightCount }}晚</span>
                <span>¥{{ (selectedRoom.price * nightCount).toLocaleString() }}</span>
              </div>
              <div class="breakdown-row">
                <span>服务费</span>
                <span>¥{{ serviceFee }}</span>
              </div>
              <div class="breakdown-total">
                <span>合计</span>
                <span class="total-num">¥{{ totalPrice.toLocaleString() }}</span>
              </div>
            </div>
            <button class="book-btn" :disabled="!canBook" @click="handleBook">{{ canBook ? '立即预订' : '请选择日期和房型' }}</button>
            <div class="hotel-facilities">
              <div class="facilities-title">酒店设施</div>
              <div class="facilities-tags">
                <span v-for="f in currentHotel.facilities" :key="f">{{ f }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="hotel-intro-section" ref="introSectionRef">
      <div class="intro-inner">
        <div class="intro-label">关于酒店</div>
        <h2 ref="introTitleRef">{{ currentHotel.name }}</h2>
        <p class="intro-desc" ref="introDescRef">{{ currentHotel.description }}</p>
        <div class="intro-gallery" ref="galleryRef">
          <div v-for="(img, i) in currentHotel.images.slice(0, 4)" :key="i" class="gallery-item" :class="{ wide: i === 0 }" @click="carouselIdx = i">
            <img :src="img" alt="" />
          </div>
        </div>
      </div>
    </div>

    <div class="reviews-section" ref="reviewsSectionRef">
      <div class="reviews-inner">
        <div class="reviews-header">
          <div class="reviews-title">住客点评</div>
          <div class="reviews-summary">
            <span class="summary-score">{{ currentHotel.score }}</span>
            <div class="summary-info">
              <div class="summary-stars">{{ '⭐'.repeat(Math.round(currentHotel.score)) }}</div>
              <div class="summary-count">{{ currentHotel.commentCount }}条评价</div>
            </div>
          </div>
        </div>
        <div class="reviews-list" ref="reviewsListRef">
          <div v-for="review in currentHotel.reviews" :key="review.id" class="review-card">
            <div class="review-header">
              <div class="reviewer-avatar">{{ review.nickname.slice(0, 1) }}</div>
              <div class="reviewer-info">
                <div class="reviewer-name">{{ review.nickname }}</div>
                <div class="reviewer-meta">{{ review.roomType }} · {{ review.date }}</div>
              </div>
              <div class="review-score">⭐ {{ review.score }}</div>
            </div>
            <div class="review-body">{{ review.content }}</div>
            <div v-if="review.images?.length" class="review-images">
              <img v-for="(img, i) in review.images" :key="i" :src="img" alt="" />
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'
import { getHotelDetailApi, getHotelRoomsApi } from '@/api/hotel'

gsap.registerPlugin(ScrollTrigger)

const route = useRoute()
const router = useRouter()
const hotelId = computed(() => route.params.id as string)

// ─── 轮播 ───────────────────────────────────────────────────────────────────
const carouselIdx = ref(0)
const carouselRef = ref<HTMLElement | null>(null)

function prevImage() {
  carouselIdx.value = (carouselIdx.value - 1 + currentHotel.value.images.length) % currentHotel.value.images.length
}
function nextImage() {
  carouselIdx.value = (carouselIdx.value + 1) % currentHotel.value.images.length
}

// ─── 弹幕 ───────────────────────────────────────────────────────────────────
const danmakuLayerRef = ref<HTMLElement | null>(null)
const danmakuPaused = ref(false)

interface Danmaku {
  id: number
  text: string
  avatar: string
  top: number
  duration: number
  delay: number
  color: string
}

const allDanmaku = ref<Danmaku[]>([])
const visibleDanmaku = ref<Danmaku[]>([])

function genDanmaku() {
  const avatars = ['🐰', '🐻', '🦊', '🐼', '🐨', '🦁', '🐯', '🐸']
  const texts = ['位置超棒！', '服务很好～', '下次还来', '性价比高', '早餐丰富', '房间干净', '推荐推荐！', '设施完善']
  const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F']
  const items: Danmaku[] = []
  for (let i = 0; i < 12; i++) {
    items.push({
      id: i,
      text: texts[i % texts.length],
      avatar: avatars[Math.floor(Math.random() * avatars.length)],
      top: Math.random() * 300 + 20,
      duration: 4 + Math.random() * 4,
      delay: Math.random() * 5,
      color: colors[Math.floor(Math.random() * colors.length)],
    })
  }
  allDanmaku.value = items
  visibleDanmaku.value = [...items]
}

let danmakuTimer: ReturnType<typeof setInterval> | null = null

function startDanmaku() {
  danmakuTimer = setInterval(() => {
    if (danmakuPaused.value) return
    const idx = allDanmaku.value.length
    const avatars = ['🐰', '🐻', '🦊', '🐼', '🐨', '🦁', '🐯', '🐸']
    const texts = ['位置超棒！', '服务很好～', '下次还来', '性价比高', '早餐丰富', '房间干净', '推荐推荐！', '设施完善']
    const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F']
    allDanmaku.value.push({
      id: idx,
      text: texts[Math.floor(Math.random() * texts.length)],
      avatar: avatars[Math.floor(Math.random() * avatars.length)],
      top: Math.random() * 300 + 20,
      duration: 4 + Math.random() * 4,
      delay: 0,
      color: colors[Math.floor(Math.random() * colors.length)],
    })
    if (allDanmaku.value.length > 20) {
      allDanmaku.value.shift()
    }
    visibleDanmaku.value = [...allDanmaku.value]
  }, 2000)
}

function pauseDanmaku() { danmakuPaused.value = true }
function resumeDanmaku() { danmakuPaused.value = false }


const hotelData = ref<any>(null)
const roomData = ref<any[]>([])

const currentHotel = computed(() => {
  if (hotelData.value) {
    const h = hotelData.value
    return {
      name: h.name,
      city: h.city,
      address: h.address || '',
      score: '4.8',
      commentCount: 0,
      tags: h.facilities ? h.facilities.split(',').slice(0, 3) : [],
      images: h.coverImage ? [h.coverImage] : [],
      facilities: h.facilities ? h.facilities.split(',') : [],
      description: h.description || '',
      rooms: roomData.value.map((r: any) => ({
        id: String(r.id),
        name: r.name,
        price: r.price,
        img: r.images || `https://picsum.photos/seed/room${r.id}/600/400`,
        facilities: r.amenities ? r.amenities.split(',') : [],
        maxGuest: r.maxGuest || 2,
        popular: false,
      })),
      reviews: [],
    }
  }
  return { name: '', city: '', address: '', score: '0', commentCount: 0, tags: [], images: [], facilities: [], description: '', rooms: [], reviews: [] }
})

// ─── 预订 ───────────────────────────────────────────────────────────────────
const selectedRoom = ref<any>(null)
const checkInDate = ref('')
const checkOutDate = ref('')
const guestCount = ref(1)

const nightCount = computed(() => {
  if (!checkInDate.value || !checkOutDate.value) return 0
  const d1 = new Date(checkInDate.value)
  const d2 = new Date(checkOutDate.value)
  const diff = (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)
  return diff > 0 ? diff : 0
})

const serviceFee = computed(() => {
  if (!selectedRoom.value || nightCount.value === 0) return 0
  return Math.round(selectedRoom.value.price * nightCount.value * 0.1)
})

const totalPrice = computed(() => {
  if (!selectedRoom.value || nightCount.value === 0) return 0
  return selectedRoom.value.price * nightCount.value + serviceFee.value
})

const canBook = computed(() => {
  return !!(selectedRoom.value && checkInDate.value && checkOutDate.value && nightCount.value > 0)
})

function disabledCheckInDate(date: Date) {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

function disabledCheckOutDate(date: Date) {
  if (!checkInDate.value) return true
  return date <= new Date(checkInDate.value)
}

function handleBook() {
  if (!canBook.value) return
  ElMessage.success(`已提交预订：${currentHotel.value.name} - ${selectedRoom.value.name}，共${nightCount.value}晚`)
  router.push('/orders')
}

// ─── GSAP 动画 ───────────────────────────────────────────────────────────────
const bookingRef = ref<HTMLElement | null>(null)
const bookingFormRef = ref<HTMLElement | null>(null)
const roomListRef = ref<HTMLElement | null>(null)
const introSectionRef = ref<HTMLElement | null>(null)
const introTitleRef = ref<HTMLElement | null>(null)
const introDescRef = ref<HTMLElement | null>(null)
const galleryRef = ref<HTMLElement | null>(null)
const reviewsSectionRef = ref<HTMLElement | null>(null)
const reviewsListRef = ref<HTMLElement | null>(null)

function initGsapAnimations() {
  // 轮播淡入
  gsap.fromTo(carouselRef.value, { opacity: 0 }, { opacity: 1, duration: 0.8, ease: 'power2.out' })

  // 预订区
  if (bookingRef.value) {
    gsap.fromTo(bookingRef.value,
      { opacity: 0, y: 60 },
      { opacity: 1, y: 0, duration: 0.9, ease: 'power3.out', scrollTrigger: { trigger: bookingRef.value, start: 'top 80%', once: true } }
    )
  }

  // 房间卡片
  if (roomListRef.value) {
    gsap.fromTo(roomListRef.value?.children,
      { opacity: 0, x: -40 },
      { opacity: 1, x: 0, duration: 0.6, ease: 'power3.out', stagger: 0.1, delay: 0.2 }
    )
  }

  // 简介
  if (introSectionRef.value) {
    gsap.fromTo([introTitleRef.value, introDescRef.value],
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.8, ease: 'power3.out', stagger: 0.15,
        scrollTrigger: { trigger: introSectionRef.value, start: 'top 80%', once: true } }
    )
  }

  // 图库
  if (galleryRef.value) {
    gsap.fromTo(galleryRef.value?.children,
      { opacity: 0, scale: 0.9 },
      { opacity: 1, scale: 1, duration: 0.7, ease: 'back.out(1.4)', stagger: 0.1,
        scrollTrigger: { trigger: galleryRef.value, start: 'top 85%', once: true } }
    )
  }

  // 点评
  if (reviewsSectionRef.value) {
    gsap.fromTo(reviewsListRef.value?.children,
      { opacity: 0, y: 30 },
      { opacity: 1, y: 0, duration: 0.7, ease: 'power3.out', stagger: 0.15,
        scrollTrigger: { trigger: reviewsSectionRef.value, start: 'top 80%', once: true } }
    )
  }
}

async function fetchHotel() {
  try {
    const [hotelRes, roomsRes] = await Promise.all([
      getHotelDetailApi(Number(hotelId.value)),
      getHotelRoomsApi(Number(hotelId.value)),
    ])
    hotelData.value = (hotelRes as any).data || null
    roomData.value = ((roomsRes as any).data || []) as any[]
  } catch (e) {
    console.error('加载酒店详情失败', e)
  }
}

onMounted(() => {
  fetchHotel()
  genDanmaku()
  startDanmaku()
  initGsapAnimations()
})

onUnmounted(() => {
  if (danmakuTimer) clearInterval(danmakuTimer)
  ScrollTrigger.getAll().forEach(t => t.kill())
})
</script>

<style scoped lang="scss">
.hotel-detail-page {
  background: #f8fafc;
  min-height: 100vh;
}

/* ── 轮播 ── */
.carousel-section {
  position: relative;
  height: 100vh;
  overflow: hidden;
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-item {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.8s ease;

  &.active { opacity: 1; }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.carousel-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.6) 70%, rgba(0,0,0,0.9) 100%);
}

.danmaku-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.danmaku-item {
  position: absolute;
  left: -200px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px 6px 8px;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  white-space: nowrap;
  animation: danmakuMove var(--dm-duration, 6s) linear forwards;
  color: var(--dm-color, #fff);
  font-size: 14px;
  font-weight: 600;

  .dm-avatar { font-size: 18px; }
}

@keyframes danmakuMove {
  from { transform: translateX(0); }
  to { transform: translateX(calc(100vw + 200px)); }
}

.carousel-controls {
  position: absolute;
  bottom: 200px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 16px;
  z-index: 10;
}

.ctrl-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  font-size: 22px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;

  &:hover { background: rgba(255,255,255,0.35); }
}

.carousel-dots {
  display: flex;
  gap: 8px;

  .dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(255,255,255,0.4);
    cursor: pointer;
    transition: all 0.3s;

    &.active {
      background: white;
      width: 24px;
      border-radius: 4px;
    }
  }
}

.carousel-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32px 60px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  z-index: 10;

  .hotel-score {
    .score-num { font-size: 72px; font-weight: 900; color: white; line-height: 1; }
    .score-label { font-size: 20px; color: rgba(255,255,255,0.6); margin-left: 4px; }
  }

  .hotel-reviews { color: rgba(255,255,255,0.6); font-size: 14px; margin-top: 4px; }

  .hotel-name { font-size: 36px; font-weight: 900; color: white; margin-bottom: 8px; }

  .hotel-tags {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    margin-bottom: 8px;

    span {
      padding: 4px 12px;
      background: rgba(255,255,255,0.15);
      border-radius: 20px;
      color: white;
      font-size: 12px;
    }
  }

  .hotel-address { color: rgba(255,255,255,0.6); font-size: 14px; }
}

/* ── 预订区 ── */
.booking-section {
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  padding: 60px 24px;
}

.booking-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;

  @media (max-width: 900px) { grid-template-columns: 1fr; }
}

.room-list-title {
  font-size: 22px;
  font-weight: 800;
  color: white;
  margin-bottom: 20px;
}

.room-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 12px;

  &:hover, &.selected {
    background: rgba(102,126,234,0.2);
    border-color: rgba(102,126,234,0.5);
  }

  .room-img {
    position: relative;
    width: 120px;
    height: 90px;
    flex-shrink: 0;
    border-radius: 12px;
    overflow: hidden;

    img { width: 100%; height: 100%; object-fit: cover; }

    .room-popular-tag {
      position: absolute;
      top: 6px;
      left: 6px;
      padding: 2px 8px;
      background: #ef4444;
      border-radius: 8px;
      color: white;
      font-size: 10px;
      font-weight: 700;
    }
  }

  .room-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .room-name { font-size: 16px; font-weight: 700; color: white; margin-bottom: 4px; }
    .room-facilities { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 6px; }
    .room-facilities span { font-size: 11px; color: rgba(255,255,255,0.5); background: rgba(255,255,255,0.08); padding: 2px 8px; border-radius: 6px; }
    .room-price .price-num { font-size: 22px; font-weight: 900; color: #ef4444; }
    .room-price .price-unit { font-size: 12px; color: rgba(255,255,255,0.4); }
  }

  .room-select-indicator {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: rgba(255,255,255,0.4);

    .selected { color: #667eea; font-weight: 700; }
  }
}

.booking-card {
  background: rgba(255,255,255,0.06);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 20px;
  padding: 28px;

  .booking-price-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(255,255,255,0.1);

    .selected-room-name { font-size: 15px; color: rgba(255,255,255,0.6); }
    .big-price { font-size: 36px; font-weight: 900; color: #ef4444; }
    .price-unit { font-size: 14px; color: rgba(255,255,255,0.4); }
  }

  .form-group {
    margin-bottom: 16px;

    .form-label { display: block; font-size: 13px; color: rgba(255,255,255,0.6); margin-bottom: 6px; font-weight: 600; }
  }

  .night-info {
    text-align: center;
    padding: 8px;
    background: rgba(102,126,234,0.15);
    border-radius: 8px;
    color: #667eea;
    font-size: 14px;
    margin-bottom: 16px;
  }

  .price-breakdown {
    background: rgba(0,0,0,0.2);
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 16px;

    .breakdown-row {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      color: rgba(255,255,255,0.6);
      margin-bottom: 8px;
    }

    .breakdown-total {
      display: flex;
      justify-content: space-between;
      font-size: 16px;
      font-weight: 800;
      color: white;
      padding-top: 8px;
      border-top: 1px solid rgba(255,255,255,0.1);
    }
  }

  .book-btn {
    width: 100%;
    padding: 14px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.3s;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 8px 30px rgba(102,126,234,0.5);
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .hotel-facilities {
    margin-top: 20px;
    padding-top: 16px;
    border-top: 1px solid rgba(255,255,255,0.1);

    .facilities-title { font-size: 14px; color: rgba(255,255,255,0.5); font-weight: 600; margin-bottom: 10px; }

    .facilities-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;

      span {
        padding: 4px 12px;
        background: rgba(255,255,255,0.08);
        border-radius: 20px;
        color: rgba(255,255,255,0.6);
        font-size: 12px;
      }
    }
  }
}

/* ── 简介 ── */
.hotel-intro-section {
  background: white;
  padding: 80px 24px;

  .intro-inner { max-width: 900px; margin: 0 auto; }

  .intro-label {
    font-size: 13px;
    color: #667eea;
    text-transform: uppercase;
    letter-spacing: 3px;
    font-weight: 700;
    margin-bottom: 12px;
  }

  h2 {
    font-size: clamp(28px, 4vw, 48px);
    font-weight: 900;
    color: #1e293b;
    margin-bottom: 20px;
  }

  .intro-desc {
    font-size: 16px;
    color: #64748b;
    line-height: 1.9;
    margin-bottom: 32px;
  }

  .intro-gallery {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    grid-template-rows: repeat(2, 180px);
    gap: 12px;

    @media (max-width: 700px) { grid-template-columns: 1fr 1fr; grid-template-rows: auto; }

    .gallery-item {
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;

      &.wide { grid-column: span 2; grid-row: span 2; }

      img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
      &:hover img { transform: scale(1.05); }
    }
  }
}

/* ── 点评 ── */
.reviews-section {
  background: #f8fafc;
  padding: 80px 24px;

  .reviews-inner { max-width: 900px; margin: 0 auto; }

  .reviews-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;

    .reviews-title { font-size: 28px; font-weight: 900; color: #1e293b; }

    .reviews-summary {
      display: flex;
      align-items: center;
      gap: 12px;

      .summary-score { font-size: 48px; font-weight: 900; color: #f59e0b; }
      .summary-stars { font-size: 14px; margin-bottom: 2px; }
      .summary-count { font-size: 13px; color: #94a3b8; }
    }
  }

  .reviews-list { display: flex; flex-direction: column; gap: 16px; }

  .review-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);

    .review-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;

      .reviewer-avatar {
        width: 44px;
        height: 44px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        font-weight: 700;
        flex-shrink: 0;
      }

      .reviewer-info { flex: 1; }
      .reviewer-name { font-size: 15px; font-weight: 700; color: #1e293b; }
      .reviewer-meta { font-size: 12px; color: #94a3b8; margin-top: 2px; }
      .review-score { font-size: 16px; color: #f59e0b; font-weight: 700; }
    }

    .review-body { font-size: 14px; color: #475569; line-height: 1.8; }

    .review-images {
      display: flex;
      gap: 8px;
      margin-top: 12px;

      img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; }
    }
  }
}
</style>