# 朱雀广告平台重写技术方案

## 一、项目概述

### 1.1 重写目标
- **升级技术栈**：使用最新稳定版本，解决安全漏洞
- **改进架构**：采用现代化分层架构，提升可维护性
- **完善功能**：实现缺失的核心业务功能
- **提升质量**：完善测试、文档和监控

### 1.2 重写策略
- **完全重写**：从零开始重新设计和实现
- **保留业务逻辑**：参考现有业务模型和流程
- **渐进交付**：分模块逐步交付

---

## 二、技术架构设计

### 2.1 后端技术栈

| 分类 | 技术选型 | 版本 | 说明 |
|-----|---------|------|------|
| **核心框架** | Spring Boot | 3.2.x | 最新LTS版本 |
| **开发语言** | Java | 17/21 | LTS版本 |
| **ORM框架** | MyBatis-Plus | 3.5.x | 增强MyBatis |
| **数据库** | MySQL | 8.0.x | 最新稳定版 |
| **缓存** | Redis | 7.x | 数据缓存/分布式锁 |
| **安全框架** | Spring Security + JWT | - | 认证授权 |
| **API文档** | SpringDoc (OpenAPI 3) | - | 替代Swagger 2 |
| **监控** | Micrometer + Prometheus | - | 指标收集 |
| **日志** | Logback | - | 日志记录 |
| **构建工具** | Maven | 3.9.x | 依赖管理 |
| **协议** | Protobuf | 3.25.x | RTB协议 |

### 2.2 前端技术栈

| 分类 | 技术选型 | 版本 | 说明 |
|-----|---------|------|------|
| **核心框架** | Vue | 3.4.x | Composition API |
| **开发语言** | TypeScript | 5.x | 类型安全 |
| **构建工具** | Vite | 5.x | 快速开发构建 |
| **UI组件库** | Element Plus | 2.5.x | Vue 3组件库 |
| **状态管理** | Pinia | 2.x | 替代Vuex |
| **路由** | Vue Router | 4.x | 官方路由 |
| **HTTP客户端** | Axios | 1.6.x | 请求封装 |
| **代码规范** | ESLint + Prettier | - | 代码质量 |
| **CSS框架** | Tailwind CSS / UnoCSS | - | 原子化CSS |
| **图表库** | ECharts | 5.x | 数据可视化 |

### 2.3 单体应用架构（模块化）

**架构说明：**
- 采用单体应用架构，所有模块部署在一个应用中
- 通过Maven多模块划分业务边界
- 模块间通过接口依赖进行交互
- 降低部署复杂度，提升开发效率

**模块划分（实际采用）：**

```
/home/wake/code/zhuque.worktrees/20260109_v2_dev/v2/  # 项目根目录
│
├── zhuque-backend/                # 后端项目根目录
│   ├── pom.xml                    # 父POM文件
│   │
│   ├── zhuque-common/             # 公共基础模块（单一模块，内部分包）
│   │   ├── core/                  # 核心工具类、常量、异常
│   │   ├── web/                   # Web相关（统一响应、异常处理）
│   │   ├── security/              # 安全相关（JWT、权限注解）
│   │   ├── redis/                 # Redis配置和工具
│   │   └── database/              # 数据库配置（MyBatis-Plus）
│   │
│   ├── zhuque-model/              # 数据模型模块
│   │   ├── entity/                # 数据库实体
│   │   ├── dto/                   # 数据传输对象
│   │   ├── vo/                    # 视图对象
│   │   └── query/                 # 查询对象
│   │
│   ├── zhuque-service-api/        # 服务接口定义模块
│   │   ├── auth-service-api/      # 认证服务接口
│   │   ├── advertiser-service-api/# 广告主服务接口
│   │   ├── campaign-service-api/  # 推广活动服务接口
│   │   ├── creative-service-api/  # 创意服务接口
│   │   ├── rtb-service-api/       # RTB竞价服务接口
│   │   └── pixel-service-api/     # 监测服务接口
│   │
│   ├── zhuque-service/            # 服务实现模块
│   │   ├── auth-service/          # 认证服务实现
│   │   ├── advertiser-service/    # 广告主服务实现
│   │   ├── campaign-service/      # 推广活动服务实现
│   │   ├── creative-service/      # 创意服务实现
│   │   ├── rtb-service/           # RTB竞价服务实现
│   │   └── pixel-service/         # 监测服务实现
│   │
│   ├── zhuque-dao/                # 数据访问模块
│   │   ├── mapper/                # MyBatis Mapper接口
│   │   └── xml/                   # MyBatis XML映射文件
│   │
│   ├── web-admin/                 # 管理后台Web模块（启动模块）
│   │   └── src/main/java/
│   │       └── su/
│   │           └── zhuque/
│   │               └── admin/
│   │                   └── AdminApplication.java
│   │
│   └── web-openapi/               # 开放平台API（待添加）
│   └── web-rtb/                   # RTB竞价接口（待添加）
│
├── zhuque-frontend/               # 前端项目
│   └── zhuque-dashboard-fe/       # 管理后台前端
│
├── docs/                          # 文档目录
│   ├── overview/                  # 概览文档
│   └── design/                    # 设计文档
│
└── logs/                          # 日志目录
```

**说明：**
- 包名：`wake.su`（保持现状）
- `zhuque-common` 采用单一模块，内部通过 package 分包（core/web/security/redis/database）
- `web-admin` 独立作为启动模块，后续可添加 `web-openapi`、`web-rtb`
- 项目位于 Git worktree: `/home/wake/code/zhuque.worktrees/20260109_v2_dev/v2/`

**模块依赖关系：**

```
┌─────────────────────────────────────────────────────┐
│              zhuque-web (Web应用层)                  │
│  ┌──────────┬──────────┬──────────┬──────────────┐  │
│  │ web-admin│web-openap│ web-rtb  │  (启动模块)  │  │
│  └────┬──────┴────┬─────┴────┬─────┴──────────────┘  │
└───────┼───────────┼─────────┼────────────────────────┘
        │           │         │
        └───────────┴─────────┴─────────┐
                    │                     │
        ┌───────────▼─────────────────────▼───────┐
        │      zhuque-service-api (接口层)        │
        │  模块间交互通过接口定义，降低耦合        │
        └───────────┬─────────────────────┬───────┘
                    │                     │
        ┌───────────▼─────────────────────▼───────┐
        │      zhuque-service (业务实现层)        │
        │  auth-service  │  advertiser-service   │
        │  campaign-svc  │  creative-service     │
        │  rtb-service   │  pixel-service        │
        └───────────┬─────────────────────┬───────┘
                    │                     │
        ┌───────────▼─────────────────────▼───────┐
        │      zhuque-dao (数据访问层)            │
        │      zhuque-model (数据模型层)          │
        └───────────┬─────────────────────┬───────┘
                    │                     │
        ┌───────────▼─────────────────────▼───────┐
        │      zhuque-commons (公共基础层)         │
        │  common-core │ common-web │ common-sec  │
        └──────────────────────────────────────────┘
```

**模块间交互规范：**

1. **接口依赖原则**
   ```java
   // zhuque-service-api 模块定义接口
   public interface CampaignService {
       Long createCampaign(CampaignCreateDTO dto);
       CampaignVO getCampaign(Long id);
   }

   // zhuque-service 模块实现接口
   @Service
   public class CampaignServiceImpl implements CampaignService {
       // 实现逻辑
   }

   // zhuque-web 模块通过接口调用
   @RestController
   public class CampaignController {
       @Autowired
       private CampaignService campaignService; // 注入接口
   }
   ```

2. **模块依赖规则**
   - zhuque-web 依赖 zhuque-service-api
   - zhuque-service 依赖 zhuque-service-api
   - 所有模块可依赖 zhuque-commons 和 zhuque-model
   - 禁止下层模块依赖上层模块

3. **依赖注入方式**
   - 优先使用接口注入（面向接口编程）
   - 使用 `@Autowired` 或构造函数注入
   - 服务间调用通过接口，不直接依赖实现类

---

## 三、分层架构设计

### 3.1 后端分层

```
┌─────────────────────────────────────────┐
│         Controller Layer                │  API接口层
│  - 参数验证 (@Validated)                │
│  - 统一响应封装                          │
│  - OpenAPI文档注解                       │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Service Layer                   │  业务逻辑层
│  - 业务接口定义                          │
│  - 事务管理 (@Transactional)            │
│  - 缓存管理 (@Cacheable)                │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Manager Layer (新增)            │  领域服务层
│  - 复杂业务编排                          │
│  - 跨领域服务调用                        │
│  - 外部系统集成                          │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         DAO Layer                       │  数据访问层
│  - MyBatis-Plus Mapper                  │
│  - 自定义Mapper XML                     │
│  - 数据库操作                            │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Domain Layer                    │  领域模型层
│  - Entity (数据库实体)                   │
│  - DTO (数据传输对象)                    │
│  - VO (视图对象)                         │
│  - Query (查询对象)                      │
└─────────────────────────────────────────┘
```

### 3.2 前端分层

```
┌─────────────────────────────────────────┐
│         View Layer                      │  视图层
│  - 页面组件 (.vue)                      │
│  - 业务组件                             │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Composition Logic Layer         │  组合式函数层
│  - Composables (useXXX)                │
│  - 业务逻辑复用                          │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Store Layer                     │  状态管理层
│  - Pinia Stores                        │
│  - 全局状态管理                          │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Service Layer                   │  服务层
│  - API封装                              │
│  - 请求拦截                             │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Type Layer                      │  类型层
│  - TypeScript接口/类型                  │
│  - 枚举定义                             │
└─────────────────────────────────────────┘
```

---

## 四、核心模块设计

### 4.1 认证授权模块 (zhuque-auth)

**功能清单：**
- JWT认证机制
- 刷新Token机制
- RBAC权限模型
- 用户/角色/权限/菜单管理
- 登录日志审计
- 密码加密存储

**技术实现：**
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 登录获取Token
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto);

    // 刷新Token
    @PostMapping("/refresh")
    public Result<TokenVO> refreshToken(@RequestParam String refreshToken);

    // 登出
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token);

    // 获取当前用户信息
    @GetMapping("/user-info")
    public Result<UserInfoVO> getUserInfo();
}
```

**安全配置：**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(s -> s
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter,
                           UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### 4.2 核心业务模块 (zhuque-core)

**实体设计：**

```java
// 广告主
@Data
@TableName("advertiser")
public class Advertiser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long agentId;
    private Integer auditStatus; // 0-待审核 1-已通过 2-已拒绝
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

// 推广活动
@Data
@TableName("campaign")
public class Campaign {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long advertiserId;
    private String name;
    private BigDecimal budget;
    private Integer status; // 0-暂停 1-投放中
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

// 广告组
@Data
@TableName("ad_group")
public class AdGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long campaignId;
    private String name;
    private BigDecimal bidPrice;
    private String targeting; // JSON格式定向配置
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
```

**服务层设计：**

```java
public interface CampaignService extends IService<Campaign> {

    /**
     * 创建推广活动
     */
    Long createCampaign(CampaignCreateDTO dto);

    /**
     * 更新推广活动
     */
    void updateCampaign(Long id, CampaignUpdateDTO dto);

    /**
     * 分页查询
     */
    IPage<CampaignVO> pageQuery(CampaignQueryDTO dto);

    /**
     * 启用/禁用
     */
    void updateStatus(Long id, Integer status);

    /**
     * 预算控制
     */
    boolean checkBudget(Long campaignId);
}
```

### 4.3 RTB竞价模块 (zhuque-rtb)

**竞价流程：**

```
1. SSP发起竞价请求
   ↓
2. ADX接收并转发给DSP
   ↓
3. DSP出价决策
   ↓
4. ADX竞价排序
   ↓
5. 返回获胜创意
   ↓
6. 上报展示/点击
```

**核心接口：**

```java
@RestController
@RequestMapping("/rtb")
public class RtbController {

    /**
     * 竞价请求
     */
    @PostMapping("/bid")
    public BidResponse bid(@RequestBody BidRequest request) {
        // 1. 解析竞价请求
        // 2. 查询匹配的广告组
        // 3. 执行定向过滤
        // 4. 计算出价
        // 5. 返回竞价响应
    }

    /**
     * 赢价通知
     */
    @PostMapping("/win")
    public Result<Void> win(@RequestBody WinNotice notice);

    /**
     * 曝光监测
     */
    @PostMapping("/impression")
    public Result<Void> impression(@RequestBody TrackingData data);

    /**
     * 点击监测
     */
    @PostMapping("/click")
    public Result<Void> click(@RequestBody TrackingData data);
}
```

### 4.4 监测服务模块 (zhuque-pixel)

**架构设计：**

```
┌────────────────────────────────────┐
│      Pixel Tracking Service        │
├────────────────────────────────────┤
│  1. 曝光监测 (Impression)           │
│  2. 点击监测 (Click)                │
│  3. 到达监测 (Arrival)              │
│  4. 转化监测 (Conversion)           │
└──────────────────┬─────────────────┘
                   │
       ┌───────────┴───────────┐
       │                       │
┌──────▼──────────┐    ┌──────▼──────┐
│  异步线程池      │    │   MySQL     │
│  (@Async)       │    │  (持久化)   │
└─────────────────┘    └─────────────┘
        │
        ▼
┌──────▼──────┐
│   Redis     │  (实时统计/缓存)
│  (可选)     │
└─────────────┘
```

**设计说明：**
- 使用Spring `@Async`异步处理监测请求，快速响应
- 数据批量写入MySQL，减少IO压力
- Redis用于实时统计和热点数据缓存

**接口实现：**

```java
@RestController
@RequestMapping("/pixel")
public class PixelController {

    /**
     * 曝光监测 (支持GET/IMG)
     */
    @GetMapping({"/imp", "/imp.gif"})
    public void impression(
            @RequestParam String id,
            @RequestParam String bid,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 异步上报曝光数据（使用@Async）
        pixelService.recordImpressionAsync(id, bid,
            request.getRemoteAddr(),
            getUserAgent(request));

        // 立即返回1x1透明GIF
        response.setContentType("image/gif");
        // ...
    }

    /**
     * 点击监测 (支持302重定向)
     */
    @GetMapping("/click")
    public void click(
            @RequestParam String id,
            @RequestParam String bid,
            @RequestParam String landingUrl,
            HttpServletResponse response) throws IOException {

        // 异步记录点击（使用@Async）
        pixelService.recordClickAsync(id, bid,
            request.getRemoteAddr(),
            getUserAgent(request));

        // 重定向到落地页
        response.sendRedirect(landingUrl);
    }
}
```

---

## 五、数据库设计

### 5.1 表结构优化

**改进点：**
1. 添加索引优化
2. 规范字段命名
3. 添加字段注释
4. 分表策略设计（按时间/ID）
5. 软删除统一处理

**核心表SQL示例：**

```sql
-- 广告主表
CREATE TABLE `advertiser` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `advertiser_audit_id` BIGINT NOT NULL COMMENT '审核表ID',
  `agent_id` BIGINT NOT NULL COMMENT '代理商ID',
  `name` VARCHAR(100) NOT NULL COMMENT '广告主名称',
  `industry_id` BIGINT COMMENT '行业ID',
  `contact_name` VARCHAR(50) COMMENT '联系人',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `contact_email` VARCHAR(100) COMMENT '联系邮箱',
  `audit_status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态 0-待审核 1-已通过 2-已拒绝',
  `balance` DECIMAL(10,2) DEFAULT 0 COMMENT '账户余额',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_agent_id` (`agent_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告主表';

-- 推广活动表
CREATE TABLE `campaign` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `advertiser_id` BIGINT NOT NULL COMMENT '广告主ID',
  `name` VARCHAR(100) NOT NULL COMMENT '活动名称',
  `budget` DECIMAL(10,2) NOT NULL COMMENT '总预算',
  `daily_budget` DECIMAL(10,2) COMMENT '每日预算',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-暂停 1-投放中 2-已完成',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_advertiser_id` (`advertiser_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_end_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广活动表';

-- 广告组表
CREATE TABLE `ad_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `campaign_id` BIGINT NOT NULL COMMENT '活动ID',
  `name` VARCHAR(100) NOT NULL COMMENT '广告组名称',
  `bid_price` DECIMAL(10,2) NOT NULL COMMENT '出价(元)',
  `bid_type` TINYINT NOT NULL DEFAULT 1 COMMENT '出价方式 1-CPC 2-CPM 3-CPA',
  `target_area` JSON COMMENT '地域定向',
  `target_os` JSON COMMENT '操作系统定向',
  `target_device` JSON COMMENT '设备定向',
  `target_behavior` JSON COMMENT '行为定向',
  `target_interests` JSON COMMENT '兴趣定向',
  `target_audience` JSON COMMENT '受众定向',
  `freq_cap` INT COMMENT '频次控制',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0-暂停 1-投放中',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告组表';

-- 监测数据表 (分表)
CREATE TABLE `tracking_log_202501` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `request_id` VARCHAR(50) NOT NULL COMMENT '请求ID',
  `bid_id` VARCHAR(50) NOT NULL COMMENT '竞价ID',
  `ad_group_id` BIGINT NOT NULL COMMENT '广告组ID',
  `tracking_type` TINYINT NOT NULL COMMENT '监测类型 1-曝光 2-点击 3-转化',
  `device_ip` VARCHAR(50) COMMENT '设备IP',
  `device_ua` VARCHAR(500) COMMENT 'User-Agent',
  `tracking_time` DATETIME NOT NULL COMMENT '监测时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_request_type` (`request_id`, `tracking_type`),
  KEY `idx_bid_id` (`bid_id`),
  KEY `idx_ad_group_id` (`ad_group_id`),
  KEY `idx_tracking_time` (`tracking_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监测数据表';
```

### 5.2 分表策略

**水平分表：**
- `tracking_log` 按月分表：`tracking_log_202501`, `tracking_log_202502`
- `impression_log` 按日分表（大数据量场景）

**垂直分表：**
- 将不常用字段分离到扩展表
- 大文本字段独立存储

---

## 六、前端架构设计

### 6.1 项目结构

```
zhuque-dashboard-fe/
├── src/
│   ├── api/                 # API接口
│   │   ├── modules/
│   │   │   ├── auth.ts
│   │   │   ├── advertiser.ts
│   │   │   ├── campaign.ts
│   │   │   └── creative.ts
│   │   └── index.ts
│   │
│   ├── assets/              # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   └── icons/
│   │
│   ├── components/          # 公共组件
│   │   ├── common/
│   │   │   ├── Table.vue
│   │   │   ├── Form.vue
│   │   │   └── Dialog.vue
│   │   └── business/
│   │       ├── CreativeUpload.vue
│   │       └── TargetingConfig.vue
│   │
│   ├── composables/         # 组合式函数
│   │   ├── useTable.ts
│   │   ├── useForm.ts
│   │   ├── useAuth.ts
│   │   └── usePermission.ts
│   │
│   ├── router/              # 路由
│   │   └── index.ts
│   │
│   ├── stores/              # Pinia状态管理
│   │   ├── auth.ts
│   │   ├── app.ts
│   │   └── user.ts
│   │
│   ├── types/               # TypeScript类型
│   │   ├── api.d.ts
│   │   ├── global.d.ts
│   │   └── components.d.ts
│   │
│   ├── utils/               # 工具函数
│   │   ├── request.ts       # Axios封装
│   │   ├── auth.ts          # Token管理
│   │   ├── validate.ts      # 验证函数
│   │   └── format.ts        # 格式化函数
│   │
│   ├── views/               # 页面视图
│   │   ├── login/
│   │   │   └── index.vue
│   │   ├── dashboard/
│   │   │   └── index.vue
│   │   ├── advertiser/
│   │   │   ├── list.vue
│   │   │   └── detail.vue
│   │   ├── campaign/
│   │   ├── creative/
│   │   └── layout/
│   │       └── index.vue
│   │
│   ├── App.vue
│   └── main.ts
│
├── public/
├── package.json
├── vite.config.ts
├── tsconfig.json
└── tailwind.config.js
```

### 6.2 核心代码示例

**API封装 (src/api/modules/auth.ts):**

```typescript
import request from '@/utils/request'
import type { LoginForm, LoginResult, UserInfo } from '@/types/api'

/**
 * 登录
 */
export function login(data: LoginForm) {
  return request.post<LoginResult>('/api/auth/login', data)
}

/**
 * 刷新Token
 */
export function refreshToken(refreshToken: string) {
  return request.post<{ accessToken: string }>('/api/auth/refresh', {
    refreshToken
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get<UserInfo>('/api/auth/user-info')
}

/**
 * 登出
 */
export function logout() {
  return request.post('/api/auth/logout')
}
```

**组合式函数 (src/composables/useTable.ts):**

```typescript
import { ref, computed } from 'vue'
import type { Ref } from 'vue'

export function useTable<T>(fetchFn: (params: any) => Promise<any>) {
  const loading = ref(false)
  const data = ref<T[]>([]) as Ref<T[]>
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  const fetchData = async (params?: any) => {
    loading.value = true
    try {
      const res = await fetchFn({
        page: currentPage.value,
        size: pageSize.value,
        ...params
      })
      data.value = res.records
      total.value = res.total
    } finally {
      loading.value = false
    }
  }

  const pageChange = (page: number) => {
    currentPage.value = page
    fetchData()
  }

  const sizeChange = (size: number) => {
    pageSize.value = size
    currentPage.value = 1
    fetchData()
  }

  return {
    loading,
    data,
    total,
    currentPage,
    pageSize,
    fetchData,
    pageChange,
    sizeChange
  }
}
```

**Pinia Store (src/stores/auth.ts):**

```typescript
import { defineStore } from 'pinia'
import { login, getUserInfo } from '@/api/modules/auth'
import type { LoginForm, UserInfo } from '@/types/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  const refreshTokenValue = ref<string>('')

  const isLogin = computed(() => !!token.value)

  async function loginAction(form: LoginForm) {
    const res = await login(form)
    token.value = res.accessToken
    refreshTokenValue.value = res.refreshToken
    await getUserInfoAction()
  }

  async function getUserInfoAction() {
    const res = await getUserInfo()
    userInfo.value = res
  }

  function logoutAction() {
    token.value = ''
    userInfo.value = null
    refreshTokenValue.value = ''
  }

  return {
    token,
    userInfo,
    isLogin,
    loginAction,
    getUserInfoAction,
    logoutAction
  }
})
```

---

## 七、开发规范

### 7.1 代码规范

**Java后端：**
- 遵循阿里巴巴Java开发手册
- 使用MapStruct进行对象转换
- 统一异常处理
- 统一日志规范

**前端：**
- ESLint + Prettier
- 组合式API优先
- TypeScript严格模式
- 组件命名规范（PascalCase）

### 7.2 API设计规范

**RESTful风格：**
```
GET    /api/advertisers        # 列表
POST   /api/advertisers        # 创建
GET    /api/advertisers/{id}   # 详情
PUT    /api/advertisers/{id}   # 更新
DELETE /api/advertisers/{id}   # 删除
PUT    /api/advertisers/{id}/status  # 更新状态
```

**统一响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1704451200000
}
```

### 7.3 版本管理

**Git工作流：**
- `master`: 生产环境
- `develop`: 开发环境
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复

**提交规范：**
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具变更
```

---

## 八、测试策略

### 8.1 测试金字塔

```
        /\
       /E2E\          少量端到端测试
      /------\
     /  集成  \        适量的集成测试
    /----------\
   /   单元测试  \     大量单元测试
  /--------------\
```

### 8.2 测试工具

**后端：**
- JUnit 5: 单元测试
- Mockito: Mock框架
- TestContainers: 集成测试
- RestAssured: API测试

**前端：**
- Vitest: 单元测试
- Vue Test Utils: 组件测试
- Playwright: E2E测试

### 8.3 测试覆盖率目标

- 单元测试覆盖率: ≥80%
- 关键业务逻辑覆盖率: 100%
- 集成测试覆盖核心流程

---

## 九、部署架构

### 9.1 开发环境

```
Docker Compose
├── MySQL 8.0
├── Redis 7.0
└── 应用服务
```

### 9.2 生产环境（轻量级部署）

**部署方式一：Docker Compose（推荐）**

适用于：中小规模部署，单机或少量服务器

```yaml
# docker-compose.yml
version: '3.8'
services:
  # MySQL数据库
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: zhuque
    volumes:
      - mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"
    restart: always

  # Redis缓存
  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data
    ports:
      - "6379:6379"
    restart: always

  # 后端应用
  zhuque-app:
    image: zhuque-app:latest
    environment:
      SPRING_PROFILES_ACTIVE: prod
      MYSQL_HOST: mysql
      REDIS_HOST: redis
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    restart: always

  # Nginx前端服务
  nginx:
    image: nginx:alpine
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./frontend-dist:/usr/share/nginx/html
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - zhuque-app
    restart: always

volumes:
  mysql-data:
  redis-data:
```

**部署命令：**
```bash
# 一键启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f zhuque-app

# 停止服务
docker-compose down

# 更新部署
docker-compose pull && docker-compose up -d
```

---

**部署方式二：Systemd服务（传统方式）**

适用于：无Docker环境，直接在Linux服务器运行

```bash
# 1. 安装Java 17+
sudo apt install openjdk-17-jre

# 2. 创建服务配置
cat > /etc/systemd/system/zhuque.service <<'EOF'
[Unit]
Description=Zhuque Ad Platform Application
After=network.target mysql.service

[Service]
Type=simple
User=zhuque
WorkingDirectory=/opt/zhuque
ExecStart=/usr/bin/java -jar /opt/zhuque/zhuque-app.jar \
  --spring.profiles.active=prod \
  --server.port=8080
Restart=always
RestartSec=10

EnvironmentFile=-/opt/zhuque/zhuque.conf

[Install]
WantedBy=multi-user.target
EOF

# 3. 启动服务
sudo systemctl daemon-reload
sudo systemctl enable zhuque
sudo systemctl start zhuque

# 4. 查看状态
sudo systemctl status zhuque
```

**一键部署脚本：**

```bash
#!/bin/bash
# deploy.sh

set -e

echo "=== 朱雀广告平台一键部署脚本 ==="

# 1. 备份当前版本
echo "备份当前版本..."
BACKUP_DIR="/opt/backup/zhuque-$(date +%Y%m%d-%H%M%S)"
mkdir -p $BACKUP_DIR
cp -r /opt/zhuque/zhuque-app.jar $BACKUP_DIR/ 2>/dev/null || true

# 2. 停止服务
echo "停止服务..."
sudo systemctl stop zhuque || docker-compose down

# 3. 部署新版本
echo "部署新版本..."
cp zhuque-app.jar /opt/zhuque/
cp -r frontend-dist /opt/zhuque/

# 4. 启动服务
echo "启动服务..."
sudo systemctl start zhuque || docker-compose up -d

# 5. 健康检查
echo "健康检查..."
for i in {1..30}; do
  if curl -f http://localhost:8080/actuator/health; then
    echo "部署成功！"
    exit 0
  fi
  echo "等待服务启动... ($i/30)"
  sleep 2
done

echo "部署失败，回滚..."
cp $BACKUP_DIR/zhuque-app.jar /opt/zhuque/
sudo systemctl restart zhuque
exit 1
```

---

**部署方式三：多实例负载均衡（生产推荐）**

使用Nginx做反向代理，多个应用实例

```nginx
# nginx.conf
upstream zhuque_backend {
    # 负载均衡策略：轮询
    least_conn;

    server 192.168.1.10:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 192.168.1.11:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 192.168.1.12:8080 weight=1 max_fails=3 fail_timeout=30s;
}

server {
    listen 80;
    server_name ad.example.com;

    # 前端静态资源
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://zhuque_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # RTB竞价接口（高性能要求）
    location /rtb/ {
        proxy_pass http://zhuque_backend;
        proxy_set_header Host $host;
        proxy_connect_timeout 1s;  # RTB需要快速响应
        proxy_send_timeout 1s;
        proxy_read_timeout 1s;
    }
}
```

---

**部署架构对比：**

| 方案 | 复杂度 | 成本 | 适用场景 |
|-----|--------|------|---------|
| Docker Compose | 低 | 低 | 开发/测试/小型生产 |
| Systemd | 低 | 低 | 单机生产环境 |
| Nginx + 多实例 | 中 | 中 | 中型生产环境 |
| Kubernetes | 高 | 高 | 大规模集群 |

**推荐选择：**
- **开发/测试环境**: Docker Compose
- **小型生产**（单机）: Docker Compose 或 Systemd
- **中型生产**（3-5台服务器）: Nginx + 多实例 + Docker
- **大型生产**（需要自动扩缩容）: 考虑迁移到K8s

### 9.3 CI/CD流程

```
Git Push
   ↓
GitHub Actions / GitLab CI
   ↓
1. 代码检查 (SonarQube / Checkstyle)
   ↓
2. 运行测试 (JUnit + Vitest)
   ↓
3. 构建应用 (Mvn Package + Npm Build)
   ↓
4. 构建镜像 (Docker Build - 可选)
   ↓
5. 部署 (Docker Compose / Systemd)
   ↓
6. 健康检查 & 回滚
```

**构建产物：**
- `zhuque-app.jar`: 可执行JAR包（包含所有模块）
- `frontend-dist/`: 前端静态资源（Nginx部署）
- `docker-compose.yml`: Docker Compose配置
- `deploy.sh`: 一键部署脚本
- `zhuque.service`: Systemd服务配置（可选）

---

## 十、实施计划

### Phase 1: 基础框架搭建 (2周)
- [ ] 初始化项目结构
- [ ] 配置开发环境
- [ ] 搭建基础模块
- [ ] 实现认证授权

### Phase 2: 核心业务开发 (4周)
- [ ] 广告主管理
- [ ] 推广活动管理
- [ ] 广告组管理
- [ ] 创意管理

### Phase 3: RTB竞价引擎 (3周)
- [ ] RTB协议实现
- [ ] 竞价逻辑
- [ ] DSP对接
- [ ] 监测服务

### Phase 4: 前端开发 (4周)
- [ ] 管理后台界面
- [ ] 数据可视化
- [ ] 实时监控

### Phase 5: 测试与优化 (2周)
- [ ] 单元测试
- [ ] 集成测试
- [ ] 性能优化
- [ ] 安全加固

### Phase 6: 部署上线 (1周)
- [ ] 部署配置
- [ ] 监控告警
- [ ] 文档完善

**总计：约16周（4个月）**

---

## 十一、技术风险与应对

| 风险 | 影响 | 应对措施 |
|-----|------|---------|
| RTB协议对接复杂 | 高 | 提前技术预研，准备降级方案 |
| 高并发性能 | 高 | 压力测试，缓存优化，读写分离 |
| 数据一致性 | 中 | 分布式事务，最终一致性 |
| 第三方依赖稳定性 | 中 | 熔断降级，多服务商备份 |
| 团队技术栈切换 | 低 | 技术分享，代码审查 |

---

## 十二、总结

本技术方案从架构设计、技术选型、模块设计、开发规范、测试策略、部署架构等方面进行了全面规划，确保：

1. **技术先进性**：采用最新稳定版本的技术栈
2. **架构合理性**：单体应用 + 模块化分层，清晰明确
3. **接口驱动**：模块间通过接口交互，降低耦合
4. **可维护性**：完善的代码规范和文档
5. **安全性**：JWT认证、RBAC权限、数据加密
6. **高性能**：缓存、异步、读写分离
7. **可观测性**：日志、监控、告警
8. **部署简单**：单一应用包，无需复杂的微服务编排

---

**文档版本：** v2.1
**编写日期：** 2025-01-05
**更新日期：** 2026-01-09
**架构调整：** 采用单体应用架构 + 模块化分层设计
**项目结构：** 简化common模块，统一web模块命名
**包名规范：** wake.su
**维护者：** 开发团队
