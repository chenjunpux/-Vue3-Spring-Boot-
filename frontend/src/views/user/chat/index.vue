<template>
  <div class="ai-chat-page">
    <!-- 顶部导航 -->
    <div class="chat-header">
      <button class="back-btn" @click="router.back()">
        <span class="back-icon">←</span>
        返回
      </button>
      <div class="chat-title">
        <span class="ai-avatar">🤖</span>
        <div>
          <div class="ai-name">小陈旅行规划</div>
          <div class="ai-status">随时为你服务</div>
        </div>
      </div>
      <button class="clear-btn" @click="clearChat" title="清空对话">
        🗑️
      </button>
    </div>

    <!-- 欢迎区 -->
    <div class="welcome-area" v-if="messages.length === 0">
      <div class="welcome-icon">🤖</div>
      <h2>你好，我是小陈 👋</h2>
      <p>你的专属旅行规划助手，我可以帮你：</p>
      <div class="quick-actions">
        <button
          v-for="action in quickActions"
          :key="action.label"
          class="quick-btn"
          @click="sendQuickMessage(action.prompt)"
        >
          <span class="quick-icon">{{ action.icon }}</span>
          <span>{{ action.label }}</span>
        </button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages-area" ref="messagesAreaRef" v-else>
      <div
        v-for="(msg, index) in messages"
        :key="index"
        class="message-row"
        :class="msg.role === 'user' ? 'user-row' : 'ai-row'"
      >
        <!-- AI 消息 -->
        <template v-if="msg.role === 'assistant'">
          <div class="avatar ai-avatar-sm">🤖</div>
          <div class="bubble ai-bubble">
            <div v-if="msg.loading" class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
            <div v-else class="bubble-text" v-html="renderMarkdown(msg.content)"></div>
          </div>
        </template>

        <!-- 用户消息 -->
        <template v-else>
          <div class="bubble user-bubble">{{ msg.content }}</div>
          <div class="avatar user-avatar">👤</div>
        </template>
      </div>
    </div>

    <!-- 底部输入区 -->
    <div class="input-area">
      <div class="input-wrapper">
        <textarea
          ref="inputRef"
          v-model="inputText"
          class="chat-input"
          placeholder="描述你的旅行想法..."
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
          <span v-if="sending" class="loading-dots">···</span>
          <span v-else>↑</span>
        </button>
      </div>
      <div class="input-hint">按 Enter 发送，Shift+Enter 换行</div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
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
  // 如果从首页带来了搜索词，自动发送
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

  // 添加用户消息
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  await nextTick()
  scrollToBottom()

  // 添加 AI 消息占位（显示 loading）
  const aiMsg = { role: 'assistant', content: '', loading: true }
  messages.value.push(aiMsg)
  sending.value = true
  await nextTick()
  scrollToBottom()

  try {
    // 构建历史记录
    const history = messages.value
      .filter(m => !m.loading && m.role !== 'system')
      .slice(0, -1)
      .map(m => ({ user: m.role === 'user' ? m.content : '', assistant: m.role === 'assistant' ? m.content : '' }))

    const res = await chatApi.sendMessage({
      message: text,
      history: history
    })

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
  return text
    // 粗体
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    // 换行
    .replace(/\n/g, '<br>')
    // 行内代码
    .replace(/`(.+?)`/g, '<code>$1</code>')
}
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f7f8fa;
  width: 60%;
  margin: 0 auto;
}

/* === 头部 === */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8ebed;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: background 0.2s;
}
.back-btn:hover { background: #f5f5f5; }
.back-icon { font-size: 18px; }

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
}
.ai-name { font-weight: 600; font-size: 16px; color: #1e293b; }
.ai-status { font-size: 11px; color: #94a3b8; }

.clear-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: background 0.2s;
}
.clear-btn:hover { background: #f5f5f5; }

/* === 欢迎区 === */
.welcome-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.welcome-icon {
  font-size: 64px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.welcome-area h2 {
  font-size: 24px;
  color: #1e293b;
  margin: 0 0 8px;
}

.welcome-area p {
  color: #64748b;
  margin: 0 0 24px;
  font-size: 15px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  width: 100%;
  max-width: 500px;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 8px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  cursor: pointer;
  font-size: 13px;
  color: #475569;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.quick-btn:hover {
  background: #f0f7ff;
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}
.quick-icon { font-size: 24px; }

/* === 消息列表 === */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.user-row {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  font-size: 20px;
}

.ai-avatar-sm {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-size: 18px;
}

.user-avatar {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  color: #fff;
  font-size: 18px;
}

.bubble {
  max-width: 72%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.user-bubble {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.ai-bubble {
  background: #fff;
  color: #1e293b;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 14px 18px;
}

/* AI 消息内 Markdown 样式 */
.bubble-text :deep(strong) {
  color: #1e293b;
  font-weight: 600;
}
.bubble-text :deep(code) {
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #7c3aed;
}

/* Loading */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}
.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94a3b8;
  animation: bounce 1.2s infinite;
}
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* === 输入区 === */
.input-area {
  padding: 12px 16px 20px;
  background: #fff;
  border-top: 1px solid #e8ebed;
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: #f1f5f9;
  border-radius: 24px;
  padding: 10px 14px;
  border: 2px solid transparent;
  transition: border-color 0.2s;
}
.input-wrapper:focus-within {
  border-color: #667eea;
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
  line-height: 1.5;
  max-height: 120px;
  font-family: inherit;
}
.chat-input::placeholder { color: #94a3b8; }

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: #ccc;
  color: #fff;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}
.send-btn.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  transform: scale(1.05);
}
.send-btn.active:hover { transform: scale(1.1); }

.input-hint {
  text-align: center;
  font-size: 11px;
  color: #94a3b8;
  margin-top: 6px;
}

/* 响应式 */
@media (max-width: 640px) {
  .quick-actions { grid-template-columns: repeat(2, 1fr); }
  .bubble { max-width: 84%; }
}
</style>
