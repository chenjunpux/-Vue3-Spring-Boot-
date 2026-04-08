<template>
  <div class="login-page" ref="pageRef">

    <!-- 动态背景 -->
    <div class="bg-animation">
      <div class="shape shape-1" />
      <div class="shape shape-2" />
      <div class="shape shape-3" />
      <div class="gradient-orb orb-1" />
      <div class="gradient-orb orb-2" />
    </div>

    <!-- 左侧品牌区 -->
    <div class="brand-side" ref="brandSideRef">
      <div class="brand-content">
        <div class="brand-logo">
          <span class="logo-icon">🧳</span>
          <span class="logo-text">TRAVEL</span>
        </div>

        <div class="brand-text" ref="brandTextRef">
          <h1 class="brand-title">探索<br />世界之美</h1>
          <p class="brand-desc">
            百万旅行者的选择<br />
            发现下一个目的地
          </p>
        </div>

        <div class="brand-stats">
          <div class="stat-item" v-for="s in brandStats" :key="s.label">
            <span class="stat-num">{{ s.value }}</span>
            <span class="stat-label">{{ s.label }}</span>
          </div>
        </div>

        <div class="brand-cards">
          <div class="review-card" v-for="r in reviews" :key="r.name">
            <p class="review-text">"{{ r.text }}"</p>
            <div class="review-author">
              <div class="author-avatar">{{ r.name[0] }}</div>
              <span>{{ r.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="form-side" ref="formSideRef">
      <div class="form-card" ref="formCardRef">

        <div class="form-header">
          <h2 class="form-title" ref="formTitleRef">欢迎回来</h2>
          <p class="form-subtitle">登录账号，开启你的旅行</p>
        </div>

        <form class="login-form" @submit.prevent="handleLogin">
          <!-- 用户名 -->
          <div class="input-group" ref="input1Ref">
            <label class="input-label">用户名 / 手机号</label>
            <div class="input-wrap">
              <span class="input-icon">👤</span>
              <input v-model="form.username" type="text" placeholder="请输入用户名" autocomplete="username" />
              <div class="input-line" />
            </div>
          </div>

          <!-- 密码 -->
          <div class="input-group" ref="input2Ref">
            <label class="input-label">密码</label>
            <div class="input-wrap">
              <span class="input-icon">🔒</span>
              <input v-model="form.password" :type="showPassword ? 'text' : 'password'" placeholder="请输入密码"
                autocomplete="current-password" />
              <button type="button" class="toggle-pwd" @click="showPassword = !showPassword">
                {{ showPassword ? '🙈' : '👁️' }}
              </button>
              <div class="input-line" />
            </div>
          </div>

          <!-- 记住 & 忘记 -->
          <div class="form-options" ref="input3Ref">
            <label class="remember-me">
              <input type="checkbox" v-model="rememberMe" />
              <span class="checkmark" />
              <span>记住我</span>
            </label>
            <a href="#" class="forgot-link">忘记密码？</a>
          </div>

          <!-- 登录按钮 -->
          <button type="submit" class="login-btn" ref="loginBtnRef" :class="{ loading: loading }">
            <span class="btn-text">{{ loading ? '登录中...' : '登录' }}</span>
            <span class="btn-arrow">→</span>
            <div class="btn-loading-ring" v-if="loading" />
          </button>
        </form>

        <!-- 分隔线 -->


        <!-- 其他登录方式 -->
        <div class="social-login" ref="socialRef">
          <button class="social-btn" @click="handleSocialLogin('wechat')">
            <span class="social-icon">💬</span>
            微信登录
          </button>
          <div class="divider" ref="dividerRef">
            <span>或</span>
          </div>
          <button class="social-btn" @click="handleSocialLogin('phone')">
            <span class="social-icon">📱</span>
            手机号登录
          </button>
        </div>

        <!-- 注册 -->
        <div class="form-footer" ref="footerRef">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册 →</router-link>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import gsap from 'gsap'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// Refs
const pageRef = ref<HTMLElement>()
const brandSideRef = ref<HTMLElement>()
const brandTextRef = ref<HTMLElement>()
const formSideRef = ref<HTMLElement>()
const formCardRef = ref<HTMLElement>()
const formTitleRef = ref<HTMLElement>()
const input1Ref = ref<HTMLElement>()
const input2Ref = ref<HTMLElement>()
const input3Ref = ref<HTMLElement>()
const loginBtnRef = ref<HTMLElement>()
const dividerRef = ref<HTMLElement>()
const socialRef = ref<HTMLElement>()
const footerRef = ref<HTMLElement>()

// State
const form = reactive({ username: '', password: '' })
const showPassword = ref(false)
const rememberMe = ref(false)
const loading = ref(false)

// 品牌数据
const brandStats = [
  { value: '12,847+', label: '精选景点' },
  { value: '98,652+', label: '注册用户' },
  { value: '3,428+', label: '合作酒店' },
]

const reviews = [
  { name: '小明', text: '用这个App规划了云南之旅，超棒！风景美到窒息' },
  { name: '小红', text: '酒店价格比别的平台便宜好多，推荐！' },
  { name: '旅行者', text: '游记社区太有用了，少走了很多弯路' },
]

// 登录
async function handleLogin() {
  if (!form.username.trim()) {
    ElMessage.warning('请输入用户名')
    shakeInput(input1Ref.value)
    return
  }
  if (!form.password) {
    ElMessage.warning('请输入密码')
    shakeInput(input2Ref.value)
    return
  }

  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    // 根据角色跳转：管理员 → 管理后台，普通用户 → 前台首页
    const role = userStore.userInfo?.role
    if (role === 2) {
      router.push('/admin/dashboard')
    } else {
      router.push('/home')
    }
  } catch {
    shakeInput(input2Ref.value)
  } finally {
    loading.value = false
  }
}

function shakeInput(el: HTMLElement | undefined) {
  if (!el) return
  gsap.fromTo(el, { x: 0 }, {
    x: [-8, 8, -6, 6, -4, 4, 0],
    duration: 0.4,
    ease: 'power2.out',
  })
}

function handleSocialLogin(type: string) {
  ElMessage.info(`${type === 'wechat' ? '微信' : '手机号'}登录功能开发中...`)
}

// GSAP 动画
let ctx: gsap.Context

onMounted(async () => {
  await nextTick()
  ctx = gsap.context(() => {
    try {
      // 声明 timeline
      const tl = gsap.timeline({ defaults: { ease: 'power3.out' } })

      // 确保关键 ref 已挂载再执行动画
      if (!loginBtnRef.value || !brandSideRef.value || !formCardRef.value) {
        console.warn('GSAP: 关键ref未挂载，跳过主体动画')
        return
      }

      // 左侧品牌区入场
      tl.from(brandSideRef.value, { opacity: 0, x: -80, duration: 1 })
        .from('.brand-logo', { opacity: 0, y: 30, duration: 0.6 }, '-=0.6')
        .from('.brand-title', { opacity: 0, y: 50, duration: 0.8 }, '-=0.3')
        .from('.brand-desc', { opacity: 0, y: 30, duration: 0.6 }, '-=0.5')
        .from('.stat-item', { opacity: 0, y: 20, stagger: 0.1, duration: 0.5 }, '-=0.3')
        .from('.review-card', { opacity: 0, y: 30, stagger: 0.15, duration: 0.6 }, '-=0.2')

      // 右侧表单入场
      tl.from(formCardRef.value, { opacity: 0, y: 60, scale: 0.97, duration: 0.8 }, '-=0.8')
        .from(formTitleRef.value, { opacity: 0, y: 20, duration: 0.5 }, '-=0.4')
        .from([input1Ref.value, input2Ref.value, input3Ref.value], {
          opacity: 0, y: 20, stagger: 0.12, duration: 0.5,
        }, '-=0.3')
        .fromTo(loginBtnRef.value, { opacity: 0, y: 20, scale: 0.95 }, { opacity: 1, y: 0, scale: 1, duration: 0.5 }, '-=0.2')
        .from([dividerRef.value, socialRef.value, footerRef.value], {
          opacity: 0, stagger: 0.1, duration: 0.4,
        }, '-=0.1')
        .from('.social-icon', { opacity: 0, scale: 0.95, duration: 0.3, stagger: 0.1 }, '-=0.3')
    } catch (e) { console.warn('GSAP animation error:', e) }


    // 背景动画
    gsap.to('.shape-1', {
      x: 60, y: 40, rotation: 360, duration: 20, repeat: -1, yoyo: true, ease: 'none',
    })
    gsap.to('.shape-2', {
      x: -50, y: -60, rotation: -180, duration: 15, repeat: -1, yoyo: true, ease: 'none',
    })
    gsap.to('.shape-3', {
      x: 30, y: -40, rotation: 270, duration: 25, repeat: -1, yoyo: true, ease: 'none',
    })
    gsap.to('.orb-1', { x: 100, y: 80, duration: 8, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.orb-2', { x: -80, y: -60, duration: 10, repeat: -1, yoyo: true, ease: 'sine.inOut' })

    // 输入框聚焦动画
    document.querySelectorAll('.input-wrap input').forEach((input) => {
      input.addEventListener('focus', () => {
        const line = input.closest('.input-wrap')?.querySelector('.input-line')
        gsap.to(line, { scaleX: 1, duration: 0.3, ease: 'power2.out' })
      })
      input.addEventListener('blur', () => {
        const line = input.closest('.input-wrap')?.querySelector('.input-line')
        gsap.to(line, { scaleX: 0, duration: 0.3, ease: 'power2.out' })
      })
    })

    // 登录按钮 hover
    loginBtnRef.value?.addEventListener('mouseenter', () => {
      gsap.to('.btn-arrow', { x: 6, duration: 0.3, ease: 'power2.out' })
    })
    loginBtnRef.value?.addEventListener('mouseleave', () => {
      gsap.to('.btn-arrow', { x: 0, duration: 0.3, ease: 'power2.out' })
    })

  }, pageRef.value!)
})

onUnmounted(() => {
  ctx?.revert()
})
</script>

<style scoped lang="scss">
.login-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  background: #06060f;
  overflow: hidden;
  position: relative;
}

// ==================== 动态背景 ====================
.bg-animation {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
}

.shape-1 {
  width: 500px;
  height: 500px;
  background: #667eea;
  top: -100px;
  left: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #f5576c;
  bottom: -80px;
  right: 10%;
}

.shape-3 {
  width: 350px;
  height: 350px;
  background: #4facfe;
  top: 40%;
  left: 30%;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.08;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -200px;
  right: -200px;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  bottom: -150px;
  left: -150px;
}

// ==================== 左侧品牌区 ====================
.brand-side {
  position: relative;
  z-index: 1;
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 80px;
  min-height: 100vh;
}

.brand-content {
  max-width: 480px;
  margin: 0 auto;
  color: white;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 60px;

  .logo-icon {
    font-size: 36px;
  }

  .logo-text {
    font-size: 22px;
    font-weight: 900;
    letter-spacing: 6px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.brand-title {
  font-size: clamp(48px, 6vw, 72px);
  font-weight: 900;
  line-height: 1.05;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.6) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-desc {
  font-size: 18px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 48px;
}

.brand-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 48px;

  .stat-item {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .stat-num {
      font-size: 28px;
      font-weight: 800;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .stat-label {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.4);
      text-transform: uppercase;
      letter-spacing: 1px;
    }
  }
}

.brand-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  backdrop-filter: blur(20px);

  .review-text {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
    line-height: 1.6;
    margin-bottom: 12px;
  }

  .review-author {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);

    .author-avatar {
      width: 28px;
      height: 28px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 700;
      color: white;
    }
  }
}

// ==================== 右侧表单 ====================
.form-side {
  position: relative;
  z-index: 1;
  width: 50%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 80px;
}

.form-card {
  width: 100%;
  max-width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-header {
  text-align: center;
  margin-bottom: 36px;

  .form-title {
    font-size: 30px;
    font-weight: 800;
    color: white;
    margin-bottom: 8px;
    letter-spacing: -0.5px;
    background: linear-gradient(135deg, #ffffff, rgba(255, 255, 255, 0.75));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-size: 200% auto;
    animation: shimmer-text 3s ease-in-out infinite;
  }

  .form-subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.35);
    letter-spacing: 0.3px;
  }
}

@keyframes shimmer-text {

  0%,
  100% {
    background-position: 0% center;
  }

  50% {
    background-position: 100% center;
  }
}

// 表单
.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 420px;
  gap: 20px;
  margin-bottom: 28px;
}

.input-group {
  .input-label {
    display: block;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.5);
    margin-bottom: 10px;
    text-transform: uppercase;
    letter-spacing: 1px;
  }
}

.input-wrap {
  position: relative;
  width: 360px;
  display: flex;
  align-items: center;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid rgba(255, 255, 255, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

  &::before {
    content: '';
    position: absolute;
    inset: -2px;
    border-radius: 18px;
    background: linear-gradient(135deg, #667eea, #764ba2, #f5576c);
    opacity: 0;
    z-index: -1;
    transition: opacity 0.4s;
    filter: blur(8px);
  }

  &:focus-within {
    background: rgba(255, 255, 255, 0.07);
    border-color: transparent;
    box-shadow: 0 0 0 1.5px rgba(102, 126, 234, 0.6),
      0 4px 20px rgba(102, 126, 234, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);

    &::before {
      opacity: 0.4;
    }

    .input-label {
      color: rgba(102, 126, 234, 0.9);
    }

    .input-icon {
      opacity: 0.9;
      transform: scale(1.1);
      filter: drop-shadow(0 0 6px rgba(102, 126, 234, 0.5));
    }

    .input-line {
      transform: scaleX(1);
    }
  }

  .input-icon {
    position: absolute;
    left: 16px;
    font-size: 18px;
    opacity: 0.45;
    transition: all 0.3s;
    z-index: 1;
    pointer-events: none;
  }

  input {
    width: 420px;
    height: 52px;
    padding: 0 44px 0 46px;
    background: transparent;
    border: none;
    border-radius: 16px;
    color: white;
    font-size: 15px;
    outline: none;
    transition: all 0.3s;

    &::placeholder {
      color: rgba(255, 255, 255, 0.2);
      transition: color 0.3s;
    }

    &:focus::placeholder {
      color: rgba(255, 255, 255, 0.35);
    }
  }

  .toggle-pwd {
    position: absolute;
    right: 12px;
    background: none;
    border: none;
    font-size: 16px;
    cursor: pointer;
    padding: 6px;
    opacity: 0.35;
    transition: all 0.25s;
    border-radius: 8px;

    &:hover {
      opacity: 0.8;
      background: rgba(255, 255, 255, 0.08);
    }
  }

  .input-line {
    position: absolute;
    bottom: -1px;
    left: 10%;
    width: 80%;
    height: 2px;
    background: linear-gradient(90deg, transparent, #667eea, #764ba2, transparent);
    border-radius: 2px;
    transform: scaleX(0);
    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

// 选项
.form-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.45);

  input[type="checkbox"] {
    display: none;
  }

  .checkmark {
    width: 20px;
    height: 20px;
    border: 1.5px solid rgba(255, 255, 255, 0.15);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s;
    position: relative;
    background: rgba(255, 255, 255, 0.04);

    // checkmark dot
    &::after {
      content: '';
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: white;
      opacity: 0;
      transform: scale(0);
      transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    }
  }

  input:checked+.checkmark {
    background: linear-gradient(135deg, #667eea, #764ba2);
    border-color: transparent;
    box-shadow: 0 0 12px rgba(102, 126, 234, 0.4);

    &::after {
      opacity: 1;
      transform: scale(1);
    }
  }

  &:hover .checkmark {
    border-color: rgba(102, 126, 234, 0.5);
  }
}

.forgot-link {
  font-size: 13px;
  color: rgba(102, 126, 234, 0.8);
  text-decoration: none;

  &:hover {
    color: #667eea;
    text-decoration: underline;
  }
}

// 登录按钮
.login-btn {
  position: relative;
  width: 420px;
  height: 52px;
  background: linear-gradient(135deg, #d2d4db, #764ba2);
  border: none;
  border-radius: 14px;
  color: white;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  letter-spacing: 0.5px;
  margin-top: 8px;

  // shimmer effect
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
    transition: left 0.5s;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #7c8ff5, #9b6fd1);
    opacity: 0;
    transition: opacity 0.35s;
    border-radius: 14px;
  }

  &:hover {
    &::before {
      left: 100%;
    }

    transform: translateY(-3px);
    box-shadow: 0 12px 35px rgba(102, 126, 234, 0.45),
    0 4px 15px rgba(102, 126, 234, 0.3);

    &::after {
      opacity: 1;
    }
  }

  &:active {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);

    &::after {
      opacity: 1;
    }
  }

  .btn-text,
  .btn-arrow {
    position: relative;
    z-index: 1;
  }

  .btn-arrow {
    font-size: 18px;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  &:hover .btn-arrow {
    transform: translateX(5px);
  }

  opacity: 1 !important;
  visibility: visible !important;

  .btn-loading-ring {
    position: absolute;
    width: 24px;
    height: 24px;
    border: 2.5px solid rgba(255, 255, 255, 0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// 分隔线
.divider {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.12), transparent);
  }

  span {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.25);
    text-transform: uppercase;
    letter-spacing: 2.5px;
    font-weight: 600;
  }
}

// 社交登录
.social-login {
  display: flex;
  justify-content: center;
  gap: 12px;
  width: 100%;
  margin-bottom: 28px;
}

.social-btn {
  width: 25%;
  height: 50px;
  background: rgba(255, 255, 255, 0.04);
  border: 1.5px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
  letter-spacing: 0.3px;

  .social-icon {
    font-size: 18px;
    transition: all 0.3s;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.08);
    border-color: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
    color: white;

    .social-icon {
      transform: scale(1.2);
    }
  }
}

// 注册提示
.form-footer {
  text-align: center;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);

  a {
    color: #667eea;
    font-weight: 600;
    text-decoration: none;
    margin-left: 6px;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
