<template>
  <!-- =============================================
       管理后台布局 - 基于 DaisyUI Admin 模板
       ============================================= -->
  <div class="drawer lg:drawer-open">
    <!-- 移动端侧边栏开关（checkbox 控制 drawer） -->
    <input id="left-sidebar-drawer" v-model="drawerOpen" type="checkbox" class="drawer-toggle" />

    <!-- ========== Page Content（主内容区） ========== -->
    <div class="drawer-content flex flex-col min-h-screen bg-base-200">
      <!-- 顶部导航栏 -->
      <div class="navbar sticky top-0 bg-base-100 z-10 shadow-md px-4">
        <div>
          <!-- 移动端菜单按钮 + 桌面端侧边栏折叠按钮 -->
          <div class="flex items-center gap-1">
            <label for="left-sidebar-drawer" class="btn btn-ghost btn-sm drawer-button lg:hidden">
              <Bars3Icon class="w-5 h-5" />
            </label>
            <button class="btn btn-ghost btn-sm btn-square hidden lg:inline-flex" @click="toggleSidebar"
              :title="sidebarCollapsed ? '展开侧边栏' : '收起侧边栏'">
              <ChevronLeftIcon
                :class="['w-5 h-5 transition-transform duration-300', sidebarCollapsed ? 'rotate-180' : '']" />
            </button>
          </div>
          <div>
            <h1 class="text-2xl font-semibold ml-2 md:ml-4">{{ pageTitle }}</h1>
          </div>
        </div>

        <div class="flex-none gap-2">
          <!-- 主题切换 -->
          <label class="swap">
            <input type="checkbox" :checked="isDark" @change="toggleTheme" />
            <SunIcon v-if="isDark" class="fill-current w-6 h-6" />
            <MoonIcon v-else class="fill-current w-6 h-6" />
          </label>

          <!-- 通知按钮 -->
          <button class="btn btn-ghost btn-circle ml-2" @click="showNotifications = true">
            <div class="indicator">
              <BellIcon class="w-6 h-6" />
              <span v-if="unreadCount > 0" class="indicator-item badge badge-secondary badge-sm">
                {{ unreadCount > 9 ? '9+' : unreadCount }}
              </span>
            </div>
          </button>

          <!-- 用户下拉 -->
          <div class="dropdown dropdown-end">
            <label tabindex="0" class="btn btn-ghost btn-circle avatar">
              <div class="w-10 rounded-full">
                <img :src="avatarUrl" :alt="nickname" />
              </div>
            </label>
            <ul tabindex="0" class="mt-3 p-2 shadow menu menu-compact dropdown-content bg-base-100 rounded-box w-52">
              <li class="menu-title">
                <span>{{ nickname }}</span>
              </li>
              <li><a @click="router.push('/admin/settings')">
                  <Cog6ToothIcon class="w-5 h-5" />系统设置
                </a></li>
              <li><a @click="handleLogout">
                  <ArrowRightStartOnRectangleIcon class="w-5 h-5" />退出登录
                </a></li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 主内容 -->
      <main class="flex-1 overflow-y-auto p-4 md:p-6">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="currentRoute" />
          </transition>
        </router-view>
      </main>

      <!-- 底部占位 -->
      <div class="h-4"></div>
    </div>

    <!-- ========== Left Sidebar（左侧边栏） ========== -->
    <div class="drawer-side z-30">
      <label for="left-sidebar-drawer" class="drawer-overlay"></label>

      <!-- 侧边栏主体（折叠/展开） -->
      <aside :class="[
        sidebarCollapsed ? 'w-16' : 'w-64',
        'flex-shrink-0',
        'transition-all', 'duration-300', 'ease-in-out',
        'h-full',
        'bg-base-100',
        'border-r', 'border-base-300',
        'flex', 'flex-col',
        'relative'
      ]">
        <!-- Logo -->
        <div class="flex items-center gap-3 px-3 h-16 border-b border-base-300 flex-shrink-0 overflow-hidden">
          <img class="mask mask-squircle w-9 flex-shrink-0" src="https://placehold.co/80x80/3b82f6/ffffff?text=Logo"
            alt="Logo" />
          <span v-if="!sidebarCollapsed"
            class="font-semibold text-sm text-base-content truncate whitespace-nowrap transition-all duration-300">智慧旅游后台</span>
        </div>

        <!-- 菜单区域（可滚动） -->
        <nav class="flex-1 overflow-y-auto py-3 px-2">
          <template v-for="item in menuItems" :key="item.path">

            <!-- 有子菜单 -->
            <li v-if="item.children" class="list-none mb-1">
              <div :class="[
                'group flex items-center gap-3 px-2 py-2.5 rounded-lg cursor-pointer select-none',
                'text-sm font-medium',
                sidebarCollapsed ? 'justify-center' : '',
                openSubmenus.includes(item.path) ? 'bg-base-200 text-base-content' : 'text-base-content/60 hover:bg-base-200 hover:text-base-content'
              ]" :title="sidebarCollapsed ? item.name : undefined" @click="toggleSubmenu(item.path)">
                <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
                <span v-if="!sidebarCollapsed" class="flex-1 truncate transition-all duration-300">{{ item.name
                  }}</span>
                <ChevronDownIcon v-if="!sidebarCollapsed"
                  :class="['w-4 h-4 flex-shrink-0 transition-transform duration-300', openSubmenus.includes(item.path) ? 'rotate-180' : '']" />
              </div>
              <!-- 子菜单 -->
              <Transition name="submenu">
                <ul v-if="!sidebarCollapsed && openSubmenus.includes(item.path)"
                  class="mt-1 flex flex-col gap-0.5 border-l border-base-300 pl-3">
                  <li v-for="child in item.children" :key="child.path" class="list-none">
                    <router-link :to="child.path" :class="[
                      'flex items-center gap-3 px-2 py-2 rounded-md text-sm',
                      'transition-colors duration-200',
                      currentRoute === child.path
                        ? 'bg-primary/10 text-primary font-medium'
                        : 'text-base-content/60 hover:bg-base-200 hover:text-base-content'
                    ]">
                      <component :is="child.icon" class="w-4 h-4 flex-shrink-0" />
                      <span class="truncate">{{ child.name }}</span>
                    </router-link>
                  </li>
                </ul>
              </Transition>
            </li>

            <!-- 无子菜单 -->
            <li v-else class="list-none mb-0.5">
              <router-link :to="item.path" :class="[
                'flex items-center gap-3 px-2 py-2.5 rounded-lg text-sm font-medium',
                'transition-colors duration-200',
                sidebarCollapsed ? 'justify-center' : '',
                currentRoute === item.path
                  ? 'bg-primary/10 text-primary'
                  : 'text-base-content/60 hover:bg-base-200 hover:text-base-content'
              ]" :title="sidebarCollapsed ? item.name : undefined">
                <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
                <span v-if="!sidebarCollapsed" class="truncate transition-all duration-300">{{ item.name }}</span>
              </router-link>
            </li>
          </template>
        </nav>
      </aside>
    </div>
  </div>

  <!-- ========== 通知抽屉（DaisyUI style） ========== -->
  <div v-if="showNotifications" class="fixed inset-0 z-50" @click.self="showNotifications = false">
    <div class="absolute right-0 top-0 h-full w-80 bg-base-100 shadow-xl flex flex-col">
      <!-- 抽屉头部 -->
      <div class="flex items-center justify-between p-4 border-b border-base-300">
        <h3 class="font-bold text-lg">通知中心</h3>
        <button class="btn btn-ghost btn-sm btn-circle" @click="showNotifications = false">
          <XMarkIcon class="w-5 h-5" />
        </button>
      </div>

      <!-- 全部已读按钮 -->
      <div class="p-2 border-b border-base-300" v-if="unreadCount > 0">
        <button class="btn btn-primary btn-sm w-full" @click="markAllRead">全部标为已读</button>
      </div>

      <!-- 通知列表 -->
      <div class="flex-1 overflow-y-auto">
        <div v-if="notifications.length === 0" class="flex flex-col items-center justify-center py-12 text-neutral/50">
          <BellIcon class="w-12 h-12 mb-2" />
          <p>暂无通知</p>
        </div>
        <ul v-else class="menu menu-compact w-full">
          <li v-for="item in notifications" :key="item.id">
            <a @click="handleNotifClick(item)" :class="{ 'bg-base-200': !item.isRead }">
              <div class="flex flex-col gap-1">
                <span class="font-medium">{{ item.title }}</span>
                <span class="text-xs opacity-60">{{ formatTime(item.createTime) }}</span>
              </div>
              <span v-if="!item.isRead" class="badge badge-primary badge-xs">新</span>
            </a>
          </li>
        </ul>
      </div>

      <!-- 查看全部 -->
      <div class="p-2 border-t border-base-300">
        <router-link to="/admin/notifications" class="btn btn-ghost btn-sm w-full" @click="showNotifications = false">
          查看全部通知
        </router-link>
      </div>
    </div>
  </div>

  <!-- ========== 全局 Toast 通知 ========== -->
  <div class="toast toast-end z-[100]" style="pointer-events: none">
    <div v-if="toast.show" :class="toast.class" class="alert shadow-lg pointer-events-auto" style="min-width: 220px">
      <span>{{ toast.message }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { notificationApi } from '@/api/notification'
import { themeChange } from 'theme-change'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'

// Heroicons
import {
  Bars3Icon,
  BellIcon,
  SunIcon,
  MoonIcon,
  Cog6ToothIcon,
  ArrowRightStartOnRectangleIcon,
  XMarkIcon,
  ChevronDownIcon,
  ChevronLeftIcon,
  HomeIcon,
  MapPinIcon,
  BuildingOfficeIcon,
  DocumentTextIcon,
  UsersIcon,
  ShieldCheckIcon,
  BookOpenIcon,
  CreditCardIcon,
  TicketIcon,
  CogIcon,
  ClipboardDocumentListIcon,
} from '@heroicons/vue/24/outline'

dayjs.extend(relativeTime)

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// ===== 状态 =====
const drawerOpen = ref(false)
const sidebarCollapsed = ref(false)
const openSubmenus = ref<string[]>(['settings'])  // 默认展开 settings 子菜单
const isDark = ref(false)
const showNotifications = ref(false)
const notifications = ref<any[]>([])
const unreadCount = ref(0)

// ===== 全局 Toast =====
const toast = reactive({ show: false, message: '', class: 'alert-success' })
let toastTimer: ReturnType<typeof setTimeout> | null = null

function showToast(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'success') {
  const clsMap = {
    success: 'alert-success',
    error: 'alert-error',
    warning: 'alert-warning',
    info: 'alert-info',
  }
  toast.message = message
  toast.class = clsMap[type]
  toast.show = true
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.show = false }, 3000)
}

// 挂到 window，方便子页面调用
; (window as any).adminToast = showToast

// ===== 用户信息 =====
const nickname = computed(() => userStore.userInfo?.nickname || '管理员')
const avatarUrl = computed(() =>
  userStore.userInfo?.avatar || 'https://placehold.co/80x80/3b82f6/ffffff?text=Admin'
)

// ===== 页面标题（与 DaisyUI 模板一致） =====
const pageTitleMap: Record<string, string> = {
  '/admin/dashboard': 'Dashboard',
  '/admin/spots': '景点管理',
  '/admin/hotels': '酒店管理',
  '/admin/orders': '订单管理',
  '/admin/users': '用户管理',
  '/admin/users/admins': '管理员管理',
  '/admin/articles': '游记管理',
  '/admin/payments': '支付管理',
  '/admin/notifications': '通知管理',
  '/admin/coupons': '优惠券管理',
  '/admin/settings': '系统设置',
  '/admin/settings/profile': '个人设置',
}
const currentRoute = computed(() => route.path)
const pageTitle = computed(() => pageTitleMap[currentRoute.value] || 'Dashboard')

// ===== 菜单配置（与 DaisyUI 模板 sidebar.js 一致） =====
const menuItems = [
  { path: '/admin/dashboard', name: 'Dashboard', icon: HomeIcon },
  { path: '/admin/orders', name: '订单管理', icon: DocumentTextIcon },
  { path: '/admin/users', name: '用户管理', icon: UsersIcon },
  { path: '/admin/users/admins', name: '管理员管理', icon: ShieldCheckIcon },
  { path: '/admin/spots', name: '景点管理', icon: MapPinIcon },
  { path: '/admin/hotels', name: '酒店管理', icon: BuildingOfficeIcon },
  { path: '/admin/articles', name: '游记管理', icon: BookOpenIcon },
  { path: '/admin/payments', name: '支付管理', icon: CreditCardIcon },
  { path: '/admin/coupons', name: '优惠券管理', icon: TicketIcon },
  { path: '/admin/notifications', name: '通知管理', icon: BellIcon },
  {
    path: 'settings',
    name: '系统设置',
    icon: Cog6ToothIcon,
    children: [
      { path: '/admin/settings', name: '系统设置', icon: CogIcon },
      { path: '/admin/settings/profile', name: '个人设置', icon: UsersIcon },
    ],
  },
]

// ===== 侧边栏折叠 =====
function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
  if (sidebarCollapsed.value) {
    openSubmenus.value = []
  }
}

function toggleSubmenu(path: string) {
  const idx = openSubmenus.value.indexOf(path)
  if (idx > -1) {
    openSubmenus.value.splice(idx, 1)
  } else {
    openSubmenus.value.push(path)
  }
}

// ===== 主题切换 =====
function toggleTheme() {
  isDark.value = !isDark.value
  const html = document.documentElement
  if (isDark.value) {
    html.setAttribute('data-theme', 'dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.setAttribute('data-theme', 'light')
    localStorage.setItem('theme', 'light')
  }
}

// ===== 通知相关 =====
async function fetchUnreadCount() {
  try {
    const res = await notificationApi.unreadCount()
    if (res.code === 200) unreadCount.value = res.data || 0
  } catch { }
}

async function fetchNotifications() {
  try {
    const res = await notificationApi.list({ page: 1, pageSize: 20 })
    if (res.code === 200) {
      notifications.value = res.data?.list || []
      unreadCount.value = res.data?.unreadCount || 0
    }
  } catch {
    notifications.value = []
  }
}

async function markAllRead() {
  try {
    const res = await notificationApi.markAllAsRead()
    if (res.code === 200) {
      unreadCount.value = 0
      notifications.value.forEach((n: any) => { n.isRead = 1 })
    }
  } catch { }
}

async function handleNotifClick(item: any) {
  if (!item.isRead) {
    try {
      await notificationApi.markAsRead(item.id)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch { }
  }
  showNotifications.value = false
  if (item.relatedId) router.push('/admin/orders')
}

function formatTime(time: string) {
  return dayjs(time).fromNow()
}

// ===== 子菜单激活检测 =====
function isSubmenuActive(item: any) {
  if (!item.children) return false
  return item.children.some((c: any) => c.path === currentRoute.value)
}

// ===== 退出登录 =====
function handleLogout() {
  userStore.logout()
  router.push('/login')
}

// ===== 初始化 =====
onMounted(() => {
  // 恢复主题
  const savedTheme = localStorage.getItem('theme') || 'light'
  isDark.value = savedTheme === 'dark'
  document.documentElement.setAttribute('data-theme', savedTheme)
  themeChange(false)

  // 加载通知
  fetchUnreadCount()
  fetchNotifications()
})
</script>
<style>
.navbar{
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>


/* ========== 侧边栏折叠动画 ========== */
.submenu-enter-active,
.submenu-leave-active {
transition: all 0.2s ease;
overflow: hidden;
}
.submenu-enter-from,
.submenu-leave-to {
opacity: 0;
transform: translateY(-4px);
}

/* 菜单项 hover / active 样式（shadcn 风格） */
.router-link-active::before {
display: none;
}
