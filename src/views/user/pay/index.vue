<template>
  <div class="pay-page page-container">
    <div class="pay-container">
      <div class="pay-card">
        <h2 class="pay-title">💳 选择支付方式</h2>
        <div class="pay-amount">
          <span class="amount-label">应付金额</span>
          <span class="amount-value">¥{{ orderInfo?.payAmount || '0.00' }}</span>
        </div>

        <div class="order-info" v-if="orderInfo">
          <p><span>订单号：</span>{{ orderNo }}</p>
          <p><span>订单类型：</span>{{ orderInfo.orderType === 1 ? '景点门票' : '酒店预订' }}</p>
          <p><span>商品：</span>{{ orderInfo.targetName }}</p>
          <p><span>数量：</span>{{ orderInfo.quantity }}份</p>
          <p><span>有效期：</span>{{ orderInfo.visitDate || '长期有效' }}</p>
        </div>

        <div class="pay-channels">
          <h3>支付渠道</h3>
          <div class="channel-list">
            <div
              v-for="ch in channels"
              :key="ch.value"
              class="channel-item"
              :class="{ active: payChannel === ch.value }"
              @click="payChannel = ch.value"
            >
              <span class="channel-icon">{{ ch.icon }}</span>
              <span class="channel-name">{{ ch.name }}</span>
              <el-icon v-if="payChannel === ch.value" class="check-icon"><Check /></el-icon>
            </div>
          </div>
        </div>

        <div class="pay-action">
          <button class="pay-btn" :disabled="loading" @click="handlePay">
            <span v-if="loading">支付中...</span>
            <span v-else>立即支付 ¥{{ orderInfo?.payAmount || '0.00' }}</span>
          </button>
          <button class="cancel-btn" @click="$router.back()">取消支付</button>
        </div>

        <div class="pay-tips">
          <p>* 支付完成后，订单将自动生效</p>
          <p>* 如有问题，请联系客服</p>
        </div>
      </div>

      <!-- 支付二维码弹窗 -->
      <el-dialog v-model="showQRCode" title="扫码支付" width="360px" center>
        <div class="qrcode-box">
          <div v-if="payChannel === 'wechat'" class="qrcode-placeholder">
            <div class="qr-icon">💚</div>
            <p>请使用微信扫码支付</p>
          </div>
          <div v-else-if="payChannel === 'alipay'" class="qrcode-placeholder">
            <div class="qr-icon">💙</div>
            <p>请使用支付宝扫码支付</p>
          </div>
          <div class="qr-expire">
            <span v-if="countdown > 0">支付倒计时：{{ countdown }}秒</span>
            <span v-else class="expired">二维码已过期，请刷新</span>
          </div>
        </div>
        <template #footer>
          <el-button @click="showQRCode = false">关闭</el-button>
          <el-button type="primary" @click="confirmPay">我已支付</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { paymentApi } from '@/api/payment'
import { getOrderDetailApi, payOrderApi } from '@/api/order'

const route = useRoute()
const router = useRouter()
const orderNo = route.params.orderNo as string

const orderInfo = ref<any>(null)
const payChannel = ref('wechat')
const loading = ref(false)
const showQRCode = ref(false)
const countdown = ref(1800)
let countdownTimer: any = null

const channels = [
  { value: 'wechat', name: '微信支付', icon: '💚' },
  { value: 'alipay', name: '支付宝', icon: '💙' },
  { value: 'card', name: '银行卡', icon: '💳' },
]

onMounted(async () => {
  await loadOrderInfo()
  startCountdown()
})

onUnmounted(() => {
  clearInterval(countdownTimer)
})

async function loadOrderInfo() {
  try {
    const res = await getOrderDetailApi(orderNo as any)
    if (res.data) {
      orderInfo.value = res.data
    }
  } catch {
    ElMessage.error('订单不存在')
    router.replace('/orders')
  }
}

function startCountdown() {
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) countdown.value--
    else clearInterval(countdownTimer)
  }, 1000)
}

async function handlePay() {
  loading.value = true
  try {
    const res = await paymentApi.create(orderNo, payChannel.value)
    if (res.code === 200) {
      if (payChannel.value === 'card') {
        await confirmPay()
      } else {
        showQRCode.value = true
        countdown.value = 1800
        startCountdown()
      }
    }
  } catch (e: any) {
    ElMessage.error(e.message || '支付创建失败')
  } finally {
    loading.value = false
  }
}

async function confirmPay() {
  try {
    await payOrderApi(orderNo, payChannel.value)
    ElMessage.success('支付成功！')
    showQRCode.value = false
    setTimeout(() => router.replace('/orders'), 1500)
  } catch (e: any) {
    ElMessage.error(e.message || '支付确认失败，请稍后重试')
  }
}
</script>

<style scoped>
.pay-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
.pay-container {
  width: 100%;
  max-width: 480px;
}
.pay-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px 32px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.pay-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 24px;
  color: #333;
}
.pay-amount {
  text-align: center;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
  border-radius: 12px;
  margin-bottom: 24px;
}
.amount-label {
  display: block;
  font-size: 14px;
  color: #888;
  margin-bottom: 8px;
}
.amount-value {
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
}
.order-info {
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
  margin-bottom: 24px;
  font-size: 14px;
  line-height: 1.8;
}
.order-info p {
  display: flex;
  color: #555;
}
.order-info span {
  color: #888;
  width: 80px;
  flex-shrink: 0;
}
.pay-channels h3 {
  font-size: 16px;
  margin-bottom: 12px;
  color: #333;
}
.channel-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.channel-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border: 2px solid #e8ecf0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}
.channel-item:hover {
  border-color: #667eea;
  background: #f8f6ff;
}
.channel-item.active {
  border-color: #667eea;
  background: #f0eeff;
}
.channel-icon {
  font-size: 24px;
  margin-right: 12px;
}
.channel-name {
  flex: 1;
  font-size: 15px;
  color: #333;
}
.check-icon {
  color: #667eea;
  font-size: 18px;
}
.pay-action {
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.pay-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 17px;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.2s;
}
.pay-btn:hover:not(:disabled) { opacity: 0.9; }
.pay-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.cancel-btn {
  width: 100%;
  padding: 12px;
  background: #fff;
  color: #888;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  cursor: pointer;
}
.cancel-btn:hover { color: #333; }
.pay-tips {
  margin-top: 20px;
  font-size: 12px;
  color: #aaa;
  line-height: 1.8;
}
.qrcode-box {
  text-align: center;
  padding: 20px;
}
.qrcode-placeholder {
  padding: 40px;
  background: #f9fafb;
  border-radius: 12px;
  margin-bottom: 16px;
}
.qr-icon { font-size: 64px; margin-bottom: 12px; }
.qr-expire { font-size: 14px; color: #888; }
.expired { color: #f56c6c; }
</style>
