<template>
  <div class="checkout-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle c1"></div>
      <div class="circle c2"></div>
      <div class="circle c3"></div>
    </div>

    <div class="checkout-wrapper">
      <!-- 统一大卡片 -->
      <div class="unified-card">
        <!-- 左侧：预订信息 55% -->
        <div class="left-panel">
          <!-- 商品信息 -->
          <div class="section product-section" v-if="goodsInfo">
            <div class="product-wrap">
              <div class="product-img">
                <img :src="goodsInfo.cover || goodsInfo.coverImage" :alt="goodsInfo.name" />
                <div class="type-badge">
                  <svg v-if="bookType === 'spot'" class="badge-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
                  <svg v-else class="badge-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                  <span>{{ bookType === 'spot' ? '景点门票' : '酒店预订' }}</span>
                </div>
              </div>
              <div class="product-info">
                <h2 class="product-name">{{ goodsInfo.name }}</h2>
                <div class="product-tags">
                  <span class="tag" v-if="goodsInfo.city">
                    <svg class="tag-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                    {{ goodsInfo.city }}
                  </span>
                  <span class="tag" v-if="goodsInfo.level">
                    <svg class="tag-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
                    {{ goodsInfo.level }}
                  </span>
                </div>
                <div class="product-price">
                  <span class="yuan">¥</span><span class="amount">{{ bookType === 'spot' ? (goodsInfo.ticketPrice || 0) : (goodsInfo.minPrice || 0) }}</span>
                  <span class="unit">/{{ bookType === 'spot' ? '人' : '晚' }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 预订表单 -->
          <div class="section">
            <div class="section-head">
              <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
              <span>预订信息</span>
            </div>

            <!-- 景点 -->
            <div v-if="bookType === 'spot'" class="form-rows">
              <div class="form-row">
                <div class="field">
                  <label>游玩日期</label>
                  <el-date-picker v-model="form.visitDate" type="date" placeholder="选择日期" :disabled-date="disabledDate" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
                </div>
                <div class="field">
                  <label>人数</label>
                  <div class="qty-ctrl">
                    <button class="qty-btn" @click="form.quantity = Math.max(1, form.quantity-1)">−</button>
                    <span class="qty-num">{{ form.quantity }}</span>
                    <button class="qty-btn" @click="form.quantity = Math.min(10, form.quantity+1)">+</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 酒店 -->
            <div v-if="bookType === 'hotel'" class="form-rows">
              <div class="form-row">
                <div class="field">
                  <label>入住日期</label>
                  <el-date-picker v-model="form.checkInDate" type="date" placeholder="入住" :disabled-date="disabledDate" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
                </div>
                <div class="field">
                  <label>退房日期</label>
                  <el-date-picker v-model="form.checkOutDate" type="date" placeholder="退房" :disabled="!form.checkInDate" :disabled-date="(d:Date) => disabledDate(d) || d < new Date(form.checkInDate)" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
                </div>
                <div class="field">
                  <label>房间数</label>
                  <div class="qty-ctrl">
                    <button class="qty-btn" @click="form.roomCount = Math.max(1, form.roomCount-1)">−</button>
                    <span class="qty-num">{{ form.roomCount }}</span>
                    <button class="qty-btn" @click="form.roomCount = Math.min(5, form.roomCount+1)">+</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 联系人 -->
            <div class="form-row contact-row">
              <div class="field">
                <label>
                  <svg class="lbl-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                  联系人姓名
                </label>
                <el-input v-model="form.contactName" placeholder="请输入联系人" size="large" />
              </div>
              <div class="field">
                <label>
                  <svg class="lbl-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
                  联系电话
                </label>
                <el-input v-model="form.contactPhone" placeholder="请输入手机号" size="large" />
              </div>
            </div>
          </div>

          <!-- 优惠券 -->
          <div class="section coupon-section" v-if="priceDetail.basePrice > 0">
            <div class="section-head">
              <svg class="section-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
              <span>优惠券</span>
              <span class="c-hint" v-if="availableCoupons.length > 0">{{ availableCoupons.length }}张可用</span>
            </div>

            <div class="coupon-toggle" @click="showCouponPicker = !showCouponPicker">
              <div class="coupon-toggle-left">
                <svg class="toggle-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="8" width="18" height="12" rx="2"/><path d="M7 8V6a5 5 0 0 1 10 0v2"/></svg>
                <template v-if="selectedCoupon">
                  <span class="sel-name">{{ selectedCoupon.name }}</span>
                  <span class="sel-val">-¥{{ priceDetail.couponDiscount.toFixed(2) }}</span>
                </template>
                <span v-else class="no-coupon">选择优惠券</span>
              </div>
              <svg class="arr" :class="{up: showCouponPicker}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
            </div>

            <div class="coupon-list" v-if="showCouponPicker">
              <div v-if="couponLoading" class="loading">加载中...</div>
              <div v-else-if="availableCoupons.length === 0" class="no-data">暂无可用优惠券</div>
              <div v-for="c in availableCoupons" :key="c.id" class="coupon-row" :class="{off: !canUseCoupon(c), on: selectedCoupon?.id === c.id}" @click="selectCoupon(c)">
                <div class="cr-left">
                  <span class="cr-amount">
                    <template v-if="c.type === 1">¥{{ c.discountValue }}</template>
                    <template v-else>{{ (c.discountValue).toFixed(2) }}</template>
                  </span>
                </div>
                <div class="cr-mid">
                  <span class="cr-name">{{ c.name }}</span>
                  <span class="cr-rule">满{{ c.minAmount }}可用</span>
                </div>
                <div class="cr-right">
                  <span v-if="selectedCoupon?.id === c.id" class="ok">✓</span>
                  <span v-else-if="!canUseCoupon(c)" class="no">{{ getUnavailableReason(c) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：支付核心 45% -->
        <div class="right-panel">
          <!-- 价格明细 -->
          <div class="pay-block">
            <div class="pb-title">
              <svg class="pb-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
              费用明细
            </div>
            <div class="pb-list">
              <div class="pb-row">
                <span class="pb-lbl">
                  {{ bookType === 'spot' ? '门票' : '房费' }}
                  <span class="pb-qty">×{{ bookType === 'spot' ? form.quantity : form.roomCount }}</span>
                </span>
                <span class="pb-val">¥{{ priceDetail.basePrice.toFixed(2) }}</span>
              </div>
              <div class="pb-row green" v-if="priceDetail.couponDiscount > 0">
                <span class="pb-lbl"><span class="cpn-tag">券</span></span>
                <span class="pb-val">-¥{{ priceDetail.couponDiscount.toFixed(2) }}</span>
              </div>
            </div>
            <div class="pb-total">
              <span class="tl-lbl">合计</span>
              <span class="tl-val"><span class="tl-rmb">¥</span>{{ priceDetail.totalPrice.toFixed(2) }}</span>
            </div>
          </div>

          <!-- 支付方式 -->
          <div class="pay-block">
            <div class="pb-title">
              <svg class="pb-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
              支付方式
            </div>
            <div class="ch-list">
              <div v-for="ch in channels" :key="ch.value" class="ch-item" :class="{active: payChannel === ch.value}" @click="payChannel = ch.value">
                <span class="ch-icon">{{ ch.icon }}</span>
                <span class="ch-name">{{ ch.name }}</span>
                <span class="ch-dot" v-if="payChannel === ch.value"></span>
              </div>
            </div>
          </div>

          <!-- 按钮 -->
          <div class="btn-wrap">
            <button class="btn-gopay" :loading="loading" @click="handlePay">
              <span>立即支付</span>
              <span class="bp-rmb">¥</span>
              <span class="bp-amt">{{ priceDetail.totalPrice.toFixed(2) }}</span>
            </button>
            <button class="btn-cancel" @click="handleCancel">取消预订</button>
          </div>

          <!-- 底部 -->
          <div class="pay-foot">
            <svg class="pf-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 2L11 13"/><path d="M22 2l-7 20-4-9-9-4 20-7z"/></svg>
            <span>支付安全 · 极速到账 · 轻松出行</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSpotDetailApi } from '@/api/spot'
import { getHotelDetailApi } from '@/api/hotel'
import { createSpotOrderApi, createHotelOrderApi, payOrderApi } from '@/api/order'
import { couponApi } from '@/api/coupon'

const route = useRoute()
const router = useRouter()

const bookType = ref<'spot' | 'hotel'>('spot')
const goodsInfo = ref<any>(null)
const loading = ref(false)
const payChannel = ref('wechat')

const showCouponPicker = ref(false)
const couponLoading = ref(false)
const availableCoupons = ref<any[]>([])
const selectedCoupon = ref<any | null>(null)

const form = ref({
  visitDate: '',
  quantity: 1,
  checkInDate: '',
  checkOutDate: '',
  roomCount: 1,
  contactName: '',
  contactPhone: ''
})

const channels = [
  { value: 'wechat', name: '微信支付', icon: '💚' },
  { value: 'alipay', name: '支付宝', icon: '💙' },
  { value: 'card', name: '银行卡', icon: '💳' }
]

const spotPrice = computed(() => goodsInfo.value?.ticketPrice || 0)
const hotelPrice = computed(() => goodsInfo.value?.minPrice || 0)

const basePrice = computed(() => {
  if (bookType.value === 'spot') return spotPrice.value * form.value.quantity
  if (form.value.checkInDate && form.value.checkOutDate) {
    const days = Math.ceil((new Date(form.value.checkOutDate).getTime() - new Date(form.value.checkInDate).getTime()) / 86400000)
    return hotelPrice.value * days * form.value.roomCount
  }
  return hotelPrice.value * form.value.roomCount
})

const priceDetail = computed(() => {
  const amount = basePrice.value
  let cd = 0
  if (selectedCoupon.value) {
    const c = selectedCoupon.value
    if (c.couponType === 1 || c.type === 1) cd = Math.min(Number(c.discountValue), amount)
    else if (c.couponType === 2 || c.type === 2) {
      let d = amount * (1 - Number(c.discountValue))
      if (c.maxDiscount && d > Number(c.maxDiscount)) d = Number(c.maxDiscount)
      cd = Math.round(d * 100) / 100
    }
  }
  return { basePrice: amount, couponDiscount: cd, totalPrice: Math.max(0, amount - cd) }
})

function canUseCoupon(c: any) { return basePrice.value >= Number(c.minAmount) }
function getUnavailableReason(c: any) { return basePrice.value < Number(c.minAmount) ? `满${c.minAmount}` : '' }

function selectCoupon(c: any) {
  if (!canUseCoupon(c)) return
  selectedCoupon.value = selectedCoupon.value?.id === c.id ? null : c
  showCouponPicker.value = false
}

async function loadCoupons() {
  if (selectedCoupon.value && basePrice.value < Number(selectedCoupon.value.minAmount)) selectedCoupon.value = null
  try {
    couponLoading.value = true
    const res = await couponApi.myCoupons(0)
    if (res.code === 200) availableCoupons.value = res.data || []
  } catch { availableCoupons.value = [] }
  finally { couponLoading.value = false }
}

const disabledDate = (d: Date) => d < new Date(new Date().setHours(0,0,0,0))

async function loadGoodsInfo() {
  const id = parseInt(route.params.id as string)
  const type = route.params.type as string
  bookType.value = type === 'hotel' ? 'hotel' : 'spot'
  try {
    if (bookType.value === 'spot') {
      const res = await getSpotDetailApi(id)
      if (res.code === 200) {
        const d = res.data
        goodsInfo.value = { id: d.id, name: d.name, coverImage: d.coverImage, cover: d.coverImage, city: d.city, address: d.address, level: d.level, ticketPrice: d.ticketPrice, openTime: d.openTime }
      }
    } else {
      const res = await getHotelDetailApi(id)
      if (res.code === 200) {
        const d = res.data
        goodsInfo.value = { id: d.id, name: d.name, coverImage: d.coverImage, cover: d.coverImage, city: d.city, address: d.address, level: d.starLevel ? d.starLevel + '星酒店' : '', minPrice: d.minPrice || 0 }
      }
    }
    loadCoupons()
  } catch { ElMessage.error('加载失败'); router.back() }
}

async function handlePay() {
  if (!form.value.contactName) { ElMessage.warning('请填写联系人'); return }
  if (!form.value.contactPhone) { ElMessage.warning('请填写电话'); return }
  if (bookType.value === 'spot' && !form.value.visitDate) { ElMessage.warning('请选择日期'); return }
  if (bookType.value === 'hotel' && (!form.value.checkInDate || !form.value.checkOutDate)) { ElMessage.warning('请选择日期'); return }

  loading.value = true
  try {
    let od: any = { totalAmount: priceDetail.value.totalPrice, contactName: form.value.contactName, contactPhone: form.value.contactPhone }
    if (selectedCoupon.value) { od.couponId = selectedCoupon.value.id; od.couponName = selectedCoupon.value.name; od.discountAmount = priceDetail.value.couponDiscount }
    let orderNo = ''

    if (bookType.value === 'spot') {
      Object.assign(od, { spotId: goodsInfo.value.id, spotName: goodsInfo.value.name, quantity: form.value.quantity, visitDate: form.value.visitDate })
      const res = await createSpotOrderApi(od)
      if (res.code !== 200) { ElMessage.error(res.msg || '失败'); return }
      orderNo = res.data.orderNo
    } else {
      Object.assign(od, { hotelId: goodsInfo.value.id, hotelName: goodsInfo.value.name, roomCount: form.value.roomCount, checkInDate: form.value.checkInDate, checkOutDate: form.value.checkOutDate })
      const res = await createHotelOrderApi(od)
      if (res.code !== 200) { ElMessage.error(res.msg || '失败'); return }
      orderNo = res.data.orderNo
    }

    await payOrderApi(orderNo, payChannel.value)
    ElMessage.success('🎉 支付成功！')
    router.replace('/orders')
  } catch (e: any) { ElMessage.error(e.message || '支付失败') }
  finally { loading.value = false }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定取消吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '继续', type: 'warning' })
    router.replace('/')
  } catch {}
}

onMounted(() => { loadGoodsInfo() })
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f4ff 0%, #fff5eb 50%, #f5f0ff 100%);
  padding: 100px 20px;
  position: relative;
  overflow: hidden;
}

.bg-decoration .circle {
  position: fixed;
  border-radius: 50%;
  z-index: 0;
}
.bg-decoration .c1 { width: 500px; height: 500px; background: radial-gradient(circle, rgba(59,130,246,0.12), transparent 70%); top: -150px; right: -150px; }
.bg-decoration .c2 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(249,115,22,0.10), transparent 70%); bottom: -100px; left: -100px; }
.bg-decoration .c3 { width: 300px; height: 300px; background: radial-gradient(circle, rgba(59,130,246,0.06), rgba(249,115,22,0.06)); top: 40%; left: 25%; }

.checkout-wrapper {
  max-width: 1100px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* ===== 统一大卡片 ===== */
.unified-card {
  background: #fff;
  height: 1000px;
  border-radius: 28px;
  box-shadow: 0 20px 60px rgba(59, 130, 246, 0.10), 0 8px 24px rgba(0,0,0,0.06);
  display: flex;
  overflow: hidden;
}

@media (max-width: 860px) {
  .unified-card { flex-direction: column; }
}

/* ===== 左侧 55% ===== */
.left-panel {
  flex: 0 0 55%;
  padding: 32px 36px;
  border-right: 1.5px dashed #e8eef8;
}

@media (max-width: 860px) {
  .left-panel { flex: none; border-right: none; border-bottom: 1.5px dashed #e8eef8; padding: 24px; }
}

/* section */
.section { margin-bottom: 28px; }
.section:last-child { margin-bottom: 0; }

.section-head {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 18px;
  padding-bottom: 12px;
  border-bottom: 1.5px dashed #e8eef8;
}

.section-icon { width: 22px; height: 22px; color: #3b82f6; }

/* 商品 */
.product-wrap {
  display: flex;
  gap: 20px;
  align-items: center;
}

.product-img {
  width: 180px;
  height: 140px;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
}
.product-img img { width: 100%; height: 100%; object-fit: cover; }

.type-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 12px;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  color: #fff;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 3px 10px rgba(59,130,246,0.35);
}
.badge-icon { width: 14px; height: 14px; }

.product-info { flex: 1; min-width: 0; }

.product-name {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 10px;
  line-height: 1.3;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}
.tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 8px;
}
.tag-icon { width: 13px; height: 13px; color: #3b82f6; }

.product-price { display: flex; align-items: baseline; }
.yuan { font-size: 16px; font-weight: 600; color: #f97316; }
.amount { font-size: 30px; font-weight: 800; color: #f97316; line-height: 1; }
.unit { font-size: 13px; color: #94a3b8; margin-left: 2px; }

/* 表单 */
.form-rows { margin-bottom: 16px; }

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 14px;
}
.form-row:last-child { margin-bottom: 0; }

@media (max-width: 560px) { .form-row { grid-template-columns: 1fr; } }

.contact-row { grid-template-columns: 1fr 1fr; }
@media (max-width: 560px) { .contact-row { grid-template-columns: 1fr; } }

.field label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
}
.lbl-icon { width: 14px; height: 14px; color: #3b82f6; }

.qty-ctrl {
  display: flex;
  align-items: center;
  background: #f8fafc;
  border-radius: 14px;
  padding: 4px;
  width: fit-content;
}
.qty-btn {
  width: 38px; height: 38px;
  border: none;
  background: #fff;
  border-radius: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #3b82f6;
  cursor: pointer;
  transition: all .2s;
  box-shadow: 0 2px 6px rgba(59,130,246,0.10);
}
.qty-btn:hover { background: #3b82f6; color: #fff; }
.qty-num { min-width: 44px; text-align: center; font-size: 17px; font-weight: 700; color: #1e293b; }

/* 优惠券 */
.c-hint {
  margin-left: auto;
  padding: 3px 10px;
  background: linear-gradient(135deg, #fff5eb, #ffedd5);
  color: #f97316;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.coupon-toggle {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #f8fafc;
  border: 1.5px dashed #d1d9e6;
  border-radius: 14px;
  cursor: pointer;
  transition: all .25s;
}
.coupon-toggle:hover { border-color: #3b82f6; background: #f0f7ff; }

.coupon-toggle-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.toggle-icon { width: 20px; height: 20px; color: #f97316; }
.sel-name { font-size: 14px; font-weight: 600; color: #1e293b; }
.sel-val { font-size: 15px; font-weight: 700; color: #22c55e; padding: 2px 10px; background: #f0fdf4; border-radius: 8px; }
.no-coupon { font-size: 14px; color: #94a3b8; }

.arr { width: 18px; height: 18px; color: #94a3b8; transition: transform .3s; }
.arr.up { transform: rotate(180deg); }

.coupon-list {
  margin-top: 10px;
  border: 1px solid #f1f5f9;
  border-radius: 14px;
  overflow: hidden;
  max-height: 240px;
  overflow-y: auto;
}

.loading, .no-data { padding: 20px; text-align: center; color: #94a3b8; font-size: 14px; }

.coupon-row {
  display: flex;
  align-items: center;
  padding: 13px 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background .2s;
}
.coupon-row:last-child { border-bottom: none; }
.coupon-row:hover:not(.off) { background: #f8fafc; }
.coupon-row.on { background: linear-gradient(135deg, rgba(59,130,246,0.04), rgba(249,115,22,0.04)); }
.coupon-row.off { opacity: 0.45; cursor: not-allowed; }

.cr-left { margin-right: 12px; }
.cr-amount { font-size: 20px; font-weight: 800; color: #f97316; }

.cr-mid { flex: 1; }
.cr-name { display: block; font-size: 13px; font-weight: 600; color: #1e293b; }
.cr-rule { font-size: 11px; color: #94a3b8; }

.cr-right { margin-left: auto; }
.ok { display: flex; width: 24px; height: 24px; background: linear-gradient(135deg, #3b82f6, #60a5fa); color: #fff; border-radius: 50%; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; }
.no { font-size: 11px; color: #94a3b8; padding: 3px 8px; background: #f1f5f9; border-radius: 6px; }

/* ===== 右侧 45% ===== */
.right-panel {
  flex: 0 0 45%;
  padding: 32px 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: linear-gradient(180deg, #fafbff 0%, #fff 100%);
}

@media (max-width: 860px) {
  .right-panel { flex: none; padding: 24px; }
}

/* 支付块 */
.pay-block {
  background: #fff;
  border-radius: 18px;
  padding: 20px;
  border: 1.5px solid #f1f5f9;
}

.pb-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 14px;
}
.pb-icon { width: 20px; height: 20px; color: #3b82f6; }

.pb-list { margin-bottom: 12px; }

.pb-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}
.pb-lbl { font-size: 13px; color: #64748b; display: flex; align-items: center; gap: 6px; }
.pb-qty { font-size: 11px; color: #94a3b8; }
.pb-val { font-size: 14px; font-weight: 600; color: #1e293b; }
.pb-row.green .pb-val { color: #22c55e; }

.cpn-tag { padding: 2px 8px; background: linear-gradient(135deg, #22c55e, #4ade80); color: #fff; border-radius: 6px; font-size: 11px; font-weight: 700; }

.pb-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1.5px dashed #e8eef8;
}
.tl-lbl { font-size: 15px; font-weight: 700; color: #1e293b; }
.tl-val {  display: flex;
  align-items: baseline;
}
.tl-rmb { font-size: 16px; font-weight: 700; color: #f97316; }
.tl-val { font-size: 34px; font-weight: 800; color: #f97316; line-height: 1; }

/* 支付方式 */
.ch-list { display: flex; flex-direction: column; gap: 8px; }

.ch-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border: 2px solid transparent;
  border-radius: 14px;
  cursor: pointer;
  transition: all .25s;
}
.ch-item:hover { background: #f0f7ff; border-color: #bfdbfe; }
.ch-item.active { background: linear-gradient(135deg, rgba(59,130,246,0.06), rgba(249,115,22,0.04)); border-color: #3b82f6; }

.ch-icon { font-size: 22px; margin-right: 10px; }
.ch-name { flex: 1; font-size: 14px; font-weight: 600; color: #1e293b; }
.ch-dot { width: 18px; height: 18px; background: linear-gradient(135deg, #3b82f6, #60a5fa); border-radius: 50%; }

/* 按钮 */
.btn-wrap { display: flex; flex-direction: column; gap: 10px; }

.btn-gopay {
  width: 100%;
  height: 54px;
  background: linear-gradient(135deg, #3b82f6 0%, #f97316 100%);
  border: none;
  border-radius: 27px;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all .3s;
  box-shadow: 0 8px 24px rgba(59,130,246,0.30);
}
.btn-gopay:hover { transform: translateY(-2px); box-shadow: 0 12px 32px rgba(59,130,246,0.40); }
.bp-rmb { font-size: 14px; opacity: 0.9; }
.bp-amt { padding: 3px 12px; background: rgba(255,255,255,0.2); border-radius: 14px; font-size: 15px; }

.btn-cancel {
  width: 100%;
  height: 44px;
  background: transparent;
  border: 2px solid #e2e8f0;
  border-radius: 22px;
  color: #94a3b8;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all .25s;
}
.btn-cancel:hover { border-color: #f97316; color: #f97316; }

/* 底部 */
.pay-foot {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 11px;
  color: #94a3b8;
  padding-top: 4px;
}
.pf-icon { width: 16px; height: 16px; color: #3b82f6; }
</style>
