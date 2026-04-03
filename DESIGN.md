# 🧳 创意旅游管理系统 - 设计文档

> 适合冲优秀毕业设计 | 技术栈：Vue3+TS+Spring Boot 3 | 版本：V1.1 | 日期：2026-04-02

---

## 一、项目概述

### 1.1 项目背景

随着旅游业蓬勃发展，游客对旅游体验的需求日益增长，传统旅游管理方式已无法满足个性化、智能化服务的要求。本系统旨在打造一个集景点展示、酒店预订、行程规划、互动社区于一体的**智慧旅游管理平台**。

### 1.2 项目目标

- ✅ 提供**沉浸式景点展示**（3D 全景、VR 体验）
- ✅ 实现**智能化行程规划**与酒店预订
- ✅ 构建**实时互动社区**（游记分享、实时评论）
- ✅ 提供**数据可视化驾驶舱**（ECharts 大屏）
- ✅ 展示**高创意交互体验**（GSAP 动画、弹幕效果）

### 1.3 预期成果

| 指标 | 目标 | 当前状态 |
|------|------|---------|
| 功能模块数 | ≥ 8 个 | 🔄 进行中 |
| 接口数量 | ≥ 40 个 | 🔄 进行中 |
| 前端页面数 | ≥ 25 个 | 🔄 20/25 |
| 创意动效页 | ≥ 5 个 | 🔄 进行中 |
| 代码规范 | TypeScript 严格模式 + ESLint | ✅ 已完成 |

---

## 二、技术架构设计

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户层 (User)                            │
│   PC浏览器(管理后台) / H5响应式(用户端) / 微信小程序(可选)        │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS
┌────────────────────────────▼────────────────────────────────────┐
│                       前端层 (Vue3 Frontend)                      │
│                                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ Vue3+TS  │  │ UnoCSS   │  │ GSAP     │  │ Element Plus     │  │
│  │ Vite 5   │  │ Tailwind │  │ Scroll   │  │ (全站统一)       │  │
│  │ Router4  │  │ CSS      │  │ Trigger  │  │                  │  │
│  │ Pinia2   │  │ 创意样式 │  │ Lottie   │  │ Naive UI(规划中) │  │
│  │ Axios    │  │ ECharts5 │  │ 高德地图 │  │                  │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │ REST API / WebSocket
┌────────────────────────────▼────────────────────────────────────┐
│                       网关层 (API Gateway)                        │
│                     Nginx (反向代理 + 静态资源)                     │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    后端 Spring Boot 3 Layer                       │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                  Controller (控制层)                        │     │
│  │   用户/景点/酒店/订单/游记/评论/统计/聊天/支付              │     │
│  └──────────────────────────────────────────────────────────┘     │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                  Service (业务层)                          │     │
│  │   业务逻辑/事务管理/数据校验                               │     │
│  └──────────────────────────────────────────────────────────┘     │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                  Repository (持久层)                       │     │
│  │   MyBatis-Plus CRUD / 复杂查询                           │     │
│  └──────────────────────────────────────────────────────────┘     │
└────────────────────────────┬────────────────────────────────────┘
                             │
          ┌─────────────────┼─────────────────┐
          │                 │                 │
┌─────────▼──────┐ ┌────────▼──────┐ ┌───────▼────────┐
│    MySQL 8.0   │ │    Redis 7.0  │ │  OSS 对象存储  │
│   主数据库      │ │   缓存/会话   │ │  图片/文件存储  │
│                │ │   热度排行    │ │  MinIO/七牛云  │
└────────────────┘ └──────────────┘ └────────────────┘
```

### 2.2 技术选型清单

#### 前端技术栈（✅ 已锁定版本）

| 技术 | 版本 | 用途 | 状态 |
|------|------|------|------|
| Vue 3 | ^3.4.21 | 核心框架 | ✅ |
| TypeScript | ^5.4.0 | 类型系统 | ✅ |
| Vite | ^5.2.0 | 构建工具 | ✅ |
| Vue Router | ^4.3.0 | 路由管理 | ✅ |
| Pinia | ^2.1.7 | 状态管理 | ✅ |
| Axios | ^1.6.8 | HTTP 客户端 | ✅ |
| Element Plus | ^2.5.6 | 全站 UI 组件库 | ✅ |
| UnoCSS | ^0.58.5 | 原子化 CSS | ✅ |
| GSAP | ^3.12.5 | 高级动画引擎 | ✅ |
| ScrollTrigger | ^3.12（内置）| 滚动驱动动画 | ✅ |
| ECharts | ^5.5.0 | 数据可视化 | ✅ |
| echarts-for-vue | ^1.4.1 | Vue3 集成 | ✅ |
| lottie-web | ^5.12.2 | JSON 动画 | ✅ |
| @element-plus/icons-vue | ^2.3.1 | 图标库 | ✅ |
| @amap/amap-jsapi-loader | ^1.0.1 | 高德地图 | ✅ |
| dayjs | ^1.11.10 | 日期处理 | ✅ |
| nprogress | ^0.2.0 | 进度条 | ✅ |
| sass-embedded | ^1.98.0 | SCSS 编译 | ✅ |

#### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.2+ | 核心框架 |
| Spring Security | 6.2+ | 安全框架 |
| JWT | 0.12 | Token 认证 |
| MyBatis-Plus | ^3.5 | ORM 框架 |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.0 | 缓存/Session |
| Lombok | ^1.18 | 简化代码 |
| Hutool | ^5.8 | 工具库 |
| knife4j | ^4.5 | API 文档 |
| MinIO | ^8.5 | 对象存储 |

---

## 三、功能模块设计

### 3.1 模块总览

```
旅游管理系统
│
├── 📱 用户端（游客使用）
│   ├── 🏠 首页（GSAP Hero 轮播 + 数字统计动画）
│   ├── 🗺️ 景点模块（瀑布流列表 + 详情页 + 收藏）
│   ├── 🏨 酒店模块（卡片列表 + 弹幕特效详情页 + 预订）
│   ├── 📅 行程规划（智能推荐 + 日程管理）
│   ├── 📝 游记社区（发布 + 评论 + 点赞）
│   ├── 🛒 订单中心（我的订单 + 取消/退款）
│   ├── 💬 实时客服（WebSocket 聊天）
│   └── 👤 个人中心（资料 + 足迹地图）
│
└── 🖥️ 管理端（后台运营）
    ├── 📊 数据大屏（ECharts 实时统计）
    ├── 🗺️ 景点管理（增删改查 + 上下架）
    ├── 🏨 酒店管理（房型 + 价格日历）
    ├── 📦 订单管理（处理 + 退款）
    ├── 👥 用户管理（会员列表 + 封禁）
    ├── 📝 游记管理（审核 + 置顶）
    ├── 💬 客服管理（聊天记录）
    └── ⚙️ 系统设置（权限/配置）
```

### ③② 核心业务流程

#### 景点预订流程
```
用户浏览景点
    ↓
点击预订 → 选择日期 → 选择票型
    ↓
确认订单 → 登录/注册 → 支付
    ↓
支付成功 → 生成订单 → 发送通知
    ↓
入园游玩 → 评价打分
```

#### 游记发布流程
```
用户写游记 → 上传图片/视频
    ↓
提交审核 → 管理端审核
    ↓
审核通过 → 发布上线
    ↓
其他用户浏览 → 评论互动 → 点赞收藏
```

---

## 四、数据库设计

### 4.1 ER 图概览

```
┌──────────┐     ┌──────────┐     ┌──────────┐
│   用户    │────<│   订单    │>────│   景点    │
│  users   │     │  orders  │     │  spots   │
└────┬─────┘     └────┬─────┘     └────┬─────┘
     │                 │                 │
     │            ┌────▼────┐            │
     │            │ 订单明细 │            │
     │            │order_item│            │
     │            └──────────┘            │
     │                                   │
┌────▼─────┐                    ┌───────▼───────┐
│  用户收藏 │                    │    景点图片    │
│ favorites│                    │  spot_images  │
└────┬─────┘                    └───────────────┘
     │
┌────▼─────┐     ┌──────────┐     ┌──────────┐
│  游记    │────<│  评论    │     │  酒店    │
│ articles │     │ comments │     │  hotels  │
└────┬─────┘     └──────────┘     └────┬─────┘
     │                                  │
     │                            ┌─────▼─────┐
     └────────────────────────────>│   房型    │
           关联                     │room_types │
                                   └───────────┘
```

### 4.2 核心数据表

#### 用户表 (users)
```sql
CREATE TABLE users (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    nickname    VARCHAR(100) COMMENT '昵称',
    avatar      VARCHAR(500) COMMENT '头像URL',
    phone       VARCHAR(20) COMMENT '手机号',
    email       VARCHAR(100) COMMENT '邮箱',
    gender      TINYINT DEFAULT 0 COMMENT '0未知 1男 2女',
    birthday    DATE COMMENT '生日',
    role        TINYINT DEFAULT 1 COMMENT '1普通用户 2管理员',
    status      TINYINT DEFAULT 1 COMMENT '1正常 0封禁',
    last_login  DATETIME COMMENT '最后登录时间',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 景点表 (spots)
```sql
CREATE TABLE spots (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(200) NOT NULL COMMENT '景点名称',
    cover_image     VARCHAR(500) COMMENT '封面图',
    city            VARCHAR(50) NOT NULL COMMENT '所在城市',
    address         VARCHAR(500) COMMENT '详细地址',
    longitude       DECIMAL(10,6) COMMENT '经度',
    latitude        DECIMAL(10,6) COMMENT '纬度',
    description     TEXT COMMENT '景点描述',
    ticket_price    DECIMAL(10,2) DEFAULT 0 COMMENT '门票价格',
    open_time       VARCHAR(200) COMMENT '开放时间',
    suggested_time  INT COMMENT '建议游览时长(小时)',
    level           VARCHAR(50) COMMENT '景区等级(5A/4A等)',
    tags            VARCHAR(500) COMMENT '标签,逗号分隔',
    hot_score       INT DEFAULT 0 COMMENT '热度分数',
    view_count      INT DEFAULT 0 COMMENT '浏览次数',
    status          TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_status (status),
    INDEX idx_hot (hot_score DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';
```

#### 酒店表 (hotels)
```sql
CREATE TABLE hotels (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(200) NOT NULL COMMENT '酒店名称',
    cover_image     VARCHAR(500) COMMENT '封面图',
    city            VARCHAR(50) NOT NULL COMMENT '所在城市',
    address         VARCHAR(500) COMMENT '详细地址',
    longitude       DECIMAL(10,6) COMMENT '经度',
    latitude        DECIMAL(10,6) COMMENT '纬度',
    description     TEXT COMMENT '酒店描述',
    star_level      TINYINT COMMENT '星级(3-5)',
    facilities      VARCHAR(500) COMMENT '设施,逗号分隔',
    hot_score       INT DEFAULT 0 COMMENT '热度分数',
    status          TINYINT DEFAULT 1 COMMENT '1上架 0下架',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_star (star_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店表';
```

#### 房型表 (room_types)
```sql
CREATE TABLE room_types (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    hotel_id        BIGINT NOT NULL COMMENT '所属酒店',
    name            VARCHAR(100) NOT NULL COMMENT '房型名称',
    price           DECIMAL(10,2) NOT NULL COMMENT '单价/晚',
    bed_type        VARCHAR(50) COMMENT '床型(大床/双床)',
    max_guest       INT DEFAULT 2 COMMENT '最大入住人数',
    total_rooms     INT DEFAULT 10 COMMENT '房间总数',
    amenities       VARCHAR(500) COMMENT ' amenities',
    images          VARCHAR(1000) COMMENT '房型图片,逗号分隔',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';
```

#### 订单表 (orders)
```sql
CREATE TABLE orders (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no        VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    order_type      TINYINT NOT NULL COMMENT '1景点订单 2酒店订单',
    target_id       BIGINT NOT NULL COMMENT '景点ID或酒店ID',
    total_amount    DECIMAL(10,2) NOT NULL COMMENT '总金额',
    pay_amount      DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status          TINYINT DEFAULT 1 COMMENT '1待支付 2已支付 3已取消 4已退款',
    pay_time        DATETIME COMMENT '支付时间',
    pay_channel     VARCHAR(50) COMMENT '支付渠道',
    contact_name    VARCHAR(50) COMMENT '联系人',
    contact_phone   VARCHAR(20) COMMENT '联系电话',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

#### 游记表 (articles)
```sql
CREATE TABLE articles (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL COMMENT '作者ID',
    title           VARCHAR(200) NOT NULL COMMENT '标题',
    cover_image     VARCHAR(500) COMMENT '封面图',
    content         TEXT NOT NULL COMMENT '正文(Markdown)',
    spot_ids        VARCHAR(500) COMMENT '关联景点ID,逗号分隔',
    tags            VARCHAR(200) COMMENT '标签',
    view_count      INT DEFAULT 0 COMMENT '浏览次数',
    like_count      INT DEFAULT 0 COMMENT '点赞数',
    collect_count   INT DEFAULT 0 COMMENT '收藏数',
    comment_count   INT DEFAULT 0 COMMENT '评论数',
    status          TINYINT DEFAULT 0 COMMENT '0待审核 1已发布 2已下架',
    is_top          TINYINT DEFAULT 0 COMMENT '是否置顶',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_create (created_at DESC),
    FULLTEXT idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游记表';
```

#### 评论表 (comments)
```sql
CREATE TABLE comments (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL COMMENT '评论者ID',
    target_type     TINYINT NOT NULL COMMENT '1景点 2酒店 3游记 4订单',
    target_id       BIGINT NOT NULL COMMENT '目标ID',
    parent_id       BIGINT DEFAULT 0 COMMENT '父评论ID(0为顶级)',
    content         VARCHAR(1000) NOT NULL COMMENT '评论内容',
    rating          TINYINT COMMENT '评分(1-5)',
    like_count      INT DEFAULT 0 COMMENT '点赞数',
    status          TINYINT DEFAULT 1 COMMENT '1正常 0删除',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_target (target_type, target_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

---

## 五、API 接口设计

### 5.1 接口规范

```
Base URL: /api/v1

统一响应格式:
{
    "code": 200,        // 业务状态码
    "msg": "success",   // 消息
    "data": {},         // 数据体
    "timestamp": 1699999999999
}

状态码约定:
200 成功
400 参数错误
401 未登录
403 无权限
404 资源不存在
500 服务器异常
```

### 5.2 核心接口清单

#### 认证模块 `/auth`
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | /auth/register | 用户注册 | ✅ 后端可用 |
| POST | /auth/login | 用户登录 | ✅ 后端可用 |
| POST | /auth/logout | 退出登录 | ✅ 后端可用 |
| GET | /auth/me | 获取当前用户信息 | ✅ 后端可用 |
| POST | /auth/refresh | 刷新Token | 🔄 待实现 |

#### 用户模块 `/user`
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | /user/profile | 获取个人信息 | 🔄 待实现 |
| PUT | /user/profile | 更新个人信息 | 🔄 待实现 |
| PUT | /user/avatar | 修改头像 | 🔄 待实现 |
| PUT | /user/password | 修改密码 | 🔄 待实现 |

#### 景点模块 `/spot`
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | /spot/list | 景点列表(分页+筛选) | 🔄 后端开发中 |
| GET | /spot/hot | 热门景点排行 | 🔄 后端开发中 |
| GET | /spot/:id | 景点详情 | 🔄 后端开发中 |
| GET | /spot/:id/comments | 景点评论列表 | 🔄 待实现 |
| POST | /spot/:id/comment | 评论景点 | 🔄 待实现 |
| POST | /spot/:id/favorite | 收藏/取消收藏 | 🔄 待实现 |
| GET | /spot/favorites | 我的收藏列表 | 🔄 待实现 |
| GET | /spot/nearby | 附近景点(LBS) | 🔄 待实现 |

#### 酒店模块 `/hotel`
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | /hotel/list | 酒店列表 | 🔄 后端开发中 |
| GET | /hotel/hot | 热门酒店排行 | 🔄 后端开发中 |
| GET | /hotel/:id | 酒店详情(含房型) | 🔄 后端开发中 |
| GET | /hotel/:id/rooms | 房型列表+价格日历 | 🔄 待实现 |

#### 订单模块 `/order`
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | /order/create | 创建订单 | 🔄 后端开发中 |
| GET | /order/list | 我的订单列表 | 🔄 后端开发中 |
| GET | /order/:id | 订单详情 | 🔄 后端开发中 |
| PUT | /order/:id/cancel | 取消订单 | 🔄 待实现 |
| PUT | /order/:id/refund | 申请退款 | 🔄 待实现 |
| POST | /order/:id/pay | 模拟支付 | 🔄 后端开发中 |

#### 游记模块 `/article`
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | /article/list | 游记列表 | 🔄 后端开发中 |
| GET | /article/hot | 热门游记 | 🔄 后端开发中 |
| GET | /article/:id | 游记详情 | 🔄 后端开发中 |
| POST | /article | 发布游记 | 🔄 后端开发中 |
| PUT | /article/:id | 编辑游记 | 🔄 后端开发中 |
| DELETE | /article/:id | 删除游记 | 🔄 后端开发中 |
| POST | /article/:id/like | 点赞/取消点赞 | 🔄 后端开发中 |

#### 聊天模块 `/chat` (WebSocket)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| WS | /ws/chat | 即时聊天(WebSocket) | 🔄 后端开发中 |
| GET | /chat/history | 聊天记录 | 🔄 待实现 |

#### 统计模块 `/statistics` (管理端)
| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | /statistics/dashboard | 仪表盘数据 | 🔄 后端开发中 |
| GET | /statistics/orderTrend | 订单趋势 | 🔄 待实现 |
| GET | /statistics/userGrowth | 用户增长 | 🔄 待实现 |

---

## 六、前端页面设计

### 6.1 页面清单（✅ 已实现 vs 🔄 规划中）

#### 用户端页面

| 页面 | 路由 | 创意点 | 状态 |
|------|------|-------|------|
| 首页 | `/home` | GSAP Hero 轮播 + 数字统计 + 热门景点瀑布流 | ✅ 已完成 |
| 景点列表 | `/spots` | 瀑布流卡片 + 鼠标视差 + GSAP 入场动画 | ✅ 已完成 |
| 景点详情 | `/spots/:id` | 全景图入口 + 设施标签 + 评分系统 | ✅ 已完成 |
| 酒店列表 | `/hotels` | 卡片悬浮 + GSAP 鼠标跟随 + 筛选栏 | ✅ 已完成 |
| 酒店详情 | `/hotels/:id` | 全屏轮播 + **弹幕特效** + 房型选择 + 价格日历 | ✅ 已完成 |
| 游记列表 | `/articles` | 瀑布流 + 标签筛选 | ✅ 已完成 |
| 游记详情 | `/articles/:id` | 阅读进度条 + 目录导航 | 🔄 进行中 |
| 写游记 | `/articles/create` | Markdown 编辑器 + 图片上传 | ✅ 已完成 |
| 我的订单 | `/orders` | 状态Tab + 时间线展示 | ✅ 已完成 |
| 智慧旅行 | `/travel` | 行程规划界面 | 🔄 进行中 |
| 个人中心 | `/profile` | 足迹地图 + 数据统计 | 🔄 进行中 |
| 实时客服 | `/chat` | WebSocket 聊天界面 | ✅ 已完成 |

#### 管理端页面

| 页面 | 路由 | 说明 | 状态 |
|------|------|------|------|
| 登录页 | `/login` | 创意登录背景 | ✅ 已完成 |
| 注册页 | `/register` | 用户注册 | ✅ 已完成 |
| 仪表盘 | `/admin/dashboard` | ECharts 大屏 | ✅ 已完成 |
| 景点管理 | `/admin/spots` | 增删改查 + 上下架 | ✅ 已完成 |
| 酒店管理 | `/admin/hotels` | 房型+价格管理 | ✅ 已完成 |
| 订单管理 | `/admin/orders` | 状态流转 + 处理 | ✅ 已完成 |
| 用户管理 | `/admin/users` | 会员列表 | ✅ 已完成 |
| 游记管理 | `/admin/articles` | 审核+置顶 | ✅ 已完成 |
| 评论管理 | `/admin/comments` | 审核+删除 | 🔄 待开发 |
| 客服管理 | `/admin/chat` | 聊天记录 | 🔄 待开发 |

### 6.2 已实现页面详细设计

#### 🏠 首页（用户端）- ✅ 已完成

```
┌─────────────────────────────────────────────────────┐
│  Logo    首页  景点  酒店  智慧旅行  游记  [头像]    │ ← 导航栏（sticky + 毛玻璃）
├─────────────────────────────────────────────────────┤
│                                                     │
│   [Hero 全屏轮播背景图]                              │
│   ════════════════════════════════════════════      │
│   ●───────────────────────────────────────────●    │
│                                                     │
│   🎯 探索世界，发现美好                              │
│   发现绝美风景                                       │
│   百万旅行者的选择，开启你的下一段旅程                  │
│                                                     │
│   ┌──────────────────────────────────┐  [搜索框]   │
│   │ 搜索目的地、酒店、景点...           │  🔍       │
│   └──────────────────────────────────┘             │
│                                                     │
│   [北京] [上海] [杭州] [成都] [厦门]   ← 快速标签    │
│                                                     │
│   ════════════════════════════════════════════      │
│              ↓ 向下滚动                              │
├─────────────────────────────────────────────────────┤
│                                                     │
│   ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐           │
│   │ 12,847│  │ 1,234 │  │98,760 │  │  328  │  ← 数字滚动动画
│   │ 用户  │  │ 订单  │  │ 收入  │  │ 酒店  │           │
│   └──────┘  └──────┘  └──────┘  └──────┘           │
│                                                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│   01 精选景点                                        │
│   发现绝美风景                                       │
│                                                     │
│   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐     │
│   │ 景点1  │ │ 景点2  │ │ 景点3  │ │ 景点4  │     │
│   │ ¥128  │ │ ¥98   │ │ ¥168  │ │ ¥88   │     │
│   └────────┘ └────────┘ └────────┘ └────────┘     │
│   GSAP ScrollTrigger 瀑布流入场                     │
│                                                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│   精选酒店                                           │
│   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐     │
│   │ 酒店1  │ │ 酒店2  │ │ 酒店3  │ │ 酒店4  │     │
│   │ ¥1888 │ │ ¥3588 │ │ ¥1588 │ │ ¥2288 │     │
│   └────────┘ └────────┘ └────────┘ └────────┘     │
│   鼠标移动时卡片跟随倾斜 (GSAP)                      │
│                                                     │
└─────────────────────────────────────────────────────┘
```

#### 🏨 酒店详情页（用户端）- ✅ 已完成（亮点功能）

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│   ┌─────────────────────────────────────────────┐   │
│   │                                             │   │
│   │         全屏轮播酒店图片 (5张)               │   │
│   │         ⭐ 4.9  2847条评价                  │   │
│   │                                             │   │
│   │  ════════════════════════════════════════   │   │
│   │  北京华尔道夫酒店                           │   │
│   │  [豪华五星] [王府井商圈] [近地铁]            │   │
│   │  📍 北京 · 北京市东城区金宝街8号            │   │
│   └─────────────────────────────────────────────┘   │
│                                                     │
│   ← ‹  ·····························  ›            │
│                                                     │
│   ═══════════════════════════════════════════════   │
│                                                     │
│   ┌─────────────────────────┐ ┌──────────────────┐ │
│   │  选择房型                │ │  预订卡片（固定） │ │
│   │                         │ │                  │ │
│   │  ┌────┐  豪华大床房     │ │  豪华大床房      │ │
│   │  │图片│  ¥1888/晚      │ │  ¥1888 /晚       │ │
│   │  └────┘  大床1.8m含双早 │ │                  │ │
│   │           ✓ 已选        │ │  入住日期 [📅]   │ │
│   │                         │ │  退房日期 [📅]   │ │
│   │  ┌────┐  行政双床房     │ │  共 2 晚         │ │
│   │  │图片│  ¥2288/晚      │ │  入住人数 [1]    │ │
│   │  └────┘                │ │                  │ │
│   │                         │ │  ¥1888 × 2    ¥3776│ │
│   │  ┌────┐  套房🔥        │ │  服务费        ¥377 │ │
│   │  │图片│  ¥3888/晚     │ │  ──────────────── │ │
│   │  └────┘                │ │  合计  ¥4153    │ │
│   │                         │ │                  │ │
│   │                         │ │ [  立即预订  ]   │ │
│   │                         │ │                  │ │
│   │                         │ │ 酒店设施:        │ │
│   │                         │ │ [WiFi][泳池]... │ │
│   └─────────────────────────┘ └──────────────────┘ │
│                                                     │
│   ═══════════════════════════════════════════════   │
│                                                     │
│   关于酒店                                          │
│   北京华尔道夫酒店坐落于繁华的王府井商圈...          │
│                                                     │
│   ┌──────┐ ┌──────┐ ┌──────┐ ┌──────────────┐      │
│   │ 图片 │ │ 图片 │ │ 图片 │ │   宽幅图片   │      │
│   │      │ │      │ │      │ │   (2x2)     │      │
│   └──────┘ └──────┘ └──────┘ └──────────────┘      │
│                                                     │
│   ═══════════════════════════════════════════════   │
│                                                     │
│   住客点评                                          │
│   ⭐ 4.9                                            │
│                                                     │
│   ┌──────────────────────────────────────────┐     │
│   │ 👤 旅行家小王    豪华大床房 · 2026-03-15 │     │
│   │ ⭐⭐⭐⭐⭐                                    │     │
│   │ 位置绝佳，就在王府井旁边，逛街超方便！... │     │
│   └──────────────────────────────────────────┘     │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 七、创意动效设计

### 7.1 GSAP 动画清单（✅ 已实现）

| 页面 | 动效描述 | 实现方式 | 状态 |
|------|---------|---------|------|
| 首页 Hero | 多图轮播淡入淡出 + 背景渐变切换 | `setInterval + opacity transition` | ✅ |
| 首页数字统计 | 数字滚动递增动画 | `gsap.to({ counter })` | ✅ |
| 首页景点卡片 | 滚动入场 + 悬浮缩放 | `ScrollTrigger stagger` | ✅ |
| 景点列表卡片 | GSAP 鼠标跟随视差 | `mousemove + gsap.to` | ✅ |
| 景点列表入场 | 瀑布流入场动画 | `gsap.fromTo stagger` | ✅ |
| 酒店列表卡片 | 鼠标跟随倾斜效果 | `mousemove + rotate3d` | ✅ |
| 酒店详情轮播 | 全屏图片自动轮播 | `setInterval + opacity` | ✅ |
| 酒店详情弹幕 | 弹幕飘过动画（半透明毛玻璃） | `@keyframes danmakuMove` | ✅ ✅ **亮点** |
| 酒店详情预订 | 预订卡片固定 + 滑入动画 | `gsap.timeline ScrollTrigger` | ✅ |
| 酒店详情图文 | 图片画廊 + 缩放悬浮 | `scale + transition` | ✅ |
| 游记发布 | Markdown 编辑器 | `@input` 实时解析 | ✅ |
| 页面切换 | 淡入淡出过渡 | `Vue Transition page-fade` | ✅ |
| 管理端大屏 | ECharts 多种图表 | `echarts-for-vue` | ✅ |

### 7.2 Lottie 动画清单

| 场景 | 动画描述 | 用途 | 状态 |
|------|---------|------|------|
| 加载中 | 飞机飞行 / 地球旋转 | 页面 loading | 🔄 待集成 |
| 空状态 | 无订单 / 无游记 | 空数据插画 | 🔄 待集成 |
| 成功反馈 | 支付成功 / 提交成功 | 反馈提示 | 🔄 待集成 |
| 地图标记 | 打卡标记动画 | 足迹地图 | 🔄 待集成 |
| 引导提示 | 首次使用引导 | 新手教程 | 🔄 待集成 |

### 7.3 高德地图交互

```javascript
// 景点地图 - 聚合标记 + 点击弹窗
const map = new AMap.Map('container', {
  zoom: 11,
  center: [116.397428, 39.90923],
});

// 聚合点标记
const markers = spots.map(spot => {
  const marker = new AMap.Marker({
    position: [spot.longitude, spot.latitude],
    icon: new AMap.Icon({ image: '/icons/spot-marker.png' }),
    extData: spot,
  });

  // 点击显示信息窗
  marker.on('click', () => {
    const infoWindow = new AMap.InfoWindow({
      content: `<div class="map-popup">
        <img src="${spot.coverImage}" />
        <h3>${spot.name}</h3>
        <p>${spot.ticketPrice}</p>
      </div>`,
    });
    infoWindow.open(map, marker.getPosition());
  });

  return marker;
});

// 聚合
const cluster = new AMap.MarkerClusterer(map, markers, {
  gridSize: 80,
  renderClusterMarker: (cluster) => {
    // 自定义聚合点样式
  }
});
```

---

## 八、安全与权限设计

### 8.1 JWT 认证流程
```
用户登录
    ↓
后端验证账号密码
    ↓
生成 AccessToken (15分钟) + RefreshToken (7