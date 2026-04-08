# 智慧旅游管理系统 - 改进说明文档

> 版本：V2.0 增强版 | 日期：2026-04-05 | 状态：✅ 主要模块已完成

---

## 📋 更新概述

本次更新根据 V2.0 设计文档对系统进行了全面增强，主要包含：

1. **数据库层** - 新增 13 张数据表，完善数据模型
2. **后端模块** - 新增 5 个核心业务模块
3. **前端页面** - 新增 3 个用户页面 + 5 个 API 文件
4. **配置增强** - Docker 部署、Nginx 配置、生产环境配置

---

## 🗄️ 一、数据库层改进

### 1.1 字段修正
- `room_types.amenities` 字段已确认无拼写问题，注释完整

### 1.2 新增数据表（13 张）

| 表名 | 说明 | 状态 |
|------|------|------|
| `user_addresses` | 用户地址表 | ✅ |
| `spot_tickets` | 景点门票类型表 | ✅ |
| `spot_schedules` | 景点开放时间表 | ✅ |
| `room_inventory` | 房间库存表 | ✅ |
| `order_items` | 订单明细表 | ✅ |
| `payments` | 支付记录表 | ✅ |
| `likes` | 点赞表 | ✅ |
| `notifications` | 消息通知表 | ✅ |
| `system_notifications` | 系统通知表 | ✅ |
| `files` | 文件表 | ✅ |
| `coupons` | 优惠券表 | ✅ |
| `user_coupons` | 用户优惠券表 | ✅ |
| `operation_logs` | 操作日志表 | ✅ |

### 1.3 初始化数据
- 景点门票类型数据（6 条）
- 景点开放时间数据（8 条）
- 房间库存数据（7 条）
- 优惠券数据（4 张优惠券）
- 用户优惠券数据（2 条领取记录）
- 系统通知数据（2 条）

---

## ⚙️ 二、后端模块改进

### 2.1 支付模块 (Payment)

**文件清单：**
```
entity/Payment.java          - 支付记录实体
mapper/PaymentMapper.java    - 支付 Mapper
service/PaymentService.java  - 服务接口
service/impl/PaymentServiceImpl.java - 实现类
controller/PaymentController.java - 控制器
```

**API 接口：**
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/payment/create` | 创建支付订单 |
| GET | `/api/v1/payment/status/{orderNo}` | 获取支付状态 |
| POST | `/api/v1/payment/{paymentNo}/callback` | 支付回调 |
| DELETE | `/api/v1/payment/{paymentNo}` | 取消支付 |

**功能特性：**
- 支持微信/支付宝/银行卡三种支付渠道
- 生成唯一支付流水号
- 模拟支付回调处理
- 支付状态管理（待支付/支付成功/支付失败/已退款）

### 2.2 文件上传模块 (File)

**文件清单：**
```
entity/File.java
mapper/FileMapper.java
service/FileService.java
service/impl/FileServiceImpl.java
controller/FileController.java
config/WebMvcConfig.java
```

**API 接口：**
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/file/upload` | 单文件上传 |
| POST | `/api/v1/file/upload/multiple` | 多文件上传 |
| DELETE | `/api/v1/file/{id}` | 删除文件 |

**功能特性：**
- 本地存储（`uploads/` 目录）
- 按业务类型分类存储
- 支持头像/游记/景点/酒店等多种场景
- 文件大小/类型/扩展名记录

### 2.3 消息通知模块 (Notification)

**文件清单：**
```
entity/Notification.java
entity/SystemNotification.java
mapper/NotificationMapper.java
mapper/SystemNotificationMapper.java
service/NotificationService.java
service/impl/NotificationServiceImpl.java
controller/NotificationController.java
```

**API 接口：**
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/notification/list` | 获取通知列表 |
| GET | `/api/v1/notification/unread/count` | 未读数量 |
| PUT | `/api/v1/notification/{id}/read` | 标记已读 |
| PUT | `/api/v1/notification/read/all` | 全部已读 |
| DELETE | `/api/v1/notification/{id}` | 删除通知 |

**通知类型：**
- `order_pay` - 订单支付
- `order_cancel` - 订单取消
- `refund` - 退款通知
- `article_comment` - 游记评论
- `article_like` - 游记点赞
- `system` - 系统通知

### 2.4 优惠券模块 (Coupon)

**文件清单：**
```
entity/Coupon.java
entity/UserCoupon.java
mapper/CouponMapper.java
mapper/UserCouponMapper.java
service/CouponService.java
service/impl/CouponServiceImpl.java
controller/CouponController.java
```

**API 接口：**
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/coupon/available` | 可领取优惠券 |
| POST | `/api/v1/coupon/receive/{couponId}` | 领取优惠券 |
| GET | `/api/v1/coupon/my` | 我的优惠券 |
| POST | `/api/v1/coupon/calculate` | 计算优惠金额 |
| GET | `/api/v1/coupon/canUse/{couponId}` | 验证是否可用 |

**优惠券类型：**
- `type=1` 满减券（满 X 减 Y）
- `type=2` 折扣券（X 折，最高优惠 Y）
- `type=3` 兑换券

**适用范围：**
- `applicableType=1` 全场通用
- `applicableType=2` 仅限景点
- `applicableType=3` 仅限酒店

### 2.5 搜索模块 (Search)

**文件清单：**
```
service/SearchService.java
service/impl/SearchServiceImpl.java
controller/SearchController.java
```

**API 接口：**
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/search` | 全局搜索 |
| GET | `/api/v1/search/spots` | 搜索景点 |
| GET | `/api/v1/search/hotels` | 搜索酒店 |
| GET | `/api/v1/search/articles` | 搜索游记 |

**搜索特性：**
- 支持景点名称/城市/标签搜索
- 支持酒店名称/城市搜索
- 支持游记标题/标签搜索
- 按热度排序

---

## 🎨 三、前端改进

### 3.1 新增页面

| 页面 | 路由 | 说明 | 状态 |
|------|------|------|------|
| 支付页面 | `/pay/:orderNo` | 订单支付 | ✅ |
| 优惠券中心 | `/coupons` | 可领取优惠券 | ✅ |
| 我的优惠券 | (标签切换) | 我的优惠券列表 | ✅ |
| 消息通知 | `/notifications` | 通知列表 | ✅ |

### 3.2 新增 API 文件

| 文件 | 说明 | 状态 |
|------|------|------|
| `api/payment.ts` | 支付相关 API | ✅ |
| `api/notification.ts` | 通知相关 API | ✅ |
| `api/coupon.ts` | 优惠券相关 API | ✅ |
| `api/file.ts` | 文件上传 API | ✅ |
| `api/search.ts` | 搜索 API | ✅ |

### 3.3 路由更新
- 新增 3 个路由：`/pay/:orderNo`、`/coupons`、`/notifications`
- 用户导航栏新增"优惠券"菜单
- 用户下拉菜单新增"优惠券"和"消息通知"入口

---

## 🐳 四、部署配置

### 4.1 Docker 部署
- `docker/docker-compose.yml` - 完整服务编排
- `docker/Dockerfile-backend` - 后端镜像构建
- `docker/init.sql` - 数据库初始化
- **启动命令：** `cd docker && docker-compose up -d`

### 4.2 Nginx 配置
- `deploy/nginx.conf` - 生产环境 Nginx 配置
- 反向代理 `/api/` 到后端服务
- 静态资源缓存配置
- WebSocket 支持
- Gzip 压缩

### 4.3 生产环境配置
- `deploy/application-prod.yml` - 生产环境配置模板
- HikariCP 连接池优化
- Redis 连接池配置
- 日志文件输出

---

## 🔐 五、安全配置

### 5.1 SecurityConfig 更新
新增公开访问路径：
- `GET /api/v1/search/**` - 搜索接口公开
- `POST /api/v1/file/**` - 文件上传需认证
- `/uploads/**` - 上传文件公开访问

### 5.2 文件上传安全
- 文件大小限制：10MB
- 请求大小限制：20MB
- 本地存储隔离

---

## 📊 六、测试数据

### 6.1 优惠券测试数据
| ID | 名称 | 类型 | 面值 | 条件 |
|----|------|------|------|------|
| 1 | 新人专享券 | 满减 | ¥20 | 满100可用 |
| 2 | 景点专属券 | 满减 | ¥10 | 满50可用 |
| 3 | 酒店折扣券 | 折扣 | 8折 | 满200可用 |
| 4 | 满200减30 | 满减 | ¥30 | 满200可用 |

### 6.2 门票测试数据
| 景点 | 门票类型 | 价格 |
|------|---------|------|
| 故宫博物院 | 成人票 | ¥60 |
| 故宫博物院 | 学生票 | ¥30 |
| 故宫博物院 | 老人票 | ¥30 |
| 杭州西湖 | 船票 | ¥55 |
| 黄山风景区 | 成人票 | ¥230 |
| 厦门鼓浪屿 | 上岛船票 | ¥35 |

---

## 🚀 七、使用说明

### 7.1 数据库迁移
```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 执行 schema.sql（包含所有新表）
source backend/src/main/resources/schema.sql
```

### 7.2 Docker 部署
```bash
# 1. 构建并启动所有服务
cd docker
docker-compose up -d

# 2. 查看日志
docker-compose logs -f

# 3. 停止服务
docker-compose down
```

### 7.3 前端构建
```bash
cd frontend
pnpm build
# 产物在 dist/ 目录
```

### 7.4 手动部署
```bash
# 1. 安装依赖
cd backend && mvn package -DskipTests

# 2. 运行
java -jar target/*.jar

# 3. Nginx 部署静态文件
cp -r frontend/dist/* /usr/share/nginx/html/
```

---

## 📝 八、后续优化建议

1. **Redis 缓存** - 热点数据缓存、Session 共享
2. **OSS 对象存储** - 替换本地存储为阿里云 OSS 或 MinIO
3. **支付真实接口** - 接入微信支付/支付宝真实 API
4. **消息推送** - WebSocket 实时通知
5. **定时任务** - 优惠券过期检查、库存自动更新
6. **Elasticsearch** - 全文搜索支持
7. **SkyWalking** - 链路追踪

---

## ✅ 九、已完成清单

- [x] 数据库新增 13 张表
- [x] 支付模块（实体/Mapper/Service/Controller）
- [x] 文件上传模块（实体/Mapper/Service/Controller）
- [x] 消息通知模块（实体/Mapper/Service/Controller）
- [x] 优惠券模块（实体/Mapper/Service/Controller）
- [x] 搜索模块（Service/Controller）
- [x] 前端支付页面
- [x] 前端优惠券中心
- [x] 前端消息通知页面
- [x] 5 个前端 API 文件
- [x] Docker Compose 部署配置
- [x] Nginx 生产配置
- [x] 生产环境配置模板
- [x] SecurityConfig 权限配置
- [x] WebMvcConfig 静态资源映射
- [x] IMPROVEMENTS.md 文档

---

*文档版本：V2.0 | 最后更新：2026-04-05*
