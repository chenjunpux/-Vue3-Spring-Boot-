<template>
  <div class="vben-layout">
    <!-- 左侧侧边栏 -->
    <aside class="vben-sidebar" :class="{ 'is-collapse': isCollapse }">
      <!-- Logo 区域 -->
      <div class="sidebar-logo">
        <div class="logo-inner">
          <span class="logo-icon">🧳</span>
          <span class="logo-text" v-show="!isCollapse">旅游管理</span>
        </div>
      </div>

      <!-- 菜单 -->
      <div class="sidebar-menu-wrap">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="false"
          router
          class="vben-menu"
          background-color="transparent"
          text-color="#bebebe"
          active-text-color="#ffffff"
        >
          <!-- 顶级菜单（始终显示） -->
          <el-menu-item
            v-for="menu in topMenus"
            :key="menu.path"
            :index="menu.path"
          >
            <span :class="menu.iconClass" />
            <template #title>{{ menu.title }}</template>
          </el-menu-item>

          <!-- 系统管理分组 -->
          <el-sub-menu
            v-for="group in menuGroups.filter(g => g.children.length > 0)"
            :key="group.key"
            :index="group.key"
          >
            <template #title>
              <span :class="group.iconClass" />
              <span>{{ group.title }}</span>
            </template>
            <el-menu-item
              v-for="child in group.children"
              :key="child.path"
              :index="child.path"
            >
              <span :class="child.iconClass" />
              <template #title>{{ child.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
    </aside>

    <!-- 右侧主区域 -->
    <div class="vben-main">
      <!-- 顶部栏 -->
      <header class="vben-header">
        <!-- 左侧：折叠按钮 + 面包屑 -->
        <div class="header-left">
          <div class="collapse-btn" @click="isCollapse = !isCollapse">
            <span i="ep-expand" v-if="isCollapse" />
            <span i="ep-fold" v-else />
          </div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">
              <span i="mdi-home" class="breadcrumb-home" />
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 右侧：操作按钮 -->
        <div class="header-right">
          <el-button text class="preview-btn" @click="$router.push('/home')">
            <span i="ep-monitor" />
            预览前台
          </el-button>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar-wrap">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" class="user-avatar">
                {{ userStore.userInfo?.nickname?.slice(0, 1) || '管' }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.nickname || '管理员' }}</span>
              <span i="ep-arrow-down" class="arrow-icon" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <span i="ep-switch-button" />
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="vben-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title as string || '管理后台')

function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}

// ─── 菜单配置 ────────────────────────────────────────────────────────────

// 顶级菜单（不受权限控制，始终显示）
const topMenus = [
  { key: 'dashboard', path: '/admin/dashboard', iconClass: 'i-ep-data-analysis', title: '数据概览' },
]

// 菜单分组（受 menuPermissions 控制）
const menuGroups = computed(() => {
  const perms = userStore.menuPermissions
  const hasAll = !perms || perms.length === 0

  return [
    {
      key: 'sys_admin',
      iconClass: 'i-ep-setting',
      title: '系统管理',
      children: (() => {
        const adminMenus = [
          { key: 'spots',    path: '/admin/spots',    iconClass: 'i-ep-map-location',     title: '景点管理' },
          { key: 'hotels',   path: '/admin/hotels',   iconClass: 'i-ep-office-building',  title: '酒店管理' },
          { key: 'orders',   path: '/admin/orders',   iconClass: 'i-ep-tickets',           title: '订单管理' },
          { key: 'articles', path: '/admin/articles', iconClass: 'i-ep-document',          title: '游记管理' },
        ]
        if (hasAll) return adminMenus
        return adminMenus.filter(m => perms.includes(m.key))
      })(),
    },
    {
      key: 'sys_user',
      iconClass: 'i-ep-user',
      title: '用户管理',
      children: (() => {
        const userMenus = [
          { key: 'users', path: '/admin/users', iconClass: 'i-ep-user', title: '用户列表' },
        ]
        if (hasAll) return userMenus
        return userMenus.filter(m => perms.includes(m.key))
      })(),
    },
  ]
})
</script>

<style scoped lang="scss">
// ─── 布局整体 ────────────────────────────────────────────────────────────
.vben-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

// ─── 侧边栏（vben-admin 深色渐变风格）──────────────────────────────────
.vben-sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  position: relative;
  z-index: 10;
  overflow: hidden;

  // 左侧渐变发光边框
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background: linear-gradient(180deg, #409eff, #7aa2f7, #409eff);
    opacity: 0.8;
  }

  &.is-collapse {
    width: 64px;

    .sidebar-logo {
      justify-content: center;
      padding: 0;
    }
  }
}

// ─── Logo 区域 ───────────────────────────────────────────────────────────
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;

  .logo-inner {
    display: flex;
    align-items: center;
    gap: 10px;
    overflow: hidden;
    white-space: nowrap;
  }

  .logo-icon {
    font-size: 26px;
    flex-shrink: 0;
    filter: drop-shadow(0 0 8px rgba(64, 158, 255, 0.5));
  }

  .logo-text {
    font-size: 16px;
    font-weight: 700;
    background: linear-gradient(90deg, #409eff, #7aa2f7);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: 1px;
  }
}

// ─── 菜单 ────────────────────────────────────────────────────────────────
.sidebar-menu-wrap {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 0;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 2px;
  }
}

.vben-menu {
  border-right: none;
  background: transparent !important;

  // 菜单项基础样式
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 46px;
    line-height: 46px;
    margin: 2px 10px;
    border-radius: 10px;
    padding-left: 14px !important;
    transition: all 0.25s ease;
    color: #bebebe;
    display: flex;
    align-items: center;
    gap: 10px;

    [class^="i-"] {
      font-size: 17px;
      flex-shrink: 0;
      transition: transform 0.2s;
    }

    span:not([class^="i-"]) {
      font-size: 14px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &:hover {
      background: rgba(255, 255, 255, 0.06) !important;
      color: #ffffff !important;
    }
  }

  // 激活状态
  :deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, rgba(64, 158, 255, 0.2), rgba(64, 158, 255, 0.08)) !important;
    color: #ffffff !important;
    border-left: 3px solid #409eff;
    padding-left: 11px !important;
    box-shadow: inset 0 0 12px rgba(64, 158, 255, 0.1);

    &::before {
      content: '';
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 0;
      height: 0;
      border-top: 6px solid transparent;
      border-bottom: 6px solid transparent;
      border-right: 6px solid #fff;
      opacity: 0.15;
    }
  }

  // 子菜单收起时的样式
  :deep(.el-sub-menu) {
    .el-menu-item {
      padding-left: 42px !important;
      height: 42px;
      line-height: 42px;
      margin: 1px 10px;
      font-size: 13.5px;
    }

    .el-sub-menu__title {
      padding-left: 14px !important;
      font-size: 14px;

      .el-sub-menu__icon-arrow {
        color: #666;
        font-size: 12px;
      }
    }
  }
}

// ─── 主区域 ───────────────────────────────────────────────────────────────
.vben-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f0f2f5;
}

// ─── 顶部栏 ───────────────────────────────────────────────────────────────
.vben-header {
  height: 60px;
  background: #ffffff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  position: relative;
  z-index: 5;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .collapse-btn {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    cursor: pointer;
    color: #666;
    transition: all 0.2s;
    background: #f5f5f5;

    [class^="i-"] {
      font-size: 16px;
    }

    &:hover {
      background: #e8e8e8;
      color: #333;
    }
  }

  .breadcrumb-home {
    font-size: 16px;
    color: #409eff;
  }

  :deep(.el-breadcrumb) {
    .el-breadcrumb__inner {
      color: #666;
      font-size: 13px;
      &.is-link:hover { color: #409eff; }
    }
    .el-breadcrumb__separator { color: #ccc; }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .preview-btn {
    height: 34px;
    border-radius: 6px;
    font-size: 13px;
    color: #666;
    border: 1px solid #e0e0e0;
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 0 12px;
    transition: all 0.2s;

    [class^="i-"] { font-size: 14px; }

    &:hover {
      color: #409eff;
      border-color: #409eff;
      background: rgba(64, 158, 255, 0.04);
    }
  }

  .user-avatar-wrap {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 10px 4px 4px;
    border-radius: 8px;
    transition: background 0.2s;

    &:hover { background: #f5f5f5; }

    .user-avatar {
      background: linear-gradient(135deg, #409eff, #7aa2f7);
      color: #fff;
      font-size: 13px;
      font-weight: 600;
      flex-shrink: 0;
    }

    .user-name {
      font-size: 13.5px;
      color: #333;
      font-weight: 500;
      max-width: 80px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .arrow-icon {
      font-size: 12px;
      color: #999;
      transition: transform 0.2s;
    }
  }
}

// ─── 内容区 ───────────────────────────────────────────────────────────────
.vben-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px 24px;
}

// ─── 页面切换动画 ────────────────────────────────────────────────────────
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
