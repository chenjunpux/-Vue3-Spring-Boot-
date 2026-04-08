<template>
  <div class="admin-layout" :class="{ 'dark': isDark }">
    <!-- 移动端菜单遮罩 -->
    <div v-if="sidebarOpen" class="drawer-overlay" @click="sidebarOpen = false"></div>

    <!-- 左侧边栏 -->
    <aside class="sidebar" :class="{ 'sidebar-open': sidebarOpen }">
      <!-- Logo -->
      <div class="sidebar-header">
        <router-link to="/admin" class="logo-link">
          <div class="logo-icon"><i class="i-mdi-globe"></i></div>
          <span class="logo-text">智慧旅游后台</span>
        </router-link>
        <button class="close-btn lg:hidden" @click="sidebarOpen = false">
          <i class="i-mdi-close"></i>
        </button>
      </div>

      <!-- 菜单 -->
      <el-menu
        :default-active="currentRoute"
        class="sidebar-menu"
        :collapse="false"
        :unique-opened="true"
        @select="handleMenuSelect"
      >
        <div v-for="item in menuItems" :key="item.path">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="item.children" :index="item.path">
            <template #title>
              <span class="menu-icon"><i :class="item.icon"></i></span>
              <span>{{ item.name }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单 -->
          <el-menu-item v-else :index="item.path">
            <span class="menu-icon"><i :class="item.icon"></i></span>
            <span>{{ item.name }}</span>
          </el-menu-item>
        </div>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-wrapper">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <!-- 移动端菜单按钮 -->
        <button class="menu-toggle" @click="sidebarOpen = !sidebarOpen">
          <i class="i-mdi-menu"></i>
        </button>

        <!-- 欢迎语和日期 -->
        <div class="welcome-info">
          <span class="welcome-text">{{ welcomeMessage }}</span>
          <span class="date-text">{{ currentDate }}</span>
        </div>

        <div class="top-bar-actions">
          <!-- 主题切换 -->
          <button class="action-btn" @click="toggleTheme" :title="isDark ? '切换亮色' : '切换暗色'">
            <i v-if="isDark" class="i-mdi-weather-sunny"></i>
            <i v-else class="i-mdi-moon-waning-crescent"></i>
          </button>

          <!-- 通知 -->
          <button class="action-btn notification-btn" @click="showNotifications">
            <i class="i-mdi-bell"></i>
            <span v-if="notificationCount > 0" class="badge">{{ notificationCount }}</span>
          </button>

          <!-- 用户下拉 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userInfo?.avatar">
                {{ userInfo?.nickname?.slice(0, 1) || 'A' }}
              </el-avatar>
              <span class="user-name">{{ userInfo?.nickname || '管理员' }}</span>
              <i class="i-mdi-chevron-down"></i>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <i class="i-mdi-account mr-2"></i>个人设置
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <i class="i-mdi-cog mr-2"></i>系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="i-mdi-logout mr-2"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 标签页 -->
      <div class="tabs-bar">
        <div class="tabs-container">
          <div
            v-for="tab in tabs"
            :key="tab.path"
            class="tab-item"
            :class="{ 'is-active': tab.path === currentRoute }"
            @click="switchTab(tab.path)"
          >
            <i :class="tab.icon" class="tab-icon"></i>
            <span class="tab-name">{{ tab.name }}</span>
            <button
              v-if="tab.closable"
              class="tab-close"
              @click.stop="closeTab(tab.path)"
              :title="'关闭标签'"
            >
              <i class="i-mdi-close"></i>
            </button>
          </div>
        </div>
        <!-- 标签页操作 -->
        <div class="tabs-actions">
          <el-dropdown trigger="click" @command="handleTabCommand">
            <button class="tabs-more-btn">
              <i class="i-mdi-dots-vertical"></i>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="closeOthers">
                  <i class="i-mdi-close-box-outline mr-2"></i>关闭其他
                </el-dropdown-item>
                <el-dropdown-item command="closeAll">
                  <i class="i-mdi-close-circle-outline mr-2"></i>关闭全部
                </el-dropdown-item>
                <el-dropdown-item command="refresh">
                  <i class="i-mdi-refresh mr-2"></i>刷新当前
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <main class="page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="currentRoute" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 状态
const sidebarOpen = ref(false)
const isDark = ref(false)
const notificationCount = ref(3)

// 标签页
interface TabItem {
  path: string
  name: string
  icon: string
  closable: boolean
}

const tabs = ref<TabItem[]>([
  { path: '/admin/dashboard', name: '数据大屏', icon: 'i-mdi-view-dashboard', closable: false }
])

// 当前路由
const currentRoute = computed(() => route.path)

// 菜单配置
const menuItems = [
  { path: '/admin/dashboard', name: '数据大屏', icon: 'i-mdi-view-dashboard' },
  { path: '/admin/spots', name: '景点管理', icon: 'i-mdi-map-marker' },
  { path: '/admin/hotels', name: '酒店管理', icon: 'i-mdi-domain' },
  { path: '/admin/orders', name: '订单管理', icon: 'i-mdi-file-document' },
  { path: '/admin/users', name: '用户管理', icon: 'i-mdi-account-group' },
  { path: '/admin/articles', name: '游记管理', icon: 'i-mdi-book-open' },
  { path: '/admin/payments', name: '支付管理', icon: 'i-mdi-credit-card' },
  { path: '/admin/notifications', name: '通知管理', icon: 'i-mdi-bell' },
  { path: '/admin/coupons', name: '优惠券管理', icon: 'i-mdi-ticket' },
  { path: '/admin/settings', name: '系统设置', icon: 'i-mdi-cog' },
]

// 路由到菜单项的映射
const routeToMenu = computed(() => {
  const map: Record<string, { name: string; icon: string }> = {}
  menuItems.forEach(item => {
    map[item.path] = { name: item.name, icon: item.icon }
  })
  return map
})

// 页面标题
// 欢迎语
const welcomeMessage = computed(() => {
  const hour = new Date().getHours()
  const name = userInfo.value?.nickname || '管理员'
  if (hour < 12) return `早上好，${name}`
  if (hour < 14) return `中午好，${name}`
  if (hour < 18) return `下午好，${name}`
  return `晚上好，${name}`
})

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  const day = now.getDate()
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekday = weekdays[now.getDay()]
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${weekday}`
})

// 处理菜单选择
function handleMenuSelect(index: string) {
  // 添加或激活标签页
  addTab(index)
  router.push(index)
}

// 添加标签页
function addTab(path: string) {
  const menuInfo = routeToMenu.value[path]
  if (!menuInfo) return

  const exists = tabs.value.some(tab => tab.path === path)
  if (!exists) {
    tabs.value.push({
      path,
      name: menuInfo.name,
      icon: menuInfo.icon,
      closable: true // 除了首页，其他都可以关闭
    })
  }
}

// 切换标签页
function switchTab(path: string) {
  router.push(path)
}

// 关闭标签页
function closeTab(path: string) {
  const tab = tabs.value.find(t => t.path === path)
  if (!tab || !tab.closable) return

  const index = tabs.value.findIndex(t => t.path === path)
  tabs.value.splice(index, 1)

  // 如果关闭的是当前标签，切换到前一个或后一个
  if (path === currentRoute.value) {
    const newIndex = Math.min(index, tabs.value.length - 1)
    router.push(tabs.value[newIndex].path)
  }
}

// 标签页更多操作
function handleTabCommand(command: string) {
  switch (command) {
    case 'closeOthers':
      // 关闭其他标签，只保留当前和首页
      tabs.value = tabs.value.filter(tab => !tab.closable || tab.path === currentRoute.value)
      break
    case 'closeAll':
      // 关闭所有标签，回到首页
      const dashboard = tabs.value.find(tab => !tab.closable)
      tabs.value = dashboard ? [dashboard] : []
      router.push('/admin/dashboard')
      break
    case 'refresh':
      // 刷新当前页面
      window.location.reload()
      break
  }
}

// 主题切换
function toggleTheme() {
  isDark.value = !isDark.value
  localStorage.setItem('admin-theme', isDark.value ? 'dark' : 'light')
  document.documentElement.classList.toggle('dark', isDark.value)
}

// 显示通知
function showNotifications() {
  ElMessage.info('通知功能开发中...')
}

// 用户菜单命令
async function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      ElMessage.info('个人设置功能开发中...')
      break
    case 'settings':
      router.push('/admin/settings')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
        userStore.logout()
        router.push('/login')
      } catch {
        // 取消
      }
      break
  }
}

// 初始化
onMounted(() => {
  // 恢复主题设置
  const savedTheme = localStorage.getItem('admin-theme')
  if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    isDark.value = true
    document.documentElement.classList.add('dark')
  }

  // 添加当前路由到标签页
  if (route.path !== '/admin/dashboard') {
    addTab(route.path)
  }
})

// 路由变化时自动添加标签页
watch(() => route.path, (newPath) => {
  if (newPath !== '/admin/dashboard') {
    addTab(newPath)
  }
})

// 路由变化时关闭移动端侧边栏
watch(() => route.path, () => {
  sidebarOpen.value = false
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

.admin-layout.dark {
  background: #111827;
  color: #f9fafb;
}

/* 侧边栏 */
.sidebar {
  width: 260px;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  transition: transform 0.3s ease;
}

.dark .sidebar {
  background: #1f2937;
  border-color: #374151;
}

.sidebar-open {
  transform: translateX(0);
}

@media (max-width: 1024px) {
  .sidebar {
    transform: translateX(-100%);
  }
  .sidebar-open {
    transform: translateX(0);
  }
}

/* 遮罩层 */
.drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 99;
  display: block;
}

@media (min-width: 1024px) {
  .drawer-overlay {
    display: none;
  }
}

/* 侧边栏头部 */
.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid #e5e7eb;
}

.dark .sidebar-header {
  border-color: #374151;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: #1f2937;
}

.dark .logo-link {
  color: #f9fafb;
}

.logo-img {
  width: 32px;
  height: 32px;
  border-radius: 8px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #6b7280;
}

.close-btn:hover {
  color: #1f2937;
}

.dark .close-btn {
  color: #9ca3af;
}

.dark .close-btn:hover {
  color: #f9fafb;
}

/* 菜单 */
.sidebar-menu {
  padding: 8px 0;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* 菜单项基础样式 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: 8px;
  padding-left: 16px !important;
  padding-right: 16px;
  transition: all 0.2s;
}

/* 菜单图标 */
:deep(.el-menu-item .menu-icon),
:deep(.el-sub-menu__title .menu-icon) {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

/* 菜单项悬浮 */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: #f3f4f6 !important;
  border-radius: 8px;
}

/* 菜单项激活状态 - 左侧蓝色指示条 */
:deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.15) 0%, rgba(59, 130, 246, 0.05) 100%) !important;
  color: #3b82f6 !important;
  font-weight: 600;
  border-radius: 8px;
  position: relative;
}

:deep(.el-menu-item.is-active)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  background: #3b82f6;
  border-radius: 0 2px 2px 0;
}

/* 子菜单 */
:deep(.el-sub-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
  margin: 2px 8px;
  padding-left: 52px !important;
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  background: rgba(59, 130, 246, 0.1) !important;
  color: #3b82f6 !important;
}

:deep(.el-sub-menu .el-menu-item.is-active)::before {
  display: none;
}

/* 菜单图标与文字对齐 */
.menu-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.active-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #3b82f6;
  border-radius: 0 2px 2px 0;
}

/* 主内容区 */
.main-wrapper {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

@media (max-width: 1024px) {
  .main-wrapper {
    margin-left: 0;
  }
}

/* 顶部栏 */
.top-bar {
  height: 64px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 16px;
  position: sticky;
  top: 0;
  z-index: 50;
}

.dark .top-bar {
  background: #1f2937;
  border-color: #374151;
}

.menu-toggle {
  display: none;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 20px;
  color: #6b7280;
  border-radius: 8px;
}

.menu-toggle:hover {
  background: #f3f4f6;
}

.dark .menu-toggle {
  color: #9ca3af;
}

.dark .menu-toggle:hover {
  background: #374151;
}

@media (max-width: 1024px) {
  .menu-toggle {
    display: flex;
  }
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.dark .page-title {
  color: #f9fafb;
}

/* 欢迎语和日期 */
.welcome-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.welcome-text {
  font-weight: 600;
  color: #374151;
}

.dark .welcome-text {
  color: #f9fafb;
}

.date-text {
  color: #6b7280;
  padding: 4px 12px;
  background: #f3f4f6;
  border-radius: 16px;
  font-size: 13px;
}

.dark .date-text {
  color: #9ca3af;
  background: #374151;
}

.top-bar-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 18px;
  color: #6b7280;
  border-radius: 8px;
  position: relative;
}

.action-btn:hover {
  background: #f3f4f6;
}

.dark .action-btn {
  color: #9ca3af;
}

.dark .action-btn:hover {
  background: #374151;
}

.badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  font-weight: 600;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f3f4f6;
}

.dark .user-info:hover {
  background: #374151;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.dark .user-name {
  color: #f9fafb;
}

/* ========== 标签页样式 ========== */
.tabs-bar {
  height: 44px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 8px;
  position: sticky;
  top: 64px;
  z-index: 40;
}

.dark .tabs-bar {
  background: #1f2937;
  border-color: #374151;
}

.tabs-container {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  overflow-x: auto;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

.tabs-container::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f3f4f6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  min-width: 100px;
  max-width: 180px;
}

.dark .tab-item {
  background: #374151;
}

.tab-item:hover {
  background: #e5e7eb;
}

.dark .tab-item:hover {
  background: #4b5563;
}

.tab-item.is-active {
  background: #3b82f6;
  color: #ffffff;
}

.dark .tab-item.is-active {
  background: #3b82f6;
  color: #ffffff;
}

.tab-icon {
  font-size: 14px;
  flex-shrink: 0;
}

.tab-name {
  font-size: 13px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tab-close {
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 4px;
  font-size: 12px;
  opacity: 0.7;
  transition: all 0.2s;
  flex-shrink: 0;
  color: inherit;
}

.tab-close:hover {
  opacity: 1;
  background: rgba(0, 0, 0, 0.1);
}

.dark .tab-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 标签页更多按钮 */
.tabs-actions {
  flex-shrink: 0;
}

.tabs-more-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 6px;
  font-size: 16px;
  color: #6b7280;
  transition: all 0.2s;
}

.tabs-more-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.dark .tabs-more-btn:hover {
  background: #374151;
  color: #f9fafb;
}

/* 页面内容 */
.page-content {
  flex: 1;
  padding: 24px;
  overflow-x: hidden;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 深色模式适配 Element Plus */
.dark :deep(.el-menu) {
  background: transparent !important;
  border: none;
}

.dark :deep(.el-menu-item),
.dark :deep(.el-sub-menu__title) {
  color: #d1d5db !important;
  background: transparent !important;
}

.dark :deep(.el-menu-item:hover),
.dark :deep(.el-sub-menu__title:hover) {
  background: #374151 !important;
  border-radius: 8px;
}

.dark :deep(.el-menu-item.is-active) {
  color: #60a5fa !important;
  background: linear-gradient(90deg, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.1) 100%) !important;
  font-weight: 600;
  border-radius: 8px;
}

.dark :deep(.el-menu-item.is-active)::before {
  background: #60a5fa !important;
}

.dark :deep(.el-sub-menu .el-menu-item.is-active) {
  background: rgba(96, 165, 250, 0.15) !important;
  color: #60a5fa !important;
}

.dark :deep(.el-dropdown-menu) {
  background: #1f2937;
  border-color: #374151;
}

.dark :deep(.el-dropdown-menu__item) {
  color: #d1d5db;
}

.dark :deep(.el-dropdown-menu__item:hover) {
  background: #374151;
}

/* ========== Admin 页面通用样式 ========== */

/* 数据卡片 */
.admin-page .data-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.dark .admin-page .data-card {
  background: #1f2937;
  border-color: #374151;
}

/* 卡片头部 */
.admin-page .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
  gap: 12px;
}

.dark .admin-page .card-header {
  border-color: #374151;
}

.admin-page .header-left {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.admin-page .search-input {
  width: 200px;
}

/* 输入框和选择器样式 */
.admin-page .card-header .el-input__wrapper,
.admin-page .card-header .el-select__wrapper {
  box-shadow: none !important;
  border: 1px solid #e5e7eb !important;
  background: #f9fafb !important;
}

.dark .admin-page .card-header .el-input__wrapper,
.dark .admin-page .card-header .el-select__wrapper {
  border-color: #374151 !important;
  background: #111827 !important;
}

.admin-page .card-header .el-input__wrapper:hover,
.admin-page .card-header .el-select__wrapper:hover {
  border-color: #3b82f6 !important;
}

.admin-page .card-header .el-input__wrapper.is-focus,
.admin-page .card-header .el-select__wrapper.is-focused {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2) !important;
}

/* 表格样式 */
.admin-page .el-table {
  --el-table-border-color: #e5e7eb;
  --el-table-header-bg-color: #f9fafb;
}

.dark .admin-page .el-table {
  --el-table-border-color: #374151;
  --el-table-header-bg-color: #1f2937;
  --el-table-tr-bg-color: #1f2937;
  --el-table-row-hover-bg-color: #374151;
  --el-table-header-text-color: #d1d5db;
  --el-table-text-color: #d1d5db;
}

.admin-page .el-table th.el-table__cell {
  font-weight: 600;
  color: #374151;
}

/* 分页 */
.admin-page .pagination {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #e5e7eb;
}

.dark .admin-page .pagination {
  border-color: #374151;
}

/* 统计卡片 */
.admin-page .stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.admin-page .stat-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.admin-page .stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.dark .admin-page .stat-card {
  background: #1f2937;
  border-color: #374151;
}

.dark .admin-page .stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}
</style>
