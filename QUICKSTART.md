# 快速启动指南

## 🚀 开发环境快速搭建

### 前置要求

- JDK 17 或 21
- Maven 3.9+
- Node.js 18+
- Docker & Docker Compose（可选）

### 一、启动基础服务（MySQL + Redis）

#### 方式1：使用 Docker Compose（推荐）

```bash
# 1. 启动服务
docker-compose -f docker-compose-dev.yml up -d

# 2. 查看服务状态
docker-compose -f docker-compose-dev.yml ps

# 3. 查看日志
docker-compose -f docker-compose-dev.yml logs -f mysql
docker-compose -f docker-compose-dev.yml logs -f redis

# 4. 停止服务
docker-compose -f docker-compose-dev.yml down
```

**服务地址**：
- MySQL: `localhost:3306`
  - 数据库: `zhuque_dev`
  - 用户名: `root`
  - 密码: `root`
- Redis: `localhost:6379`
  - 无密码
- MinIO: `http://localhost:9000`（API）、`http://localhost:9001`（控制台）
  - 用户名: `minioadmin`
  - 密码: `minioadmin`

#### 方式2：本地安装

**MySQL**：
```bash
# 创建数据库
CREATE DATABASE zhuque_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入初始化脚本
mysql -u root -p zhuque_dev < docker/mysql/init/init.sql
```

**Redis**：
```bash
# 使用默认配置启动
redis-server
```

### 二、配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env.dev

# 根据实际情况修改配置（如需）
vim .env.dev
```

### 三、启动后端应用

#### 方式1：IDEA运行

1. 打开 IDEA，导入项目
2. 等待 Maven 依赖下载完成
3. 找到主启动类（待创建）
4. 右键 → Run 'Application'
5. 设置环境变量：`SPRING_PROFILES_ACTIVE=dev`

#### 方式2：命令行运行

```bash
# 进入项目目录
cd v2/zhuque-backend

# Maven构建
mvn clean package -DskipTests

# 运行应用
java -jar zhuque-web/web-admin/target/zhuque-web-admin.jar --spring.profiles.active=dev
```

### 四、启动前端应用

```bash
# 进入前端目录
cd v2/zhuque-frontend/zhuque-dashboard-fe

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问：`http://localhost:5173`

### 五、验证安装

#### 1. 健康检查

```bash
# 后端健康检查
curl http://localhost:8080/actuator/health

# 预期输出
# {"status":"UP"}
```

#### 2. 登录测试

```bash
# 使用初始管理员账号登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# 预期返回Token和用户信息
```

#### 3. 数据库验证

```bash
# 连接MySQL
mysql -u root -p

# 查看数据
USE zhuque_dev;
SELECT * FROM sys_user;
SELECT * FROM sys_role;
SELECT * FROM sys_permission;
```

---

## 📝 开发配置说明

### 数据库配置

**文件位置**：`v2/zhuque-backend/zhuque-web/web-admin/src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zhuque_dev
    username: root
    password: root
```

### Redis配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 开发环境无密码
```

### 日志配置

日志文件位置：`/data/logs/zhuque/dev/`

```bash
# 查看日志
tail -f /data/logs/zhuque/dev/zhuque.log
```

---

## 🔧 常见问题

### 1. 端口冲突

**问题**：`8080` 端口已被占用

**解决**：
```yaml
# 修改 application-dev.yml
server:
  port: 8081  # 改为其他端口
```

### 2. 数据库连接失败

**检查**：
```bash
# 测试MySQL连接
mysql -h localhost -u root -p

# 检查Docker容器
docker ps | grep mysql
docker logs zhuque-mysql-dev
```

### 3. Redis连接失败

**检查**：
```bash
# 测试Redis连接
redis-cli ping
# 应返回 PONG

# 检查Docker容器
docker ps | grep redis
docker logs zhuque-redis-dev
```

### 4. Maven依赖下载慢

**解决**：配置国内镜像源

```xml
<!-- settings.xml -->
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 5. 前端npm install失败

**解决**：使用国内镜像

```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
rm -rf node_modules package-lock.json
npm install
```

---

## 📚 相关文档

- [配置管理文档](docs/design/CONFIGURATION.md)
- [开发策略](docs/overview/DEVELOPMENT_STRATEGY.md)
- [技术方案](docs/design/REWRITE_PLAN.md)

---

## 🎯 下一步

1. 阅读 [开发策略](docs/overview/DEVELOPMENT_STRATEGY.md)，了解渐进式开发方法
2. 查看 [项目状态](docs/overview/PROJECT_STATUS.md)，了解当前进度
3. 选择一个 Milestone 开始开发

**推荐起点**：
- **Milestone 0**: 项目骨架搭建（Hello World）
- **Milestone 1**: 认证系统（登录功能）

---

**文档版本**: v1.0
**最后更新**: 2025-01-07
