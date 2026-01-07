# 朱雀广告平台架构设计

> **文档版本**: v1.0
> **编写日期**: 2025-01-07
> **架构类型**: 单体应用 + 模块化分层

---

## 一、架构概览

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue 3)                        │
│  ┌──────────────┬──────────────┬──────────────┬───────────┐ │
│  │ 管理后台      │ 广告主平台    │ 开放平台      │ 监测平台  │ │
│  │ (Admin)      │ (Advertiser)  │ (OpenAPI)    │ (Pixel)   │ │
│  └──────────────┴──────────────┴──────────────┴───────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP/HTTPS
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       Web应用层 (Web Layer)                   │
│  ┌──────────────┬──────────────┬──────────────┬───────────┐ │
│  │ web-admin    │ web-advertiser│ web-openapi  │ web-rtb   │ │
│  │ (管理后台)    │ (广告主端)    │ (开放接口)    │ (竞价接口) │ │
│  └──────────────┴──────────────┴──────────────┴───────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     服务接口层 (Service API)                  │
│  ┌──────────────┬──────────────┬──────────────┬───────────┐ │
│  │ auth-api     │ advertiser-api│ campaign-api  │ rtb-api   │ │
│  │ (接口定义)    │ (接口定义)    │ (接口定义)    │ (接口定义)│ │
│  └──────────────┴──────────────┴──────────────┴───────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     业务服务层 (Service)                      │
│  ┌──────────────┬──────────────┬──────────────┬───────────┐ │
│  │ auth-svc     │ advertiser-svc│ campaign-svc  │ rtb-svc   │ │
│  │ (认证授权)    │ (广告主)      │ (推广活动)    │ (竞价)    │ │
│  ├──────────────┼──────────────┼──────────────┼───────────┤ │
│  │ creative-svc │ adgroup-svc  │ pixel-svc    │ report-svc│ │
│  │ (创意)       │ (广告组)      │ (监测)       │ (报表)    │ │
│  └──────────────┴──────────────┴──────────────┴───────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              数据访问层 (DAO) + 模型层 (Model)                │
│  ┌──────────────┬──────────────┬──────────────┬───────────┐ │
│  │ Mapper       │ Entity       │ DTO          │ VO        │ │
│  │ (MyBatis)    │ (实体)       │ (传输对象)    │ (视图对象)│ │
│  └──────────────┴──────────────┴──────────────┴───────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    公共基础层 (Commons)                       │
│  ┌──────────────┬──────────────┬──────────────┬───────────┐ │
│  │ common-core  │ common-web   │ common-sec   │ common-db │ │
│  │ (核心工具)    │ (Web相关)    │ (安全)        │ (数据库)  │ │
│  ├──────────────┼──────────────┼──────────────┼───────────┤ │
│  │ common-redis │ common-log   │ common-test  │           │ │
│  │ (Redis)      │ (日志)        │ (测试)        │           │ │
│  └──────────────┴──────────────┴──────────────┴───────────┘ │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       外部服务 & 存储                         │
│  ┌──────────┬──────────┬──────────┬──────────┬────────────┐ │
│  │ MySQL    │ Redis    │ OSS      │ 第三方API │ 日志收集   │ │
│  │ (持久化)  │ (缓存)    │ (文件)    │ (DSP/ADX) │ (ELK)     │ │
│  └──────────┴──────────┴──────────┴──────────┴────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 架构特点

**单体应用优势**：
- ✅ 部署简单：单一JAR包，无需容器编排
- ✅ 开发高效：模块间调用简单，无需网络通信
- ✅ 调试方便：可在一个IDE中调试所有代码
- ✅ 性能优越：进程内调用，无网络开销

**模块化分层优势**：
- ✅ 职责清晰：每个模块负责特定业务
- ✅ 接口驱动：模块间通过接口交互，降低耦合
- ✅ 易于维护：修改某个模块不影响其他模块
- ✅ 团队协作：不同团队可并行开发不同模块

---

## 二、模块划分

### 2.1 Maven模块结构

```
zhuque-v2/                               # 项目根目录
│
├── pom.xml                              # 父POM
│
├── zhuque-commons/                      # 公共基础模块
│   ├── common-core/                     # 核心工具类
│   │   └── src/main/java/ai/houyi/zhuque/common/core/
│   │       ├── constant/                # 常量定义
│   │       ├── exception/               # 自定义异常
│   │       ├── util/                    # 工具类
│   │       └── enums/                   # 枚举类
│   │
│   ├── common-web/                      # Web相关
│   │   └── src/main/java/ai/houyi/zhuque/common/web/
│   │       ├── response/                # 统一响应格式
│   │       ├── interceptor/             # 拦截器
│   │       └── config/                  # Web配置
│   │
│   ├── common-security/                 # 安全相关
│   │   └── src/main/java/ai/houyi/zhuque/common/security/
│   │       ├── annotation/              # 权限注解
│   │       ├── jwt/                     # JWT工具
│   │       └── handler/                 # 安全处理器
│   │
│   ├── common-redis/                    # Redis配置
│   └── common-database/                 # 数据库配置
│
├── zhuque-model/                        # 数据模型模块
│   ├── entity/                          # 数据库实体
│   ├── dto/                             # 数据传输对象
│   ├── vo/                              # 视图对象
│   └── query/                           # 查询对象
│
├── zhuque-dao/                          # 数据访问模块
│   ├── mapper/                          # MyBatis Mapper接口
│   └── xml/                             # MyBatis XML映射文件
│
├── zhuque-service-api/                  # 服务接口定义模块
│   ├── auth-service-api/                # 认证服务接口
│   ├── advertiser-service-api/          # 广告主服务接口
│   ├── campaign-service-api/            # 推广活动服务接口
│   ├── adgroup-service-api/             # 广告组服务接口
│   ├── creative-service-api/            # 创意服务接口
│   ├── rtb-service-api/                 # RTB竞价服务接口
│   └── pixel-service-api/               # 监测服务接口
│
├── zhuque-service/                      # 服务实现模块
│   ├── auth-service/                    # 认证服务实现
│   ├── advertiser-service/              # 广告主服务实现
│   ├── campaign-service/                # 推广活动服务实现
│   ├── adgroup-service/                 # 广告组服务实现
│   ├── creative-service/                # 创意服务实现
│   ├── rtb-service/                     # RTB竞价服务实现
│   └── pixel-service/                   # 监测服务实现
│
├── zhuque-web/                          # Web应用模块
│   ├── web-admin/                       # 管理后台（主启动类）
│   ├── web-advertiser/                  # 广告主平台
│   ├── web-openapi/                     # 开放平台API
│   └── web-rtb/                         # RTB竞价接口
│
└── zhuque-frontend/                     # 前端项目
    └── zhuque-dashboard-fe/             # 管理后台前端
```

### 2.2 模块职责

| 模块 | 职责 | 依赖 |
|-----|------|------|
| **common-core** | 核心工具类、常量、异常 | 无 |
| **common-web** | Web相关配置、拦截器、统一响应 | common-core |
| **common-security** | 安全相关（JWT、权限） | common-core |
| **common-redis** | Redis配置、缓存工具 | common-core |
| **common-database** | 数据库配置、MyBatis-Plus | common-core |
| **model** | 所有数据模型 | common-core |
| **dao** | 数据访问层 | model, common-database |
| **service-api** | 服务接口定义 | model, common-core |
| **service** | 业务逻辑实现 | service-api, dao |
| **web** | Controller层、接口暴露 | service-api, common-web |

---

## 三、分层设计

### 3.1 后端分层架构

```
┌─────────────────────────────────────────┐
│         Controller 层 (接口层)           │
│   - 接收HTTP请求                         │
│   - 参数校验 (@Validated)                │
│   - 调用Service                          │
│   - 返回统一响应                          │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│         Service 层 (业务逻辑层)          │
│   - 业务逻辑处理                          │
│   - 事务控制 (@Transactional)           │
│   - 调用DAO                              │
│   - 缓存管理                             │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│         DAO 层 (数据访问层)              │
│   - 数据库CRUD                           │
│   - MyBatis映射                          │
│   - SQL优化                              │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│         Database (数据存储层)            │
│   - MySQL 8.0                            │
│   - Redis 7.x                            │
└─────────────────────────────────────────┘
```

### 3.2 前端分层架构

```
┌─────────────────────────────────────────┐
│         View 层 (视图层)                 │
│   - 页面组件 (.vue)                      │
│   - UI组件 (Element Plus)                │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│         Store 层 (状态管理层)            │
│   - Pinia Store                          │
│   - 全局状态管理                          │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│         API 层 (接口调用层)              │
│   - Axios封装                            │
│   - 请求/响应拦截                         │
│   - 错误处理                             │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│         Utils 层 (工具层)                │
│   - 权限指令                              │
│   - 工具函数                              │
│   - 常量定义                              │
└─────────────────────────────────────────┘
```

---

## 四、模块交互

### 4.1 接口驱动设计

**核心思想**：模块间通过接口交互，不直接依赖实现类

**示例**：
```java
// 1. 在 service-api 模块定义接口
public interface CampaignService {
    Long createCampaign(CampaignCreateDTO dto);
    CampaignVO getCampaign(Long id);
}

// 2. 在 service 模块实现接口
@Service
public class CampaignServiceImpl implements CampaignService {
    @Autowired
    private CampaignMapper campaignMapper;

    @Override
    public Long createCampaign(CampaignCreateDTO dto) {
        // 业务逻辑
    }
}

// 3. 在 web 模块通过接口调用
@RestController
public class CampaignController {
    @Autowired
    private CampaignService campaignService; // 注入接口

    @PostMapping("/campaigns")
    public Result<Long> create(@RequestBody CampaignCreateDTO dto) {
        return Result.success(campaignService.createCampaign(dto));
    }
}
```

### 4.2 依赖规则

**基本原则**：
- 上层模块可以依赖下层模块
- 下层模块不能依赖上层模块
- 同层模块通过接口交互

**依赖关系图**：
```
web ──depends on──> service-api
                       │
service ──implements──> service-api
                       │
service ──depends on──> dao
                       │
dao ──depends on──> model
```

### 4.3 服务间调用

**同进程调用**（推荐）：
```java
@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private AdGroupService adGroupService; // 注入其他服务接口

    @Override
    public void deleteCampaign(Long campaignId) {
        // 删除活动下的所有广告组
        adGroupService.deleteByCampaignId(campaignId);
    }
}
```

---

## 五、关键技术

### 5.1 依赖注入

使用Spring框架的依赖注入：

1. **构造函数注入**（推荐）：
```java
@Service
public class CampaignServiceImpl implements CampaignService {

    private final CampaignMapper campaignMapper;

    @Autowired
    public CampaignServiceImpl(CampaignMapper campaignMapper) {
        this.campaignMapper = campaignMapper;
    }
}
```

2. **字段注入**（简单）：
```java
@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignMapper campaignMapper;
}
```

### 5.2 事务管理

使用Spring声明式事务：

```java
@Service
@Transactional(rollbackFor = Exception.class)
public class CampaignServiceImpl implements CampaignService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCampaign(CampaignCreateDTO dto) {
        // 事务方法
    }
}
```

### 5.3 异步处理

使用Spring异步处理：

```java
@Service
public class PixelServiceImpl implements PixelService {

    @Async
    public void recordImpressionAsync(String trackingId, String bidId) {
        // 异步记录曝光
        pixelDao.insert(trackingId, bidId);
    }
}
```

### 5.4 缓存策略

使用Spring Cache + Redis：

```java
@Service
public class CampaignServiceImpl implements CampaignService {

    @Cacheable(value = "campaign", key = "#id")
    public CampaignVO getCampaign(Long id) {
        return campaignDao.selectById(id);
    }

    @CacheEvict(value = "campaign", key = "#id")
    public void updateCampaign(CampaignUpdateDTO dto) {
        campaignDao.updateById(dto);
    }
}
```

---

## 六、部署架构

### 6.1 开发环境

```
Docker Compose
├── MySQL 8.0
├── Redis 7.0
└── 应用服务（IDEA启动）
```

### 6.2 生产环境

**方案一：Docker Compose（推荐）**
```
docker-compose.yml
├── MySQL 8.0
├── Redis 7.0
├── zhuque-app (JAR)
└── nginx (前端静态文件)
```

**方案二：Systemd服务**
```
Systemd Service
├── zhuque.service (应用服务)
└── nginx (前端)
```

详见：[技术方案 - 部署架构](../design/REWRITE_PLAN.md#九部署架构)

---

## 七、扩展性设计

### 7.1 如何添加新模块

**步骤**：
1. 创建 `xxx-service-api` 模块，定义接口
2. 创建 `xxx-service` 模块，实现接口
3. 在 `web` 模块中创建 Controller
4. 配置模块依赖关系

### 7.2 如何拆分为微服务

如果将来需要拆分为微服务：

1. **将 service 模块打包为独立JAR**
2. **添加服务间通信**（REST或RPC）
3. **引入服务注册中心**（Nacos/Consul）
4. **引入配置中心**（Nacos Config）
5. **引入网关**（Spring Cloud Gateway）

当前架构已为此做好准备：
- ✅ 接口驱动，便于RPC改造
- ✅ 模块独立，便于拆分
- ✅ 依赖清晰，便于解耦

---

## 八、性能优化

### 8.1 数据库优化

- 索引优化
- SQL优化
- 读写分离（可选）
- 分库分表（可选）

### 8.2 缓存优化

- Redis缓存热点数据
- 本地缓存（Caffeine）
- 缓存预热
- 缓存更新策略

### 8.3 接口优化

- 分页查询
- 批量操作
- 异步处理
- 接口合并

---

**文档版本**: v1.0
**最后更新**: 2025-01-07
**维护者**: 开发团队
