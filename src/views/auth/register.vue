<template>
  <div class="register-page" ref="pageRef">

    <!-- 背景 -->
    <div class="bg-animation">
      <div class="mesh-gradient" />
      <div class="floating-shapes">
        <div class="fshape fshape-1" />
        <div class="fshape fshape-2" />
        <div class="fshape fshape-3" />
        <div class="fshape fshape-4" />
      </div>
    </div>

    <div class="register-container" ref="containerRef">

      <!-- 左侧 -->
      <div class="info-panel" ref="infoRef">
        <div class="info-logo">
          <span class="logo-icon">🧳</span>
          <span class="logo-text">TRAVEL</span>
        </div>

        <div class="info-content">
          <h2 class="info-title">开启你的<br />旅行探索</h2>
          <p class="info-desc">加入我们，发现全球最美风景，规划完美行程</p>
        </div>

        <div class="features">
          <div class="feature-item" v-for="f in features" :key="f.title">
            <span class="feature-icon">{{ f.icon }}</span>
            <div class="feature-text">
              <h4>{{ f.title }}</h4>
              <p>{{ f.desc }}</p>
            </div>
          </div>
        </div>

        <div class="info-quote">
          <blockquote>"旅行是唯一让你花钱却变得更富有的方式"</blockquote>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="form-panel" ref="formRef">
        <div class="form-box">

          <div class="form-header">
            <h3>创建账号</h3>
            <p>注册即表示同意我们的 <a href="#">服务条款</a></p>
          </div>

          <form @submit.prevent="handleRegister">
            <!-- 用户名 -->
            <div class="field" ref="f1Ref">
              <label>用户名</label>
              <div class="field-wrap">
                <span>👤</span>
                <input v-model="form.username" type="text" placeholder="3-20位字母或数字" />
                <div class="field-line" />
              </div>
            </div>

            <!-- 昵称 -->
            <div class="field" ref="f2Ref">
              <label>昵称（选填）</label>
              <div class="field-wrap">
                <span>✏️</span>
                <input v-model="form.nickname" type="text" placeholder="你在社区的名字" />
                <div class="field-line" />
              </div>
            </div>

            <!-- 手机号 -->
            <div class="field" ref="f3Ref">
              <label>手机号</label>
              <div class="field-wrap">
                <span>📱</span>
                <input v-model="form.phone" type="tel" placeholder="用于登录和找回密码" />
                <div class="field-line" />
              </div>
            </div>

            <!-- 密码 -->
            <div class="field" ref="f4Ref">
              <label>设置密码</label>
              <div class="field-wrap">
                <span>🔒</span>
                <input
                  v-model="form.password"
                  :type="showPwd ? 'text' : 'password'"
                  placeholder="至少6位"
                />
                <button type="button" @click="showPwd = !showPwd">{{ showPwd ? '🙈' : '👁️' }}</button>
                <div class="field-line" />
              </div>
              <!-- 密码强度 -->
              <div class="pwd-strength" v-if="form.password">
                <div class="strength-bar">
                  <div class="strength-fill" :style="{ width: strengthWidth, background: strengthColor }" />
                </div>
                <span class="strength-text" :style="{ color: strengthColor }">{{ strengthText }}</span>
              </div>
            </div>

            <!-- 确认密码 -->
            <div class="field" ref="f5Ref">
              <label>确认密码</label>
              <div class="field-wrap">
                <span>🔐</span>
                <input
                  v-model="form.confirm"
                  :type="showPwd ? 'text' : 'password'"
                  placeholder="再次输入密码"
                />
                <div class="field-line" />
              </div>
            </div>

            <!-- 协议 -->
            <div class="agreement" ref="f6Ref">
              <label class="check-agree">
                <input type="checkbox" v-model="agree" />
                <span class="check-box" />
                <span>我已阅读并同意 <a href="#">《用户协议》</a> 和 <a href="#">《隐私政策》</a></span>
              </label>
            </div>

            <!-- 注册按钮 -->
            <button type="submit" class="register-btn" ref="registerBtnRef" :disabled="loading">
              <span>{{ loading ? '注册中...' : '立即注册' }}</span>
              <span class="btn-icon" v-if="!loading">→</span>
              <div class="btn-spinner" v-if="loading" />
            </button>
          </form>

          <div class="back-login" ref="f7Ref">
            已有账号？
            <router-link to="/login">立即登录 ←</router-link>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import gsap from 'gsap'

const router = useRouter()

const pageRef = ref<HTMLElement>()
const containerRef = ref<HTMLElement>()
const infoRef = ref<HTMLElement>()
const formRef = ref<HTMLElement>()
const f1Ref = ref<HTMLElement>()
const f2Ref = ref<HTMLElement>()
const f3Ref = ref<HTMLElement>()
const f4Ref = ref<HTMLElement>()
const f5Ref = ref<HTMLElement>()
const f6Ref = ref<HTMLElement>()
const f7Ref = ref<HTMLElement>()
const registerBtnRef = ref<HTMLElement>()

const form = reactive({ username: '', nickname: '', phone: '', password: '', confirm: '' })
const showPwd = ref(false)
const agree = ref(false)
const loading = ref(false)

const features = [
  { icon: '🗺️', title: '海量景点', desc: '覆盖全球10000+热门景点' },
  { icon: '🏨', title: '优惠酒店', desc: '比价保证，入住无忧' },
  { icon: '📝', title: '旅行游记', desc: '分享旅程，收获共鸣' },
]

// 密码强度
const pwdStrength = computed(() => {
  const p = form.password
  if (!p) return 0
  let score = 0
  if (p.length >= 6) score++
  if (p.length >= 10) score++
  if (/[A-Z]/.test(p)) score++
  if (/[0-9]/.test(p)) score++
  if (/[^A-Za-z0-9]/.test(p)) score++
  return Math.min(4, score)
})

const strengthWidth = computed(() => {
  const map = ['0%', '25%', '50%', '75%', '100%']
  return map[pwdStrength.value]
})

const strengthColor = computed(() => {
  const map = ['', '#ef4444', '#f59e0b', '#22c55e', '#10b981']
  return map[pwdStrength.value]
})

const strengthText = computed(() => {
  const map = ['', '弱', '中等', '良好', '强']
  return map[pwdStrength.value]
})

async function handleRegister() {
  if (!form.username.trim() || form.username.length < 3) {
    ElMessage.warning('用户名至少3位')
    shake(f1Ref.value)
    return
  }
  if (!form.phone.match(/^1[3-9]\d{9}$/)) {
    ElMessage.warning('请输入正确的手机号')
    shake(f3Ref.value)
    return
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码至少6位')
    shake(f4Ref.value)
    return
  }
  if (form.password !== form.confirm) {
    ElMessage.warning('两次密码不一致')
    shake(f5Ref.value)
    return
  }
  if (!agree.value) {
    ElMessage.warning('请阅读并同意用户协议')
    return
  }

  loading.value = true
  try {
    // 模拟注册
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}

function shake(el: HTMLElement | undefined) {
  if (!el) return
  gsap.fromTo(el, { x: 0 }, { x: [-8, 8, -6, 6, -4, 4, 0], duration: 0.4, ease: 'power2.out' })
}

let ctx: gsap.Context

onMounted(async () => {
  await nextTick() // 等 Vue 完成渲染
  ctx = gsap.context(() => {
    // 确保关键 ref 已挂载再执行动画
    if (!registerBtnRef.value || !containerRef.value || !formRef.value) return

    // 整体入场
    gsap.from(containerRef.value, { opacity: 0, duration: 0.6 })

    // 左侧
    gsap.from(infoRef.value, { opacity: 0, x: -60, duration: 1, ease: 'power3.out' })
    gsap.from('.info-logo', { opacity: 0, y: 30, duration: 0.6 }, '-=0.7')
    gsap.from('.info-title', { opacity: 0, y: 40, duration: 0.8 }, '-=0.5')
    gsap.from('.info-desc', { opacity: 0, y: 20, duration: 0.5 }, '-=0.3')
    gsap.from('.feature-item', { opacity: 0, x: -30, stagger: 0.15, duration: 0.6 }, '-=0.2')
    gsap.from('.info-quote', { opacity: 0, y: 20, duration: 0.5 }, '-=0.1')

    // 右侧
    gsap.from(formRef.value, { opacity: 0, y: 60, duration: 1, ease: 'power3.out', delay: 0.2 })
    gsap.from([f1Ref.value, f2Ref.value, f3Ref.value, f4Ref.value, f5Ref.value, f6Ref.value], {
      opacity: 0, y: 20, stagger: 0.1, duration: 0.5, ease: 'power2.out',
    }, '-=0.6')
    gsap.fromTo(registerBtnRef.value, { opacity: 0, y: 20, scale: 0.95 }, { opacity: 1, y: 0, scale: 1, duration: 0.5 }, '-=0.3')
    gsap.from(f7Ref.value, { opacity: 0, duration: 0.4 }, '-=0.1')

    // 背景浮动
    gsap.to('.fshape-1', { y: -30, rotation: 360, duration: 18, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.fshape-2', { y: 40, rotation: -180, duration: 22, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.fshape-3', { y: -20, rotation: 200, duration: 16, repeat: -1, yoyo: true, ease: 'sine.inOut' })
    gsap.to('.fshape-4', { y: 30, rotation: -270, duration: 20, repeat: -1, yoyo: true, ease: 'sine.inOut' })

    // 输入聚焦
    document.querySelectorAll('.field-wrap input').forEach(input => {
      input.addEventListener('focus', () => {
        gsap.to(input.closest('.field-wrap')?.querySelector('.field-line'), {
          scaleX: 1, duration: 0.3, ease: 'power2.out',
        })
      })
      input.addEventListener('blur', () => {
        gsap.to(input.closest('.field-wrap')?.querySelector('.field-line'), {
          scaleX: 0, duration: 0.3,
        })
      })
    })

    // 按钮 hover
    registerBtnRef.value?.addEventListener('mouseenter', () => {
      gsap.to('.btn-icon', { x: 5, duration: 0.2 })
    })
    registerBtnRef.value?.addEventListener('mouseleave', () => {
      gsap.to('.btn-icon', { x: 0, duration: 0.2 })
    })

  }, pageRef.value!)
})

onUnmounted(() => ctx?.revert())
</script>

<style scoped lang="scss">

.register-page {
  min-height: 100vh;
  background: #050510;
  display: flex;
  overflow: hidden;
  position: relative;
}

// 背景
.bg-animation {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.mesh-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 20%, rgba(102,126,234,0.12) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 80%, rgba(118,75,162,0.12) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 50%, rgba(79,172,254,0.05) 0%, transparent 60%);
}

.floating-shapes {
  .fshape {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.12;
  }
  .fshape-1 { width: 400px; height: 400px; background: #667eea; top: -100px; right: 20%; }
  .fshape-2 { width: 300px; height: 300px; background: #f5576c; bottom: -50px; left: 10%; }
  .fshape-3 { width: 250px; height: 250px; background: #4facfe; top: 30%; right: 5%; }
  .fshape-4 { width: 200px; height: 200px; background: #22c55e; bottom: 20%; right: 30%; }
}

// 容器
.register-container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  min-height: 100vh;
}

// ==================== 左侧信息区 ====================
.info-panel {
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px 80px;
  border-right: 1px solid rgba(255,255,255,0.05);
}

.info-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 60px;

  .logo-icon { font-size: 32px; }
  .logo-text {
    font-size: 20px;
    font-weight: 900;
    letter-spacing: 5px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

.info-content {
  margin-bottom: 48px;

  .info-title {
    font-size: clamp(40px, 5vw, 60px);
    font-weight: 900;
    color: white;
    line-height: 1.1;
    margin-bottom: 16px;
  }

  .info-desc {
    font-size: 16px;
    color: rgba(255,255,255,0.4);
    line-height: 1.6;
  }
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 48px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;

  .feature-icon { font-size: 28px; }

  .feature-text {
    h4 { font-size: 16px; font-weight: 700; color: white; margin-bottom: 4px; }
    p { font-size: 13px; color: rgba(255,255,255,0.4); }
  }
}

.info-quote {
  blockquote {
    font-size: 14px;
    color: rgba(255,255,255,0.3);
    font-style: italic;
    border-left: 3px solid rgba(102,126,234,0.5);
    padding-left: 16px;
    line-height: 1.6;
  }
}

// ==================== 右侧表单 ====================
.form-panel {
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 80px;
}

.form-box {
  width: 100%;
  max-width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-header {
  margin-bottom: 36px;
  text-align: center;

  h3 { font-size: 28px; font-weight: 800; color: white; margin-bottom: 8px; }
  p { font-size: 13px; color: rgba(255,255,255,0.35); }
  a { color: #667eea; text-decoration: none; &:hover { text-decoration: underline; } }
}

form {
  width: 360px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.field {
  label {
    display: block;
    font-size: 12px;
    color: rgba(255,255,255,0.4);
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 8px;
  }
}

.field-wrap {
  position: relative;
  display: flex;
  align-items: center;

  span:first-child {
    position: absolute;
    left: 0;
    font-size: 16px;
    opacity: 0.4;
  }

  input {
    width: 360px;
    height: 50px;
    padding: 0 40px;
    background: rgba(255,255,255,0.04);
    border: 1.5px solid rgba(255,255,255,0.08);
    border-radius: 12px;
    color: white;
    font-size: 14px;
    outline: none;
    transition: all 0.3s;

    &::placeholder { color: rgba(255,255,255,0.2); }

    &:focus {
      background: rgba(255,255,255,0.07);
      border-color: rgba(102,126,234,0.5);
      box-shadow: 0 0 0 3px rgba(102,126,234,0.12);
    }
  }

  button[type="button"] {
    position: absolute;
    right: 0;
    background: none;
    border: none;
    font-size: 14px;
    cursor: pointer;
    opacity: 0.4;
    padding: 4px 8px;
    &:hover { opacity: 0.8; }
  }

  .field-line {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    background: linear-gradient(90deg, #667eea, #764ba2);
    transform: scaleX(0);
    transform-origin: center;
    border-radius: 2px;
  }
}

.pwd-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;

  .strength-bar {
    flex: 1;
    height: 3px;
    background: rgba(255,255,255,0.1);
    border-radius: 2px;
    overflow: hidden;
  }

  .strength-fill {
    height: 100%;
    border-radius: 2px;
    transition: width 0.4s, background 0.4s;
  }

  .strength-text { font-size: 12px; font-weight: 600; }
}

.agreement {
  .check-agree {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    cursor: pointer;
    font-size: 13px;
    color: rgba(255,255,255,0.4);
    line-height: 1.5;

    input { display: none; }

    .check-box {
      flex-shrink: 0;
      width: 16px; height: 16px;
      margin-top: 2px;
      border: 1.5px solid rgba(255,255,255,0.2);
      border-radius: 4px;
      transition: all 0.2s;
    }

    input:checked + .check-box {
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-color: transparent;
    }

    a { color: #667eea; text-decoration: none; &:hover { text-decoration: underline; } }
  }
}

// 注册按钮
.register-btn {
  position: relative;
  width: 360px;
  height: 50px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 14px;
  color: white;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: hidden;
  transition: all 0.3s;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #7c8ff5, #9b6fd1);
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover:not(:disabled) {
    &::before { opacity: 1; }
    transform: translateY(-2px);
    box-shadow: 0 8px 30px rgba(102,126,234,0.4);
  }

  &:disabled { opacity: 0.7; cursor: not-allowed; }
  opacity: 1 !important; visibility: visible !important;

  .btn-icon { font-size: 18px; transition: transform 0.2s; }

  .btn-spinner {
    width: 20px; height: 20px;
    border: 2px solid rgba(255,255,255,0.3);
    border-top-color: white;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin { to { transform: rotate(360deg); } }

.back-login {
  text-align: center;
  font-size: 13px;
  color: rgba(255,255,255,0.35);
  margin-top: 24px;

  a { color: #667eea; font-weight: 600; text-decoration: none; &:hover { text-decoration: underline; } }
}
</style>
