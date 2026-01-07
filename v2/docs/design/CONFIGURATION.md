# 配置管理方案

> **文档版本**: v1.0
> **编写日期**: 2025-01-07
> **更新日期**: 2025-01-07

---

## 一、配置文件结构

### 1.1 配置文件组织

```
zhuque-v2/zhuque-web/web-admin/src/main/resources/
├── application.yml                    # 公共配置
├── application-dev.yml                # 开发环境配置
├── application-test.yml               # 测试环境配置
├── application-prod.yml               # 生产环境配置
└── logback-spring.xml                 # 日志配置
```

### 1.2 配置优先级

```
application-prod.yml > application-test.yml > application-dev.yml > application.yml
```

---

## 二、环境配置

### 2.1 开发环境 (dev)

**文件**: `application-dev.yml`

```yaml
# 服务配置
server:
  port: 8080
  servlet:
    context-path: /api

# 数据源配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/zhuque_dev?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: ZhuqueHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1

  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
      timeout: 10000ms
      lettuce:
        pool:
          min-idle: 0
          max-idle: 8
          max-active: 8
          max-wait: -1ms

  # JPA配置
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: ai.houyi.zhuque.model.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: zhuque-jwt-secret-key-2025-change-in-production
  expiration: 7200000  # 2小时（毫秒）
  refresh-expiration: 604800000  # 7天（毫秒）
  header: Authorization
  token-prefix: Bearer

# 日志配置
logging:
  level:
    root: INFO
    ai.houyi.zhuque: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n'

# 文件上传配置
file:
  upload-path: /data/zhuque/upload/dev/
  max-size: 10MB
  allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx

# OSS配置（开发环境使用本地存储）
oss:
  type: local
  local:
    base-path: /data/zhuque/upload/dev/
   访问路径: http://localhost:8080/static

# 管理后台配置
admin:
  cors:
    allowed-origins: http://localhost:5173,http://localhost:3000
    allowed-methods: "*"
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
```

### 2.2 测试环境 (test)

**文件**: `application-test.yml`

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.1.100:3306/zhuque_test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: zhuque_test
    password: Test@123456
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: ZhuqueHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1

  data:
    redis:
      host: 192.168.1.101
      port: 6379
      password: Test@Redis2025
      database: 1
      timeout: 10000ms
      lettuce:
        pool:
          min-idle: 0
          max-idle: 8
          max-active: 8
          max-wait: -1ms

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: ai.houyi.zhuque.model.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: ${JWT_SECRET:zhuque-test-jwt-secret-key-2025}
  expiration: 7200000
  refresh-expiration: 604800000
  header: Authorization
  token-prefix: Bearer

logging:
  level:
    root: INFO
    ai.houyi.zhuque: DEBUG
    org.springframework.web: INFO
    org.springframework.security: INFO
  file:
    name: /data/logs/zhuque/test/zhuque.log
    max-size: 100MB
    max-history: 30

file:
  upload-path: /data/zhuque/upload/test/
  max-size: 10MB
  allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx

oss:
  type: minio
  minio:
    endpoint: http://192.168.1.102:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: zhuque-test

admin:
  cors:
    allowed-origins: http://test.example.com
    allowed-methods: "*"
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
```

### 2.3 生产环境 (prod)

**文件**: `application-prod.yml`

```yaml
server:
  port: 8080
  tomcat:
    threads:
      max: 200
      min-spare: 10
    max-connections: 10000

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:mysql-prod.internal}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:zhuque}?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER:zhuque_prod}
    password: ${MYSQL_PASSWORD}
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      auto-commit: true
      idle-timeout: 60000
      pool-name: ZhuqueHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1

  data:
    redis:
      host: ${REDIS_HOST:redis-prod.internal}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
      database: 0
      timeout: 10000ms
      lettuce:
        pool:
          min-idle: 5
          max-idle: 20
          max-active: 20
          max-wait: -1ms
        shutdown-timeout: 100ms

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    database-platform: org.hibernate.dialect.MySQL8Dialect

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: ai.houyi.zhuque.model.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

jwt:
  secret: ${JWT_SECRET}
  expiration: 7200000
  refresh-expiration: 604800000
  header: Authorization
  token-prefix: Bearer

logging:
  level:
    root: INFO
    ai.houyi.zhuque: INFO
  file:
    name: /data/logs/zhuque/prod/zhuque.log
    max-size: 500MB
    max-history: 60

file:
  upload-path: /data/zhuque/upload/prod/
  max-size: 10MB
  allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx

oss:
  type: aliyun
  aliyun:
    endpoint: ${OSS_ENDPOINT}
    access-key-id: ${OSS_ACCESS_KEY_ID}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET}
    bucket-name: ${OSS_BUCKET_NAME}

# 监控配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true

admin:
  cors:
    allowed-origins: ${CORS_ORIGINS:https://admin.example.com}
    allowed-methods: GET,POST,PUT,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
```

---

## 三、公共配置 (application.yml)

**文件**: `application.yml`

```yaml
spring:
  application:
    name: zhuque-admin

  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  # Jackson配置
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
    serialization:
      write-dates-as-timestamps: false
    default-property-inclusion: non_null

  # 文件上传配置
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 100MB

# Actuator监控配置
management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: health,info

# MyBatis-Plus公共配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true

# 应用信息
info:
  app:
    name: 朱雀广告平台
    description: 一站式广告平台
    version: @project.version@
    encoding: @project.build.sourceEncoding@
    java:
      version: @java.version@
```

---

## 四、Docker Compose 开发环境

**文件**: `docker-compose-dev.yml`

```yaml
version: '3.8'

services:
  # MySQL开发环境
  mysql:
    image: mysql:8.0
    container_name: zhuque-mysql-dev
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: zhuque_dev
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./docker/mysql/conf.d:/etc/mysql/conf.d
      - ./docker/mysql/init:/docker-entrypoint-initdb.d
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-time-zone=+08:00
    networks:
      - zhuque-network

  # Redis开发环境
  redis:
    image: redis:7-alpine
    container_name: zhuque-redis-dev
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
      - ./docker/redis/redis.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf
    networks:
      - zhuque-network

  # MinIO（对象存储，开发环境）
  minio:
    image: minio/minio:latest
    container_name: zhuque-minio-dev
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio-data:/data
    command: server /data --console-address ":9001"
    networks:
      - zhuque-network

volumes:
  mysql-data:
  redis-data:
  minio-data:

networks:
  zhuque-network:
    driver: bridge
```

### MySQL初始化脚本

**文件**: `docker/mysql/init/init.sql`

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS zhuque_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zhuque_dev;

-- 创建用户表（示例）
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  real_name VARCHAR(50) COMMENT '真实姓名',
  status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 插入初始管理员用户（密码：admin123，BCrypt加密）
INSERT INTO sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1)
ON DUPLICATE KEY UPDATE username=username;

-- 创建其他基础表...
```

### Redis配置文件

**文件**: `docker/redis/redis.conf`

```conf
# 绑定地址
bind 0.0.0.0

# 端口
port 6379

# 持久化配置
appendonly yes
appendfsync everysec

# 内存管理
maxmemory 256mb
maxmemory-policy allkeys-lru

# 日志级别
loglevel notice

# 数据库数量
databases 16

# RDB配置
save 900 1
save 300 10
save 60 10000

# 密码（开发环境无密码）
# requirepass yourpassword
```

---

## 五、环境变量配置

### 5.1 环境变量文件

**开发环境**: `.env.dev`

```bash
# Spring配置
SPRING_PROFILES_ACTIVE=dev

# MySQL配置
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=zhuque_dev
MYSQL_USER=root
MYSQL_PASSWORD=root

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT配置
JWT_SECRET=zhuque-jwt-secret-key-2025

# OSS配置
OSS_TYPE=local
OSS_LOCAL_PATH=/data/zhuque/upload/dev/

# CORS配置
CORS_ORIGINS=http://localhost:5173,http://localhost:3000
```

**生产环境**: `.env.prod`

```bash
# Spring配置
SPRING_PROFILES_ACTIVE=prod

# MySQL配置
MYSQL_HOST=mysql-prod.internal
MYSQL_PORT=3306
MYSQL_DATABASE=zhuque
MYSQL_USER=zhuque_prod
MYSQL_PASSWORD=CHANGE_ME_STRONG_PASSWORD

# Redis配置
REDIS_HOST=redis-prod.internal
REDIS_PORT=6379
REDIS_PASSWORD=CHANGE_ME_STRONG_REDIS_PASSWORD

# JWT配置
JWT_SECRET=CHANGE_ME_VERY_STRONG_SECRET_AT_LEAST_32_CHARS

# OSS配置（阿里云）
OSS_TYPE=aliyun
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=YOUR_ACCESS_KEY_ID
OSS_ACCESS_KEY_SECRET=YOUR_ACCESS_KEY_SECRET
OSS_BUCKET_NAME=zhuque-prod

# CORS配置
CORS_ORIGINS=https://admin.example.com
```

### 5.2 使用环境变量

在配置文件中使用 `${变量名:默认值}` 格式：

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:zhuque}
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:root}
```

---

## 六、配置安全规范

### 6.1 敏感信息处理

1. **禁止将敏感信息提交到Git**
   - 密码
   - API密钥
   - JWT密钥
   - 数据库密码

2. **使用环境变量**
   ```yaml
   password: ${DB_PASSWORD}
   ```

3. **使用配置中心**（生产环境推荐）
   - Nacos Config
   - Spring Cloud Config
   - Vault

### 6.2 配置文件加密

使用Jasypt加密敏感配置：

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

```yaml
# 加密后的配置
jasypt:
  encryptor:
    password: ${JASYPT_PASSWORD}

spring:
  datasource:
    password: ENC(encrypted_password_here)
```

---

## 七、配置管理最佳实践

### 7.1 配置分层

```
公共配置（application.yml）
  ↓
环境配置（application-{env}.yml）
  ↓
环境变量（.env文件或系统环境变量）
  ↓
外部配置中心（生产环境）
```

### 7.2 配置变更流程

1. **开发环境**
   - 直接修改 `application-dev.yml`
   - 重启应用

2. **测试环境**
   - 修改 `application-test.yml`
   - 提交代码
   - 重新部署

3. **生产环境**
   - 使用配置中心（推荐）
   - 或修改环境变量
   - 滚动更新

### 7.3 配置验证

启动前验证配置：

```java
@Component
public class ConfigValidator implements ApplicationRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Override
    public void run(ApplicationArguments args) {
        // 验证必要配置
        if (datasourceUrl == null || datasourceUrl.isEmpty()) {
            throw new IllegalStateException("数据源配置未设置");
        }
    }
}
```

---

## 八、配置快速参考

### 开发环境快速启动

```bash
# 1. 启动Docker服务
docker-compose -f docker-compose-dev.yml up -d

# 2. 启动应用（IDEA或命令行）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. 访问健康检查
curl http://localhost:8080/actuator/health
```

### 生产环境部署

```bash
# 1. 设置环境变量
export SPRING_PROFILES_ACTIVE=prod
export MYSQL_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret

# 2. 启动应用
java -jar zhuque-app.jar

# 或使用systemd
sudo systemctl start zhuque
```

---

**文档版本**: v1.0
**最后更新**: 2025-01-07
**维护者**: 开发团队
