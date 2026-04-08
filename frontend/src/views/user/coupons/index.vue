<template>
  <div class="coupons-page page-container">
    <h2 class="page-title">🎫 优惠券中心</h2>

    <div class="tabs">
      <button :class="{ active: tab === 'available' }" @click="tab = 'available'">
        可领取
      </button>
      <button :class="{ active: tab === 'mine' }" @click="tab = 'mine'">
        我的优惠券
      </button>
    </div>

    <!-- 可领取优惠券 -->
    <div v-if="tab === 'available'" class="coupon-list">
      <div v-if="availableLoading" class="loading-wrap">
        <el-icon class="is-loading spin"><Loading /></el-icon>
      </div>
      <div v-else-if="availableCoupons.length === 0" class="empty-wrap">
        <el-empty description="暂无可领取的优惠券" />
      </div>
      <div
        v-else
        v-for="c in availableCoupons"
        :key="c.id"
        class="coupon-card"
        :class="getCouponClass(c.type)"
      >
        <div class="coupon-left">
          <div class="coupon-value">
            <span v-if="c.type === 1" class="money">
              <small>¥</small>{{ c.discountValue }}
            </span>
            <span v-else-if="c.type === 2" class="money">
              {{ (c.discountValue * 10).toFixed(1) }}<small>折</small>
            </span>
            <span v-else class="money">兑换</span>
          </div>
          <div class="coupon-desc">满{{ c.minAmount }}可用</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-name">{{ c.name }}</div>
          <div class="coupon-detail">{{ c.description || '全场通用优惠券' }}</div>
          <div class="coupon-expire">
            {{ formatDate(c.validStart) }} - {{ formatDate(c.validEnd) }}
          </div>
          <div class="coupon-action">
            <span class="remain">{{ c.remainCount }}张剩余</span>
            <button class="receive-btn" @click="receiveCoupon(c.id)">立即领取</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 我的优惠券 -->
    <div v-if="tab === 'mine'" class="coupon-list">
      <div class="status-filter">
        <button
          v-for="s in statusOptions"
          :key="s.value"
          :class="{ active: myStatus === s.value }"
          @click="myStatus = s.value; loadMyCoupons()"
        >
          {{ s.label }}
        </button>
      </div>
      <div v-if="mineLoading" class="loading-wrap">
        <el-icon class="is-loading spin"><Loading /></el-icon>
      </div>
      <div v-else-if="myCoupons.length === 0" class="empty-wrap">
        <el-empty description="暂无优惠券" />
      </div>
      <div
        v-else
        v-for="c in myCoupons"
        :key="c.id"
        class="coupon-card mine"
        :class="{ used: c.status === 1, expired: c.status === 2 }"
      >
        <div class="coupon-left">
          <div class="coupon-value">
            <span v-if="c.couponType === 1" class="money"><small>¥</small>{{ c.discountValue }}</span>
            <span v-else-if="c.couponType === 2" class="money">{{ (c.discountValue * 10).toFixed(1) }}<small>折</small></span>
            <span v-else class="money">兑换</span>
          </div>
          <div class="coupon-desc">满{{ c.minAmount }}可用</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-name">{{ c.couponName }}</div>
          <div class="coupon-status">
            <el-tag v-if="c.status === 0" type="warning" size="small">未使用</el-tag>
            <el-tag v-else-if="c.status === 1" type="success" size="small">已使用</el-tag>
            <el-tag v-else type="info" size="small">已过期</el-tag>
          </div>
          <div class="coupon-expire">有效期至：{{ formatDate(c.expireTime) }}</div>
          <div class="coupon-action" v-if="c.status === 0">
            <el-button type="primary" size="small" @click="$router.push('/orders')">去使用</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { couponApi } from '@/api/coupon'

const tab = ref('available')
const myStatus = ref<number | undefined>(undefined)
const availableLoading = ref(false)
const mineLoading = ref(false)
const availableCoupons = ref<any[]>([])
const myCoupons = ref<any[]>([])

const statusOptions = [
  { label: '全部', value: undefined },
  { label: '未使用', value: 0 },
  { label: '已使用', value: 1 },
  { label: '已过期', value: 2 },
]

onMounted(() => {
  loadAvailable()
  loadMyCoupons()
})

async function loadAvailable() {
  availableLoading.value = true
  try {
    const res = await couponApi.available({ page: 1, pageSize: 50 })
    availableCoupons.value = res.data?.records || []
  } catch { availableCoupons.value = [] }
  finally { availableLoading.value = false }
}

async function loadMyCoupons() {
  mineLoading.value = true
  try {
    const res = await couponApi.myCoupons(myStatus.value)
    myCoupons.value = res.data || []
  } catch { myCoupons.value = [] }
  finally { mineLoading.value = false }
}

async function receiveCoupon(couponId: number) {
  try {
    await couponApi.receive(couponId)
    ElMessage.success('领取成功！')
    loadAvailable()
    loadMyCoupons()
  } catch (e: any) {
    ElMessage.error(e.message || '领取失败')
  }
}

function formatDate(d: string) {
  if (!d) return ''
  return d.substring(0, 10)
}

function getCouponClass(type: number) {
  return { 'type-1': type === 1, 'type-2': type === 2 }
}
</script>

<style scoped>
.coupons-page { padding: 40px 20px; }
.page-title { font-size: 28px; margin-bottom: 32px; }
.tabs {
  display: flex;
  gap: 0;
  border-bottom: 2px solid #eee;
  margin-bottom: 28px;
}
.tabs button {
  padding: 10px 28px;
  border: none;
  background: none;
  font-size: 16px;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  color: #888;
  transition: all 0.2s;
}
.tabs button.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: bold;
}
.coupon-list { display: flex; flex-direction: column; gap: 16px; }
.loading-wrap, .empty-wrap { padding: 40px; text-align: center; }
.spin { font-size: 32px; color: #667eea; animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.coupon-card {
  display: flex;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
}
.coupon-left {
  width: 120px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.type-2 .coupon-left { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.coupon-value { font-size: 28px; font-weight: bold; }
.coupon-value small { font-size: 14px; }
.coupon-desc { font-size: 12px; opacity: 0.85; margin-top: 4px; }
.coupon-right { flex: 1; padding: 16px 20px; background: #fff; }
.coupon-name { font-size: 16px; font-weight: bold; margin-bottom: 4px; }
.coupon-detail { font-size: 13px; color: #888; margin-bottom: 6px; }
.coupon-expire { font-size: 12px; color: #aaa; margin-bottom: 8px; }
.coupon-action { display: flex; align-items: center; justify-content: space-between; }
.remain { font-size: 12px; color: #aaa; }
.receive-btn {
  padding: 6px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
}
.coupon-card.used .coupon-left { background: #ccc; }
.coupon-card.expired .coupon-left { background: #999; }
.coupon-card.used, .coupon-card.expired { opacity: 0.7; }
.coupon-status { margin-bottom: 4px; }
.status-filter {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}
.status-filter button {
  padding: 6px 16px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}
.status-filter button.active {
  background: #667eea;
  color: #fff;
  border-color: #667eea;
}
</style>
