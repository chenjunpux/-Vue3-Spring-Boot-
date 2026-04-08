# 🧳 智慧旅游管理系统 - 设计文档（V3.2 正式版）

> 适合冲优秀毕业设计 | 技术栈：Vue3+TS+Spring Boot 3 | 版本：V3.2 | 日期：2026-04-08

**GitHub 仓库：** https://github.com/chenjunpux/-Vue3-Spring-Boot-

---

## 一、项目概述

### 1.1 项目背景

随着旅游业蓬勃发展，游客对旅游体验的需求日益增长，传统旅游管理方式已无法满足个性化、智能化服务的要求。本系统旨在打造一个集景点展示、酒店预订、行程规划、互动社区于一体的**智慧旅游管理平台**。

### 1.2 项目目标

- ✅ 提供**沉浸式景点展示**（高德地图集成、景点详情）
- ✅ 实现**智能化酒店预订**（房型展示、实时价格）
- ✅ 构建**实时互动社区**（游记分享、评论点赞）
- ✅ 提供**数据可视化驾驶舱**（ECharts 大屏）
- ✅ 实现**完整预订支付流程**（景点/酒店预订 → 订单确认 → 支付/取消）
- ✅ 实现**文件存储服务**（本地存储 + 文件管理）
- ✅ 实现**消息通知系统**（站内通知、系统公告、MQ异步推送）
- ✅ 实现**优惠券系统**（满减/折扣/兑换，实时抵扣）
- ✅ 实现**管理员通知管理**（发布/草稿/推送全用户或指定用户）
- ✅ 实现**蓝橙撞色UI重设计**（活力明快风格，年轻人友好）

### 1.3 预期成果

| 指标 | 目标 |
|------|------|
| 功能模块数 | ≥ 15 个 |
| 接口数量 | ≥ 50 个 |
| 前端页面数 | ≥ 25 个 |
| 数据库表数 | 21 张 |
| 代码规范 | TypeScript 严格模式 + ESLint |

---

## 二、技术架构设计

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户层 (User)                            │
│   PC浏览器(管理后台) / H5响应式(用户端)                          │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS / WSS
┌────────────────────────────▼────────────────────────────────────┐
│                       前端层 (Vue3 Frontend)                      │
│                                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ Vue3+TS  │  │ UnoCSS   │  │ GSAP     │  │ Element Plus     │  │
│  │ Vite     │  │ Tailwind │  │ 动画效果  │  │ (全栈 UI)        │  │
│  │ Router   │  │          │  │ Lottie   │  │                  │  │
│  │ Pinia    │  │          │  │ ECharts  │  │                  │  │
│  │ Axios    │  │          │  │ 高德地图  │  │                  │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │ REST API
┌────────────────────────────▼────────────────────────────────────┐
│                    后端 Spring Boot 3 Layer                       │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                  Controller (控制层)                       │     │
│  │   用户/景点/酒店/订单/游记/评论/统计/支付/文件/通知        │     │
│  └──────────────────────────────────────────────────────────┘     │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                  Service (业务层)                         │     │
│  │   业务逻辑/事务管理/数据校验                               │     │
│  └──────────────────────────────────────────────────────────┘     │
│  ┌──────────────────────────────────────────────────────────┐     │
│  │                  Repository (持久层)                      │     │
│  │   MyBatis-Plus CRUD / 复杂查询                           │     │
│  └──────────────────────────────────────────────────────────┘     │
└────────────────────────────┬────────────────────────────────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
┌─────────▼──────┐ ┌──────────▼──────┐ ┌───────▼────────┐
│    MySQL 8.0   │ │    本地存储     │ │   文件系统     │
│     主数据库    │ │   uploads/     │ │   文件管理     │
└────────────────┘ └─────────────────┘ └────────────────┘
```

### 2.2 技术选型清单

#### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | ^3.4 | 核心框架 |
| TypeScript | ^5.4 | 类型系统 |
| Vite | ^5.2 | 构建工具 |
| Vue Router | ^4.3 | 路由管理 |
| Pinia | ^2.1 | 状态管理 |
| Axios | ^1.6 | HTTP 客户端 |
| Element Plus | ^2.5 | 全栈 UI 组件库 |
| UnoCSS | ^0.58 | 原子化 CSS |
| GSAP | ^3.12 | 高级动画引擎 |
| Lottie-web | ^5.12 | JSON 动画播放 |
| ECharts | ^5.5 | 数据可视化 |
| echarts-for-vue | ^1.4 | Vue ECharts 封装 |
| @element-plus/icons-vue | ^2.3 | 图标库 |
| @amap/amap-jsapi-loader | ^1.0 | 高德地图 |
| dayjs | ^1.11 | 日期处理 |
| nprogress | ^0.2 | 进度条 |
| @iconify-json/mdi | ^1.2 | Material Design 图标 |

#### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.2.5 | 核心框架 |
| Spring Security | 6.2+ | 安全框架 |
| JWT (jjwt) | 0.12 | Token 认证 |
| MyBatis-Plus | ^3.5 | ORM 框架 |
| MySQL | 8.0 | 主数据库 |
| Spring Data Redis | 3.2+ | Redis 客户端（依赖已引入）|
| Lombok | ^1.18 | 简化代码 |
| Hutool | ^5.8 | 工具库 |
| Apache POI | ^5.2 | Excel 导入导出 |

---

## 三、功能模块设计

### 3.1 模块总览

```
旅游管理系统
│
├── 📱 用户端（游客使用）
│   ├── 🏠 首页（景点轮播 + 热门景点推荐）
│   ├── 🗺️ 景点模块（地图展示 + 详情 + 预订）
│   ├── 🏨 酒店模块（房型展示 + 实时价格 + 预订）
│   ├── 📝 游记社区（发布 + 评论 + 点赞 + 收藏）
│   ├── 📋 订单确认（预订信息 + 支付方式 + 费用明细）
│   ├── 🛒 订单中心（我的订单 + 待支付/已支付/已完成/已取消）
│   ├── 💳 支付中心（微信/支付宝/银行卡 + 模拟支付）
│   ├── 🎟️ 优惠券中心（领取 + 我的优惠券）
│   ├── 🔔 消息通知（通知列表 + 系统公告）
│   ├── 👤 个人中心（资料 + 收藏 + 地址管理）
│   └── 💬 客服聊天（实时对话）
│
└── 🖥️ 管理端（后台运营）
    ├── 📊 数据大屏（ECharts 统计 + 卡片）
    ├── 🗺️ 景点管理（增删改查 + 上下架）
    ├── 🏨 酒店管理（房型 + 价格日历）
    ├── 📦 订单管理（状态筛选 + 退款处理）
    ├── 👥 用户管理（会员列表 + 封禁）
    ├── 📝 游记管理（审核 + 置顶/下架）
    ├── 💳 支付管理（交易统计）
    ├── 🔔 通知管理（系统通知发布）
    ├── 🎟️ 优惠券管理（创建/发放）
    └── ⚙️ 系统设置（主题切换）
```

---

## 四、数据库设计

### 4.1 ER 图概览

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│   用户    │────<│   订单    │>────│   景点    │     │   文件    │
│  users   │     │  orders  │     │  spots   │     │  files   │
└────┬─────┘     └────┬─────┘     └────┬─────┘     └──────────┘
     │                 │               │
     │            ┌────▼────┐           │
     │            │ 订单明细 │           │
     │            │order_items│           │
     │            └────┬────┘           │
     │                 │          ┌─────▼─────┐
┌────▼─────┐     ┌────▼─────┐     │ 景点门票   │
│ 收藏表    │     │  支付记录 │     │spot_tickets│
│favorites │     │payments  │     └───────────┘
└──────────┘     └──────────┘
                                   ┌───────────┐
┌───────┐                   ┌─────>│ 景点开放时间│
│ 游记  │────<│  评论    │     │ spot_schedules│
│articles│     │comments │     └─────────────┘
└───┬───┘     └─────────┘
    │                │
    │          ┌─────▼─────┐
    └─────────>│   点赞    │
               │  likes   │
               └──────────┘

┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│   酒店   │────<│   房型    │────<│ 房间库存  │     │ 用户优惠券│
│  hotels  │     │room_types│     │room_inventory│  │user_coupons│
└──────────┘     └──────────┘     └───────────┘     └────┬─────┘
                                                         │
┌──────────┐              ┌──────────┐            ┌──────▼──────┐
│ 消息通知 │              │ 系统通知  │            │   优惠券    │
│notifications│         │system_notifications│  │   coupons   │
└──────────┘              └──────────┘            └─────────────┘

┌──────────┐
│ 操作日志  │
│operation_logs│
└──────────┘
```

### 4.2 核心数据表（21张）

#### 1. 用户表 (users)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 用户ID |
| username | VARCHAR(50) | 用户名（唯一）|
| password | VARCHAR(255) | 密码(Bcrypt) |
| nickname | VARCHAR(100) | 昵称 |
| avatar | VARCHAR(500) | 头像URL |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| gender | TINYINT | 0未知 1男 2女 |
| birthday | DATE | 生日 |
| role | TINYINT | 1普通用户 2管理员 |
| status | TINYINT | 1正常 0封禁 |
| last_login | DATETIME | 最后登录时间 |

#### 2. 景点表 (spots)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 景点ID |
| name | VARCHAR(200) | 景点名称 |
| cover_image | VARCHAR(500) | 封面图 |
| city | VARCHAR(50) | 所在城市 |
| address | VARCHAR(500) | 详细地址 |
| longitude | DECIMAL(10,6) | 经度 |
| latitude | DECIMAL(10,6) | 纬度 |
| description | TEXT | 景点描述 |
| ticket_price | DECIMAL(10,2) | 门票价格 |
| open_time | VARCHAR(200) | 开放时间 |
| suggested_time | INT | 建议游览时长(小时) |
| level | VARCHAR(50) | 景区等级(5A/4A等) |
| tags | VARCHAR(500) | 标签 |
| hot_score | INT | 热度分数 |
| view_count | INT | 浏览次数 |
| status | TINYINT | 1上架 0下架 |

#### 3. 酒店表 (hotels)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 酒店ID |
| name | VARCHAR(200) | 酒店名称 |
| cover_image | VARCHAR(500) | 封面图 |
| city | VARCHAR(50) | 所在城市 |
| address | VARCHAR(500) | 详细地址 |
| longitude | DECIMAL(10,6) | 经度 |
| latitude | DECIMAL(10,6) | 纬度 |
| description | TEXT | 酒店描述 |
| star_level | TINYINT | 星级(3-5) |
| facilities | VARCHAR(500) | 设施 |
| hot_score | INT | 热度分数 |
| status | TINYINT | 1上架 0下架 |

#### 4. 房型表 (room_types)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 房型ID |
| hotel_id | BIGINT | 所属酒店 |
| name | VARCHAR(100) | 房型名称 |
| price | DECIMAL(10,2) | 单价/晚 |
| bed_type | VARCHAR(50) | 床型 |
| max_guest | INT | 最大入住人数 |
| total_rooms | INT | 房间总数 |
| amenities | VARCHAR(500) | 设施服务 |
| images | VARCHAR(1000) | 房型图片 |

#### 5. 订单表 (orders)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 订单ID |
| order_no | VARCHAR(50) | 订单号（唯一）|
| user_id | BIGINT | 用户ID |
| order_type | TINYINT | 1景点订单 2酒店订单 |
| target_id | BIGINT | 景点ID或酒店ID |
| target_name | VARCHAR(200) | 景点/酒店名称 |
| total_amount | DECIMAL(10,2) | 总金额 |
| pay_amount | DECIMAL(10,2) | 实付金额 |
| status | TINYINT | 1待支付 2已支付 3已取消 4已退款 |
| pay_time | DATETIME | 支付时间 |
| pay_channel | VARCHAR(50) | 支付渠道 |
| contact_name | VARCHAR(50) | 联系人 |
| contact_phone | VARCHAR(20) | 联系电话 |
| quantity | INT | 数量 |
| visit_date | DATE | 游玩/入住日期 |

#### 6. 订单明细表 (order_items)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 订单明细ID |
| order_id | BIGINT | 订单ID |
| item_type | TINYINT | 1景点门票 2酒店房间 |
| item_id | BIGINT | 门票ID或房型ID |
| item_name | VARCHAR(200) | 商品名称 |
| quantity | INT | 数量 |
| unit_price | DECIMAL(10,2) | 单价 |
| subtotal | DECIMAL(10,2) | 小计 |

#### 7. 支付记录表 (payments)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 支付记录ID |
| payment_no | VARCHAR(50) | 支付流水号（唯一）|
| order_no | VARCHAR(50) | 关联订单号 |
| user_id | BIGINT | 用户ID |
| amount | DECIMAL(10,2) | 支付金额 |
| pay_channel | VARCHAR(20) | 支付渠道(wechat/alipay/card) |
| pay_status | TINYINT | 0待支付 1成功 2失败 3退款 |
| pay_time | DATETIME | 支付时间 |
| transaction_id | VARCHAR(100) | 第三方交易号 |
| extra_data | TEXT | 扩展数据 |
| refund_amount | DECIMAL(10,2) | 退款金额 |
| refund_time | DATETIME | 退款时间 |

#### 8. 景点门票类型表 (spot_tickets)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 门票类型ID |
| spot_id | BIGINT | 景点ID |
| name | VARCHAR(100) | 门票名称 |
| description | VARCHAR(500) | 门票描述 |
| price | DECIMAL(10,2) | 价格 |
| stock | INT | 库存(-1不限) |
| valid_days | INT | 有效天数 |
| refundable | TINYINT | 是否可退 |
| status | TINYINT | 1上架 0下架 |

#### 9. 景点开放时间表 (spot_schedules)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 开放时间ID |
| spot_id | BIGINT | 景点ID |
| day_of_week | TINYINT | 星期几(1-7) |
| start_time | VARCHAR(10) | 开始时间 |
| end_time | VARCHAR(10) | 结束时间 |
| is_closed | TINYINT | 是否闭园 |
| description | VARCHAR(200) | 特殊说明 |

#### 10. 房间库存表 (room_inventory)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 库存ID |
| room_type_id | BIGINT | 房型ID |
| inventory_date | DATE | 库存日期 |
| available_rooms | INT | 可用房间数 |
| price | DECIMAL(10,2) | 当日价格 |

#### 11. 游记表 (articles)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 游记ID |
| user_id | BIGINT | 作者ID |
| title | VARCHAR(200) | 标题 |
| cover_image | VARCHAR(500) | 封面图 |
| content | TEXT | 正文(Markdown) |
| spot_ids | VARCHAR(500) | 关联景点ID |
| tags | VARCHAR(200) | 标签 |
| view_count | INT | 浏览次数 |
| like_count | INT | 点赞数 |
| collect_count | INT | 收藏数 |
| comment_count | INT | 评论数 |
| status | TINYINT | 0待审核 1已发布 2已下架 |
| is_top | TINYINT | 是否置顶 |

#### 12. 评论表 (comments)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 评论ID |
| user_id | BIGINT | 评论者ID |
| target_type | TINYINT | 1景点 2酒店 3游记 4订单 |
| target_id | BIGINT | 目标ID |
| parent_id | BIGINT | 父评论ID(0为顶级) |
| content | VARCHAR(1000) | 评论内容 |
| rating | TINYINT | 评分(1-5) |
| like_count | INT | 点赞数 |
| status | TINYINT | 1正常 0删除 |

#### 13. 收藏表 (favorites)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 收藏ID |
| user_id | BIGINT | 用户ID |
| target_type | TINYINT | 1景点 2酒店 |
| target_id | BIGINT | 目标ID |

#### 14. 点赞表 (likes)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 点赞ID |
| user_id | BIGINT | 用户ID |
| target_type | TINYINT | 1景点 2酒店 3游记 |
| target_id | BIGINT | 目标ID |

#### 15. 消息通知表 (notifications)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 通知ID |
| user_id | BIGINT | 接收用户ID |
| type | VARCHAR(50) | 通知类型(1系统/2订单/3活动) |
| title | VARCHAR(200) | 通知标题 |
| content | TEXT | 通知内容 |
| related_id | BIGINT | 关联ID(订单/景点等) |
| is_read | INT | 是否已读(0未读/1已读) |
| read_time | DATETIME | 阅读时间 |
| created_at | DATETIME | 创建时间 |

#### 16. 系统通知表 (system_notifications)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 系统通知ID |
| title | VARCHAR(200) | 通知标题 |
| content | TEXT | 通知内容 |
| type | INT | 类型(1系统/2订单/3活动) |
| target_type | INT | 发送范围(1全部/2指定用户) |
| target_user_ids | TEXT | 指定用户ID列表(JSON) |
| status | INT | 状态(0草稿/1已发布) |
| published_at | DATETIME | 发布时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### 17. 文件表 (files)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 文件ID |
| file_name | VARCHAR(255) | 原始文件名 |
| file_path | VARCHAR(500) | 存储路径 |
| file_url | VARCHAR(500) | 访问URL |
| file_size | BIGINT | 文件大小 |
| file_type | VARCHAR(50) | 文件MIME类型 |
| file_ext | VARCHAR(20) | 文件扩展名 |
| storage_type | VARCHAR(20) | 存储类型(local) |
| user_id | BIGINT | 上传用户ID |
| biz_type | VARCHAR(50) | 业务类型 |
| biz_id | BIGINT | 关联业务ID |

#### 18. 优惠券表 (coupons)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 优惠券ID |
| name | VARCHAR(100) | 优惠券名称 |
| description | VARCHAR(500) | 使用说明 |
| type | TINYINT | 1满减券 2折扣券 3兑换券 |
| discount_value | DECIMAL(10,2) | 优惠值 |
| min_amount | DECIMAL(10,2) | 最低消费金额 |
| max_discount | DECIMAL(10,2) | 最高优惠金额 |
| total_count | INT | 发放总数量 |
| remain_count | INT | 剩余数量 |
| per_user_limit | INT | 每人限领数量 |
| applicable_type | TINYINT | 1全场 2景点 3酒店 |
| applicable_ids | VARCHAR(500) | 适用ID列表 |
| valid_start | DATETIME | 有效期开始 |
| valid_end | DATETIME | 有效期结束 |
| status | TINYINT | 1有效 0无效 |

#### 19. 用户优惠券表 (user_coupons)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 用户优惠券ID |
| user_id | BIGINT | 用户ID |
| coupon_id | BIGINT | 优惠券ID |
| coupon_name | VARCHAR(100) | 优惠券名称快照 |
| coupon_type | TINYINT | 优惠券类型快照 |
| discount_value | DECIMAL(10,2) | 优惠值快照 |
| min_amount | DECIMAL(10,2) | 最低消费快照 |
| max_discount | DECIMAL(10,2) | 最高优惠快照 |
| order_no | VARCHAR(50) | 关联订单号 |
| status | TINYINT | 0未使用 1已使用 2已过期 |
| receive_time | DATETIME | 领取时间 |
| use_time | DATETIME | 使用时间 |
| expire_time | DATETIME | 过期时间 |

#### 20. 用户地址表 (user_addresses)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 地址ID |
| user_id | BIGINT | 用户ID |
| contact_name | VARCHAR(50) | 联系人姓名 |
| contact_phone | VARCHAR(20) | 联系人电话 |
| province | VARCHAR(50) | 省份 |
| city | VARCHAR(50) | 城市 |
| district | VARCHAR(50) | 区县 |
| detail_address | VARCHAR(500) | 详细地址 |
| is_default | TINYINT | 是否默认地址 |
| tag | VARCHAR(50) | 地址标签 |

#### 21. 操作日志表 (operation_logs)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 日志ID |
| user_id | BIGINT | 操作用户ID |
| username | VARCHAR(50) | 操作用户名 |
| module | VARCHAR(50) | 模块 |
| action | VARCHAR(50) | 操作类型 |
| description | VARCHAR(500) | 操作描述 |
| request_method | VARCHAR(10) | 请求方法 |
| request_url | VARCHAR(500) | 请求URL |
| request_params | TEXT | 请求参数 |
| response_data | TEXT | 响应数据 |
| ip_address | VARCHAR(50) | IP地址 |
| user_agent | VARCHAR(500) | 浏览器标识 |
| status | TINYINT | 1成功 0失败 |
| error_message | VARCHAR(500) | 错误信息 |
| duration_ms | BIGINT | 耗时(毫秒) |

---

## 五、API 接口设计

### 5.1 用户认证模块

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/auth/register` | 用户注册 |
| POST | `/api/v1/auth/login` | 用户登录 |
| GET | `/api/v1/auth/info` | 获取用户信息 |
| PUT | `/api/v1/auth/password` | 修改密码 |

### 5.2 景点模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/spots` | 景点列表（分页+筛选）|
| GET | `/api/v1/spots/{id}` | 景点详情 |
| GET | `/api/v1/spots/hot` | 热门景点 |
| GET | `/api/v1/spots/{id}/tickets` | 景点门票列表 |
| GET | `/api/v1/spots/{id}/schedules` | 开放时间 |

### 5.3 酒店模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/hotels` | 酒店列表（分页+筛选）|
| GET | `/api/v1/hotels/{id}` | 酒店详情 |
| GET | `/api/v1/hotels/{id}/rooms` | 房型列表 |
| GET | `/api/v1/hotels/{id}/rooms/price` | 房型价格日历 |

### 5.4 订单模块

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/order/create/spot` | 创建景点订单 |
| POST | `/api/v1/order/create/hotel` | 创建酒店订单 |
| POST | `/api/v1/order/{orderNo}/pay` | 模拟支付 |
| GET | `/api/v1/order/list` | 订单列表 |
| GET | `/api/v1/order/{orderNo}` | 订单详情 |
| DELETE | `/api/v1/order/{orderNo}` | 取消订单 |

### 5.5 支付模块

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/payment/create` | 创建支付 |
| GET | `/api/v1/payment/status/{orderNo}` | 支付状态 |
| POST | `/api/v1/payment/{paymentNo}/callback` | 支付回调 |
| DELETE | `/api/v1/payment/{paymentNo}` | 取消支付 |

### 5.6 游记模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/articles` | 游记列表 |
| GET | `/api/v1/articles/{id}` | 游记详情 |
| POST | `/api/v1/articles` | 发布游记 |
| PUT | `/api/v1/articles/{id}` | 更新游记 |
| DELETE | `/api/v1/articles/{id}` | 删除游记 |

### 5.7 评论/点赞/收藏

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/comments` | 评论列表 |
| POST | `/api/v1/comments` | 添加评论 |
| DELETE | `/api/v1/comments/{id}` | 删除评论 |
| POST | `/api/v1/favorites` | 添加收藏 |
| DELETE | `/api/v1/favorites` | 取消收藏 |
| GET | `/api/v1/favorites` | 收藏列表 |
| POST | `/api/v1/likes` | 点赞 |
| DELETE | `/api/v1/likes` | 取消点赞 |

### 5.8 优惠券模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/coupon/available` | 可领取优惠券 |
| POST | `/api/v1/coupon/receive/{id}` | 领取优惠券 |
| GET | `/api/v1/coupon/my` | 我的优惠券 |
| POST | `/api/v1/coupon/calculate` | 计算优惠 |
| GET | `/api/v1/coupon/canUse/{id}` | 验证可用性 |

### 5.9 消息通知模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/notification/list` | 通知列表 |
| GET | `/api/v1/notification/unread/count` | 未读数量 |
| PUT | `/api/v1/notification/{id}/read` | 标记已读 |
| PUT | `/api/v1/notification/read/all` | 全部已读 |
| DELETE | `/api/v1/notification/{id}` | 删除通知 |

### 5.10 管理员通知模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/admin/notification/list` | 分页查询通知列表 |
| GET | `/api/v1/admin/notification/{id}` | 获取通知详情 |
| POST | `/api/v1/admin/notification/save` | 创建/更新通知 |
| POST | `/api/v1/admin/notification/{id}/publish` | 发布通知 |
| POST | `/api/v1/admin/notification/draft` | 保存草稿 |
| DELETE | `/api/v1/admin/notification/{id}` | 删除通知 |

### 5.11 文件模块

### 5.11 文件模块

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/file/upload` | 单文件上传 |
| POST | `/api/v1/file/upload/multiple` | 多文件上传 |
| DELETE | `/api/v1/file/{id}` | 删除文件 |

### 5.12 搜索模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/search` | 全局搜索 |
| GET | `/api/v1/search/spots` | 搜索景点 |
| GET | `/api/v1/search/hotels` | 搜索酒店 |
| GET | `/api/v1/search/articles` | 搜索游记 |

### 5.13 统计模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/stats/overview` | 数据概览 |
| GET | `/api/v1/stats/trend` | 趋势数据 |

---

## 六、前端页面清单

### 6.1 用户端页面

| 页面 | 路由 | 说明 |
|------|------|------|
| 首页 | `/` | 轮播图 + 热门景点 + 热门酒店 |
| 登录 | `/login` | 用户登录 |
| 注册 | `/register` | 用户注册 |
| 景点列表 | `/spots` | 景点筛选 + 分页 |
| 景点详情 | `/spots/:id` | 景点信息 + 门票 + 地图 |
| 酒店列表 | `/hotels` | 酒店筛选 + 分页 |
| 酒店详情 | `/hotels/:id` | 房型 + 价格日历 |
| 订单确认 | `/book/:type/:id` | 预订信息 + 支付方式 |
| 订单中心 | `/orders` | 订单列表（全部/待支付/已完成）|
| 支付页 | `/pay/:orderNo` | 支付方式选择 |
| 游记列表 | `/articles` | 游记列表 |
| 游记详情 | `/articles/:id` | 游记正文 + 评论 |
| 发布游记 | `/articles/create` | Markdown 编辑器 |
| 优惠券 | `/coupons` | 领取 + 我的优惠券 |
| 消息通知 | `/notifications` | 通知列表 |
| 个人中心 | `/profile` | 个人信息 + 地址 |
| 客服聊天 | `/chat` | WebSocket 实时聊天 |

### 6.2 管理端页面

| 页面 | 路由 | 说明 |
|------|------|------|
| Dashboard | `/admin/dashboard` | 数据大屏 |
| 景点管理 | `/admin/spots` | 景点增删改查 |
| 酒店管理 | `/admin/hotels` | 酒店增删改查 |
| 订单管理 | `/admin/orders` | 订单状态管理 |
| 用户管理 | `/admin/users` | 用户列表 |
| 游记管理 | `/admin/articles` | 游记审核 |
| 支付管理 | `/admin/payments` | 交易统计 |
| 通知管理 | `/admin/notifications` | 系统通知 |
| 优惠券 | `/admin/coupons` | 优惠券管理 |
| 系统设置 | `/admin/settings` | 主题设置 |

---

## 七、UI 设计特色

### 7.1 配色体系

| 配色 | 色值 | 用途 |
|------|------|------|
| 主色蓝 | `#3b82f6` | 按钮、图标、选中态 |
| 辅色橙 | `#f97316` | 价格、重点强调、渐变 |
| 渐变蓝橙 | `linear-gradient(135deg, #3b82f6, #f97316)` | 按钮、头像环 |
| 背景浅蓝 | `#f0f4ff` | 页面背景 |
| 背景浅橙 | `#fff8f5` | 页面背景渐变 |
| 背景浅紫 | `#f5f0ff` | 页面背景渐变 |
| 文字深 | `#1e293b` | 标题、正文 |
| 文字次 | `#64748b` | 副标题、标签 |
| 文字弱 | `#94a3b8` | 占位符、提示语 |

### 7.2 支付页面设计（/book/:type/:id）

```
┌────────────────────────────────────────────────────────────┐
│  [统一大白色卡片 - 28px圆角 - 阴影柔和]                    │
│  ┌─────────────────────────────┬───────────────────────┐   │
│  │ 左侧 55%                   │ 右侧 45%              │   │
│  │ 预订信息                    │ 费用明细              │   │
│  │  · 商品卡片(180x140图片)   │  · 门票 × 数量       │   │
│  │  · 游玩日期/人数选择器     │  · 优惠券抵扣         │   │
│  │  · 联系人表单              │  · 合计金额           │   │
│  │  · 优惠券展开选择器        │ 支付方式              │   │
│  │     (虚线分隔 #e8eef8)    │  · 微信/支付宝/银行卡 │   │
│  │                            │  [渐变按钮]立即支付   │   │
│  └─────────────────────────────┴───────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

### 7.3 AI 聊天页面设计（/chat）

- **顶部**：旋转渐变头像光环 + 在线绿点指示
- **欢迎页**：太阳光芒旋转动画 + 浮动装饰图标（✈️☀️🍜🏨⛰️）
- **气泡**：AI 白色气泡（左对齐）| 用户橙色渐变气泡（右对齐）
- **快捷卡片**：悬停上浮 + 箭头跟随动画
- **发送按钮**：灰 → 蓝橙渐变（激活态）
- **背景**：浅蓝 + 浅橙 + 浅紫 三色渐变 + 装饰圆形

### 7.4 管理员通知面板

- **触发**：顶部导航栏 🔔 按钮
- **展示**：右侧抽屉（el-drawer）380px
- **内容**：通知列表 + 时间相对显示（刚刚/5分钟前/1小时前）
- **状态**：未读蓝底 + 左侧蓝点；已读白色
- **操作**：点击标记已读 / 一键全部已读 / 查看全部跳转

---

## 八、项目结构

### 7.1 后端结构

```
backend/
├── src/main/java/com/travel/
│   ├── TravelSystemApplication.java
│   ├── common/
│   │   ├── GlobalExceptionHandler.java
│   │   └── Result.java
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── DataInitializer.java
│   │   ├── MyBatisPlusConfig.java
│   │   ├── SecurityConfig.java
│   │   └── WebMvcConfig.java
│   ├── controller/
│   │   ├── AIController.java
│   │   ├── AdminNotificationController.java
│   │   ├── AdminPaymentController.java
│   │   ├── ArticleAdminController.java
│   │   ├── ArticleController.java
│   │   ├── AuthController.java
│   │   ├── CouponController.java
│   │   ├── FileController.java
│   │   ├── HotelAdminController.java
│   │   ├── HotelController.java
│   │   ├── NotificationController.java
│   │   ├── OrderController.java
│   │   ├── PaymentController.java
│   │   ├── RoomInventoryController.java
│   │   ├── SearchController.java
│   │   ├── SpotController.java
│   │   ├── SpotTicketController.java
│   │   ├── StatisticsController.java
│   │   └── UserController.java
│   ├── dto/
│   │   ├── AuthResponse.java
│   │   ├── LoginDTO.java
│   │   ├── PaymentVO.java
│   │   ├── RegisterDTO.java
│   │   └── UserVO.java
│   ├── entity/
│   │   ├── Article.java
│   │   ├── Comment.java
│   │   ├── Coupon.java
│   │   ├── Favorite.java
│   │   ├── File.java
│   │   ├── Hotel.java
│   │   ├── Like.java
│   │   ├── Notification.java
│   │   ├── OperationLog.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   ├── RoomInventory.java
│   │   ├── RoomType.java
│   │   ├── Spot.java
│   │   ├── SpotSchedule.java
│   │   ├── SpotTicket.java
│   │   ├── SystemNotification.java
│   │   ├── User.java
│   │   ├── UserAddress.java
│   │   └── UserCoupon.java
│   ├── mapper/
│   │   └── [17个Mapper接口]
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtUtils.java
│   ├── mq/
│   │   ├── NotificationMessageConsumer.java
│   │   ├── NotificationMessageProducer.java
│   │   ├── OrderMessageConsumer.java
│   │   └── OrderMessageProducer.java
│   └── service/
│       ├── AIService.java
│       ├── ArticleService.java
│       ├── AuthService.java
│       ├── CouponService.java
│       ├── FileService.java
│       ├── HotelService.java
│       ├── NotificationService.java
│       ├── OrderService.java
│       ├── PaymentService.java
│       ├── RoomInventoryService.java
│       ├── SearchService.java
│       ├── SpotService.java
│       ├── SpotTicketService.java
│       ├── StatisticsService.java
│       ├── UserAddressService.java
│       └── UserService.java
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    └── schema.sql
```

### 7.2 前端结构

```
frontend/
├── src/
│   ├── api/
│   │   ├── adminNotification.ts
│   │   ├── admin.ts
│   │   ├── ai.ts
│   │   ├── article.ts
│   │   ├── auth.ts
│   │   ├── axios.ts
│   │   ├── coupon.ts
│   │   ├── notification.ts
│   │   ├──
---

## 九、预订支付完整流程

### 8.1 流程图

```
┌─────────────────────────────────────────────────────────┐
│  1. 用户在景点/酒店详情页点击「立即预订」                    │
└───────────────────────┬─────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  2. 跳转到「订单确认页」(/book/:type/:id)               │
│      - 显示商品信息(封面、名称、地址、等级)                  │
│      - 填写预订信息(日期、数量、联系人)                     │
│      - 选择支付方式(微信/支付宝/银行卡)                    │
│      - 查看费用明细(原价、优惠、应付总额)                   │
└───────────────────────┬─────────────────────────────────┘
                          ↓
              ┌──────────┴──────────┐
              ↓                      ↓
      ┌───────────────┐      ┌───────────────┐
      │  🎯 确认支付  │      │   ❌ 取消预订  │
      └───────┬───────┘      └───────┬───────┘
              ↓                      ↓
      ┌───────────────┐      ┌───────────────┐
      │  创建订单     │      │  创建订单     │
      │  执行模拟支付  │      │  (不支付)    │
      └───────┬───────┘      └───────┬───────┘
              ↓                      ↓
      ┌───────────────┐      ┌───────────────┐
      │  跳转订单页   │      │  跳转订单页   │
      │  显示: 已支付  │      │  显示: 待支付  │
      └───────────────┘      └───────────────┘
```

### 8.2 订单状态说明

| 状态码 | 名称 | 显示 |
|--------|------|------|
| 1 | 待支付 | 待支付 ⚠️ |
| 2 | 已支付 | 已支付 ✅ |
| 3 | 已取消 | 已取消 ❌ |
| 4 | 已退款 | 已退款 💰 |

---

## 十、项目运行状态

### 9.1 服务状态

| 服务 | 端口 | 状态 |
|------|------|------|
| 后端 (Spring Boot) | http://localhost:8080 | ✅ 运行中 |
| 前端 (Vite) | http://localhost:5173 | ✅ 运行中 |

### 9.2 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| demo123 | 123456 | 普通用户 |
| admin | admin123 | 管理员 |

### 9.3 访问地址

| 页面 | 地址 |
|------|------|
| 用户端首页 | http://localhost:5173 |
| 管理后台 | http://localhost:5173/admin |

---

## 十一、毕设答辩亮点

### 10.1 技术亮点

| 序号 | 技术亮点 | 实现方式 |
|------|---------|---------|
| 1 | **Vue3 + TypeScript** | 严格类型检查 + 泛型应用 |
| 2 | **Spring Boot 3** | 最新 Java 框架 + Security 6 |
| 3 | **JWT 无状态认证** | Token 过期自动刷新 |
| 4 | **MyBatis-Plus** | 简化 CRUD + 分页插件 |
| 5 | **ECharts 数据大屏** | 折线图 + 柱状图 + 饼图 |
| 6 | **高德地图集成** | 景点位置展示 |
| 7 | **GSAP 动画** | 首页轮播 + 过渡动画 |
| 8 | **Lottie 动画** | JSON 动画播放器 |
| 9 | **模拟支付流程** | 完整下单 → 支付 → 回调 |
| 10 | **优惠券系统** | 满减 + 折扣 + 兑换 |
| 11 | **文件上传** | 本地存储 + 分类管理 |
| 12 | **WebSocket 聊天** | 实时客服对话 |

### 10.2 架构亮点

| 序号 | 架构亮点 | 说明 |
|------|---------|------|
| 1 | 分层架构清晰 | Controller → Service → Mapper |
| 2 | 前后端分离 | RESTful API + Vue3 SPA |
| 3 | 统一响应格式 | Result<T> 泛型封装 |
| 4 | 全局异常处理 | GlobalExceptionHandler |
| 5 | 逻辑删除设计 | deleted 字段软删除 |

### 10.3 业务亮点

| 序号 | 业务亮点 | 说明 |
|------|---------|------|
| 1 | 完整的电商闭环 | 浏览 → 下单 → 支付 → 核销 |
| 2 | 景点门票多类型 | 成人票/学生票/老人票 |
| 3 | 酒店房型管理 | 实时库存 + 价格日历 |
| 4 | 游记社区 | Markdown 编辑 + 评论点赞 |
| 5 | 消息通知中心 | 站内通知 + 系统公告 |

---

## 十三、Redis + Caffeine 多级缓存

### 12.1 缓存架构

```
┌─────────────────────────────────────────────────────────┐
│                     请求进来                              │
└─────────────────────────┬───────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│            一级缓存 Caffeine（本地）                      │
│            • 热点数据，内存访问，极快                     │
│            • 最大 1000 条，10分钟过期                     │
│            • 自动 recordStats 统计                       │
└─────────────────────────┬───────────────────────────────┘
                          ↓ 未命中
┌─────────────────────────────────────────────────────────┐
│            二级缓存 Redis（分布式）                       │
│            • 跨服务共享                                  │
│            • 独立 TTL 配置                               │
│            • 支持事务                                   │
└─────────────────────────┬───────────────────────────────┘
                          ↓ 未命中
┌─────────────────────────────────────────────────────────┐
│                      MySQL 数据库                         │
└─────────────────────────────────────────────────────────┘
```

### 12.2 缓存策略表

| 缓存Key | 说明 | TTL | 淘汰策略 |
|---------|------|-----|---------|
| spots | 景点列表 | 5分钟 | 时间淘汰 |
| spotDetail | 景点详情 | 10分钟 | 时间淘汰 |
| hotSpots | 热门景点 | 1小时 | 定时刷新 |
| hotels | 酒店列表 | 5分钟 | 时间淘汰 |
| hotelDetail | 酒店详情 | 10分钟 | 时间淘汰 |
| articles | 游记列表 | 5分钟 | 时间淘汰 |
| articleDetail | 游记详情 | 30分钟 | 时间淘汰 |
| userInfo | 用户信息 | 15分钟 | 时间淘汰 |
| orderCache | 订单缓存 | 30分钟 | 时间淘汰 |
| searchHotWords | 搜索热词 | 1小时 | 定时刷新 |
| statsOverview | 统计概览 | 5分钟 | 时间淘汰 |

### 12.3 使用示例

```java
@Cacheable(value = "spotDetail", key = "#id", cacheManager = "redisCacheManager")
public Spot getSpotById(Long id) {
    return spotMapper.selectById(id);
}

@CacheEvict(value = "spots", allEntries = true)
public void updateSpot(Spot spot) {
    spotMapper.updateById(spot);
}
```

---

## 十四、RabbitMQ 消息队列

### 13.1 消息流转图

```
┌──────────────┐     ┌─────────────────────┐     ┌──────────────────────┐
│  订单支付成功  │ ──> │  order.exchange     │ ──> │  order.notify.queue  │
└──────────────┘     │  (topic交换机)        │     │  → 发送站内通知      │
                    │                     │     │  → 更新搜索索引      │
                    │                     │     │  → 发送邮件(高价值)   │
                    └─────────────────────┘     └──────────────────────┘

┌──────────────┐     ┌─────────────────────────┐     ┌──────────────────┐
│  订单取消    │ ──> │  order.cancel.queue     │ ──> │  恢复库存        │
└──────────────┘     └─────────────────────────┘     └──────────────────┘

┌──────────────┐     ┌─────────────────────────┐     ┌──────────────────┐
│  订单退款    │ ──> │  order.refund.queue     │ ──> │  更新统计        │
└──────────────┘     └─────────────────────────┘     └──────────────────┘

┌──────────────────────────┐  ┌─────────────────────────┐  ┌──────────────────────┐
│  管理员发布系统通知        │─>│  notification.exchange  │─>│  notification.queue  │
│  (全部/指定用户)          │  │  (direct交换机)          │  │  → 批量插入通知      │
└──────────────────────────┘  └─────────────────────────┘  │  → Redis幂等去重     │
                                                            └──────────────────────┘
```

### 13.2 消息确认机制

- **手动 ACK**：消费成功后手动确认
- **失败重试**：最多重试 3 次
- **死信队列**：失败消息进入 DLQ

### 13.3 队列说明

| 队列 | 路由Key | 说明 |
|------|---------|------|
| order.create.queue | order.created | 订单创建 |
| order.notify.queue | order.paid | 订单支付成功 |
| order.cancel.queue | order.canceled | 订单取消 |
| order.refund.queue | order.refunded | 订单退款 |
| notification.queue | notification.create | 发送通知 |
| payment.success.queue | payment.success | 支付成功 |
| search.index.queue | - | 搜索索引更新（广播）|
| email.queue | email.send | 发送邮件 |

---

## 十五、MinIO 对象存储

### 14.1 存储桶设计

| 桶名称 | 用途 | 访问级别 |
|--------|------|---------|
| travel-system | 默认桶 | 私有 |
| avatar | 头像存储 | 公开读 |
| spots | 景点图片 | 公开读 |
| hotels | 酒店图片 | 公开读 |
| articles | 游记图片 | 公开读 |
| orders | 订单附件 | 私有 |

### 14.2 文件路径规范

```
{桶名}/{类型}/{年}/{月}/{日}/{UUID}.{扩展名}

示例:
travel-system/avatar/2026/04/07/550e8400-e29b-41d4-a716-446655440000.jpg
travel-system/spots/2026/04/07/6ba7b810-9dad-11d1-80b4-00c04fd430c8.png
```

### 14.3 预签名URL

用于前端直传，不暴露 secret-key：

```java
// 生成预签名上传URL（有效期1小时）
String uploadUrl = ossService.getPresignedUploadUrl(bucket, fileName, 3600);

// 前端直接上传到 MinIO
fetch(uploadUrl, { method: 'PUT', body: file })
```

---

## 十六、Spring Retry 重试机制

### 15.1 重试策略

| 场景 | 最大次数 | 初始延迟 | 退避倍数 |
|------|---------|---------|---------|
| 支付回调 | 3 | 1秒 | 2.0x |
| 通知发送 | 3 | 0.5秒 | 2.0x |
| 搜索索引更新 | 3 | 1秒 | 1.0x |
| 邮件发送 | 3 | 2秒 | 1.5x |

### 15.2 使用示例

```java
@Retryable(
    value = Exception.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
public boolean processPaymentCallback(String orderNo) {
    // 业务逻辑
}

@Recover
public boolean recoverProcessPaymentCallback(Exception e, String orderNo) {
    log.error("支付回调处理失败: orderNo={}", orderNo, e);
    // 记录日志 + 发送告警
    return false;
}
```

---

## 十七、Knife4j API 文档

### 16.1 访问地址

| 地址 | 说明 |
|------|------|
| http://localhost:8080/doc.html | Knife4j 增强版 UI |
| http://localhost:8080/swagger-ui/index.html | Swagger UI |
| http://localhost:8080/v3/api-docs | OpenAPI 3.0 JSON |

### 16.2 注解示例

```java
@Operation(summary = "创建景点订单", description = "创建景点门票预订订单")
@ApiImplicitParams({
    @ApiImplicitParam(name = "spotId", value = "景点ID", required = true),
    @ApiImplicitParam(name = "quantity", value = "数量", required = true)
})
@PostMapping("/create/spot")
public Result<OrderVO> createSpotOrder(@RequestBody @Valid CreateOrderDTO dto) {
    return Result.success(orderService.createSpotOrder(dto));
}
```

---

## 十八、Docker 一键部署

### 17.1 启动所有服务

```bash
cd docker

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend

# 停止所有服务
docker-compose down
```

### 17.2 启动可选工具

```bash
# 启动包含 phpMyAdmin 的服务
docker-compose --profile tools up -d
```

### 17.3 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx | 80, 443 | 前端入口 |
| Backend | 8080 | 后端 API |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672 | AMQP |
| RabbitMQ UI | 15672 | 管理界面 |
| MinIO API | 9000 | 对象存储 |
| MinIO Console | 9001 | 管理界面 |
| phpMyAdmin | 8081 | 数据库管理（可选）|

---

## 十九、版本历史

| 版本 | 日期 | 更新内容 |
|------|------|---------|
| V1.0 | 早期 | 基础 CRUD 功能 |
| V2.0 | 2026-04-05 | 新增 13 张数据表，完善业务模块 |
| V2.2 | 2026-04-06 | 预订支付完整流程，Admin 管理端重构 |
| V3.0 | 2026-04-07 | 文档与实际实现同步 |
| V3.1 | 2026-04-07 | 新增 Redis+Caffeine 缓存、RabbitMQ 消息队列、MinIO 对象存储、Spring Retry、Knife4j API 文档、Docker Compose |
| V3.2 | 2026-04-08 | 支付页面蓝橙撞色UI重设计、AI聊天页面活力风格重设计、管理员通知系统完整实现（CRUD+MQ推送+通知面板） |

---

**文档版本：V3.2 | 最后更新：2026-04-08**
