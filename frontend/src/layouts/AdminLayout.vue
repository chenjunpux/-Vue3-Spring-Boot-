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
        <!-- 移动端菜单按钮 -->
        <div class="flex-1">
          <label for="left-sidebar-drawer" class="btn btn-primary drawer-button lg:hidden">
            <Bars3Icon class="w-5 h-5" />
          </label>
          <h1 class="text-2xl font-semibold ml-2 md:ml-4">{{ pageTitle }}</h1>
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
              <li><a @click="router.push('/admin/settings')"><Cog6ToothIcon class="w-5 h-5" />系统设置</a></li>
              <li><a @click="handleLogout"><ArrowRightStartOnRectangleIcon class="w-5 h-5" />退出登录</a></li>
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
      <ul class="menu pt-2 w-72 min-h-full bg-base-100 text-base-content">

        <!-- 关闭按钮（移动端） -->
        <button class="btn btn-ghost bg-base-300 btn-circle z-50 top-0 right-0 mt-4 mr-2 absolute lg:hidden" @click="drawerOpen = false">
          <XMarkIcon class="w-5 h-5" />
        </button>

        <!-- Logo -->
        <li class="mb-4 font-semibold text-xl">
          <router-link to="/admin/dashboard">
            <img class="mask mask-squircle w-10" src="https://placehold.co/80x80/3b82f6/ffffff?text=Logo" alt="Logo" />
            智慧旅游后台
          </router-link>
        </li>

        <!-- 菜单项 -->
        <template v-for="item in menuItems" :key="item.path">
          <!-- 有子菜单 -->
          <li v-if="item.children">
            <details :open="isSubmenuActive(item)">
              <summary>
                <component :is="item.icon" class="w-6 h-6" />
                {{ item.name }}
                <ChevronDownIcon class="w-4 h-4 ml-auto transition-transform duration-300" />
              </summary>
              <ul class="menu menu-compact">
                <li v-for="child in item.children" :key="child.path">
                  <router-link :to="child.path" :class="{ 'active': currentRoute === child.path }">
                    <component :is="child.icon" class="w-5 h-5" />
                    {{ child.name }}
                  </router-link>
                </li>
              </ul>
            </details>
          </li>

          <!-- 无子菜单 -->
          <li v-else>
            <router-link :to="item.path" :class="{ 'active': currentRoute === item.path }">
              <component :is="item.icon" class="w-6 h-6" />
              {{ item.name }}
            </router-link>
          </li>
        </template>
      </ul>
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
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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
const isDark = ref(false)
const showNotifications = ref(false)
const notifications = ref<any[]>([])
const unreadCount = ref(0)

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
  } catch {}
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
  } catch {}
}

async function handleNotifClick(item: any) {
  if (!item.isRead) {
    try {
      await notificationApi.markAsRead(item.id)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch {}
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


/* 路由激活指示器（与 DaisyUI 模板一致） */
.router-link-active::before {
  content: '';
  position: absolute;
  inset-y-0 left-0 w-1 rounded-tr-md rounded-br-md bg-primary;
}

/* 子菜单样式 */
details summary .chevron {
  transition: transform 0.3s;
}
details[open] summary .chevron {
  transform: rotate(180deg);
}
