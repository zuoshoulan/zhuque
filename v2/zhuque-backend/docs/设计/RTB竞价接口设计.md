# RTB 竞价接口设计文档

> **版本**: v1.0
> **日期**: 2025-01-24
> **状态**: 设计阶段

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

```
web-admin/src/main/java/wake/su/zhuque/
│
├── controller/rtb/
│   └── RtbBidController.java              # 竞价接口入口
│
├── service/
│   ├── RtbBidService.java                 # 竞价核心服务 (接口)
│   ├── BidPriceService.java               # 出价计算服务 (接口)
│   ├── TargetingMatchService.java        # 定向匹配服务 (接口)
│   ├── FrequencyCapService.java          # 频次控制服务 (接口)
│   ├── BudgetControlService.java         # 预算控制服务 (接口)
│   └── CreativeAssemblyService.java      # 创意组装服务 (接口)
│
├── service/impl/
│   ├── RtbBidServiceImpl.java             # 竞价核心服务实现
│   ├── BidPriceServiceImpl.java           # 出价计算服务实现
│   ├── TargetingMatchServiceImpl.java    # 定向匹配服务实现
│   ├── FrequencyCapServiceImpl.java      # 频次控制服务实现
│   ├── BudgetControlServiceImpl.java     # 预算控制服务实现
│   └── CreativeAssemblyServiceImpl.java  # 创意组装服务实现
│
├── strategy/                             # 出价策略
│   ├── BidPriceStrategy.java             # 策略接口
│   ├── FixedCpmStrategy.java             # 固定CPM实现
│   ├── SmartBidStrategy.java             # 智能出价实现
│   ├── TargetCpaStrategy.java            # 目标CPA实现
│   └── MaxWinStrategy.java               # 最高赢价实现
│
├── matcher/                              # 定向匹配器
│   ├── GeoMatcher.java                   # 地域匹配
│   ├── DeviceMatcher.java                # 设备匹配
│   ├── ScheduleMatcher.java              # 时段匹配
│   └── UserSegmentMatcher.java           # 人群包匹配
│
├── filter/                               # 过滤器
│   ├── BudgetFilter.java                # 预算过滤
│   ├── FrequencyFilter.java              # 频次过滤
│   ├── BrandSafetyFilter.java            # 品牌安全过滤
│   └── TimeRangeFilter.java             # 时间范围过滤
│
└── dto/openrtb/                          # OpenRTB DTO (已存在)
    ├── BidRequest.java
    ├── BidResponse.java
    └── ...
```

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

    /**
     * 处理单个展示机会的竞价
     */
    SeatBid processImp(BidRequest request, Imp imp);
}
```

### 11.2 TargetingMatchService

```java
public interface TargetingMatchService {
    /**
     * 判断广告组是否匹配请求条件
     */
    boolean matches(BidRequest request, Imp imp, RtbAdGroupDO adGroup);

    /**
     * 地域匹配
     */
    boolean matchGeo(BidRequest.Geo requestGeo, String targetingGeo);

    /**
     * 设备匹配
     */
    boolean matchDevice(Device requestDevice, String targetingDevice, String targetingOs);

    /**
     * 时段匹配
     */
    boolean matchSchedule(RtbAdGroupDO adGroup);
}
```

### 11.3 FrequencyCapService

```java
public interface FrequencyCapService {
    /**
     * 检查是否超出频次限制
     * @return true=可以展示, false=超出限制
     */
    boolean checkFrequencyCap(String userId, Long adGroupId, Imp imp, Integer cap, Integer period);

    /**
     * 记录一次展示
     */
    void recordImpression(String userId, Long adGroupId, Integer period);

    /**
     * 生成频次Key
     */
    String buildFreqCapKey(Long adGroupId, String userId, Integer period);
}
```

### 11.4 CreativeAssemblyService

```java
public interface CreativeAssemblyService {
    /**
     * 构造 ADM (创意素材)
     * @param ad 广告
     * @param creative 创意
     * @param imp 展示机会
     * @param bid 竞价对象
     * @return ADM 字符串
     */
    String buildAdm(RtbAdDO ad, RtbCreativeDO creative, Imp imp, Bid bid);
}
```

---

## 12. 策略模式设计

### 12.1 策略接口

```java
public interface BidPriceStrategy {
    /**
     * 计算出价
     * @param basePrice 基础出价
     * @param maxPrice 最高出价
     * @param floorPrice 底价
     * @param pctr 预测CTR (可选, 0~1)
     * @return 出价（微元/千次单位）
     */
    Long calculate(BigDecimal basePrice, BigDecimal maxPrice,
                  BigDecimal floorPrice, Double pctr);

    /**
     * 获取策略类型
     */
    Integer getType();
}
```

### 12.2 策略工厂

```java
@Service
public class BidPriceStrategyFactory {
    private final Map<Integer, BidPriceStrategy> strategyMap;

    public BidPriceStrategy getStrategy(Integer bidStrategyType) {
        return strategyMap.get(bidStrategyType);
    }
}
```

---

## 13. 缓存设计

### 13.1 频次控制缓存

```
Key:    freq_cap:{adGroupId}:{userId}:{period}
Value:   Integer (展示次数)
TTL:     到周期结束 (动态计算)

示例:
freq_cap:123:user001:2  (用户user001在广告组123的今日展示次数)
```

### 13.2 预算使用缓存

```
Key:    budget_used:{adGroupId}:{date}
Value:   BigDecimal (已消耗金额)
TTL:     2天

示例:
budget_used:123:2025-01-24  (广告组123在2025-01-24的已消耗金额)
```

### 13.3 pCTR 预估缓存

```
Key:    pctr:{adGroupId}:{deviceType}:{geo}
Value:   Double (预估CTR)
TTL:     1小时

示例:
pctr:123:mobile:CN-11  (广告组123在移动端北京的预估CTR)
```

---

## 14. 错误处理

### 14.1 错误码

| 错误码 | 说明 |
|--------|------|
| INVALID_REQUEST | 请求格式无效 |
| MISSING_REQUIRED_FIELD | 缺少必需字段 |
| NO_MATCHING_AD | 无匹配广告 |
| BID_BELOW_FLOOR | 出价低于底价 |
| BUDGET_EXHAUSTED | 预算耗尽 |
| FREQUENCY_EXCEEDED | 频次超限 |

### 14.2 处理逻辑

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

## 15. 性能考虑

### 15.1 性能目标

| 指标 | 目标值 |
|------|--------|
| 响应时间 | P99 < 100ms |
| 吞吐量 | > 10,000 QPS |
| 可用性 | 99.9% |

### 15.2 优化措施

1. **Redis 缓存**: 频次、预算、pCTR
2. **本地缓存**: 广告组配置热点数据
3. **异步日志**: 竞价日志异步写入
4. **连接池**: 数据库连接池优化
5. **索引优化**: 数据库查询索引

---

## 16. 监控指标

### 16.1 业务指标

- 竞价请求量 (QPS)
- 竞价成功率
- 平均出价
- 赢率
- 展示率

### 16.2 技术指标

- 响应时间 (P50, P99)
- 错误率
- 服务可用性
- Redis 命中率

---

## 17. 后续扩展

### 17.1 待实现功能

1. **智能出价**: 引入机器学习预测 pCTR/pCVR
2. **程序化创意**: 动态生成创意内容
3. **A/B 测试**: 支持创意多版本测试
4. **实时报告**: 实时竞价数据回传

### 17.2 优化方向

1. **多竞价**: 支持单次请求返回多个竞价
2. **价格拆分**: 支持底价拆分 (Price Splitting)
3. **私有市场**: 支持 PMP/Deal 交易
4. **视频广告**: 支持 VAST 4.0 完整功能

---

## 18. 参考资料

- [OpenRTB 2.6 Specification](https://iabtechlab.com/wp-content/uploads/2024/07/OpenRTB-v2_6-Final.pdf)
- [IAB Tech Lab](https://iabtechlab.com/)
- Prebid Server (开源 SSP 参考实现)

---

**文档版本**: v1.0
**最后更新**: 2025-01-24
**作者**: wake.zheng
