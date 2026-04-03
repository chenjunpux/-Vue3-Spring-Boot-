# 🧳 智慧旅游管理系统 - 前端项目

> Vue3 + TypeScript + Vite + UnoCSS + GSAP + Element Plus

---

## 🚀 快速开始

### 1. 安装依赖

```bash
cd travel-system/frontend
npm install
```

### 2. 配置环境变量

```bash
cp .env.example .env.local
# 填写高德地图 Key（可选）
```

### 3. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

---

## 📁 项目结构

```
frontend/
├── src/
│   ├── api/            # Axios 封装 + 所有接口
│   ├── assets/         # 静态资源
│   ├── components/     # 公共组件
│   ├── composables/     # Composition API 复用
│   ├── layouts/        # 布局组件（用户端 + 管理端）
│   ├── router/        # 路由配置 + 守卫
│   ├── stores/        # Pinia 状态管理
│   ├── styles/        # 全局样式
│   ├── types/          # TypeScript 类型定义
│   ├── utils/          # 工具函数
│   └── views/          # 页面
│       ├── auth/       # 登录/注册
│       ├── user/       # 用户端页面
│       ├── admin/      # 管理端页面
│       └── error/      # 错误页
├── public/             # 静态资源
├── package.json
├── vite.config.ts
├── uno.config.ts
└── tsconfig.json
```

---

## 🛠️ 技术栈

| 分类 | 技术 | 说明 |
|------|------|------|
| 框架 | Vue 3 + Composition API | `<script setup>` 写法 |
| 语言 | TypeScript (strict) | 严格类型检查 |
| 构建 | Vite 5 | 极速 HMR |
| 样式 | UnoCSS + TailwindCSS | 原子化 CSS |
| UI | Element Plus | 管理端组件库 |
| 状态 | Pinia | 现代化状态管理 |
| 路由 | Vue Router 4 | SPA 路由 |
| HTTP | Axios | 请求拦截 + 统一错误处理 |
| 动画 | GSAP + ScrollTrigger | 高级滚动动画 |
| 可视化 | ECharts 5 | 数据大屏图表 |
| 地图 | 高德地图 JS API | 景点定位/路线规划 |

---

## 📝 开发规范

### 目录命名
- 组件文件：`PascalCase.vue`
- 工具/类型文件：`camelCase.ts`
- API 文件：按模块拆分，如 `spot.ts`、`hotel.ts`

### 代码风格
- 使用 `<script setup lang="ts">` 语法
- 所有组件 props 和 emits 需定义类型
- API 请求统一走 `@/api/` 目录
- 样式使用 UnoCSS 工具类 + scoped SCSS

### Git 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档修改
style: 代码格式
refactor: 重构
perf: 性能优化
test: 测试相关
chore: 构建/工具
```

---

## 🌟 创意页面

| 页面 | 技术亮点 |
|------|---------|
| 首页 Hero | GSAP 视差 + Typed.js 打字机效果 |
| 景点列表 | 瀑布流 + GSAP 滚动入场动画 |
| 景点详情 | 高德地图 + 聚合标记点 |
| 数据大屏 | ECharts 实时图表 |
| 管理后台 | Element Plus 完整 CRUD |

---

## 📋 待完成页面

- [ ] 景点详情页（高德地图 + VR 全景）
- [ ] 酒店详情页（720°室内图）
- [ ] 游记发布页（Markdown 编辑器）
- [ ] 个人中心 + 足迹地图
- [ ] WebSocket 实时客服
- [ ] Lottie 空状态动画

---

## 许可证

MIT License - 仅供毕设学习使用
