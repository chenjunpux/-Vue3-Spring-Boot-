# 智慧旅游管理系统 - 后端

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0
- Redis 7.0

## 数据库配置

创建数据库并导入初始化脚本：

```sql
CREATE DATABASE travel_system DEFAULT CHARSET utf8mb4;
USE travel_system;
-- 运行 schema.sql 中的建表语句
```

或者直接启动项目，程序会自动初始化测试数据。

## 配置文件

编辑 `src/main/resources/application.yml` 或创建 `application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的MySQL密码
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如果有密码
```

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package -DskipTests
java -jar target/travel-system-1.0.0.jar
```

后端启动在 `http://localhost:8080`

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动在 `http://localhost:5173`

## 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | `admin` | `admin123` | 进入管理端 `/admin` |
| 普通用户 | `testuser` | `user123` | 进入用户端 |

## API 文档

基础路径: `http://localhost:8080/api/v1`

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/login | 登录 |
| POST | /auth/register | 注册 |
| POST | /auth/logout | 退出 |
| GET | /auth/me | 当前用户信息 |
| POST | /auth/refresh | 刷新Token |

### 景点接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /spot/list | 景点列表 |
| GET | /spot/hot | 热门景点 |
| GET | /spot/{id} | 景点详情 |
| POST | /spot | 创建景点（管理端）|
| PUT | /spot/{id} | 更新景点 |
| DELETE | /spot/{id} | 删除景点 |
| PUT | /spot/{id}/status | 上下架 |

### 酒店接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /hotel/list | 酒店列表 |
| GET | /hotel/hot | 热门酒店 |
| GET | /hotel/{id} | 酒店详情 |
| GET | /hotel/{id}/rooms | 房型列表 |
| POST | /hotel | 创建酒店 |
| PUT | /hotel/{id} | 更新酒店 |
| POST | /hotel/rooms | 创建房型 |

### 订单接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /order/list | 我的订单 |
| GET | /order/admin/list | 订单列表（管理端）|
| GET | /order/{id} | 订单详情 |
| POST | /order/create/spot | 创建景点订单 |
| POST | /order/create/hotel | 创建酒店订单 |
| POST | /order/{orderNo}/pay | 模拟支付 |
| PUT | /order/{id}/cancel | 取消订单 |
| PUT | /order/{id}/refund | 退款 |

### 游记接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /article/list | 游记列表 |
| GET | /article/hot | 热门游记 |
| GET | /article/{id} | 游记详情 |
| POST | /article | 发布游记 |
| PUT | /article/{id} | 编辑游记 |
| DELETE | /article/{id} | 删除游记 |
| PUT | /article/{id}/status | 审核状态 |
| PUT | /article/{id}/top | 置顶/取消置顶 |

### 用户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /user/admin/list | 用户列表（管理端）|
| PUT | /user/admin/{id}/status | 封禁/解封 |
| GET | /user/profile | 个人资料 |
| PUT | /user/profile | 更新资料 |
| PUT | /user/avatar | 修改头像 |
| PUT | /user/password | 修改密码 |

### 统计接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /statistics/dashboard | 仪表盘数据 |
| GET | /statistics/orderTrend | 订单趋势 |
| GET | /statistics/userGrowth | 用户增长 |

## 项目结构

```
backend/
├── src/main/java/com/travel/
│   ├── TravelSystemApplication.java    # 启动类
│   ├── config/                         # 配置类
│   │   ├── SecurityConfig.java         # Spring Security配置
│   │   ├── CorsConfig.java             # 跨域配置
│   │   ├── MyBatisPlusConfig.java      # MyBatis Plus配置
│   │   └── DataInitializer.java         # 数据初始化
│   ├── controller/                      # 控制器
│   ├── service/                        # 业务层
│   ├── mapper/                         # 数据访问层
│   ├── entity/                         # 实体类
│   ├── dto/                            # 数据传输对象
│   ├── security/                       # JWT认证
│   └── common/                         # 公共工具
└── src/main/resources/
    ├── application.yml                 # 主配置
    └── schema.sql                       # 数据库脚本
```
