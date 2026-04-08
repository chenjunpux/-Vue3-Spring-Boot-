<template>
  <div class="ai-chat-page">
    <!-- 背景装饰 -->
    <div class="bg-deco">
      <div class="deco-plane">✈️</div>
      <div class="deco-sun">☀️</div>
      <div class="deco-food">🍜</div>
      <div class="deco-hotel">🏨</div>
      <div class="deco-mountain">⛰️</div>
    </div>

    <!-- 顶部标题区 -->
    <div class="chat-title-bar">
      <div class="title-bar-inner">
        <div class="ai-avatar-box">
          <div class="ai-avatar-ring"></div>
          <div class="ai-avatar-core">🤖</div>
          <div class="ai-online-dot"></div>
        </div>
        <div class="ai-info">
          <div class="ai-name">小陈旅行助手</div>
          <div class="ai-status">
            <span class="status-dot"></span>
            在线为你服务
          </div>
        </div>
        <button class="clear-btn" @click="clearChat" title="清空对话">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
        </button>
      </div>
    </div>

    <!-- 欢迎区 -->
    <div class="welcome-area" v-if="messages.length === 0">
      <div class="welcome-hero">
        <div class="hero-icon">
          <span class="hero-emoji">🤖</span>
          <div class="hero-rays">
            <span></span><span></span><span></span><span></span>
          </div>
        </div>
        <div class="welcome-text">
          <h2>你好，我是小陈！👋</h2>
          <p>你的专属旅行规划助手，告诉我你想去哪儿、玩几天</p>
          <p class="sub">我帮你安排得明明白白 ✨</p>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-grid">
        <button
          v-for="action in quickActions"
          :key="action.label"
          class="quick-card"
          @click="sendQuickMessage(action.prompt)"
        >
          <div class="qc-icon">{{ action.icon }}</div>
          <div class="qc-text">{{ action.label }}</div>
          <div class="qc-arrow">→</div>
        </button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages-area" ref="messagesAreaRef" v-else>
      <div class="msg-start-hint" v-if="messages.length <= 2">
        <span>✨ 开始和小陈聊聊你的旅行计划吧</span>
      </div>

      <template v-for="(msg, index) in messages" :key="index">
        <!-- AI 消息 -->
        <div v-if="msg.role === 'assistant' && !msg.loading" class="message-row ai-row">
          <div class="msg-avatar ai-avatar-sm">🤖</div>
          <div class="bubble ai-bubble">
            <div class="bubble-content" v-html="renderMarkdown(msg.content)"></div>
          </div>
        </div>

        <!-- AI Loading -->
        <div v-else-if="msg.role === 'assistant' && msg.loading" class="message-row ai-row">
          <div class="msg-avatar ai-avatar-sm">🤖</div>
          <div class="bubble ai-bubble loading-bubble">
            <div class="typing-dots">
              <span></span><span></span><span></span>
            </div>
            <span class="typing-text">小陈正在思考...</span>
          </div>
        </div>

        <!-- 用户消息 -->
        <div v-else class="message-row user-row">
          <div class="bubble user-bubble">
            <div class="bubble-content">{{ msg.content }}</div>
          </div>
          <div class="msg-avatar user-avatar-sm">👤</div>
        </div>
      </template>
    </div>

    <!-- 底部输入区 -->
    <div class="input-area">
      <div class="input-inner">
        <div class="input-field">
          <textarea
            ref="inputRef"
            v-model="inputText"
            class="chat-input"
            placeholder="说说你想去哪儿玩~ 例如：去云南5天，推荐行程"
            rows="1"
            @keydown.enter.exact.prevent="sendMessage"
            @input="autoResize"
          ></textarea>
          <button
            class="send-btn"
            :class="{ active: inputText.trim() }"
            @click="sendMessage"
            :disabled="sending || !inputText.trim()"
          >
            <svg class="send-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="22" y1="2" x2="11" y2="13"/>
              <polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
          </button>
        </div>
        <div class="input-foot">
          <span class="input-tip">按 Enter 发送 · Shift+Enter 换行</span>
          <span class="ai-tag">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
            AI 助手
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { chatApi } from '@/api/ai'

const router = useRouter()
const route = useRoute()
const inputText = ref('')
const sending = ref(false)
const messages = ref([])
const messagesAreaRef = ref(null)
const inputRef = ref(null)

const quickActions = [
  { icon: '🗺️', label: '制定行程', prompt: '我想去云南旅行，5天4晚，帮推荐一下行程' },
  { icon: '🍜', label: '美食推荐', prompt: '成都有什么必吃的美食？' },
  { icon: '🏨', label: '住宿建议', prompt: '去三亚住哪里比较方便？' },
  { icon: '💰', label: '预算估算', prompt: '北京4天3夜需要多少预算？' },
  { icon: '🌤️', label: '季节建议', prompt: '什么时候去西藏最好？' },
  { icon: '🎯', label: '景点推荐', prompt: '重庆有哪些景点值得去？' },
]

onMounted(async () => {
  inputRef.value?.focus()
  const q = route.query.q
  if (q) {
    inputText.value = String(q)
    await nextTick()
    sendMessage()
  }
})

function sendQuickMessage(prompt) {
  inputText.value = prompt
  sendMessage()
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  await nextTick()
  scrollToBottom()

  const aiMsg = { role: 'assistant', content: '', loading: true }
  messages.value.push(aiMsg)
  sending.value = true
  await nextTick()
  scrollToBottom()

  try {
    const history = messages.value
      .filter(m => !m.loading && m.role !== 'system')
      .slice(0, -1)
      .map(m => ({ user: m.role === 'user' ? m.content : '', assistant: m.role === 'assistant' ? m.content : '' }))

    const res = await chatApi.sendMessage({ message: text, history })

    if (res.code === 0 || res.code === 200) {
      aiMsg.content = res.data.reply || res.data.content || '小陈暂时没想好怎么回答，换个问题试试？ 😊'
    } else {
      aiMsg.content = '小陈遇到了一点问题，请稍后再试~ 😅'
    }
  } catch (e) {
    console.error('AI chat error:', e)
    aiMsg.content = '网络有点问题，请检查网络后重试~ 📶'
  } finally {
    aiMsg.loading = false
    sending.value = false
    await nextTick()
    scrollToBottom()
  }
}

function scrollToBottom() {
  if (messagesAreaRef.value) {
    messagesAreaRef.value.scrollTop = messagesAreaRef.value.scrollHeight
  }
}

function autoResize(e) {
  const el = e.target
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

function clearChat() {
  messages.value = []
}

function renderMarkdown(text) {
  if (!text) return ''

  // 时间轴样式处理：检测 Day 开头的行
  let html = text
    .replace(/^(Day\d+[:：]?\s*.*)$/gm, '<div class="day-header">$1</div>')
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/`(.+?)`/g, '<code>$1</code>')

  // 表情高亮
  html = html.replace(/([🌸🌴🐼🏯🏛️🌆🌶️😊🎯🍜🏨💰🌤️📍⚠️✅🎉✈️☀️⛰️🍜🏠📌])/g, '<span class="emoji-hl">$1</span>')

  return html
}
</script>

<style scoped>
/* === 页面基础 === */
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 56px); /* 减去顶部导航高度 */
  background: linear-gradient(160deg, #f0f4ff 0%, #fff8f5 50%, #f5f0ff 100%);
  position: relative;
  overflow: hidden;
}

/* === 背景装饰 === */
.bg-deco {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.bg-deco > div {
  position: absolute;
  font-size: 28px;
  opacity: 0.12;
  animation: floatDeco 6s ease-in-out infinite;
}
.deco-plane { top: 12%; left: 5%; animation-delay: 0s; }
.deco-sun { top: 20%; right: 8%; animation-delay: 1s; font-size: 36px; }
.deco-food { top: 55%; left: 3%; animation-delay: 2s; }
.deco-hotel { bottom: 25%; right: 5%; animation-delay: 0.5s; }
.deco-mountain { bottom: 15%; left: 8%; animation-delay: 1.5s; }

@keyframes floatDeco {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-12px) rotate(5deg); }
}

/* === 头部 === */
.chat-title-bar {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1.5px solid rgba(59,130,246,0.10);
  flex-shrink: 0;
}

.title-bar-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar-box {
  position: relative;
  width: 48px;
  height: 48px;
}
.ai-avatar-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #f97316);
  animation: spin 3s linear infinite;
}
.ai-avatar-core {
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.ai-online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #22c55e;
  border-radius: 50%;
  border: 2px solid #fff;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.ai-info { }
.ai-name { font-size: 17px; font-weight: 800; color: #1e293b; }
.ai-status {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}
.status-dot {
  width: 7px;
  height: 7px;
  background: #22c55e;
  border-radius: 50%;
  animation: pulse 2s infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.clear-btn {
  background: #f1f5f9;
  border: none;
  font-size: 16px;
  cursor: pointer;
  padding: 8px;
  border-radius: 10px;
  transition: all .25s;
  color: #94a3b8;
  display: flex;
  align-items: center;
}
.clear-btn svg { width: 18px; height: 18px; }
.clear-btn:hover { background: #fee2e2; color: #ef4444; }

/* === 欢迎区 === */
.welcome-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
  position: relative;
  z-index: 1;
}

.welcome-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-bottom: 36px;
}

.hero-icon {
  position: relative;
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}
.hero-emoji {
  font-size: 64px;
  display: block;
  line-height: 80px;
  animation: bounceIn 0.8s ease-out;
}
.hero-rays {
  position: absolute;
  inset: -10px;
}
.hero-rays span {
  position: absolute;
  width: 4px;
  height: 14px;
  background: linear-gradient(to bottom, #f97316, transparent);
  border-radius: 4px;
  left: 50%;
  top: 0;
  transform-origin: center 44px;
  animation: raySpin 4s linear infinite;
}
.hero-rays span:nth-child(1) { transform: translateX(-50%) rotate(0deg); animation-delay: 0s; }
.hero-rays span:nth-child(2) { transform: translateX(-50%) rotate(90deg); animation-delay: 0.5s; }
.hero-rays span:nth-child(3) { transform: translateX(-50%) rotate(180deg); animation-delay: 1s; }
.hero-rays span:nth-child(4) { transform: translateX(-50%) rotate(270deg); animation-delay: 1.5s; }

@keyframes raySpin { from { opacity: 0.3; } 50% { opacity: 1; } to { opacity: 0.3; } }
@keyframes bounceIn { 0% { transform: scale(0); } 60% { transform: scale(1.15); } 100% { transform: scale(1); } }

.welcome-text h2 {
  font-size: 26px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 8px;
}
.welcome-text p {
  font-size: 15px;
  color: #64748b;
  margin: 0;
}
.welcome-text .sub {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 6px;
}

/* === 快捷卡片 === */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  width: 100%;
  max-width: 680px;
}

.quick-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: rgba(255,255,255,0.90);
  border: 1.5px solid rgba(59,130,246,0.12);
  border-radius: 16px;
  cursor: pointer;
  transition: all .3s;
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}
.quick-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59,130,246,0.04), rgba(249,115,22,0.04));
  opacity: 0;
  transition: opacity .3s;
}
.quick-card:hover {
  border-color: rgba(59,130,246,0.35);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(59,130,246,0.12);
}
.quick-card:hover::before { opacity: 1; }
.quick-card:hover .qc-arrow { transform: translateX(3px); color: #3b82f6; }

.qc-icon { font-size: 22px; }
.qc-text { flex: 1; font-size: 14px; font-weight: 600; color: #334155; }
.qc-arrow { font-size: 16px; color: #cbd5e1; transition: all .25s; }

/* === 消息列表 === */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  position: relative;
  z-index: 1;
}

.msg-start-hint {
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
  padding: 8px 0 4px;
}

.message-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  animation: slideIn 0.3s ease-out;
}
@keyframes slideIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }

.user-row { flex-direction: row-reverse; }

.msg-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.ai-avatar-sm {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  box-shadow: 0 4px 12px rgba(59,130,246,0.25);
}

.user-avatar-sm {
  background: linear-gradient(135deg, #f97316, #fb923c);
  box-shadow: 0 4px 12px rgba(249,115,22,0.25);
}

/* === 气泡 === */
.bubble {
  max-width: 68%;
  padding: 14px 18px;
  border-radius: 20px;
  font-size: 15px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.user-bubble {
  background: linear-gradient(135deg, #f97316, #fb923c);
  color: #fff;
  border-bottom-right-radius: 6px;
  box-shadow: 0 6px 20px rgba(249,115,22,0.25);
}

.ai-bubble {
  background: rgba(255,255,255,0.95);
  color: #1e293b;
  border-bottom-left-radius: 6px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06), 0 1px 3px rgba(0,0,0,0.04);
  backdrop-filter: blur(10px);
}

.loading-bubble {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
}

/* === AI 消息内容样式 === */
.bubble-content :deep(strong) { color: #1e293b; font-weight: 700; }
.bubble-content :deep(code) { background: #f1f5f9; padding: 2px 7px; border-radius: 6px; font-size: 13px; color: #7c3aed; }
.bubble-content :deep(.day-header) {
  display: inline-block;
  background: linear-gradient(135deg, rgba(59,130,246,0.08), rgba(249,115,22,0.06));
  color: #3b82f6;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 8px;
  margin-bottom: 4px;
  font-size: 14px;
}
.bubble-content :deep(.emoji-hl) { font-size: 16px; }

/* Loading 动画 */
.typing-dots {
  display: flex;
  gap: 5px;
}
.typing-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  animation: dotBounce 1.2s infinite;
}
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes dotBounce {
  0%, 80%, 100% { transform: scale(0.7); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}
.typing-text { font-size: 13px; color: #94a3b8; }

/* === 输入区 === */
.input-area {
  padding: 14px 24px 24px;
  position: relative;
  z-index: 1;
}

.input-inner {
  max-width: 760px;
  margin: 0 auto;
}

.input-field {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: rgba(255,255,255,0.95);
  border: 2px solid rgba(59,130,246,0.15);
  border-radius: 24px;
  padding: 10px 10px 10px 20px;
  box-shadow: 0 4px 20px rgba(59,130,246,0.08);
  transition: all .3s;
  backdrop-filter: blur(10px);
}
.input-field:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 4px 24px rgba(59,130,246,0.15);
  background: #fff;
}

.chat-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #1e293b;
  resize: none;
  outline: none;
  line-height: 1.6;
  max-height: 120px;
  font-family: inherit;
  padding: 4px 0;
}
.chat-input::placeholder { color: #94a3b8; font-size: 14px; }

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #e2e8f0, #cbd5e1);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all .35s;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.send-btn.active {
  background: linear-gradient(135deg, #3b82f6 0%, #f97316 100%);
  box-shadow: 0 4px 16px rgba(59,130,246,0.30);
  transform: scale(1.05);
}
.send-btn.active:hover { transform: scale(1.12); box-shadow: 0 6px 20px rgba(59,130,246,0.40); }
.send-btn:disabled { cursor: not-allowed; }
.send-icon { width: 20px; height: 20px; }

.input-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 16px 0;
}
.input-tip { font-size: 11px; color: #94a3b8; }
.ai-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #94a3b8;
}
.ai-tag svg { width: 12px; height: 12px; }

/* === 响应式 === */
@media (max-width: 700px) {
  .quick-grid { grid-template-columns: repeat(2, 1fr); }
  .bubble { max-width: 82%; }
  .ai-chat-page { width: 100%; }
}
</style>
