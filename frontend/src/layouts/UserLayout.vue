<template>
  <div class="user-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/home')">
          <span class="logo-icon">🧳</span>
          <span class="logo-text">智游旅行</span>
        </div>

        <nav class="nav">
          <router-link v-for="item in navList" :key="item.path" :to="item.path" class="nav-item">
            <span class="nav-icon">{{ item.icon }}</span>
            {{ item.name }}
          </router-link>
        </nav>

        <div class="header-actions">
          <!-- 已登录 -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="user-avatar">
                <el-avatar :size="36" :src="userStore.userInfo?.avatar">
                  {{ userStore.nickname.slice(0, 1) }}
                </el-avatar>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">
                    <span i="ep-user" /> 个人中心
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/orders')">
                    <span i="ep-tickets" /> 我的订单
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <span i="ep-switch-button" /> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <!-- 未登录 -->
          <template v-else>
            <el-button text @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-inner">
        <p>© 2026 智慧旅游管理系统 - 基于 Vue3 + TypeScript 构建</p>
        <p>毕业设计作品</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const navList = [
  { path: '/home', name: '首页', icon: '🏠' },
  { path: '/spots', name: '景点', icon: '🗺️' },
  { path: '/hotels', name: '酒店', icon: '🏨' },
  { path: '/travel', name: '智慧旅行', icon: '✨' },
  { path: '/articles', name: '游记', icon: '📝' },
]

async function handleLogout() {
  await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
  userStore.logout()
  router.push('/home')
}
</script>

<style scoped lang="scss">
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .header-inner {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 24px;
    height: 64px;
    display: flex;
    align-items: center;
    gap: 40px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    flex-shrink: 0;

    .logo-icon {
      font-size: 28px;
    }

    .logo-text {
      font-size: 20px;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }

  .nav {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;

    .nav-item {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      border-radius: 8px;
      font-size: 15px;
      color: #64748b;
      transition: all 0.2s;
      text-decoration: none;

      &:hover {
        color: #3b82f6;
        background: #eff6ff;
      }

      &.router-link-active {
        color: #3b82f6;
        background: #eff6ff;
        font-weight: 500;
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-shrink: 0;

    .user-avatar {
      cursor: pointer;
      border-radius: 50%;
      transition: transform 0.2s;

      &:hover {
        transform: scale(1.05);
      }
    }
  }
}

.main-content {
  flex: 1;
  width: 100%;
  min-width: 0;
}

.footer {
  background: #1e293b;
  color: #94a3b8;
  padding: 32px 24px;

  .footer-inner {
    max-width: 1280px;
    margin: 0 auto;
    text-align: center;
    font-size: 14px;

    p + p {
      margin-top: 8px;
      opacity: 0.7;
    }
  }
}

// 页面切换动画
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}
</style>
