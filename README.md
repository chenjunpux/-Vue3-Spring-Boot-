# 🧳 智慧旅游管理系统

> Vue3 + Spring Boot 3 + Docker 全栈项目

---

## 🚀 一键 Docker 部署（Mac / Linux / Windows 通用）

### 前置条件

- 安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)（Windows 推荐 WSL2 后端）
- Docker 版本 >= 20.10，docker compose 版本 >= 2.0

### 1. 克隆项目

```bash
git clone https://github.com/chenjunpux/-Vue3-Spring-Boot-.git
cd -Vue3-Spring-Boot-/travel-system
```

### 2. 一键启动（所有服务）

```bash
cd docker
docker compose up -d
```

> 首次启动会自动构建后端镜像（需要几分钟下载依赖），后续启动直接运行即可。

### 3. 等待服务就绪

```bash
docker compose ps
```

确认所有容器状态为 `healthy` 后即可访问。

### 4. 访问系统

| 服务 | 地址 |
|------|------|
| 🌐 旅游管理系统前台 | http://localhost |
| 🔧 后端 API | http://localhost:8080 |
| 📬 RabbitMQ 管理界面 | http://localhost:15672（guest/guest） |
| 🗄️ MinIO 控制台 | http://localhost:9001（minioadmin/minioadmin123456） |
| 🐬 phpMyAdmin（数据库） | http://localhost:8081 |
| 📋 Swagger API 文档 | http://localhost:8080/swagger-ui.html |

### 5. 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| `admin` | `admin123` | 管理员 |
| `testuser` | `user123` | 普通用户 |

---

## 📁 项目结构

```
travel-system/
├── docker/                 # Docker 部署配置
│   ├── docker-compose.yml   # 容器编排（MySQL/Redis/RabbitMQ/MinIO/Nginx/后端）
│   ├── Dockerfile-backend   # 后端构建镜像
│   ├── nginx/conf.d/        # Nginx 反向代理配置
│   ├── mysql/conf.d/        # MySQL 配置
│   ├── redis/redis.conf     # Redis 配置
│   ├── init.sql             # 数据库初始化
│   └── rabbitmq/enabled_plugins  # RabbitMQ 插件配置
├── backend/                # Spring Boot 3 后端
│   └── src/main/resources/
│       ├── application.yml  # 主配置（dev + prod profile）
│       └── schema.sql       # 数据库建表脚本
├── frontend/               # Vue3 + TypeScript 前端
│   ├── src/api/            # 所有接口定义
│   ├── src/views/           # 页面组件
│   └── dist/               # 打包后的静态文件（由 Nginx 托管）
└── deploy/                  # 部署脚本
```

---

## 🛠️ Docker 服务说明

| 容器名 | 镜像 | 说明 |
|--------|------|------|
| `travel-mysql` | mysql:8.0 | MySQL 8 数据库 |
| `travel-redis` | redis:7-alpine | Redis 缓存 |
| `travel-rabbitmq` | rabbitmq:3.12-management | 消息队列 |
| `travel-minio` | minio/minio | 对象存储（文件/图片） |
| `travel-backend` | docker-backend | Spring Boot 后端 |
| `travel-nginx` | nginx:alpine | 前端静态资源 + API 反向代理 |
| `travel-phpmyadmin` | phpmyadmin | 可选：数据库可视化（需 `docker compose --profile tools up -d`） |

---

## 🔧 常用 Docker 命令

```bash
# 停止所有服务
docker compose down

# 重启某个服务
docker compose restart backend

# 查看日志
docker compose logs -f backend      # 后端日志
docker compose logs -f nginx        # Nginx 日志
docker compose logs -f mysql        # 数据库日志

# 重新构建并启动
docker compose up -d --build

# 进入容器内部
docker compose exec backend sh

# 查看资源占用
docker stats
```

---

## ⚠️ Windows 特别注意事项

### 1. 确保 WSL2 已安装（推荐）

Docker Desktop for Windows 需要 WSL2 作为后端：

```powershell
# 管理员 PowerShell 运行
wsl --install
```

然后在 Docker Desktop 设置 → General →勾选 "Use the WSL2 based engine"

### 2. 项目放哪个盘都可以，但推荐非 C 盘

WSL2 访问宿主机文件系统性能较差，项目放 `D:\Projects\` 比 `C:\` 更快。

### 3. 克隆项目

```powershell
git clone https://github.com/chenjunpux/-Vue3-Spring-Boot-.git
cd -Vue3-Spring-Boot-/travel-system
cd docker
docker compose up -d
```

### 4. 如果遇到端口占用

确保以下端口未被占用：

```powershell
netstat -ano | findstr "80 3307 6379 5672 9000 9001 8080 8081"
```

如果端口被占用，修改 `docker-compose.yml` 中的端口映射。

### 5. Docker Desktop 内存设置

建议给 Docker 分配 4GB+ 内存，避免 MySQL/Elasticsearch 等服务内存不足：

Docker Desktop → Settings → Resources → Memory: 4GB+

---

## 📝 环境变量说明

后端连接配置通过 `docker-compose.yml` 中的环境变量注入：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `SPRING_DATASOURCE_URL` | MySQL 连接地址 | `jdbc:mysql://mysql:3306/travel_system` |
| `SPRING_DATASOURCE_PASSWORD` | MySQL root 密码 | `root_password_2026` |
| `SPRING_DATA_REDIS_HOST` | Redis 地址 | `redis` |
| `MINIO_ENDPOINT` | MinIO API 地址 | `http://minio:9000` |
| `MINIO_ACCESS_KEY` | MinIO 用户名 | `minioadmin` |
| `MINIO_SECRET_KEY` | MinIO 密码 | `minioadmin123456` |

---

## 🔄 数据持久化

所有数据（MySQL、Redis、RabbitMQ、MinIO）均通过 Docker Volume 持久化，删除容器不会丢失数据。重新 `docker compose up -d` 会自动恢复数据。

如需**重置所有数据**，执行：

```bash
docker compose down -v   # -v 会删除所有数据卷
docker compose up -d
```

---

## 📖 开发模式（非 Docker）

### 前端开发

```bash
cd frontend
npm install
npm run dev        # 访问 http://localhost:5173
# 需同时启动后端：cd backend && mvn spring-boot:run
```

### 后端开发

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# 连接 localhost:3306 的本地 MySQL
```

---

## 技术栈

| 端 | 技术 |
|----|------|
| 前端 | Vue 3 + TypeScript + Vite + UnoCSS + Element Plus + ECharts |
| 后端 | Spring Boot 3 + MyBatis-Plus + JWT + Redis |
| 数据库 | MySQL 8 |
| 缓存 | Redis 7 |
| 消息队列 | RabbitMQ 3.12 |
| 对象存储 | MinIO |
| 反向代理 | Nginx |
| DevOps | Docker + Docker Compose |

---

MIT License - 仅供毕设学习使用
