# RTB 竞价接口设计文档

> **模块**: java-bid (独立竞价服务模块)
> **状态**: 已实现

---

## 1. 概述

### 1.1 目标

实现基于 OpenRTB 2.6 协议的实时竞价（RTB）接口，作为 DSP（需求方平台）接收 SSP/Exchange 的竞价请求，返回广告展示内容和出价。

### 1.2 核心流程

```
SSP/Exchange                    朱雀 DSP
    │                              │
    │  ① Bid Request (OpenRTB)    │
    ├─────────────────────────────>│
    │                              │
    │                      ② 解析请求
    │                      ③ 匹配广告
    │                      ④ 计算出价
    │                      ⑤ 构造响应
    │                              │
    │  ⑥ Bid Response (OpenRTB)   │
    │<─────────────────────────────┤
    │                              │
    │      ⑦ 赢拍/展示              │
    │                              │
    │  ⑧ Win Notice / 展示通知     │
    ├─────────────────────────────>│
```

---

## 2. 接口定义

### 2.1 竞价端点

| 属性 | 值 |
|------|-----|
| 路径 | `/openrtb/bid` |
| 方法 | `POST` |
| Content-Type | `application/json` |
| 认证 | 签名验证（可选） |

### 2.2 响应规则

| HTTP 状态 | 说明 |
|-----------|------|
| `200 OK` + `BidResponse` | 有竞价，返回竞价响应 |
| `204 No Content` | 无竞价，不返回任何内容 |
| `400 Bad Request` | 请求无效，返回错误信息 |
| `500 Internal Server Error` | 服务器错误 |

---

## 3. 业务流程设计

### 3.1 完整处理流程

```
┌─────────────────────────────────────────────────────────────────┐
│                       竞价请求处理流程                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ① 请求解析与校验                                          │   │
│  │   - 解析 BidRequest JSON                                  │   │
│  │   - 校验必需字段                                          │   │
│  │   - 提取关键信息                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ② 特征提取                                                │   │
│  │   - 设备: IP, UA, GEO                                      │   │
│  │   - 用户: user.id                                         │   │
│  │   - 广告位: 尺寸, 类型, 底价                               │   │
│  │   - 上下文: domain, page, app                              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ③ 候选广告匹配                                            │   │
│  │   查询条件:                                                │   │
│  │   - Campaign.status = 1 (进行中)                          │   │
│  │   - AdGroup.status = 1 (进行中)                            │   │
│  │   - Ad.status = 1 (进行中)                                  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ④ 定向匹配 (TargetingMatchService)                       │   │
│  │   ✓ 地域匹配 (targeting_geo)                               │   │
│  │   ✓ 设备/OS匹配 (targeting_device, targeting_os)            │   │
│  │   ✓ 时段匹配 (schedule_type, schedule_config)              │   │
│  │   ✓ 人群包匹配 (targeting_user_segments)                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⑤ 过滤处理 (FrequencyCapService, BudgetService)         │   │
│  │   ✗ 预算耗尽: daily_budget_used >= daily_budget            │   │
│  │   ✗ 时间超出: 当前时间不在 startTime~endTime 范围          │   │
│  │   ✗ 频次超限: Redis记录的展示次数 >= frequency_cap         │   │
│  │   ✗ 品牌安全: 页面IAB类别在排除列表中                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⑥ 排序与选择                                              │   │
│  │   排序公式: weight × bid_price × quality_score            │   │
│  │   选择: Top 1 (通常只返回一个最优竞价)                     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⑦ 出价计算 (BidPriceService)                             │   │
│  │   根据出价策略计算:                                         │   │
│  │   - 固定CPM: base_bid_price                                │   │
│  │   - 智能出价: base_bid_price × pCTR系数                     │   │
│  │   - 检查范围: [bid_floor, max_bid]                          │   │
│  │   转换单位: 元/千次 → 微元                                  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⑧ 构造响应 (CreativeAssemblyService)                      │   │
│  │   - 选择素材 (RtbMaterial)                                  │   │
│  │   - 生成 ADM (创意内容)                                     │   │
│  │   - 拼接追踪链接                                            │   │
│  │   - 设置 ext 扩展字段                                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 匹配优先级

```
1. 必须条件 (全部满足)
   ├── Campaign.status = 1 (进行中)
   ├── Campaign.startTime <= now <= Campaign.endTime
   ├── AdGroup.status = 1
   ├── Ad.status = 1
   └── 创意素材存在且状态正常

2. 定向条件 (全部满足)
   ├── 地域匹配
   ├── 设备/OS匹配
   ├── 时段匹配
   └── 人群包匹配 (可选)

3. 过滤条件 (全部通过)
   ├── 预算未耗尽
   ├── 频次未超限
   ├── 品牌安全检查通过
   └── 出价 >= 底价

4. 排序选择
   └── 按 weight × bid_price 降序，取Top 1
```

---

## 4. 出价策略设计

### 4.1 策略类型

| 策略 | 说明 | 计算公式 |
|------|------|---------|
| 固定CPM | 固定出价 | `bid_price = base_bid_price` |
| 智能出价 | 根据pCTR动态调整 | `bid_price = base_bid_price × pCTR_coefficient` |
| 目标CPA | 根据转化成本优化 | `bid_price = target_cpa × pCVR × pCTR` |
| 最高赢价 | 以最大可能价格竞价 | `bid_price = max_bid` |

### 4.2 出价约束

```java
// 出价必须满足以下约束
bid_floor ≤ bid_price ≤ max_bid
bid_price ≥ request.imp[].bidfloor

// 最终出价转换为 OpenRTB 单位（微元/千次）
price_micros = bid_price * 1000
```

### 4.3 出价计算服务接口

```java
public interface BidPriceService {
    /**
     * 计算最终出价
     *
     * @param adGroup 广告组
     * @param requestBidFloor 请求底价
     * @param predictedCtr 预测CTR (0~1)
     * @return 出价（微元/千次单位）
     */
    Long calculateBidPrice(RtbAdGroupDO adGroup,
                          BigDecimal requestBidFloor,
                          Double predictedCtr);
}
```

---

## 5. 定向匹配设计

### 5.1 地域定向 (Geo Targeting)

```java
// 请求中的地理位置
request.device.geo.country   // 国家代码，如 "CN"
request.device.geo.region    // 省份代码，如 "Beijing"

// 广告组配置
adGroup.targetingGeo = ["CN", "CN-11", "CN-31"]  // JSON数组

// 匹配规则
// 1. 如果配置了国家代码，则国家必须匹配
// 2. 如果配置了国家+省份，则省份必须匹配
// 3. 空 = 不限制地域
```

### 5.2 设备/OS定向

```java
// 根据 User-Agent 解析设备类型
DeviceType: mobile | tablet | desktop | tv
OS: iOS | Android | Windows | macOS | Linux

// 广告组配置
adGroup.targetingDevice = ["mobile", "tablet"]
adGroup.targetingOs = ["iOS", "Android"]

// 匹配: 设备类型 AND 操作系统 都在配置列表中
```

### 5.3 时段定向

```java
// schedule_type: 1=全天 / 2=工作日 / 3=自定义
// schedule_config: JSON配置

// 全天: 不限制
// 工作日: 周一到周五
// 自定义:
{
  "time_ranges": ["09:00-12:00", "18:00-22:00"],
  "weekdays": [1, 2, 3, 4, 5, 6, 7]  // 1=周一, 7=周日
}
```

### 5.4 人群包定向

```java
// 广告组配置
adGroup.targetingUserSegments = ["seg_001", "seg_002"]

// 匹配逻辑
// 1. 从请求中获取用户ID (user.id)
// 2. 查询用户所属的人群包
// 3. 如果广告组配置了人群包，用户必须至少属于其中一个
// 4. 空 = 不限制人群
```

---

## 6. 频次控制设计

### 6.1 频次配置

```java
// 广告组配置
adGroup.frequencyCap = 10        // 展示次数上限
adGroup.frequencyCapPeriod = 2   // 1=小时 / 2=天 / 3=周 / 4=月
```

### 6.2 Redis 缓存设计

```
┌─────────────────────────────────────────────────────────────┐
│                    频次控制 Redis Key 设计                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  KEY: freq_cap:{adGroupId}:{userId}:{period}               │
│  TTL: 到周期结束时间                                         │
│  VALUE: 展示次数 (Integer)                                   │
│                                                             │
│  示例:                                                       │
│  freq_cap:123:456789:2 (用户456789在广告组123的今日展示次数)     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 6.3 频次检查逻辑

```java
public boolean checkFrequencyCap(String userId, Long adGroupId,
                                 Integer frequencyCap, Integer period) {
    String key = buildFreqCapKey(adGroupId, userId, period);

    // 获取当前计数
    Integer count = redisTemplate.opsForValue().get(key);
    if (count == null) count = 0;

    // 检查是否超限
    return count < frequencyCap;
}

// 展示后更新计数
public void recordImpression(String userId, Long adGroupId, Integer period) {
    String key = buildFreqCapKey(adGroupId, userId, period);
    redisTemplate.opsForValue().increment(key);

    // 设置过期时间（到周期结束）
    Duration ttl = calculateTtl(period);
    redisTemplate.expire(key, ttl);
}
```

---

## 7. 预算控制设计

### 7.1 预算类型

| 预算类型 | 字段 | 说明 |
|---------|------|------|
| 活动总预算 | Campaign.lifetimeBudget | 整个活动的总预算 |
| 活动日预算 | Campaign.dailyBudget | 活动每日预算 |
| 广告组日预算 | AdGroup.dailyBudget | 广告组每日预算 |

### 7.2 预算检查

```java
// 检查广告组日预算
if (adGroup.getDailyBudget() != null) {
    if (adGroup.getDailyBudgetUsed() >= adGroup.getDailyBudget()) {
        return null; // 预算耗尽，不竞价
    }
}

// 检查活动预算
if (campaign.getDailyBudget() != null) {
    // 查询活动今日已消耗（汇总所有广告组）
    BigDecimal usedToday = sumCampaignDailyUsed(campaign.getId());
    if (usedToday >= campaign.getDailyBudget()) {
        return null;
    }
}
```

---

## 8. 追踪链接设计

### 8.1 OpenRTB 宏替换

OpenRTB 支持在追踪链接中使用宏，竞价时动态替换：

| 宏 | 说明 | 示例值 |
|----|------|--------|
| `${AUCTION_ID}` | 竞价请求ID | "80ce30c5..." |
| `${AUCTION_IMP_ID}` | 展示机会ID | "1" |
| `${AUCTION_SEAT_ID}` | 座位ID | "seat_123" |
| `${AUCTION_BID_ID}` | 竞价ID | "bid_456" |
| `${DOMAIN}` | 页面域名 | "example.com" |
| `${PAGE}` | 页面URL | "http://example.com/page" |
| `${DEVICE_IP}` | 设备IP | "192.168.1.1" |
| `${DEVICE_UA}` | User-Agent | "Mozilla/5.0..." |

### 8.2 追踪链接类型

```java
// 展示追踪 (Impression Tracking)
lurl: https://tracker.example.com/imp?ad_id=123&imp_id=${AUCTION_IMP_ID}

// 赢出追踪 (Win Notice)
nurl: https://tracker.example.com/win?ad_id=123&price=${AUCTION_PRICE}

// 点击追踪 (Click Tracking)
curl: https://tracker.example.com/click?ad_id=123&imp_id=${AUCTION_IMP_ID}
```

### 8.3 追踪参数拼接

```java
private String appendTrackingParams(String baseUrl, BidRequest request,
                                     Imp imp, Bid bid, RtbAdDO ad) {
    StringBuilder url = new StringBuilder(baseUrl);

    // 基础参数
    url.append("?utm_source=rtb");
    url.append("&campaign_id=").append(ad.getCampaignId());
    url.append("&ad_group_id=").append(ad.getAdGroupId());
    url.append("&ad_id=").append(ad.getId());

    // 用户自定义参数
    if (ad.getTrackingParams() != null) {
        url.append("&").append(ad.getTrackingParams());
    }

    return url.toString();
}
```

---

## 9. 创意组装设计

### 9.1 创意类型映射

| Imp类型 | RtbMaterial.format | ADM格式 |
|---------|-------------------|---------|
| `imp.banner` | 1 (Banner) | Banner ADM |
| `imp.video` | 2 (Video) | VAST XML |
| `imp.audio` | 3 (Audio) | Audio ADM |
| `imp.native` | 4 (Native) | Native ADM |

### 9.2 ADM 生成

```java
// Banner ADM
String buildBannerAdm(RtbMaterialDO material, Imp imp, String clickUrl) {
    return String.format(
        "<!--ADM--><img src=\"%s\" width=\"%d\" height=\"%d\" " +
        "onclick=\"window.open('%s')\"/>",
        material.getFileUrl(),
        material.getWidth(),
        material.getHeight(),
        clickUrl
    );
}

// Video ADM (VAST)
String buildVideoAdm(RtbMaterialDO material, String clickUrl, String impTracker) {
    // 返回 VAST XML 格式
}

// Native ADM
String buildNativeAdm(RtbMaterialDO material, Map<String, String> assets, String clickUrl) {
    // 返回 Native JSON 格式
}
```

---

## 10. 文件结构

### 10.1 模块结构

```
java-bid/                                 # 竞价服务模块 (独立模块)
├── src/main/java/wake/su/zhuque/bid/
│   ├── JavaBidApplication.java           # 启动类
│   │
│   ├── controller/                       # 控制器层
│   │   └── OpenRtbController.java        # 竞价接口入口 (/openrtb/bid)
│   │
│   ├── service/                          # 服务接口层
│   │   ├── RtbBidService.java            # 竞价核心服务 (接口)
│   │   ├── BidPriceService.java          # 出价计算服务 (接口)
│   │   ├── TargetingMatcher.java         # 定向匹配服务 (接口)
│   │   ├── FrequencyCapService.java      # 频次控制服务 (接口)
│   │   ├── BudgetControlService.java     # 预算控制服务 (接口)
│   │   ├── CreativeAssemblyService.java  # 创意组装服务 (接口)
│   │   ├── filter/
│   │   │   └── BidFilter.java            # 过滤器接口
│   │   ├── matcher/
│   │   │   ├── GeoMatcher.java           # 地域匹配接口
│   │   │   ├── DeviceMatcher.java        # 设备匹配接口
│   │   │   ├── ScheduleMatcher.java      # 时段匹配接口
│   │   │   └── TargetingMatcher.java     # 定向匹配组合接口
│   │   ├── pricing/
│   │   │   ├── BidPriceService.java      # 出价服务接口
│   │   │   └── BidPriceStrategy.java     # 出价策略接口
│   │   ├── budget/
│   │   │   └── BudgetControlService.java # 预算控制接口
│   │   ├── frequency/
│   │   │   └── FrequencyCapService.java  # 频次控制接口
│   │   └── creative/
│   │       └── CreativeAssemblyService.java # 创意组装接口
│   │
│   ├── service/impl/                     # 服务实现层
│   │   ├── RtbBidServiceImpl.java        # 竞价核心服务实现
│   │   ├── filter/
│   │   │   ├── StatusFilter.java         # 状态过滤器 (Order=1)
│   │   │   ├── TargetingFilter.java      # 定向过滤器 (Order=2)
│   │   │   ├── BudgetFilter.java         # 预算过滤器 (Order=3)
│   │   │   └── FrequencyFilter.java      # 频次过滤器 (Order=4)
│   │   ├── matcher/
│   │   │   ├── GeoMatcherImpl.java       # 地域匹配实现
│   │   │   ├── DeviceMatcherImpl.java    # 设备匹配实现
│   │   │   ├── ScheduleMatcherImpl.java  # 时段匹配实现
│   │   │   └── TargetingMatcherImpl.java # 定向匹配组合实现
│   │   ├── pricing/
│   │   │   ├── BidPriceServiceImpl.java  # 出价服务实现
│   │   │   ├── FixedCpmStrategy.java     # 固定CPM策略 (Type=1)
│   │   │   └── SmartBidStrategy.java     # 智能出价策略 (Type=2)
│   │   ├── budget/
│   │   │   └── BudgetControlServiceImpl.java # 预算控制实现 (CAS SQL)
│   │   ├── frequency/
│   │   │   └── FrequencyCapServiceImpl.java # 频次控制实现 (Redis Lua)
│   │   └── creative/
│   │       └── CreativeAssemblyServiceImpl.java # 创意组装实现
│   │
│   ├── context/                          # 上下文对象
│   │   ├── BidContext.java               # 竞价上下文 (携带请求信息)
│   │   └── BidCandidate.java             # 竞价候选 (广告组+广告+分数)
│   │
│   ├── dto/openrtb/                      # OpenRTB DTO
│   │   ├── BidRequest.java               # 竞价请求
│   │   ├── BidResponse.java              # 竞价响应
│   │   ├── SeatBid.java                  # 座位竞价
│   │   ├── Bid.java                      # 竞价对象
│   │   ├── Imp.java                      # 展示机会
│   │   ├── Device.java                   # 设备信息
│   │   ├── Geo.java                      # 地理信息
│   │   ├── User.java                     # 用户信息
│   │   ├── Site.java                     # 站点信息
│   │   ├── App.java                      # 应用信息
│   │   └── Ext.java                      # 扩展字段
│   │
│   └── config/                           # 配置类
│       └── RedisConfig.java              # Redis配置
│
└── src/main/resources/
    ├── application.yml                   # 应用配置
    └── scripts/
        └── frequency_check_and_incr.lua  # 频次控制 Lua 脚本
```

### 10.2 设计模式

| 模式 | 应用场景 |
|------|---------|
| **责任链模式** | BidFilter 过滤器链 (Status → Targeting → Budget → Frequency) |
| **策略模式** | BidPriceStrategy 出价策略 (FixedCPM / SmartBid) |
| **工厂模式** | BidPriceService 根据 bid_strategy 选择策略 |
| **上下文模式** | BidContext 携带请求信息传递各层 |
| **原子操作** | CAS SQL (预算) / Redis Lua (频次) |

---

## 11. 核心服务接口定义

### 11.1 RtbBidService

```java
public interface RtbBidService {
    /**
     * 处理竞价请求
     * @param request OpenRTB BidRequest
     * @return BidResponse 有竞价，null 无竞价
     */
    BidResponse processBid(BidRequest request);
}
```

### 11.2 BidFilter (责任链模式)

```java
public interface BidFilter {
    /**
     * 测试广告组是否通过此过滤器
     * @param context 竞价上下文
     * @param adGroup 广告组
     * @return true=通过, false=过滤
     */
    boolean test(BidContext context, RtbAdGroupDO adGroup);

    /**
     * 获取过滤器执行顺序
     */
    int order();
}
```

**过滤器执行顺序:**

| 过滤器 | Order | 说明 |
|--------|-------|------|
| StatusFilter | 1 | 基本状态检查 |
| TargetingFilter | 2 | 地域/设备/时段定向 |
| BudgetFilter | 3 | 预算预检查 (只查不扣) |
| FrequencyFilter | 4 | 频次预检查 (只查不累加) |

### 11.3 TargetingMatcher

```java
public interface TargetingMatcher {
    /**
     * 综合定向匹配
     */
    boolean matches(BidContext context, RtbAdGroupDO adGroup);
}

public interface GeoMatcher {
    boolean matches(BidContext context, RtbAdGroupDO adGroup);
}

public interface DeviceMatcher {
    boolean matches(BidContext context, RtbAdGroupDO adGroup);
}

public interface ScheduleMatcher {
    boolean matches(BidContext context, RtbAdGroupDO adGroup);
}
```

### 11.4 BidPriceStrategy (策略模式)

```java
public interface BidPriceStrategy {
    /**
     * 计算出价
     * @param basePrice 基础出价
     * @param maxPrice 最高出价
     * @param minPrice 最低出价
     * @param floorPrice 底价
     * @param predictedCtr 预测CTR (0~1)
     * @return 出价（微元/千次单位）
     */
    Long calculate(BigDecimal basePrice, BigDecimal maxPrice,
                  BigDecimal minPrice, BigDecimal floorPrice,
                  Double predictedCtr);

    /**
     * 获取策略类型
     */
    Integer getType();
}
```

**策略类型:**

| 策略 | Type | 说明 |
|------|------|------|
| FixedCpmStrategy | 1 | 固定CPM出价 |
| SmartBidStrategy | 2 | 智能出价 (根据pCTR调整) |

### 11.5 FrequencyCapService (Redis Lua 原子操作)

```java
public interface FrequencyCapService {
    /**
     * 检查频次是否超限 (只查不累加)
     */
    boolean checkFrequency(String userId, RtbAdGroupDO adGroup);

    /**
     * 尝试记录展示 (原子操作: 检查+累加)
     * @return true=成功, false=超限
     */
    boolean tryRecord(String userId, RtbAdGroupDO adGroup);

    /**
     * 回滚展示计数
     */
    void rollback(String userId, RtbAdGroupDO adGroup);

    /**
     * 构建频次Key
     */
    String buildKey(Long adGroupId, String userId);
}
```

**频次Key设计:**
```
freq:day:{date}:{adGroupId}:{userId}
freq:hour:{date}:{hour}:{adGroupId}:{userId}
freq:week:{week}:{adGroupId}:{userId}
freq:month:{year}:{month}:{adGroupId}:{userId}
```

### 11.6 BudgetControlService (CAS SQL 原子操作)

```java
public interface BudgetControlService {
    /**
     * 检查预算是否充足 (只查不扣)
     */
    boolean checkBudget(RtbAdGroupDO adGroup, BigDecimal bidPrice);

    /**
     * 尝试扣减预算 (CAS原子操作)
     * @return true=成功, false=预算不足或并发失败
     */
    boolean tryDeduct(RtbAdGroupDO adGroup, BigDecimal bidPrice);

    /**
     * 回滚预算
     */
    void rollback(RtbAdGroupDO adGroup, BigDecimal bidPrice);
}
```

### 11.7 CreativeAssemblyService

```java
public interface CreativeAssemblyService {
    /**
     * 构造 ADM (创意素材)
     */
    String buildAdm(RtbAdDO ad, RtbCreativeDO creative, BidContext context);

    /**
     * 构建点击链接
     */
    String buildClickUrl(String baseUrl, BidContext context);

    /**
     * 构建展示追踪链接
     */
    String buildImpressionUrl(String baseUrl, BidContext context);

    /**
     * 构建赢拍追踪链接
     */
    String buildWinUrl(String baseUrl, BidContext context, Long price);
}
```

---

## 12. 业务流程实现

### 12.1 竞价处理流程

```
┌─────────────────────────────────────────────────────────────────┐
│                       竞价请求处理流程                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ① 请求解析与校验 (OpenRtbController)                     │   │
│  │   - 接收 BidRequest JSON                                 │   │
│  │   - 校验必需字段 (imp)                                    │   │
│  │   - 调用 RtbBidService.processBid()                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ② 获取候选广告组 (RtbBidServiceImpl.getCandidates)       │   │
│  │   - 查询进行中的 Campaign                                 │   │
│  │   - 查询进行中的 AdGroup                                  │   │
│  │   - 查询进行中的 Ad                                       │   │
│  │   - 组装 BidCandidate 列表                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ③ 预过滤阶段 (RtbBidServiceImpl.preFilter)              │   │
│  │   按顺序执行 BidFilter 责任链:                            │   │
│  │   1. StatusFilter - 基本状态检查                         │   │
│  │   2. TargetingFilter - 定向匹配                          │   │
│  │   3. BudgetFilter - 预算预检查                           │   │
│  │   4. FrequencyFilter - 频次预检查                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ④ 计算出价和分数 (RtbBidServiceImpl)                     │   │
│  │   - BidPriceService.calculateBidPrice()                 │   │
│  │   - candidate.setBidPrice()                              │   │
│  │   - candidate.calculateScore()                          │   │
│  │   - 按分数排序                                           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⑤ 资源扣减阶段 (RtbBidServiceImpl.trySelectWinner)      │   │
│  │   按排序顺序遍历候选:                                     │   │
│  │   1. BudgetControlService.tryDeduct() - CAS SQL         │   │
│  │   2. FrequencyCapService.tryRecord() - Redis Lua        │   │
│  │   3. 成功则选中，失败则继续下一个                         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ↓                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ ⑥ 构造响应 (RtbBidServiceImpl.buildResponse)            │   │
│  │   - CreativeAssemblyService.buildAdm()                  │   │
│  │   - 组装 BidResponse                                     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 12.2 原子操作保证

**预算扣减 (CAS SQL):**
```sql
UPDATE rtb_ad_group
SET daily_budget_used = daily_budget_used + {bidPrice}
WHERE id = {adGroupId}
  AND daily_budget_used <= daily_budget - {bidPrice}
-- 返回 affected_rows = 1 表示成功
```

**频次控制 (Redis Lua):**
```lua
local key = KEYS[1]
local cap = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])

local current = tonumber(redis.call('GET', key)) or 0
if current < cap then
    redis.call('INCR', key)
    if ttl > 0 then
        redis.call('EXPIRE', key, ttl)
    end
    return 1  -- 成功
else
    return 0  -- 失败
end
```

---

## 13. 模块配置

### 13.1 application.yml

```yaml
server:
  port: 8081

spring:
  application:
    name: java-bid

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/zhuque_rtb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root

  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      password:
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: wake.su.zhuque.model.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

logging:
  level:
    wake.su.zhuque.bid: DEBUG
    org.springframework.data.redis: INFO
```

### 13.2 Redis Lua 脚本

**文件:** `src/main/resources/scripts/frequency_check_and_incr.lua`

```lua
-- 频次检查并递增 (原子操作)
-- ARGV[1]: 频次上限
-- ARGV[2]: TTL (秒)

local key = KEYS[1]
local cap = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])

local current = tonumber(redis.call('GET', key)) or 0
if current < cap then
    redis.call('INCR', key)
    if ttl > 0 then
        redis.call('EXPIRE', key, ttl)
    end
    return 1  -- 成功
else
    return 0  -- 失败
end
```

---

## 14. 编译与运行

### 14.1 编译

```bash
# 编译 java-bid 模块
mvn clean compile -pl java-bid -am

# 打包
mvn clean package -pl java-bid -am
```

### 14.2 运行

```bash
# 直接运行
java -jar java-bid/target/java-bid-2.0.0.jar

# 或使用 Maven 插件
mvn spring-boot:run -pl java-bid
```

### 14.3 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| java-bid | 8081 | RTB 竞价接口 |
| web-admin | 8080 | 管理后台 |

---

## 15. 错误处理

### 15.1 错误码

| 错误码 | 说明 |
|--------|------|
| INVALID_REQUEST | 请求格式无效 |
| MISSING_REQUIRED_FIELD | 缺少必需字段 |
| NO_MATCHING_AD | 无匹配广告 |
| BID_BELOW_FLOOR | 出价低于底价 |
| BUDGET_EXHAUSTED | 预算耗尽 |
| FREQUENCY_EXCEEDED | 频次超限 |

### 15.2 处理逻辑

```java
@RestControllerAdvice
public class RtbExceptionHandler {
    @ExceptionHandler(InvalidBidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidBidRequestException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_REQUEST", e.getMessage()));
    }
}
```

---

## 16. 性能考虑

### 16.1 性能目标

| 指标 | 目标值 |
|------|--------|
| 响应时间 | P99 < 100ms |
| 吞吐量 | > 10,000 QPS |
| 可用性 | 99.9% |

### 16.2 优化措施

1. **Redis 缓存**: 频次、预算、pCTR
2. **本地缓存**: 广告组配置热点数据
3. **异步日志**: 竞价日志异步写入
4. **连接池**: 数据库连接池优化
5. **索引优化**: 数据库查询索引

---

## 17. 监控指标

### 17.1 业务指标

- 竞价请求量 (QPS)
- 竞价成功率
- 平均出价
- 赢率
- 展示率

### 17.2 技术指标

- 响应时间 (P50, P99)
- 错误率
- 服务可用性
- Redis 命中率

---

## 18. 后续扩展

### 18.1 待实现功能

1. **智能出价**: 引入机器学习预测 pCTR/pCVR
2. **程序化创意**: 动态生成创意内容
3. **A/B 测试**: 支持创意多版本测试
4. **实时报告**: 实时竞价数据回传

### 18.2 优化方向

1. **多竞价**: 支持单次请求返回多个竞价
2. **价格拆分**: 支持底价拆分 (Price Splitting)
3. **私有市场**: 支持 PMP/Deal 交易
4. **视频广告**: 支持 VAST 4.0 完整功能

---

## 19. 参考资料

- [OpenRTB 2.6 Specification](https://iabtechlab.com/wp-content/uploads/2024/07/OpenRTB-v2_6-Final.pdf)
- [IAB Tech Lab](https://iabtechlab.com/)
- Prebid Server (开源 SSP 参考实现)

---

## 20. 性能优化方案 (JDK 21+)

### 20.1 优化目标

| 指标 | 优化前 | 优化后目标 | 提升幅度 |
|------|--------|-----------|---------|
| **P99 延迟** | ~100ms | < 60ms | 40% ↓ |
| **GC 暂停** | 10-50ms (G1 GC) | < 1ms (ZGC) | 95% ↓ |
| **并发能力** | 200 请求/平台线程 | 数万请求/虚拟线程 | 100x ↑ |
| **缓存命中率** | 0% | > 90% | - |
| **吞吐量** | ~5,000 QPS | > 10,000 QPS | 2x ↑ |

---

### 20.2 JVM GC 优化 (ZGC + ZGenerational)

#### 20.2.1 为什么选择 ZGC？

ZGC (Z Garbage Collector) 是 JDK 11+ 引入的低延迟垃圾回收器，JDK 21+ 增加了分代支持 (ZGenerational)：

| 特性 | G1 GC | ZGC (非分代) | ZGC + ZGenerational |
|------|-------|--------------|---------------------|
| Young GC 暂停 | 5-20ms | < 1ms | **< 0.1ms** |
| Old GC 暂停 | 50-200ms | < 2ms | **< 1ms** |
| Old GC 频率 | - | 较频繁 | **减少 10x** |
| 吞吐量影响 | 5-10% | 5-10% | **提升 15-20%** |

**ZGenerational 的优势**：
- 分离新生代和老年代，减少全堆扫描
- 新生代对象生命周期短，GC 效率更高
- 老年代 GC 频率大幅降低
- **RTB 场景特别适合**：请求对象生命周期短（几毫秒到几十毫秒）

#### 20.2.2 JVM 参数配置

**生产环境推荐参数**：

```bash
java \
  # ========== ZGC 配置 ==========
  -XX:+UseZGC \
  -XX:+ZGenerational \
  -XX:ZCollectionInterval=5 \
  -XX:+AlwaysPreTouch \
  -XX:+DisableExplicitGC \
  \
  # ========== 内存配置 ==========
  -Xms4g -Xmx4g \
  \
  # ========== GC 日志 ==========
  -Xlog:gc*:file=gc.log:time,tags:level=info \
  \
  -jar bid-java-2.0.0.jar
```

**参数说明**：

| 参数 | 说明 |
|------|------|
| `-XX:+UseZGC` | 启用 ZGC 垃圾回收器 |
| `-XX:+ZGenerational` | 启用分代 ZGC (JDK 21+) |
| `-XX:ZCollectionInterval=5` | GC 间隔（秒），控制触发频率 |
| `-XX:+AlwaysPreTouch` | 启动时预分配内存，避免运行时内存分配延迟 |
| `-XX:+DisableExplicitGC` | 禁用 System.gc()，避免手动触发 Full GC |
| `-Xms4g -Xmx4g` | 初始和最大堆内存 4GB（根据实际负载调整） |

#### 20.2.3 GC 监控指标

| 指标 | 正常范围 | 告警阈值 |
|------|---------|---------|
| Young GC 暂停时间 | < 0.1ms | > 0.5ms |
| Old GC 暂停时间 | < 1ms | > 5ms |
| GC 总时间占比 | < 5% | > 10% |
| Young GC 频率 | 1-5 次/秒 | > 10 次/秒 |
| Old GC 频率 | < 1 次/分钟 | > 5 次/分钟 |

**GC 日志分析**：

```bash
# 查看 GC 统计
grep "GC(" gc.log | tail -100

# 使用 jstat 实时监控
jstat -gcutil <pid> 1000

# 使用 JDK Mission Control (JMC) 可视化分析
jmc
```

---

### 20.3 Caffeine 本地缓存

#### 20.3.1 缓存架构

```
┌─────────────────────────────────────────────────────────────┐
│                      多层缓存架构                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  L1: Caffeine 本地缓存 (JVM 进程内)                   │  │
│  │  ├── candidateCache (10s) - 候选广告组               │  │
│  │  ├── campaignCache (30s) - 投放活动                  │  │
│  │  ├── adGroupCache (30s) - 广告组                     │  │
│  │  ├── adCache (30s) - 广告                            │  │
│  │  ├── creativeCache (30s) - 创意                      │  │
│  │  └── materialCache (60s) - 素材                      │  │
│  └──────────────────────────────────────────────────────┘  │
│                         ↓ 命中时                           │
│                   直接返回 (0-1ms)                          │
│                         ↓ 未命中                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  L2: MySQL 数据库                                     │  │
│  │  └── 查询并加载到缓存 (5-20ms)                        │  │
│  └──────────────────────────────────────────────────────┘  │
│                         ↓ 数据变更                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  手动刷新 API (CacheController)                      │  │
│  │  ├── POST /api/cache/candidates/refresh             │  │
│  │  ├── POST /api/cache/creatives/{id}/refresh         │  │
│  │  └── POST /api/cache/refresh-all                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 20.3.2 缓存配置

**CacheConfig.java**：

```java
@Configuration
@EnableCaching
public class CacheConfig {

    // 候选数据缓存：10秒刷新
    // 包含: Campaign + AdGroup + Ad 的组合数据
    // 用途: 竞价流程的候选匹配阶段
    public static final String CANDIDATE_CACHE = "candidateCache";

    // 创意缓存：30秒刷新
    // 用途: 最终响应组装时的创意素材加载
    public static final String CREATIVE_CACHE = "creativeCache";

    // 实体缓存：30秒刷新
    // 用途: 按需加载单个实体（Campaign/AdGroup/Ad）
    public static final String CAMPAIGN_CACHE = "campaignCache";
    public static final String AD_GROUP_CACHE = "adGroupCache";
    public static final String AD_CACHE = "adCache";

    // 素材缓存：60秒刷新
    // 用途: 图片/视频素材文件URL
    public static final String MATERIAL_CACHE = "materialCache";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 候选数据缓存 (10秒)
        cacheManager.registerCustomCache(CANDIDATE_CACHE,
            buildCaffeine(50, 200, 10).build());

        // 创意缓存 (30秒)
        cacheManager.registerCustomCache(CREATIVE_CACHE,
            buildCaffeine(100, 500, 30).build());

        // 实体缓存 (30秒)
        cacheManager.registerCustomCache(CAMPAIGN_CACHE,
            buildCaffeine(100, 500, 30).build());
        cacheManager.registerCustomCache(AD_GROUP_CACHE,
            buildCaffeine(200, 1000, 30).build());
        cacheManager.registerCustomCache(AD_CACHE,
            buildCaffeine(200, 1000, 30).build());

        // 素材缓存 (60秒)
        cacheManager.registerCustomCache(MATERIAL_CACHE,
            buildCaffeine(100, 500, 60).build());

        return cacheManager;
    }

    private Caffeine<Object, Object> buildCaffeine(int initialCapacity,
                                                    int maximumSize,
                                                    int expireAfterSeconds) {
        return Caffeine.newBuilder()
            .initialCapacity(initialCapacity)
            .maximumSize(maximumSize)
            .expireAfterWrite(expireAfterSeconds, TimeUnit.SECONDS)
            .recordStats()  // 启用统计，便于监控
            .removalListener((key, value, cause) -> {
                // 可选: 记录缓存移除事件
            });
    }
}
```

#### 20.3.3 缓存服务

**CandidateCacheService.java** (候选广告组缓存)：

```java
@Service
@RequiredArgsConstructor
public class CandidateCacheService {

    // 缓存所有活跃的候选广告组（Campaign + AdGroup + Ad 组合）
    @Cacheable(value = CacheConfig.CANDIDATE_CACHE, key = "'all'")
    public List<BidCandidate> getAllActiveCandidates() {
        // 1. 查询进行中的 Campaign
        // 2. 查询这些 Campaign 下的 AdGroup
        // 3. 查询每个 AdGroup 对应的 Ad
        // 4. 组装成 BidCandidate 列表
    }

    // 手动刷新候选缓存
    @CacheEvict(value = CacheConfig.CANDIDATE_CACHE, key = "'all'")
    public void evictCandidateCache() {
        log.info("候选数据缓存已清空");
    }
}
```

**CreativeCacheService.java** (创意缓存)：

```java
@Service
@RequiredArgsConstructor
public class CreativeCacheService {

    // 根据 ID 获取创意（带缓存）
    @Cacheable(value = CacheConfig.CREATIVE_CACHE, key = "#creativeId")
    public RtbCreativeDO getCreativeById(Long creativeId) {
        // 从数据库查询
    }

    // 刷新单个创意缓存
    @CacheEvict(value = CacheConfig.CREATIVE_CACHE, key = "#creativeId")
    public void evictCreativeCache(Long creativeId) {
        log.info("创意缓存已清空, creativeId={}", creativeId);
    }

    // 刷新所有创意缓存
    @CacheEvict(value = CacheConfig.CREATIVE_CACHE, allEntries = true)
    public void evictAllCreativeCache() {
        log.info("所有创意缓存已清空");
    }
}
```

#### 20.3.4 缓存刷新 API

**CacheController.java** 提供 REST API 用于手动刷新缓存：

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/cache/candidates/refresh` | POST | 刷新候选数据缓存 |
| `/api/cache/creatives/{id}/refresh` | POST | 刷新指定创意缓存 |
| `/api/cache/creatives/refresh-all` | POST | 刷新所有创意缓存 |
| `/api/cache/refresh-all` | POST | 刷新所有缓存 |

**调用场景**：
- Campaign/AdGroup/Ad 状态变更后
- 创意内容/素材文件更新后
- 批量数据导入后
- 发现缓存数据不一致时

#### 20.3.5 缓存监控

**Caffeine 统计信息**：

```java
// 获取缓存统计
CacheStats stats = cacheManager.getCache(CANDIDATE_CACHE)
    .getNativeCache()
    .stats();

// 关键指标
- hitRate(): 命中率 (目标 > 90%)
- hitCount(): 命中次数
- missCount(): 未命中次数
- evictionCount(): 驱逐次数
- loadSuccessCount(): 加载成功次数
- loadFailureCount(): 加载失败次数
```

**监控告警阈值**：

| 指标 | 正常范围 | 告警阈值 |
|------|---------|---------|
| 候选缓存命中率 | > 95% | < 90% |
| 创意缓存命中率 | > 90% | < 80% |
| 缓存加载时间 | < 50ms | > 100ms |
| 缓存驱逐频率 | < 10/分钟 | > 50/分钟 |

---

### 20.4 虚拟线程优化

#### 20.4.1 虚拟线程架构

虚拟线程 (Virtual Threads) 是 JDK 21+ 的正式特性，用于替代传统的平台线程：

```
┌─────────────────────────────────────────────────────────────┐
│                    虚拟线程分层架构                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Level 1: Tomcat HTTP 线程池 (虚拟线程)               │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │  Executors.newVirtualThreadPerTaskExecutor()  │  │  │
│  │  │  配置: TomcatVirtualThreadConfig.java         │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  │  效果: 单机可处理数万并发请求                         │  │
│  └──────────────────────────────────────────────────────┘  │
│                         ↓ 每个请求                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Level 2: 应用层并行处理 (虚拟线程)                   │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │  StructuredTaskScope.ShutdownOnFailure()      │  │  │
│  │  │  场景: 多 imp 并行处理                         │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  │  效果: 单请求多 imp 的并行加速                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                         ↓ I/O 操作                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  阻塞操作 (虚拟线程自动挂起)                          │  │
│  │  ├── MySQL 查询                                       │  │
│  │  ├── Redis 操作                                       │  │
│  │  └── 外部 API 调用                                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 20.4.2 Tomcat 虚拟线程配置

**TomcatVirtualThreadConfig.java**：

```java
@Configuration
public class TomcatVirtualThreadConfig {

  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
    return factory -> {
      factory.addConnectorCustomizers(connector -> {
        // 使用虚拟线程执行器替换 Tomcat 默认的平台线程池
        connector.getProtocolHandler().setExecutor(
            Executors.newVirtualThreadPerTaskExecutor());
      });
    };
  }
}
```

**收益**：
- **并发能力**: 200 个平台线程 → 数万虚拟线程
- **内存占用**: 虚拟线程栈 ~几 KB vs 平台线程 ~1MB
- **高 QPS 场景**: 10,000+ QPS 无阻塞

#### 20.4.3 应用层虚拟线程 (StructuredTaskScope)

**RtbBidServiceImpl.java** - 多 imp 并行处理：

```java
public BidResponse processBid(BidRequest request) {

    // 单个 imp：串行处理（避免虚拟线程创建开销）
    if (request.getImp().size() == 1) {
        return processSingleImp(request, request.getImp().get(0));
    }

    // 多个 imp：使用虚拟线程并行处理
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        // 为每个 imp 创建一个虚拟线程任务
        List<Supplier<BidResponse>> tasks = request.getImp().stream()
            .map(imp -> scope.fork(() -> processSingleImp(request, imp)))
            .toList();

        // 等待所有任务完成（或任一失败）
        scope.join().throwIfFailed();

        // 收集所有成功的响应
        List<Bid> bids = tasks.stream()
            .map(Supplier::get)
            .filter(Objects::nonNull)
            .flatMap(response -> response.getSeatbid().stream())
            .map(SeatBid::getBid)
            .flatMap(List::stream)
            .toList();

        // 组装最终响应
        return buildResponse(request, bids);
    } catch (Exception e) {
        log.error("并行处理失败", e);
        return null;
    }
}
```

**收益**：
- **多 imp 场景**: 并行处理多个展示机会
- **延迟降低**: 3 个 imp 从 60ms → 25ms
- **自动失败传播**: 任一 imp 失败快速失败

#### 20.4.4 虚拟线程配置对比

| 配置方式 | 作用范围 | 是否必需 | 使用场景 |
|---------|---------|---------|---------|
| `TomcatVirtualThreadConfig.java` | Tomcat HTTP 请求处理 | **必需** | 高 QPS 场景 |
| `spring.threads.virtual.enabled=true` | Spring 内部组件 (@Async, TaskExecutor) | 可选 | 有 @Async 方法时 |
| `StructuredTaskScope` | 应用层并行处理 | **必需** | 多 imp 并行 |

**结论**: 两者需要同时使用，`TomcatVirtualThreadConfig` 负责 HTTP 层面的并发，`StructuredTaskScope` 负责单个请求内部的并行。

#### 20.4.5 虚拟线程监控

```bash
# 使用 jcmd 查看虚拟线程统计
jcmd <pid> Thread.dump_to_file -format=json threads.txt

# 关键指标
- 虚拟线程数量
- 平台线程数量 (Carrier Threads)
- 虚拟线程状态分布
```

---

### 20.5 代码级优化

#### 20.5.1 预排序过滤器链

**优化前**：每次请求都排序过滤器

```java
// ❌ 每次请求都排序
List<BidFilter> sortedFilters = bidFilters.stream()
    .sorted(Comparator.comparingInt(BidFilter::order))
    .toList();
```

**优化后**：启动时预排序

```java
// ✅ 构造函数中预排序
public RtbBidServiceImpl(...) {
    this.sortedBidFilters = bidFilters.stream()
        .sorted(Comparator.comparingInt(BidFilter::order))
        .toList();
}
```

**收益**：每个请求节省 ~0.1ms

#### 20.5.2 缓存常量

**优化前**：每次创建 BigDecimal

```java
// ❌ 每次创建新对象
BigDecimal priceMicros = bidPrice.multiply(new BigDecimal("1000"));
```

**优化后**：缓存常量

```java
// ✅ 静态常量
private static final BigDecimal PRICE_DIVISOR = BigDecimal.valueOf(1000);

// 使用时
BigDecimal priceMicros = bidPrice.multiply(PRICE_DIVISOR);
```

**收益**：减少对象分配，降低 GC 压力

---

### 20.6 性能监控与调优

#### 20.6.1 关键性能指标 (KPI)

| 指标 | 目标值 | 告警阈值 | 监控方式 |
|------|--------|---------|---------|
| **P99 延迟** | < 60ms | > 100ms | Micrometer + Prometheus |
| **P50 延迟** | < 20ms | > 40ms | Micrometer + Prometheus |
| **QPS** | > 10,000 | < 5,000 | Nginx 日志 / Prometheus |
| **Young GC 暂停** | < 0.1ms | > 0.5ms | GC 日志 |
| **Old GC 暂停** | < 1ms | > 5ms | GC 日志 |
| **缓存命中率** | > 90% | < 80% | Caffeine Stats |
| **错误率** | < 0.1% | > 1% | 日志统计 |

#### 20.6.2 性能测试工具

**wrk 压测**：

```bash
# 单元压测 (单线程)
wrk -t1 -c10 -d30s http://localhost:8081/openrtb/bid

# 并发压测 (多线程)
wrk -t12 -c100 -d30s http://localhost:8081/openrtb/bid

# 带 JSON payload
wrk -t12 -c100 -d30s -s request.lua http://localhost:8081/openrtb/bid
```

**request.lua**:

```lua
wrk.method = "POST"
wrk.body   = '{"imp":[{"id":"1","banner":{"w":320,"h":50}}]}'
wrk.headers["Content-Type"] = "application/json"
```

#### 20.6.3 JVM 参数调优流程

1. **启动阶段**
   ```bash
   # 使用推荐的 ZGC 参数启动
   java -XX:+UseZGC -XX:+ZGenerational -XX:ZCollectionInterval=5 \
        -Xms4g -Xmx4g -jar bid-java-2.0.0.jar
   ```

2. **监控阶段** (24-48 小时)
   ```bash
   # 收集 GC 日志
   tail -f gc.log | grep "GC("

   # 监控 JVM 内存
   jstat -gcutil <pid> 1000
   ```

3. **调优阶段**
   - 如果 Young GC 频率过高 → 调整 `-XX:ZCollectionInterval`
   - 如果内存不足 → 增加堆内存 `-Xmx8g`
   - 如果启动延迟 → 移除 `-XX:+AlwaysPreTouch`

4. **验证阶段**
   - 运行压测工具 (wrk)
   - 观察 P99 延迟和 GC 暂停时间
   - 确认达到 < 60ms 目标

---

### 20.7 优化效果总结

| 优化项 | 延迟降低 | 吞吐量提升 | 备注 |
|--------|---------|-----------|------|
| **ZGC + ZGenerational** | GC 暂停 50ms → 1ms | 10% | 彻底解决 GC 长暂停 |
| **Caffeine 缓存** | 数据库查询 20ms → 缓存 1ms | 2x | 候选数据命中率高 |
| **Tomcat 虚拟线程** | 高负载下排队延迟 | 5x | 从 200 → 数万并发 |
| **StructuredTaskScope** | 多 imp 并行加速 | - | 3 imp 从 60ms → 25ms |
| **代码级优化** | 每次 ~0.1ms | - | 累积效应明显 |

**综合效果**：
- **延迟**: P99 从 ~100ms → < 60ms
- **吞吐量**: 从 ~5,000 QPS → > 10,000 QPS
- **并发能力**: 从 200 并发 → 数万并发
- **可用性**: GC 长暂停导致的超时基本消除

---

**作者**: wake.zheng
