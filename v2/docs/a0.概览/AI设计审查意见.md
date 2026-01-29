# 设计文档 AI 审查意见

> **审查日期**: 2025-01-29
> **审查范围**: docs 目录下所有设计文档
> **审查状态**: 待处理

---

## 文档审查总结

### 整体评价

朱雀广告平台是一个基于 OpenRTB 2.6 标准的 DSP 平台，整体设计质量很高：

- ✅ 架构设计清晰 - 单体应用 + 模块化分层，职责明确
- ✅ 三层架构设计 - Campaign → AdGroup → Ad 符合业界标准
- ✅ 完全遵循 OpenRTB 2.6 - 数据库设计和接口实现严格对应标准
- ✅ 原子操作保证 - CAS SQL + Redis Lua 保证了并发安全

---

## 一、高优先级问题

### 1.1 预算控制设计存在性能隐患

**相关文档**: [投放活动与广告系统设计.md](../b.设计文档/投放活动与广告系统设计.md)

**问题描述**:
当前设计每次竞价都要从 Redis 读取预算，高并发下 Redis 成为瓶颈：
```java
// 当前设计：每次竞价都访问 Redis
public void onWinNotice(Long campaignId, BigDecimal cost) {
    String key = "campaign:budget:" + campaignId;
    redis.incrBy(key, cost);  // 高并发下瓶颈
}
```

**影响**:
- 10000 QPS 竞价场景下，Redis QPS 需要支持 10000+
- Redis 成为单点瓶颈
- 网络延迟增加（Redis 访问约 1-5ms）

**建议方案**:

方案一：**预算本地缓存 + 异步更新**（推荐）
```java
// 1. 应用启动时加载所有进行中 Campaign 的预算到本地缓存
// 2. 竞价时直接操作本地缓存
// 3. 赢拍后异步更新 Redis（批量或定时）
// 4. 定时（如每分钟）同步 Redis 到数据库

@PostConstruct
public void loadBudgetToCache() {
    // 加载所有进行中的 Campaign 预算到本地
}

public void onWinNotice(Long campaignId, BigDecimal cost) {
    // 1. 立即更新本地缓存（无延迟）
    localBudgetCache.increment(campaignId, cost);

    // 2. 异步更新 Redis（批量）
    asyncUpdateRedis(campaignId, cost);
}
```

方案二：**Redis 集群 + 本地缓存**
- Redis 使用集群模式
- 本地缓存热点 Campaign 预算
- 定时从 Redis 刷新

**决策待定**: 采用方案一还是方案二？

---

### 1.2 状态管理设计存在不一致

**相关文档**: [投放活动与广告系统设计.md](../b.设计文档/投放活动与广告系统设计.md)

**问题描述**:
文档中状态定义存在两套设计，导致理解混乱：

**第一套定义**（第 467 行）：
| 状态值 | 名称 | 说明 |
|--------|------|------|
| 0 | 草稿 | 创建后默认状态 |
| 1 | 进行中 | 正在投放 |
| 2 | 暂停 | 暂时停止 |
| 3 | 已完成 | 结束时间到了或预算用完 |
| 4 | 已取消 | 手动取消 |

**第二套定义**（第 741 行）：
| 状态值 | 名称 | 说明 |
|--------|------|------|
| 0 | 草稿 | 新建未发布 |
| 1 | 进行中 | 正在投放 |
| 2 | 暂停 | 手动暂停 |

**影响**:
- 数据库表设计不明确
- 前端展示逻辑混乱
- 状态流转规则不清晰

**建议方案**:
统一使用**第二套定义**（3种状态），原因：
1. ✅ 简单明了，只有 3 种状态
2. ✅ 用户操作逻辑清晰（草稿 → 进行中 ↔ 暂停）
3. ✅ 时间到期或预算耗尽**不改变** status 字段
4. ✅ 展示状态（displayStatus）通过业务规则计算得出

**展示状态计算逻辑**：
```java
private String calculateDisplayStatus(Campaign campaign, BigDecimal used) {
    LocalDateTime now = LocalDateTime.now();

    // 1. 预算耗尽（最高优先级）
    if (used.compareTo(campaign.getLifetimeBudget()) >= 0) {
        return "预算耗尽";
    }

    // 2. 投放时间耗尽
    if (now.isAfter(campaign.getEndTime())) {
        return "投放时间耗尽";
    }

    // 3. 根据 status 判断
    switch (campaign.getStatus()) {
        case 0: return "草稿";
        case 1:
            if (now.isBefore(campaign.getStartTime())) {
                return "待开始";
            } else {
                return "进行中";
            }
        case 2: return "暂停";
    }
}
```

**决策待定**: 确认采用 3 状态还是 5 状态设计？

---

### 1.3 频次控制的 Key 设计不一致

**相关文档**: [RTB竞价接口设计.md](../b.设计文档/RTB竞价接口设计.md)

**问题描述**:
频次控制的 Redis Key 设计存在两套格式：

**第一套**（第 283 行）：
```
freq_cap:{adGroupId}:{userId}:{period}
示例：freq_cap:123:456789:2
```

**第二套**（第 669 行）：
```
freq:day:{date}:{adGroupId}:{userId}
freq:hour:{date}:{hour}:{adGroupId}:{userId}
freq:week:{week}:{adGroupId}:{userId}
freq:month:{year}:{month}:{adGroupId}:{userId}
```

**影响**:
- 代码实现不一致
- TTL 计算不清晰
- 调试困难

**建议方案**:
统一使用**第二套格式**，原因：
1. ✅ 明确包含时间维度（day/hour/week/month）
2. ✅ TTL 计算简单（到周期结束时间）
3. ✅ 支持按时间清理过期数据

**统一后的格式**：
```java
// 格式：freq:{period}:{period_value}:{adGroupId}:{userId}
public String buildKey(Long adGroupId, String userId, Integer period) {
    LocalDate now = LocalDate.now();
    switch (period) {
        case 1: // 小时
            return String.format("freq:hour:%s:%d:%s",
                now.format(DateTimeFormatter.ofPattern("yyyyMMddHH")),
                adGroupId, userId);
        case 2: // 天
            return String.format("freq:day:%s:%d:%s",
                now.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                adGroupId, userId);
        case 3: // 周
            int week = now.get(WeekFields.of(DayOfWeek.MONDAY, 7).weekOfWeekBasedYear());
            return String.format("freq:week:%d:%d:%s", week, adGroupId, userId);
        case 4: // 月
            return String.format("freq:month:%s:%d:%s",
                now.format(DateTimeFormatter.ofPattern("yyyyMM")),
                adGroupId, userId);
    }
}
```

**决策待定**: 确认频次 Key 格式？

---

## 二、中优先级问题

### 2.1 缺少监控和告警设计

**相关文档**: 所有设计文档

**问题描述**:
设计文档中缺少监控指标和告警规则的设计，系统上线后无法及时发现和解决问题。

**建议方案**:

#### 监控指标设计

**业务指标**：
```yaml
竞价指标:
  - 竞价请求 QPS (Request QPS)
  - 竞价响应时间 P50/P95/P99 (Response Time)
  - 竞价成功率 (Bid Success Rate)
  - 无竞价率 (No Bid Rate)

效果指标:
  - 赢率 (Win Rate)
  - 展示率 (Impression Rate)
  - 点击率 (CTR)
  - 转化率 (CVR)

财务指标:
  - 千次展示价格 (CPM)
  - 点击成本 (CPC)
  - 转化成本 (CPA)
  - 消耗速率 (Spend Rate)
```

**技术指标**：
```yaml
应用指标:
  - JVM 堆内存使用率
  - GC 频率和耗时
  - 线程池使用情况
  - 接口响应时间

数据库指标:
  - 慢查询数量
  - 连接池使用率
  - TPS/QPS
  - 死锁次数

缓存指标:
  - Redis 命中率
  - Redis 连接池使用率
  - Redis 内存使用率
  - Redis 响应时间
```

#### 告警规则设计

```yaml
P0 级告警（立即处理）:
  - 竞价服务不可用（连续 3 次心跳失败）
  - 竞价响应时间 P99 > 500ms
  - 数据库连接失败
  - Redis 连接失败

P1 级告警（30 分钟内处理）:
  - 竞价成功率 < 80%
  - 无竞价率 > 50%
  - 预算耗尽的 Campaign 数量 > 10
  - 慢查询数量 > 100/分钟

P2 级告警（当天处理）:
  - 赢率 < 10%
  - CTR < 0.5%
  - Redis 命中率 < 80%
  - GC 耗时 > 1s
```

**决策待定**: 监控工具选型（Prometheus + Grafana 或其他）？

---

### 2.2 缺少测试设计

**相关文档**: 所有设计文档

**问题描述**:
设计文档中缺少测试策略和测试用例的设计，无法保证代码质量。

**建议方案**:

#### 单元测试

```java
// 预算扣减测试
@Test
public void testBudgetDeduction() {
    // 1. 正常扣减
    // 2. 预算不足扣减失败
    // 3. 并发扣减（CAS 保证原子性）
    // 4. 扣减后回滚
}

// 频次控制测试
@Test
public void testFrequencyCap() {
    // 1. 未超限通过
    // 2. 超限拒绝
    // 3. 周期到期后重置
    // 4. 并发记录（Lua 保证原子性）
}

// 出价计算测试
@Test
public void testBidPriceCalculation() {
    // 1. 固定 CPM 策略
    // 2. 智能出价策略（含 pCTR）
    // 3. 出价边界检查（floor/max）
    // 4. 单位转换（元 → 微元）
}
```

#### 集成测试

```yaml
场景1: 完整竞价流程
  - 收到 BidRequest
  - 匹配 Campaign/AdGroup/Ad
  - 通过定向匹配
  - 通过预算检查
  - 通过频次检查
  - 计算出价
  - 返回 BidResponse

场景2: 无竞价场景
  - Campaign 状态不是进行中
  - 时间不在范围内
  - 预算耗尽
  - 频次超限
  - 定向不匹配

场景3: 多广告组竞争
  - 同一 Campaign 下多个 AdGroup 匹配
  - 选出价最高的返回
```

#### 压力测试

```yaml
工具: JMeter / Gatling / K6

场景1: 竞价接口压测
  - QPS 目标: 10000
  - 响应时间: P99 < 100ms
  - 持续时间: 10 分钟
  - 并发用户: 100

场景2: 预算扣减压测
  - 测试 CAS SQL 的并发性能
  - 验证无超扣情况

场景3: 频次控制压测
  - 测试 Redis Lua 的并发性能
  - 验证无超限情况
```

**决策待定**: 测试框架选型？

---

### 2.3 数据库索引设计不完整

**相关文档**: [投放活动与广告系统设计.md](../b.设计文档/投放活动与广告系统设计.md)

**问题描述**:
`rtb_ad_group` 表的索引设计不完整，部分查询场景缺少索引支持。

**当前索引**：
```sql
KEY `idx_campaign_status` (`campaign_id`, `status`),
KEY `idx_advertiser_status` (`advertiser_id`, `status`)
```

**缺失的查询场景**：

1. **查询某个广告主下所有进行中的广告组**：
```sql
-- 当前索引：idx_advertiser_status 可以支持 ✅
SELECT * FROM rtb_ad_group
WHERE advertiser_id = 123 AND status = 1;
```

2. **查询时段投放的广告组**：
```sql
-- 没有索引支持 ❌
SELECT * FROM rtb_ad_group
WHERE schedule_type = 3 AND status = 1;
```

3. **查询某个广告组下所有启用的广告**：
```sql
-- 当前索引：idx_ad_group_status 可以支持 ✅
SELECT * FROM rtb_ad
WHERE ad_group_id = 123 AND status = 1;
```

**建议增加的索引**：
```sql
-- 时段查询索引
ALTER TABLE rtb_ad_group
ADD KEY `idx_schedule_status` (`schedule_type`, `status`);

-- 出价策略查询索引（如果需要）
ALTER TABLE rtb_ad_group
ADD KEY `idx_bid_strategy_status` (`bid_strategy`, `status`);

-- 地域定向查询索引（如果需要，需要使用 JSON 索引）
ALTER TABLE rtb_ad_group
ADD KEY `idx_targeting_geo` ((CAST(targeting_geo AS CHAR(255))), `status`);
```

**决策待定**: 是否需要增加这些索引？

---

## 三、低优先级问题

### 3.1 代码示例不完整

**相关文档**: [RTB竞价接口设计.md](../b.设计文档/RTB竞价接口设计.md)

**问题描述**:
部分接口只有定义，缺少完整的实现示例，开发者参考困难。

**建议补充的示例**：

```java
// BidPriceServiceImpl 完整实现
@Service
public class BidPriceServiceImpl implements BidPriceService {

    @Autowired
    private BidPriceStrategyFactory strategyFactory;

    @Override
    public Long calculateBidPrice(RtbAdGroupDO adGroup,
                                  BigDecimal requestBidFloor,
                                  Double predictedCtr) {
        // 1. 获取出价策略
        BidPriceStrategy strategy = strategyFactory
            .getStrategy(adGroup.getBidStrategy());

        // 2. 准备参数
        BigDecimal basePrice = adGroup.getBaseBidPrice();
        BigDecimal maxPrice = adGroup.getMaxBid();
        BigDecimal minPrice = adGroup.getBidFloor();

        // 3. 计算出价（微元/千次）
        Long priceMicros = strategy.calculate(
            basePrice, maxPrice, minPrice, requestBidFloor, predictedCtr
        );

        // 4. 边界检查
        if (priceMicros < 0) {
            return null; // 不出价
        }

        return priceMicros;
    }
}

// FixedCpmStrategy 实现
@Component
public class FixedCpmStrategy implements BidPriceStrategy {

    @Override
    public Long calculate(BigDecimal basePrice, BigDecimal maxPrice,
                         BigDecimal minPrice, BigDecimal floorPrice,
                         Double predictedCtr) {
        // 固定 CPM：直接使用基础出价
        BigDecimal bidPrice = basePrice;

        // 边界检查
        if (bidPrice.compareTo(minPrice) < 0) {
            bidPrice = minPrice;
        }
        if (bidPrice.compareTo(maxPrice) > 0) {
            bidPrice = maxPrice;
        }
        if (bidPrice.compareTo(floorPrice) < 0) {
            return null; // 低于底价，不出价
        }

        // 转换为微元（1 元 = 1,000,000 微元）
        return bidPrice.multiply(new BigDecimal("1000000"))
            .setScale(0, RoundingMode.HALF_UP).longValue();
    }

    @Override
    public Integer getType() {
        return 1; // 固定CPM
    }
}

// SmartBidStrategy 实现
@Component
public class SmartBidStrategy implements BidPriceStrategy {

    @Override
    public Long calculate(BigDecimal basePrice, BigDecimal maxPrice,
                         BigDecimal minPrice, BigDecimal floorPrice,
                         Double predictedCtr) {
        // 智能出价：基础出价 × pCTR 系数
        BigDecimal ctrFactor = new BigDecimal(predictedCtr);
        BigDecimal bidPrice = basePrice.multiply(ctrFactor);

        // 边界检查
        if (bidPrice.compareTo(minPrice) < 0) {
            bidPrice = minPrice;
        }
        if (bidPrice.compareTo(maxPrice) > 0) {
            bidPrice = maxPrice;
        }
        if (bidPrice.compareTo(floorPrice) < 0) {
            return null;
        }

        // 转换为微元
        return bidPrice.multiply(new BigDecimal("1000000"))
            .setScale(0, RoundingMode.HALF_UP).longValue();
    }

    @Override
    public Integer getType() {
        return 2; // 智能出价
    }
}
```

**决策待定**: 是否需要补充所有 Service 的完整实现示例？

---

### 3.2 文档间缺少交叉引用

**相关文档**: 所有设计文档

**问题描述**:
相关文档之间缺少交叉引用，阅读时需要手动查找相关内容。

**建议增加的交叉引用**：

| 文档 | 应引用的文档 |
|------|------------|
| [RTB竞价接口设计.md](../b.设计文档/RTB竞价接口设计.md) | [创意和素材数据库设计.md](../b.设计文档/数据库设计/OpenRTB-2.6-创意和素材数据库设计.md) - 创意组装部分 |
| [投放活动与广告系统设计.md](../b.设计文档/投放活动与广告系统设计.md) | [架构设计.md](../b.设计文档/架构设计.md) - 模块依赖关系 |
| [数据库选择.md](../b.设计文档/数据库选择.md) | [部署架构](../b.设计文档/部署架构.md) - 数据库部署方案 |
| [概览.md](./概述.md) | 所有设计文档 - 建立导航索引 |

**示例**：
```markdown
## 4. 竞价流程设计

详见 [RTB竞价接口设计.md](../b.设计文档/RTB竞价接口设计.md)

## 5. 创意组装

创意数据模型详见 [创意和素材数据库设计.md](../b.设计文档/数据库设计/OpenRTB-2.6-创意和素材数据库设计.md)
```

**决策待定**: 是否需要建立完整的文档索引？

---

### 3.3 缺少部署文档

**相关文档**: 无

**问题描述**:
缺少部署相关文档，开发者和运维人员无法快速搭建环境。

**建议新增的文档**：

#### 1. Docker Compose 配置

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: zhuque_rtb
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7.0
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  java-admin:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/zhuque_rtb
      SPRING_REDIS_HOST: redis

  java-bid:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/zhuque_rtb
      SPRING_REDIS_HOST: redis

volumes:
  mysql_data:
  redis_data:
```

#### 2. 环境变量说明

```yaml
# application.yml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/zhuque_rtb}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:root}

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
```

#### 3. 数据库初始化脚本

```sql
-- init.sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS zhuque_rtb DEFAULT CHARSET utf8mb4;

-- 使用数据库
USE zhuque_rtb;

-- 创建表（包含所有 DDL）
```

**决策待定**: 部署方式选择（Docker Compose / Kubernetes / 传统部署）？

---

### 3.4 素材文件存储设计不明确

**相关文档**: [创意和素材数据库设计.md](../b.设计文档/数据库设计/OpenRTB-2.6-创意和素材数据库设计.md)

**问题描述**:
素材文件只记录了 `file_id`，但缺少文件上传、存储、访问的完整设计。

**建议补充的设计**：

#### 文件上传接口

```java
@PostMapping("/api/materials/upload")
public Result<MaterialUploadVO> upload(
    @RequestParam("file") MultipartFile file,
    @RequestParam("creativeId") Long creativeId
) {
    // 1. 文件校验（大小、格式）
    // 2. 上传到 OSS
    // 3. 生成 file_id
    // 4. 记录到数据库
}
```

#### OSS 存储设计

```yaml
存储方案: 阿里云 OSS / 腾讯云 COS / 自建 MinIO

目录结构:
  - creative/{year}/{month}/{file_id}.jpg
  - creative/{year}/{month}/{file_id}_thumb.jpg  (缩略图)

访问控制:
  - 公共读: 创意文件可以直接访问
  - CDN 加速: 建议使用 CDN 加速访问
```

#### 缩略图生成

```java
// 视频和音频需要生成缩略图
public String generateThumbnail(String fileUrl) {
    // 使用 FFmpeg 生成第一帧作为缩略图
    // 存储到 OSS
    // 返回缩略图 URL
}
```

**决策待定**:
1. OSS 服务选型（阿里云 OSS / 腾讯云 COS / 自建 MinIO）
2. 是否需要 CDN 加速
3. 是否需要视频处理服务（转码、裁剪）

---

## 四、需要澄清的设计决策

### 4.1 预算存储方案

**问题**: 为什么预算使用 Redis 存储而不是数据库字段？

**分析**:

| 方案 | 优势 | 劣势 |
|------|------|------|
| Redis 存储 | 性能高，读写快 | 数据丢失风险，需要持久化 |
| 数据库存储 | 数据安全，持久化保证 | 性能较低，并发需要 CAS |
| 混合方案 | 性能和安全的平衡 | 复杂度高，需要同步 |

**建议**:
- 初期：数据库 + Redis 缓存
- 中期：Redis 主存储 + 定期持久化
- 后期：分布式预算服务

**决策待定**: 采用哪种方案？

---

### 4.2 竞价日志是否需要分区

**问题**: `rtb_bid_log` 表是否需要分区？

**场景分析**:

| 数据量 | 分区建议 | 清理策略 |
|--------|---------|----------|
| < 1000 万 | 不需要 | 直接 DELETE |
| 1000 万 - 5000 万 | 可选 | 按 DELETE |
| > 5000 万 | 需要 | 按月分区 + DROP PARTITION |

**建议**:
- MVP 阶段：不分区，定期 DELETE 90 天前数据
- 数据量 > 5000 万：按月分区

```sql
-- 分区示例
ALTER TABLE rtb_bid_log
PARTITION BY RANGE (TO_DAYS(request_time)) (
  PARTITION p202401 VALUES LESS THAN (TO_DAYS('2024-02-01')),
  PARTITION p202402 VALUES LESS THAN (TO_DAYS('2024-03-01')),
  ...
);
```

**决策待定**: 初期是否需要分区？

---

### 4.3 素材文件是否需要 CDN

**问题**: 素材文件访问是否需要 CDN 加速？

**分析**:

| 方案 | 延迟 | 成本 | 适用场景 |
|------|------|------|----------|
| 直连 OSS | 100-300ms | 低 | 初期，流量小 |
| CDN 加速 | 10-50ms | 中 | 中期，流量大 |
| 自建 CDN | 5-20ms | 高 | 后期，超大流量 |

**建议**:
- 初期：直连 OSS
- 日展示 > 100 万：使用 CDN

**决策待定**: 何时引入 CDN？

---

## 五、后续行动

### 5.1 立即处理（本周）

- [ ] 统一状态管理设计（确定 3 状态还是 5 状态）
- [ ] 统一频次 Key 设计
- [ ] 补充 `rtb_ad_group` 表的缺失索引

### 5.2 短期处理（本月）

- [ ] 优化预算控制方案（本地缓存 + 异步更新）
- [ ] 新增监控和告警设计文档
- [ ] 补充测试设计文档
- [ ] 新增部署文档

### 5.3 中期处理（下季度）

- [ ] 补充完整的代码示例
- [ ] 建立文档交叉引用索引
- [ ] 新增素材文件存储设计

---

## 六、问题跟踪

| 编号 | 问题描述 | 优先级 | 状态 | 负责人 | 截止日期 |
|------|---------|--------|------|--------|----------|
| 1 | 预算控制性能优化 | 高 | 待处理 | | |
| 2 | 状态管理设计统一 | 高 | 待处理 | | |
| 3 | 频次 Key 设计统一 | 高 | 待处理 | | |
| 4 | 监控告警设计 | 中 | 待处理 | | |
| 5 | 测试设计 | 中 | 待处理 | | |
| 6 | 索引设计补充 | 中 | 待处理 | | |
| 7 | 代码示例补充 | 低 | 待处理 | | |
| 8 | 文档交叉引用 | 低 | 待处理 | | |
| 9 | 部署文档 | 低 | 待处理 | | |
| 10 | 素材存储设计 | 低 | 待处理 | | |

---

**文档版本**: v1.0
**创建日期**: 2025-01-29
**维护者**: AI 设计审查
