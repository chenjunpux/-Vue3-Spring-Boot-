import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

// ==================== 路由配置 ====================
const routes: RouteRecordRaw[] = [
  // -------------------- 用户端 --------------------
  {
    path: '/',
    component: () => import('@/layouts/UserLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/user/home/index.vue'),
        meta: { title: '首页' },
      },
      {
        path: '/spots',
        name: 'Spots',
        component: () => import('@/views/user/spots/index.vue'),
        meta: { title: '景点列表' },
      },
      {
        path: '/spots/:id',
        name: 'SpotDetail',
        component: () => import('@/views/user/spots/detail.vue'),
        meta: { title: '景点详情' },
      },
      {
        path: '/travel',
        name: 'Travel',
        component: () => import('@/views/user/travel/index.vue'),
        meta: { title: '智慧旅行' },
      },
      {
        path: '/hotels',
        name: 'Hotels',
        component: () => import('@/views/user/hotels/index.vue'),
        meta: { title: '酒店列表' },
      },
      {
        path: '/hotels/:id',
        name: 'HotelDetail',
        component: () => import('@/views/user/hotels/detail.vue'),
        meta: { title: '酒店详情' },
      },
      {
        path: '/articles',
        name: 'Articles',
        component: () => import('@/views/user/articles/index.vue'),
        meta: { title: '游记列表' },
      },
      {
        path: '/articles/create',
        name: 'ArticleCreate',
        component: () => import('@/views/user/articles/create.vue'),
        meta: { title: '发布游记', requireAuth: true },
      },
      {
        path: '/articles/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/user/articles/detail.vue'),
        meta: { title: '游记详情' },
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('@/views/user/orders/index.vue'),
        meta: { title: '我的订单', requireAuth: true },
      },
      {
        path: '/book/:type/:id',
        name: 'Book',
        component: () => import('@/views/user/book/index.vue'),
        meta: { title: '订单确认', requireAuth: true },
      },
      {
        path: '/pay/:orderNo',
        name: 'Pay',
        component: () => import('@/views/user/pay/index.vue'),
        meta: { title: '支付订单', requireAuth: true },
      },
      {
        path: '/coupons',
        name: 'Coupons',
        component: () => import('@/views/user/coupons/index.vue'),
        meta: { title: '优惠券中心', requireAuth: true },
      },
      {
        path: '/notifications',
        name: 'Notifications',
        component: () => import('@/views/user/notifications/index.vue'),
        meta: { title: '消息通知', requireAuth: true },
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/user/profile/index.vue'),
        meta: { title: '个人中心', requireAuth: true },
      },
      {
        path: '/chat',
        name: 'Chat',
        component: () => import('@/views/user/chat/index.vue'),
        meta: { title: '智慧规划' },
      },
    ],
  },

  // -------------------- 认证 --------------------
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/register.vue'),
    meta: { title: '注册' },
  },

  // -------------------- 管理端 --------------------
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard.vue'),
        meta: { title: '数据大屏' },
      },
      {
        path: '/admin/spots',
        name: 'AdminSpots',
        component: () => import('@/views/admin/spots/index.vue'),
        meta: { title: '景点管理' },
      },
      {
        path: '/admin/hotels',
        name: 'AdminHotels',
        component: () => import('@/views/admin/hotels/index.vue'),
        meta: { title: '酒店管理' },
      },
      {
        path: '/admin/orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/orders/index.vue'),
        meta: { title: '订单管理' },
      },
      {
        path: '/admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/users/index.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: '/admin/articles',
        name: 'AdminArticles',
        component: () => import('@/views/admin/articles/index.vue'),
        meta: { title: '游记管理' },
      },
      {
        path: '/admin/payments',
        name: 'AdminPayments',
        component: () => import('@/views/admin/payments/index.vue'),
        meta: { title: '支付管理' },
      },
      {
        path: '/admin/notifications',
        name: 'AdminNotifications',
        component: () => import('@/views/admin/notifications/index.vue'),
        meta: { title: '通知管理' },
      },
      {
        path: '/admin/coupons',
        name: 'AdminCoupons',
        component: () => import('@/views/admin/coupons/index.vue'),
        meta: { title: '优惠券管理' },
      },
      {
        path: '/admin/settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/settings/index.vue'),
        meta: { title: '系统设置' },
      },
    ],
  },

  // -------------------- 404 --------------------
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' },
  },
]

// ==================== 路由实例 ====================
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
})

// ==================== 路由守卫 ====================
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  // 设置页面标题
  document.title = `${to.meta.title || '页面'} - 智慧旅游管理系统`

  // 登入页面标题特殊处理
  if (to.name === 'Login' || to.name === 'Register') {
    document.title = `${to.meta.title} - 智慧旅游管理系统`
  }

  // 需要登录的页面
  if (to.meta.requireAuth) {
    const userStore = useUserStore()
    const token = userStore.token || localStorage.getItem('token')

    if (!token) {
      return next({ name: 'Login', query: { redirect: to.fullPath } })
    }

    // 如果有 token 但没有用户信息，尝试获取
    if (!userStore.userInfo) {
      try {
        await userStore.fetchUserInfo()
      } catch {
        userStore.logout()
        return next({ name: 'Login', query: { redirect: to.fullPath } })
      }
    }

    // 管理端权限校验
    if (to.meta.requireAdmin && userStore.userInfo?.role !== 2) {
      return next('/')
    }
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
