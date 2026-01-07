# 朱雀广告平台后端服务

## 项目结构

```
zhuque-backend/
├── zhuque-commons/          # 公共基础模块
│   ├── common-core         # 核心工具类、常量、异常
│   ├── common-web          # Web相关配置
│   ├── common-security     # 安全模块（JWT、权限）
│   ├── common-redis        # Redis配置
│   └── common-database     # 数据库配置
│
├── zhuque-model/           # 数据模型模块
├── zhuque-dao/             # 数据访问模块
├── zhuque-service-api/     # 服务接口定义
├── zhuque-service/         # 服务实现
└── zhuque-web/             # Web应用模块
    └── web-admin/          # 管理后台启动模块
```

## 技术栈

- Java 17
- Spring Boot 3.2.1
- MySQL 8.0
- MyBatis-Plus 3.5.5
- Redis 7.x
- JWT (io.jsonwebtoken:jjwt:0.11.5)

## 快速启动

### 1. 启动MySQL

```bash
cd /home/wake/code/zhuque
docker-compose -f docker-compose-dev.yml up -d mysql
```

### 2. 编译项目

```bash
cd /home/wake/code/zhuque/v2/zhuque-backend
mvn clean install
```

### 3. 启动应用

```bash
cd zhuque-web/web-admin
mvn spring-boot:run
```

或直接运行主类：`ai.houyi.zhuque.ZhuqueApplication`

### 4. 验证启动

访问健康检查接口：http://localhost:8080/api/health

或访问Hello接口：http://localhost:8080/api/hello

## 开发环境配置

默认配置文件：`application-dev.yml`

数据库连接：
- Host: localhost:3306
- Database: zhuque_dev
- Username: root
- Password: root

## 模块说明

### zhuque-commons
公共基础模块，包含所有子模块共用的代码。

### zhuque-model
数据模型模块，包含：
- entity: 数据库实体
- dto: 数据传输对象
- vo: 视图对象

### zhuque-dao
数据访问模块，包含MyBatis Mapper接口。

### zhuque-service-api
服务接口定义模块，定义所有业务接口。

### zhuque-service
服务实现模块，实现业务逻辑。

### zhuque-web
Web应用模块，包含Controller和启动类。

## 开发规范

### 包命名

```
ai.houyi.zhuque.{module}.{layer}
```

例如：
- `ai.houyi.zhuque.common.core` - 公共核心
- `ai.houyi.zhuque.service.auth` - 认证服务
- `ai.houyi.zhuque.controller` - 控制器

### 代码规范

- 使用Lombok简化代码
- 统一使用`Result<T>`返回结果
- 使用`@RequiresPermission`注解控制权限
- Service使用`@Service`注解
- Controller使用`@RestController`注解

## 下一步

1. ✅ 项目框架搭建
2. ⏳ 数据库初始化
3. ⏳ JWT工具类实现
4. ⏳ 登录接口开发
5. ⏳ 用户管理接口

## 文档

详细文档请查看项目根目录的docs文件夹。
