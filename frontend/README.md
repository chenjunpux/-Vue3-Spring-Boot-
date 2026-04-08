# 智慧旅游管理系统 - 前端

> 🎉 基于 Vue 3 + TypeScript 的现代化旅游管理平台
> 
> 📅 文档更新时间：2026-04-07

---

## 📖 目录

1. [项目简介](#1-项目简介)
2. [技术栈详解](#2-技术栈详解)
3. [快速开始](#3-快速开始)
4. [项目结构](#4-项目结构)
5. [路由与页面导航](#5-路由与页面导航)
6. [组件使用规范](#6-组件使用规范)
7. [样式规范](#7-样式规范)
8. [API 接口](#8-api-接口)
9. [图标使用](#9-图标使用)
10. [常见问题](#10-常见问题)

---

## 1. 项目简介

### 1.1 这是什么项目？
一个完整的**智慧旅游管理系统**，包含：
- 👤 **用户端**：供普通用户浏览景点、酒店、预订、支付等
- 🔧 **管理端**：供管理员管理景点、酒店、订单、用户等

### 1.2 目录位置
```
/Users/feizhaishiyou/Documents/New project/travel-system/frontend/
```

### 1.3 界面预览

**用户端界面**
- 首页、景点列表、酒店列表
- 游记社区、订单管理
- 个人中心

**管理端界面**
- 数据大屏（图表统计）
- 景点/酒店/订单/用户管理
- 系统设置

---

## 2. 技术栈详解

### 2.1 核心技术

| 技术 | 是什么 | 用来做什么 |
|------|--------|-----------|
| **Vue 3** | 一套渐进式 JavaScript 框架 | 构建用户界面 |
| **TypeScript** | JavaScript 的超集，加了类型检查 | 让代码更健壮、更易维护 |
| **Vite** | 下一代前端构建工具 | 快速启动项目、热更新 |
| **Pinia** | Vue 3 的状态管理库 | 管理全局数据（如用户登录状态） |

### 2.2 UI 组件库

| 技术 | 是什么 | 用来做什么 |
|------|--------|-----------|
| **Element Plus** | Vue 3 的 UI 组件库 | 提供现成的按钮、表格、表单等组件 |
| **UnoCSS** | 原子化 CSS 引擎 | 用 class 名快速写样式，如 `class="w-full"` |

**Element Plus 组件示例：**
```vue
<el-button type="primary">按钮</el-button>
<el-table :data="tableData"><el-table>
<el-input v-model="value" placeholder="请输入"></el-input>
<el-select v-model="value"><el-select>
```

### 2.3 数据可视化

| 技术 | 是什么 | 用来做什么 |
|------|--------|-----------|
| **ECharts** | 百度开源的图表库 | 画折线图、饼图、柱状图等 |

**ECharts 示例：**
```vue
<template>
  <div ref="chartRef" style="width: 100%; height: 300px;"></div>
</template>

<script setup>
import * as echarts from 'echarts'

const chartRef = ref()
onMounted(() => {
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    xAxis: { type: 'category', data: ['Mon', 'Tue', 'Wed'] },
    yAxis: { type: 'value' },
    series: [{ data: [120, 200, 150], type: 'line' }]
  })
})
</script>
```

### 2.4 其他工具

| 技术 | 用途 |
|------|------|
| **Axios** | 发送 HTTP 请求（调用后端 API） |
| **Day.js** | 处理日期时间 |
| **Vue Router** | 管理页面跳转 |
| **GSAP / Lottie** | 动画效果 |

---

## 3. 快速开始

### 3.1 安装依赖
```bash
# 进入项目目录
cd /Users/feizhaishiyou/Documents/New\ project/travel-system/frontend

# 安装依赖
pnpm install
# 或
npm install
```

### 3.2 启动开发服务器
```bash
pnpm dev
```

启动成功后访问：**http://localhost:5173**

### 3.3 访问管理端
1. 先登录一个管理员账号
2. 访问 **http://localhost:5173/admin** 进入管理后台

### 3.4 其他常用命令

| 命令 | 作用 |
|------|------|
| `pnpm dev` | 启动开发服务器 |
| `pnpm build` | 打包生产版本 |
| `pnpm type-check` | 检查 TypeScript 类型错误 |
| `pnpm lint` | 检查代码规范 |

---

## 4. 项目结构

```
frontend/
├── public/                    # 静态资源（图片、favicon 等）
│   └── favicon.svg
│
├── src/
│   ├── api/                   # 📌 API 接口文件夹
│   │   ├── axios.ts          # Axios 配置（请求拦截器等）
│   │   ├── admin.ts          # 管理端 API
│   │   ├── spot.ts           # 景点 API
│   │   ├── hotel.ts          # 酒店 API
│   │   ├── order.ts          # 订单 API
│   │   ├── user.ts          # 用户 API
│   │   └── ...              # 其他 API
│   │
│   ├── layouts/              # 📌 布局组件
│   │   ├── UserLayout.vue    # 用户端布局（有 Header、Footer）
│   │   └── AdminLayout.vue   # 管理端布局（有侧边栏、顶栏）
│   │
│   ├── router/               # 📌 路由配置
│   │   └── index.ts         # 所有路由定义在这里
│   │
│   ├── stores/               # 📌 状态管理（Pinia）
│   │   └── user.ts          # 用户登录状态
│   │
│   ├── styles/              # 📌 全局样式
│   │   ├── index.scss        # 全局样式入口
│   │   └── admin.scss        # 管理端通用样式
│   │
│   ├── views/                # 📌 页面组件
│   │   ├── admin/           # 管理端页面
│   │   │   ├── dashboard.vue       # 数据大屏
│   │   │   ├── spots/index.vue     # 景点管理
│   │   │   ├── hotels/index.vue    # 酒店管理
│   │   │   ├── orders/index.vue    # 订单管理
│   │   │   ├── users/index.vue     # 用户管理
│   │   │   └── ...
│   │   │
│   │   ├── user/            # 用户端页面
│   │   │   ├── home/index.vue      # 首页
│   │   │   ├── spots/index.vue     # 景点列表
│   │   │   ├── spots/detail.vue    # 景点详情
│   │   │   └── ...
│   │   │
│   │   └── auth/            # 登录/注册页面
│   │
│   ├── App.vue              # 根组件
│   └── main.ts             # 入口文件
│
├── vite.config.ts           # Vite 配置文件
├── uno.config.ts           # UnoCSS 配置文件
└── package.json            # 项目依赖
```

### 4.1 文件夹用途说明

| 文件夹 | 存放什么 | 重要性 |
|--------|---------|--------|
| `api/` | 所有和后端接口通信的代码 | ⭐⭐⭐ 重要 |
| `views/` | 所有页面组件 | ⭐⭐⭐ 重要 |
| `router/` | 路由配置（页面跳转规则） | ⭐⭐⭐ 重要 |
| `layouts/` | 页面布局框架 | ⭐⭐ 常用 |
| `stores/` | 全局状态（用户信息等） | ⭐⭐ 常用 |
| `styles/` | 全局样式 | ⭐ 了解 |

---

## 5. 路由与页面导航

> 💡 路由配置在 `src/router/index.ts`

### 5.1 用户端页面（面向普通用户）

| 路由地址 | 页面文件 | 功能说明 |
|---------|---------|---------|
| `/home` | `views/user/home/index.vue` | 🏠 首页轮播、推荐 |
| `/spots` | `views/user/spots/index.vue` | 🗺️ 景点列表、搜索 |
| `/spots/:id` | `views/user/spots/detail.vue` | 🗺️ 景点详细信息 |
| `/hotels` | `views/user/hotels/index.vue` | 🏨 酒店列表 |
| `/hotels/:id` | `views/user/hotels/detail.vue` | 🏨 酒店详情 |
| `/travel` | `views/user/travel/index.vue` | ✨ AI 智能行程规划 |
| `/articles` | `views/user/articles/index.vue` | 📝 游记列表 |
| `/articles/create` | `views/user/articles/create.vue` | ✍️ 发布游记 |
| `/articles/:id` | `views/user/articles/detail.vue` | 📖 游记正文 |
| `/orders` | `views/user/orders/index.vue` | 📦 我的订单 |
| `/book/:type/:id` | `views/user/book/index.vue` | 💳 预订确认 |
| `/pay/:orderNo` | `views/user/pay/index.vue` | 💰 支付页面 |
| `/coupons` | `views/user/coupons/index.vue` | 🎫 优惠券 |
| `/notifications` | `views/user/notifications/index.vue` | 🔔 消息通知 |
| `/profile` | `views/user/profile/index.vue` | 👤 个人中心 |
| `/chat` | `views/user/chat/index.vue` | 💬 AI 聊天规划 |

### 5.2 管理端页面（面向管理员）

| 路由地址 | 页面文件 | 功能说明 |
|---------|---------|---------|
| `/admin` → `/admin/dashboard` | `views/admin/dashboard.vue` | 📊 数据统计大屏 |
| `/admin/spots` | `views/admin/spots/index.vue` | 🗺️ 景点增删改查 |
| `/admin/hotels` | `views/admin/hotels/index.vue` | 🏨 酒店增删改查 |
| `/admin/orders` | `views/admin/orders/index.vue` | 📦 订单管理 |
| `/admin/users` | `views/admin/users/index.vue` | 👥 用户管理 |
| `/admin/articles` | `views/admin/articles/index.vue` | 📝 游记审核 |
| `/admin/payments` | `views/admin/payments/index.vue` | 💳 支付记录 |
| `/admin/notifications` | `views/admin/notifications/index.vue` | 🔔 发送通知 |
| `/admin/coupons` | `views/admin/coupons/index.vue` | 🎫 优惠券管理 |
| `/admin/settings` | `views/admin/settings/index.vue` | ⚙️ 系统设置 |

### 5.3 认证页面

| 路由地址 | 页面文件 | 功能说明 |
|---------|---------|---------|
| `/login` | `views/auth/login.vue` | 🔑 登录页面 |
| `/register` | `views/auth/register.vue` | 📝 注册页面 |

---

## 6. 组件使用规范

### 6.1 页面代码结构（管理端）

所有管理页面建议使用以下统一结构：

```vue
<template>
  <div class="admin-page">
    <!-- 数据卡片 -->
    <div class="admin-card">
      
      <!-- 头部：搜索 + 按钮 -->
      <div class="admin-header">
        <div class="admin-header-left">
          <el-input class="admin-search" placeholder="搜索..." />
          <el-select class="admin-search" placeholder="筛选..." />
        </div>
        <div class="admin-header-right">
          <el-button type="primary">新增</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <div class="admin-table">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="名称" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="primary" text>编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="admin-pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getList } from '@/api/admin'

// 数据
const loading = ref(false)
const tableData = ref<any[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 获取列表
async function fetchList() {
  try {
    loading.value = true
    const res: any = await getList(pagination)
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
/* 这里只写当前页面特有的样式 */
/* 通用样式已在外层 admin.scss 中定义 */
</style>
```

### 6.2 常用 Element Plus 组件

| 组件 | 用法 | 示例 |
|------|------|------|
| 按钮 | `<el-button>` | `<el-button type="primary">确定</el-button>` |
| 输入框 | `<el-input>` | `<el-input v-model="value" />` |
| 选择框 | `<el-select>` | `<el-select v-model="value"><el-option /></el-select>` |
| 表格 | `<el-table>` | `<el-table :data="data"><el-table-column /></el-table>` |
| 弹窗 | `<el-dialog>` | `<el-dialog v-model="visible">内容</el-dialog>` |
| 消息提示 | `ElMessage` | `ElMessage.success('成功')` |
| 确认框 | `ElMessageBox` | `ElMessageBox.confirm('确定?')` |
| 分页 | `<el-pagination>` | `<el-pagination v-model:current-page="page" />` |
| 表单 | `<el-form>` | `<el-form :model="form"><el-form-item /></el-form>` |
| 图片 | `<el-image>` | `<el-image :src="url" />` |
| 标签 | `<el-tag>` | `<el-tag type="success">成功</el-tag>` |
| 星级 | `<el-rate>` | `<el-rate v-model="rate" />` |
| 日期选择 | `<el-date-picker>` | `<el-date-picker v-model="date" />` |

---

## 7. 样式规范

### 7.1 管理端通用样式类名

> 📁 样式文件：`src/styles/admin.scss`（自动全局引入）

| 类名 | 说明 | 典型用法 |
|------|------|---------|
| `.admin-page` | 页面容器，居中显示 | `<div class="admin-page">` |
| `.admin-card` | 白色卡片，圆角带阴影 | 包裹表格的容器 |
| `.admin-header` | 卡片头部，flex 布局 | 搜索栏和按钮区域 |
| `.admin-header-left` | 头部左侧，flex 布局 | 放搜索框和筛选 |
| `.admin-header-right` | 头部右侧 | 放新增等按钮 |
| `.admin-search` | 搜索框样式，宽 240px | `<el-input class="admin-search">` |
| `.admin-table` | 表格容器 | 包含深色模式适配 |
| `.admin-cover` | 封面图片，60x40 | 表格内封面图 |
| `.admin-pagination` | 分页容器 | 底部居右的分页 |

### 7.2 UnoCSS 原子类（常用）

| 类名 | 等价 CSS | 用途 |
|------|---------|------|
| `w-full` | `width: 100%` | 宽度100% |
| `h-full` | `height: 100%` | 高度100% |
| `flex` | `display: flex` | Flex 布局 |
| `flex-center` | `flex + justify-center + items-center` | 居中 |
| `flex-between` | `flex + justify-between` | 两端对齐 |
| `gap-2` | `gap: 0.5rem` | 间距 |
| `p-4` | `padding: 1rem` | 内边距 |
| `m-4` | `margin: 1rem` | 外边距 |
| `text-center` | `text-align: center` | 居中文字 |
| `text-sm` | `font-size: 0.875rem` | 小号文字 |
| `font-medium` | `font-weight: 500` | 中等字重 |
| `rounded` | `border-radius: 0.25rem` | 圆角 |
| `shadow` | `box-shadow` | 阴影 |
| `border` | `border: 1px solid` | 边框 |
| `bg-white` | `background: white` | 白色背景 |
| `text-gray-500` | `color: #6b7280` | 灰色文字 |
| `text-red-500` | `color: #ef4444` | 红色文字 |
| `text-green-500` | `color: #22c55e` | 绿色文字 |

### 7.3 深色模式

在 `dark` 类下自动适配：
```vue
<div class="dark">
  <!-- 这里面的样式会自动适配深色模式 -->
</div>
```

---

## 8. API 接口

### 8.1 API 文件说明

| 文件 | 负责的接口 |
|------|------------|
| `api/admin.ts` | 管理端所有接口 |
| `api/spot.ts` | 景点相关接口 |
| `api/hotel.ts` | 酒店相关接口 |
| `api/order.ts` | 订单相关接口 |
| `api/user.ts` | 用户相关接口 |
| `api/article.ts` | 游记相关接口 |
| `api/axios.ts` | Axios 实例配置 |

### 8.2 调用 API 示例

```typescript
// 1. 从 api 文件夹引入
import { getAdminSpotList, createSpot } from '@/api/admin'

// 2. 调用接口
const res = await getAdminSpotList({
  page: 1,
  pageSize: 10
})

// 3. 获取数据（根据后端返回格式调整）
const list = res.data?.records || []
```

### 8.3 后端接口地址

- 开发环境：`http://localhost:8080`
- API 前缀：`/api/v1`
- 完整地址示例：`http://localhost:8080/api/v1/spot/list`

---

## 9. 图标使用

### 9.1 图标前缀说明

本项目使用 UnoCSS 的 Iconify 集成，支持三个图标集：

| 图标集 | 前缀 | 示例 | 图标数量 |
|--------|------|------|---------|
| MDI | `i-mdi-` | `<i class="i-mdi-home"></i>` | 5000+ |
| Carbon | `i-carbon-` | `<i class="i-carbon-search"></i>` | 1000+ |
| EP | `i-ep-` | `<i class="i-ep-user"></i>` | 200+ |

### 9.2 常用图标对照

| 用途 | MDI | Carbon | EP |
|------|-----|--------|-----|
| 首页 | `i-mdi-home` | `i-carbon-home` | `i-ep-home` |
| 搜索 | `i-mdi-magnify` | `i-carbon-search` | `i-ep-search` |
| 用户 | `i-mdi-account` | `i-carbon-user` | `i-ep-user` |
| 添加 | `i-mdi-plus` | `i-carbon-add` | `i-ep-plus` |
| 删除 | `i-mdi-delete` | `i-carbon-trash-can` | `i-ep-delete` |
| 编辑 | `i-mdi-pencil` | `i-carbon-edit` | `i-ep-edit` |
| 消息 | `i-mdi-bell` | `i-carbon-notification` | `i-ep-bell` |
| 设置 | `i-mdi-cog` | `i-carbon-settings` | `i-ep-setting` |
| 返回 | `i-mdi-arrow-left` | `i-carbon-arrow-left` | `i-ep-back` |

### 9.3 图标网站

需要查找图标？访问：
- **https://icones.js.org/** - 输入图标名搜索

---

## 10. 常见问题

### Q1: 页面不显示？
检查路由是否正确配置在 `src/router/index.ts`

### Q2: 样式不生效？
确认使用了正确的类名，参考本文档第 7 节

### Q3: API 请求失败？
1. 检查后端是否启动（端口 8080）
2. 检查网络请求是否有跨域问题
3. 检查 Token 是否过期

### Q4: TypeScript 报错？
运行 `pnpm type-check` 查看详细错误

### Q5: 如何新增一个管理页面？
1. 在 `views/admin/` 下创建 `xxx/index.vue`
2. 在 `src/router/index.ts` 添加路由
3. 使用统一样式类（参考本文档第 6 节）

---

## 📝 更新记录

| 日期 | 版本 | 更新内容 |
|------|------|----------|
| 2026-04-07 | v1.0.1 | 完善支付管理页面，前后端完整功能 |
| 2026-04-07 | v1.0.0 | 初始版本，完成所有页面开发 |

---

## 💡 提示

- 📖 阅读路由配置：`src/router/index.ts`
- 🎨 查看全局样式：`src/styles/admin.scss`
- 📡 查看 API 接口：`src/api/`
- 🧩 查看页面组件：`src/views/`

---

*文档更新时间：2026-04-07*
