# OpenRTB 2.5 API 实现说明

## 概述

本目录包含了基于 OpenRTB 2.5 规范的完整 Java 实现,用于实时竞价(RTB)系统。

## 文件结构

```
openrtb/
├── OpenRtbApi.java              # REST API 控制器
└── dto/                         # 数据传输对象
    ├── BidRequest.java          # 竞价请求对象
    ├── BidResponse.java         # 竞价响应对象
    ├── SeatBid.java             # 席位竞价对象
    ├── Bid.java                 # 单个竞价对象
    ├── Imp.java                 # 展示对象
    ├── Site.java                # 网站对象
    ├── App.java                 # 应用对象
    ├── Device.java              # 设备对象
    ├── User.java                # 用户对象
    ├── Geo.java                 # 地理位置对象
    ├── Banner.java              # 横幅广告对象
    ├── Video.java               # 视频广告对象
    ├── Audio.java               # 音频广告对象
    ├── Native.java              # 原生广告对象
    ├── Format.java              # 格式对象
    ├── Pmp.java                 # 私有市场对象
    ├── Deal.java                # 交易对象
    ├── Publisher.java           # 发布商对象
    ├── Content.java             # 内容对象
    ├── Producer.java            # 制作者对象
    ├── Source.java              # 库存源对象
    ├── Regs.java               # 法规对象
    ├── Metric.java              # 度量对象
    ├── DataDto.java             # 数据对象(Data重命名)
    └── Segment.java             # 细分对象
```

## 核心对象说明

### 1. 竞价请求流程

#### BidRequest (竞价请求)
- **路径**: `/api/openrtb/bid`
- **方法**: POST
- **Content-Type**: `application/json`
- **Headers**:
  - `x-openrtb-version`: OpenRTB版本(默认2.5)

**必需字段**:
- `id`: 竞价请求的唯一ID
- `imp`: 至少一个展示对象

**示例请求**:
```json
{
  "id": "80ce30c53c16e6ede735fe1227162d61",
  "imp": [
    {
      "id": "1",
      "banner": {
        "w": 728,
        "h": 90,
        "pos": 1
      }
    }
  ],
  "site": {
    "id": "102855",
    "domain": "example.com",
    "page": "http://example.com/page"
  },
  "device": {
    "ua": "Mozilla/5.0...",
    "ip": "192.168.1.1"
  },
  "user": {
    "id": "user123"
  }
}
```

### 2. 展示类型 (Imp)

一个Imp对象可以包含以下一种或多种展示类型:
- **Banner**: 横幅广告(包括横幅内视频)
- **Video**: 视频广告
- **Audio**: 音频广告
- **Native**: 原生广告

### 3. 竞价响应流程

#### BidResponse (竞价响应)
**必需字段**:
- `id`: 必须与请求中的id匹配
- `seatbid`: 至少一个SeatBid对象

**示例响应**:
```json
{
  "id": "80ce30c53c16e6ede735fe1227162d61",
  "seatbid": [
    {
      "bid": [
        {
          "id": "bid1",
          "impid": "1",
          "price": 0.751537,
          "adid": "ad123",
          "nurl": "http://example.com/win-notice"
        }
      ]
    }
  ],
  "bidid": "response-id-123",
  "cur": "USD"
}
```

## 关键特性

### 1. 多种广告类型支持
- 横幅广告(Banner)
- 视频广告(Video) - 支持VAST协议
- 音频广告(Audio)
- 原生广告(Native)

### 2. 私有市场(PMP)
- 支持直接交易(Deal)
- 支持私有拍卖
- 支持底价控制

### 3. 设备和用户定位
- 详细的设备信息
- 地理位置信息
- 用户行为数据

### 4. 法规合规
- COPPA合规标志
- 请勿跟踪(DNT)支持
- GDPR相关字段(通过ext扩展)

## 价格说明

所有价格字段使用**微美元**为单位:
- $1.00 = 1,000,000 微美元
- $0.75 = 750,000 微美元
- 示例: price字段值0.751537表示$751,537微美元

## 扩展字段

所有对象都包含`ext`字段用于自定义扩展:
```java
@JsonProperty("ext")
@Schema(description = "扩展字段")
private Object ext;
```

## API端点

### 竞价端点
```
POST /api/openrtb/bid
```

**请求头**:
```
Content-Type: application/json
x-openrtb-version: 2.5
```

**响应**:
- 成功: 200 OK (返回BidResponse)
- 无竞价: 204 No Content
- 无效请求: 400 Bad Request

## 使用示例

### 1. 简单横幅广告请求

```java
BidRequest request = new BidRequest();
request.setId("req-123");

Imp imp = new Imp();
imp.setId("1");

Banner banner = new Banner();
banner.setW(728);
banner.setH(90);
banner.setPos(1);

imp.setBanner(banner);
request.setImp(Arrays.asList(imp));

// 发送请求到竞价端点
BidResponse response = openRtbApi.bid("2.5", "application/json", request);
```

### 2. 视频广告请求

```java
Video video = new Video();
video.setMimes(Arrays.asList("video/mp4", "video/x-flv"));
video.setW(640);
video.setH(480);
video.setLinearity(1); // 线性视频
video.setStartdelay(0); // 前贴片

Imp imp = new Imp();
imp.setId("1");
imp.setVideo(video);
```

### 3. 原生广告请求

```java
Native native = new Native();
native.setRequest("{\"assets\":[{\"id\":1,\"title\":{\"len\":25}}]}");

Imp imp = new Imp();
imp.setId("1");
imp.setNative_(native);
```

## 注意事项

1. **类型转换**: Java中使用`Float`表示价格,注意精度问题
2. **命名冲突**: `Data`类重命名为`DataDto`以避免与`java.sql.Data`冲突
3. **Jackson注解**: 使用`@JsonProperty`确保JSON字段映射正确
4. **Lombok**: 使用`@Data`注解自动生成getter/setter
5. **Swagger**: 使用`@Schema`注解生成API文档

## 完整的OpenRTB对象映射

### 核心请求对象
- ✅ BidRequest - 顶级请求对象
- ✅ Imp - 展示对象
- ✅ Site - 网站信息
- ✅ App - 应用信息
- ✅ Device - 设备信息
- ✅ User - 用户信息
- ✅ Source - 库存源
- ✅ Regs - 法规信息

### 展示类型对象
- ✅ Banner - 横幅广告
- ✅ Video - 视频广告
- ✅ Audio - 音频广告
- ✅ Native - 原生广告
- ✅ Format - 格式规范

### 私有市场对象
- ✅ Pmp - 私有市场
- ✅ Deal - 直接交易

### 内容相关对象
- ✅ Content - 内容信息
- ✅ Producer - 制作者信息
- ✅ Publisher - 发布商信息

### 辅助对象
- ✅ Geo - 地理位置
- ✅ Metric - 度量数据
- ✅ DataDto - 用户数据
- ✅ Segment - 数据细分

### 响应对象
- ✅ BidResponse - 响应对象
- ✅ SeatBid - 席位竞价
- ✅ Bid - 单个竞价

## 参考文档

- [OpenRTB 2.5 规范中文版](../../../docs/参考文档/OpenRTB-API规范-2.5版本-中文版.md)
- [OpenRTB 2.5 Specification (PDF)](../../../docs/参考文档/OpenRTB-API-Specification-Version-2-5-FINAL.pdf)
- [IAB Tech Lab](https://iabtechlab.com/)
- [OpenRTB GitHub](https://github.com/openrtb/OpenRTB)

## 版本历史

- **2025-01-20**: 创建OpenRTB 2.5完整实现
- 基于OpenRTB API Specification Version 2.5 (December 2016)

## 下一步

1. 实现竞价业务逻辑
2. 添加数据库持久化
3. 实现缓存机制
4. 添加监控和日志
5. 实现A/B测试支持
