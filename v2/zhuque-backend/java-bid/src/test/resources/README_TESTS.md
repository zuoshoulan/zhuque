# RTB 竞价服务测试说明

## 测试目录结构

```
java-bid/src/test/
├── java/wake/su/zhuque/bid/
│   ├── controller/           # Controller API 测试
│   │   └── OpenRtbControllerTest.java
│   ├── service/              # 集成测试
│   │   └── RtbBidServiceIntegrationTest.java
│   ├── matcher/              # Matcher 单元测试
│   │   ├── GeoMatcherTest.java
│   │   ├── DeviceMatcherTest.java
│   │   └── ScheduleMatcherTest.java
│   ├── filter/               # Filter 单元测试
│   │   ├── BudgetFilterTest.java
│   │   └── FrequencyFilterTest.java
│   ├── pricing/              # Strategy 单元测试
│   │   └── BidPriceStrategyTest.java
│   ├── helper/               # 测试辅助类
│   │   └── OpenRtbTestDataLoader.java
│   └── BidServiceTestSuite.java  # 测试套件
│
└── resources/
    ├── application-test.yml  # 测试配置
    └── openrtb/              # 测试数据文件
        ├── bid_request_minimal.json
        ├── bid_request_full.json
        ├── bid_request_video.json
        ├── bid_request_no_imp.json
        └── bid_request_no_geo.json
```

## 运行测试

### 运行所有测试
```bash
mvn test -pl java-bid
```

### 运行特定测试类
```bash
# Matcher 测试
mvn test -pl java-bid -Dtest=GeoMatcherTest
mvn test -pl java-bid -Dtest=DeviceMatcherTest
mvn test -pl java-bid -Dtest=ScheduleMatcherTest

# Filter 测试
mvn test -pl java-bid -Dtest=BudgetFilterTest
mvn test -pl java-bid -Dtest=FrequencyFilterTest

# Strategy 测试
mvn test -pl java-bid -Dtest=BidPriceStrategyTest

# Controller 测试
mvn test -pl java-bid -Dtest=OpenRtbControllerTest

# 集成测试
mvn test -pl java-bid -Dtest=RtbBidServiceIntegrationTest
```

### 运行特定测试方法
```bash
mvn test -pl java-bid -Dtest=GeoMatcherTest#testCountryMatch
mvn test -pl java-bid -Dtest=FixedCpmStrategyTest#testFixedCpmBasicBid
```

## 测试数据

测试数据存储在 `src/test/resources/openrtb/` 目录下：

| 文件 | 说明 |
|------|------|
| `bid_request_minimal.json` | 最小有效请求 |
| `bid_request_full.json` | 完整请求 (多Imp) |
| `bid_request_video.json` | 视频广告请求 |
| `bid_request_no_imp.json` | 无展示机会的无效请求 |
| `bid_request_no_geo.json` | 无地域信息的请求 |

## 测试覆盖范围

### 单元测试
- [x] GeoMatcher - 地域匹配测试
- [x] DeviceMatcher - 设备匹配测试
- [x] ScheduleMatcher - 时段匹配测试
- [x] BudgetFilter - 预算过滤测试
- [x] FrequencyFilter - 频次过滤测试
- [x] FixedCpmStrategy - 固定CPM策略测试
- [x] SmartBidStrategy - 智能出价策略测试

### API测试
- [x] POST /openrtb/bid - 有效请求
- [x] POST /openrtb/bid - 无竞价返回204
- [x] POST /openrtb/bid - 无效请求返回400
- [x] GET /openrtb/health - 健康检查
- [x] GET /openrtb/ready - 就绪检查

### 集成测试
- [x] 测试数据加载
- [x] BidContext 创建
- [x] Spring Bean 加载

## 依赖

测试需要以下依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```
