# OpenRTB 2.6 规范中文版

**原文**: OpenRTB API Specification Version 2.6 (Final)
**发布日期**: 2022年4月
**翻译日期**: 2025年1月20日

---

## 文档信息

- **版本**: 2.6 (最终版)
- **页数**: 90页
- **发布方**: IAB Technology Laboratory
- **状态**: 已发布,可实施

---

## 目录

1. [变更摘要](#变更摘要)
2. [概述](#概述)
3. [对象定义](#对象定义)
4. [对象参考](#对象参考)
5. [合规与最佳实践](#合规与最佳实践)
6. [附录](#附录)

---

## 变更摘要

### OpenRTB 2.5 到 2.6 的主要变更

#### 1. 通用变更

**新增字段**:
- `BidRequest.source.schain`: 供应链对象
- `BidRequest.source.skhadomain`: SKAdNetwork归因域名
- `BidRequest.user.ext.consent`: GDPR同意字符串(新增位置)
- `BidRequest.device.ext.mmdevicetype`: MMA设备类型
- `BidRequest.device.ifa`: 移动设备标识符(新增说明)

**字段更新**:
- `BidRequest.imp.banner.w` 和 `h`: 支持多尺寸数组
- `BidRequest.user.buyeruid`: 更新说明,明确为买方用户ID
- `BidRequest.device.os`: 新增更多操作系统值
- `BidRequest.device.connectiontype`: 新增5G连接类型

**废弃字段**:
- `BidRequest.user.yob`: 年份出生(建议使用dob字段)

#### 2. Connected TV (CTV) 增强

**新增字段**:
- `BidRequest.device.ext.carrier`: 运营商信息
- `BidRequest.device.ext.devicemodel`: 设备型号
- `BidRequest.device.ext.ispon`: 开启隐私VPN标志
- `BidRequest.imp.video.placement`: 新增CTV相关放置类型
- `BidRequest.imp.video.playbackend`: 新增CTV播放方法

#### 3. ID Bridging (身份桥接)

**新增字段**:
- `BidRequest.user.ext.eids`: 扩展ID列表
- `BidRequest.user.ext.idsourcetype`: ID源类型
- `BidRequest.user.ext.id": "第三方ID源标识

#### 4. Contextual Targeting (上下文定向)

**新增字段**:
- `BidRequest.imp.context`: 上下文对象
- `BidRequest.imp.context.contexttype`: 上下文类型
- `BidRequest.imp.context.contextsubtypes`: 上下文子类型列表

#### 5. Transparency & Disclosure (透明度与披露)

**新增字段**:
- `BidRequest.regs`: 新增GPP(全球隐私平台)支持
- `BidRequest.regs.gpp`: GPP同意字符串
- `BidRequest.regs.gpp_sid`: GPP章节ID列表

#### 6. Supply Chain (供应链)

**新增对象**:
- `SupplyChain`: 供应链对象(符合ads.cert标准)
- `SupplyChainNode`: 供应链节点对象

---

## 概述

### OpenRTB 是什么?

OpenRTB(Open Real-Time Bidding)是一个开放的实时竞价协议,用于在数字广告生态系统中进行程序化广告交易。它定义了买方(Demand-Side Platforms, DSP)和卖方(Supply-Side Platforms, SSP)之间交换竞价请求和响应的标准格式。

### 核心概念

**实时竞价(RTB)**:
- 广告展示机会在毫秒级时间内进行竞价
- 买方根据展示机会的价值出价
- 出价最高者的广告被展示

**OpenRTB流程**:
1. 用户访问网站或打开应用
2. SSP向多个DSP发送竞价请求(BidRequest)
3. DSP分析展示机会并返回竞价响应(BidResponse)
4. SSP选择出价最高的DSP
5. DSP的广告被展示给用户

### 协议格式

OpenRTB使用JSON格式进行数据交换:
- **编码**: UTF-8
- **HTTP方法**: POST
- **Content-Type**: application/json

---

## 对象定义

### BidRequest (竞价请求)

**描述**: 竞价请求的顶层对象

**必需字段**:
- `id` (string): 竞价请求的唯一标识符
- `imp` (array): 至少包含一个展示(Impression)对象

**可选字段**:
- `app` (object): 应用信息(如果是在应用环境中)
- `site` (object): 网站信息(如果是在网站环境中)
- `device` (object): 设备信息
- `user` (object): 用户信息
- `source` (object): 库存源信息
- `regs` (object): 法规信息
- `ext` (object): 自定义扩展

**示例**:
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
    "ip": "192.168.1.1",
    "os": "iOS",
    "connectiontype": 4
  },
  "user": {
    "id": "user123"
  }
}
```

---

### Imp (展示/Impression)

**描述**: 展示机会对象,定义可用的广告位

**必需字段**:
- `id` (string): 展示对象的唯一标识符

**可选字段** (至少包含一个):
- `banner` (object): 横幅广告位
- `video` (object): 视频广告位
- `audio` (object): 音频广告位
- `native` (object): 原生广告位

**其他字段**:
- `bidfloor` (float): 底价(以CPM为单位)
- `bidfloorcur` (string): 底价货币(默认为USD)
- `secure` (integer): 是否使用HTTPS (0=否, 1=是)
- `iframebuster` (array): IAB buster列表
- `pmp` (object): 私有市场对象
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "1",
  "banner": {
    "w": 728,
    "h": 90,
    "pos": 1
  },
  "bidfloor": 0.5,
  "bidfloorcur": "USD",
  "secure": 1
}
```

---

### Banner (横幅广告)

**描述**: 横幅广告位对象

**字段**:
- `w` (integer): 宽度(像素)
- `h` (integer): 高度(像素)
- `wmax` (integer): 最大宽度
- `hmax` (integer): 最大高度
- `wmin` (integer): 最小宽度
- `hmin` (integer): 最小高度
- `btype` (array): 横幅类型
  - 1=当屏横幅
  - 2=扩展横幅等
- `pos` (integer): 位置
  - 0=未知
  - 1=首屏
  - 2=次屏
- `mimes` (array): 支持的MIME类型
- `topframe` (integer): 是否在顶层框架 (0=否, 1=是)
- `expdir` (array): 展开方向
- `api` (array): 支持的API框架
- `id` (string): 横幅ID
- `vcm` (integer): Viewable可衡量性
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增**:
- `battr` (array): 阻止的创意属性
- `format` (array): 支持的格式列表

**示例**:
```json
{
  "w": 728,
  "h": 90,
  "pos": 1,
  "btype": [1, 2],
  "mimes": ["image/jpeg", "image/png"]
}
```

---

### Video (视频广告)

**描述**: 视频广告位对象

**字段**:
- `mimes` (array, 必需): 支持的MIME类型
  - 例如: ["video/mp4", "video/x-flv"]
- `w` (integer): 宽度(像素)
- `h` (integer): 高度(像素)
- `linearity` (integer): 线性
  - 1=线性广告
  - 2=非线性广告
- `minduration` (integer): 最小时长(秒)
- `maxduration` (integer): 最大时长(秒)
- `startdelay` (integer): 开始延迟
  - 0=前贴片
  - -1=中贴片
  - >0=后贴片(秒数)
- `placement` (integer): 放置类型
  - 1=流内
  - 2=插屏
  - 3=悬停等
- `skip` (integer): 是否可跳过 (0=否, 1=是)
- `skipmin` (integer): 最少播放多少秒后可跳过
- `skipafter` (integer): 多少秒后显示跳过按钮
- `sequence` (integer): 视频序列号
- `battr` (array): 阻止的创意属性
- `maxextended` (integer): 最大延长时长
- `minbitrate` (integer): 最小码率
- `maxbitrate` (integer): 最大码率
- `boxingallowed` (integer): 是否允许信箱模式
- `playbackend` (integer): 播放方法
  - 1=自动播放有声
  - 2=自动播放静音
  - 3=点击播放
  - 4=鼠标悬停播放
- `playableafter` (integer): 可播放的最长秒数
- `delivery` (array): 交付类型
  - 1=流式传输
  - 2=渐进式下载
- `companionad` (array): 伴随横幅数组
- `companiontype` (array): 伴随横幅类型
- `api` (array): 支持的API框架
- `companionad` (array): 伴随广告对象数组
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增**:
- `plcmt` (integer): 放置类型(更细粒度)
- `podid` (string): 广告组ID
- `podsize` (integer): 广告组大小
- `podseq` (integer): 广告组序列号
- `mincpmpersec` (float): 每秒最低CPM
- `maxseq` (integer): 最大序列号
- `render` (integer): 渲染方式

**示例**:
```json
{
  "mimes": ["video/mp4"],
  "w": 640,
  "h": 480,
  "linearity": 1,
  "startdelay": 0,
  "placement": 1,
  "playbackend": 2
}
```

---

### Audio (音频广告)

**描述**: 音频广告位对象

**字段**:
- `mimes` (array, 必需): 支持的MIME类型
- `w` (integer): 宽度(像素,如果适用)
- `h` (integer): 高度(像素,如果适用)
- `linearity` (integer): 线性 (1=线性, 2=非线性)
- `minduration` (integer): 最小时长(秒)
- `maxduration` (integer): 最大时长(秒)
- `startdelay` (integer): 开始延迟
- `sequence` (integer): 音频序列号
- `battr` (array): 阻止的创意属性
- `minbitrate` (integer): 最小码率
- `maxbitrate` (integer): 最大码率
- `deliver` (array): 交付类型
- `companionad` (array): 伴随横幅数组
- `api` (array): 支持的API框架
- `ext` (object): 扩展字段

**示例**:
```json
{
  "mimes": ["audio/mp3"],
  "minduration": 15,
  "maxduration": 30,
  "sequence": 1
}
```

---

### Native (原生广告)

**描述**: 原生广告位对象

**字段**:
- `request` (string, 必需): 原生广告请求JSON字符串
- `ver` (string): 原生API版本
- `api` (array): 支持的API框架
- `battr` (array): 阻止的创意属性
- `ext` (object): 扩展字段

**示例**:
```json
{
  "request": "{\"assets\":[{\"id\":1,\"required\":1,\"title\":{\"text\":25}}]}",
  "ver": "1.2"
}
```

---

### Site (网站)

**描述**: 网站信息对象

**字段**:
- `id` (string): 网站ID
- `name` (string): 网站名称
- `domain` (string): 网站域名
- `cat` (array): IAB内容类别列表
- `sectioncat` (array): IAB章节类别列表
- `pagecat` (array): IAB页面类别列表
- `page` (string): 页面URL
- `ref` (string): 引用URL
- `keywords` (string): 关键词(逗号分隔)
- `search` (string): 搜索查询
- `content` (object): 内容对象
- `publisher` (object): 发布商对象
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "102855",
  "name": "Example Site",
  "domain": "example.com",
  "cat": ["IAB3-1"],
  "page": "http://example.com/page"
}
```

---

### App (应用)

**描述**: 应用信息对象

**字段**:
- `id` (string): 应用ID
- `name` (string): 应用名称
- `bundle` (string): 应用包名
- `domain` (string): 应用域名
- `storeurl` (string): 应用商店URL
- `cat` (array): IAB内容类别列表
- `sectioncat` (array): IAB章节类别列表
- `pagecat` (array): IAB页面类别列表
- `ver` (string): 应用版本
- `privacypolicy` (integer): 隐私政策 (0=否, 1=是)
- `paid` (integer): 付费应用 (0=否, 1=是)
- `publisher` (object): 发布商对象
- `content` (object): 内容对象
- `keywords` (string): 关键词
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "app123",
  "name": "Example App",
  "bundle": "com.example.app",
  "ver": "1.2.3",
  "paid": 1
}
```

---

### Device (设备)

**描述**: 设备信息对象

**字段**:
- `ua` (string, 必需): 用户代理字符串
- `geo` (object): 地理位置(建议)
- `dnt` (integer): 请勿跟踪 (0=否, 1=是)
- `lmt` (integer): 限制广告跟踪 (0=否, 1=是)
- `ip` (string): IP地址
- `ipv6` (string): IPv6地址
- `devicetype` (integer): 设备类型
  - 1=手机/手持设备
  - 2=个人电脑
  - 3=平板
  - 4=联网电视
  - 5=机顶盒等
- `make` (string): 设备制造商
- `model` (string): 设备型号
- `os` (string): 操作系统
  - 例如: "iOS", "Android", "Windows", "macOS", "Linux", "ROKU"
- `osv` (string): 操作系统版本
- `hwv` (string): 硬件版本
- `h` (integer): 屏幕高度(像素)
- `w` (integer): 屏幕宽度(像素)
- `ppi` (integer): 每英寸像素数
- `pxratio` (float): 像素比(例如: 1.0, 2.0, 3.0)
- `js` (integer): 是否支持JavaScript (0=否, 1=是)
- `geo` (object): 地理位置对象
- `dnt` (integer): 请勿跟踪
- `lmt` (integer): 限制广告跟踪
- `ip` (string): IP地址
- `ipv6` (string): IPv6地址
- `carrier` (string): 运营商
- `mccmnc` (string): 移动国家代码和网络代码
- `connectiontype` (integer): 连接类型
  - 0=未知
  - 1=以太网
  - 2=WiFi
  - 3=2G网络
  - 4=3G网络
  - 5=4G网络
  - 6=5G网络 (OpenRTB 2.6新增)
- `flashver` (string): Flash版本
- `language` (string): 语言(ISO-639-1-alpha-2)
- `carrier` (string): 运营商
- `mccmnc` (string): 移动国家代码和网络代码
- `ifa` (string): 移动设备标识符(IDFA/AID)
- `didsha1` (string): 设备ID SHA1哈希
- `didmd5` (string): 设备ID MD5哈希
- `dpidsha1` (string): 平台设备ID SHA1哈希
- `dpidmd5` (string): 平台设备ID MD5哈希
- `macsha1` (string): MAC地址SHA1哈希
- `macmd5` (string): MAC地址MD5哈希
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增字段**:
- `ext.carrier`: 运营商详细信息
- `ext.devicemodel`: 设备型号详情
- `ext.ispon`: 隐私VPN标志
- `ext.mmdevicetype`: MMA设备类型
- `ext.skhadomain`: SKAdNetwork归因域名

**示例**:
```json
{
  "ua": "Mozilla/5.0...",
  "ip": "192.168.1.1",
  "devicetype": 1,
  "os": "iOS",
  "osv": "15.0",
  "make": "Apple",
  "model": "iPhone",
  "connectiontype": 5,
  "ifa": "AEBE52E7-03EE-455A-B3C4-E57283966239"
}
```

---

### User (用户)

**描述**: 用户信息对象

**字段**:
- `id` (string): 用户唯一标识符(建议)
- `buyeruid` (string): 买方用户ID
- `yob` (integer): 出生年份
- `gender` (string): 性别 (M=男, F=女, O=其他)
- `keywords` (string): 关键词
- `customdata` (string): 自定义数据
- `geo` (object): 地理位置对象
- `data` (array): 数据对象数组
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增字段**:
- `ext.eids`: 扩展ID列表(ID Bridging)
- `ext.consent`: GDPR同意字符串
- `ext.digid`: 数字ID

**示例**:
```json
{
  "id": "user123",
  "buyeruid": "buyer_user_456",
  "yob": 1990,
  "gender": "M",
  "geo": {
    "country": "USA"
  }
}
```

---

### Geo (地理位置)

**描述**: 地理位置信息对象

**字段**:
- `lat` (float): 纬度
- `lon` (float): 经度
- `type` (integer): 位置来源类型
  - 1=GPS/精确位置
  - 2=IP位置
  - 3=用户提供
- `accuracy` (integer): 精度(米)
- `lastfix` (integer): 最后定位时间(Unix时间戳)
- `ipservice` (integer): IP位置服务
- `country` (string): 国家代码(ISO-3166-1-alpha-3)
- `region` (string): 区域代码(ISO-3166-2)
- `regionfips` (string): FIPS区域代码
- `metro` (string): 大都市区域代码(US)
- `city` (string): 城市名称
- `zip` (string): 邮政编码
- `utcoffset` (integer): UTC偏移(分钟)

**示例**:
```json
{
  "lat": 37.7749,
  "lon": -122.4194,
  "type": 1,
  "country": "USA",
  "region": "CA",
  "city": "San Francisco",
  "zip": "94102"
}
```

---

### BidResponse (竞价响应)

**描述**: 竞价响应的顶层对象

**必需字段**:
- `id` (string, 必需): 必须与请求中的id匹配
- `seatbid` (array, 必需): 至少包含一个SeatBid对象

**可选字段**:
- `bidid` (string): 竞价响应的唯一标识符
- `cur` (string): 竞价货币(ISO-4217, 默认为USD)
- `customdata` (string): 自定义数据
- `nbr` (integer): 不竞价原因代码
- `ext` (object): 扩展字段

**不竞价原因代码 (nbr)**:
- 0=未知错误
- 1=技术错误
- 2=无效请求
- 3=已知网络蜘蛛
- 4=疑似不诚实流量
- 5=每小时/每天预算不足
- 6=每天预算不足
- 7=无匹配内容
- 8=开始时间或结束时间不匹配
- 9=无匹配广告
- 10=地域过滤
- 11=未知

**示例**:
```json
{
  "id": "80ce30c53c16e6ede735fe1227162d61",
  "seatbid": [
    {
      "bid": [
        {
          "id": "bid1",
          "impid": "1",
          "price": 0.75,
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

---

### SeatBid (席位竞价)

**描述**: 席位竞价对象

**字段**:
- `bid` (array, 必需): 竞价对象数组
- `seat` (string): 席位ID
- `group` (integer): 是否作为一组竞价 (0=否, 1=是)
- `ext` (object): 扩展字段

**示例**:
```json
{
  "bid": [
    {
      "id": "bid1",
      "impid": "1",
      "price": 0.75
    }
  ],
  "seat": "12345"
}
```

---

### Bid (竞价)

**描述**: 单个竞价对象

**必需字段**:
- `id` (string): 竞价的唯一标识符
- `impid` (string, 必需): 所竞价展示的ID
- `price` (float, 必需): 竞价价格(微美元)

**可选字段**:
- `adid` (string): 广告ID
- `nurl` (string): 获胜通知URL
- `burl` (string): 计费通知URL
- `lurl` (string): 失败通知URL
- `adomain` (array): 广告主域
- `bundle` (string): 应用包或捆绑标识符
- `iurl` (string): 创意图像URL
- `cid` (string): 创意ID
- `crid` (string): 创意版本ID
- `cat` (array): IAB内容类别
- `attr` (array): 创意属性
- `api` (array): API框架
- `protocol` (integer): 视频响应协议
- `qagmediarating` (integer): IQG媒体评级
- `language` (string): 创意语言(ISO-639-1-alpha-2)
- `dealid` (string): 交易ID
- `w` (integer): 创意宽度(像素)
- `h` (integer): 创意高度(像素)
- `ratio` (integer): 宽高比(例如100表示1:1)
- `dur` (integer): 视频或音频创意持续时间(秒)
- `mime` (string): 创意MIME类型
- `adm` (string): 广告标记
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增字段**:
- `ext.prebid`: Prebid特定扩展
- `ext.segbdy`: 分段主体标识符

**示例**:
```json
{
  "id": "bid1",
  "impid": "1",
  "price": 0.75,
  "adid": "ad123",
  "crid": "creative_v1",
  "adm": "<a href='http://landing-page.com'><img src='http://creative.com/banner.jpg'/></a>",
  "adomain": ["advertiser.com"]
}
```

---

### Pmp (私有市场)

**描述**: 私有市场对象

**字段**:
- `private_auction` (integer): 私有拍卖标志 (0=否, 1=是)
- `deals` (array): 交易对象数组
- `ext` (object): 扩展字段

**示例**:
```json
{
  "private_auction": 1,
  "deals": [
    {
      "id": "deal123",
      "bidfloor": 2.5,
      "bidfloorcur": "USD"
    }
  ]
}
```

---

### Deal (交易)

**描述**: 交易对象

**字段**:
- `id` (string, 必需): 交易唯一标识符
- `bidfloor` (float): 交易底价
- `bidfloorcur` (string): 底价货币(默认为USD)
- `at` (integer): 拍卖类型
  - 1=第一价格拍卖
  - 2=第二价格拍卖
- `wseat` (array): 获胜席位列表
- `wadomain` (array): 获胜广告主域列表
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "deal123",
  "bidfloor": 2.5,
  "bidfloorcur": "USD",
  "at": 2
}
```

---

### Publisher (发布商)

**描述**: 发布商对象

**字段**:
- `id` (string): 发布商ID
- `name` (string): 发布商名称
- `cat` (array): IAB内容类别列表
- `domain` (string): 发布商域名
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "pub123",
  "name": "Example Publisher",
  "domain": "example.com"
}
```

---

### Content (内容)

**描述**: 内容对象

**字段**:
- `id` (string): 内容ID
- `episode` (integer): 剧集号
- `title` (string): 内容标题
- `series` (string): 系列
- `season` (string): 季度
- `artist` (string): 艺术家
- `genre` (string): 流派
- `album` (string): 专辑
- `isrc` (string): 国际标准录音代码
- `producer` (object): 制作者对象
- `url` (string): 内容URL
- `cat` (array): IAB内容类别列表
- `prodq` (integer): 生产质量
- `context` (integer): 内容上下文
- `contentrating` (string): 内容评级
- `userrating` (string): 用户评级
- `qagmediarating` (integer): IQG媒体评级
- `keywords` (string): 关键词
- `livestream` (integer): 直播流 (0=否, 1=是)
- `source_relationship` (integer): 来源关系
- `len` (integer): 内容长度(秒)
- `language` (string): 语言
- `embeddable` (integer): 可嵌入 (0=否, 1=是)
- `data` (array): 数据对象数组
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "content123",
  "title": "Example Video",
  "genre": "Comedy",
  "cat": ["IAB23"],
  "len": 1800
}
```

---

### Producer (制作者)

**描述**: 制作者对象

**字段**:
- `id` (string): 制作者ID
- `name` (string): 制作者名称
- `cat` (array): IAB内容类别列表
- `domain` (string): 制作者域名
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "prod123",
  "name": "Example Producer",
  "domain": "producer.com"
}
```

---

### Source (库存源)

**描述**: 库存源对象

**字段**:
- `id` (string): 源ID
- `tid` (string): 交易ID
- `pmt` (integer): 支付方式
- `fd` (integer): 流向方向
- `schain` (object): 供应链对象
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增字段**:
- `schain`: 供应链对象(符合ads.cert标准)
- `skhadomain`: SKAdNetwork归因域名

**示例**:
```json
{
  "id": "source123",
  "tid": "txn456",
  "schain": {
    "nodes": [
      {
        "asi": "example.com",
        "sid": "12345",
        "hp": 1
      }
    ],
    "complete": 1,
    "ver": "1.0"
  }
}
```

---

### SupplyChain (供应链)

**描述**: 供应链对象(OpenRTB 2.6新增)

**用途**: 用于追踪广告库存的完整供应链路径,符合ads.cert标准

**字段**:
- `nodes` (array, 必需): 供应链节点数组
- `complete` (integer, 必需): 完整性标志 (0=否, 1=是)
- `ver` (string, 必需): 供应链版本
- `ext` (object): 扩展字段

**SupplyChainNode (供应链节点)**:
- `asi` (string, 必需): 广告系统标识符
- `sid` (string, 必需): 供应链节点ID
- `hp` (integer): 付费类型 (0=交换/中间人, 1=卖方, 2=买方)
- `rid` (string): 关联到源的第三方唯一ID
- `name` (string): 供应链节点名称
- `domain` (string): 供应链节点域名
- `cat` (array): IAB内容类别列表

**示例**:
```json
{
  "schain": {
    "complete": 1,
    "nodes": [
      {
        "asi": "publisher.com",
        "sid": "pub123",
        "hp": 1
      },
      {
        "asi": "ssp.com",
        "sid": "ssp456",
        "hp": 0
      },
      {
        "asi": "exchange.com",
        "sid": "exc789",
        "hp": 0
      }
    ],
    "ver": "1.0"
  }
}
```

---

### Regs (法规)

**描述**: 法规信息对象

**字段**:
- `coppa` (integer): COPPA合规 (0=否, 1=是)
- `gdpr` (integer): GDPR合规 (0=否, 1=是)
- `ext` (object): 扩展字段

**OpenRTB 2.6 新增字段**:
- `gpp`: 全球隐私平台(GPP)同意字符串
- `gpp_sid`: GPP章节ID列表

**示例**:
```json
{
  "coppa": 0,
  "gdpr": 1,
  "gpp": "DBACNYW~CPg...",
  "gpp_sid": [3, 6]
}
```

---

### Metric (度量)

**描述**: 度量数据对象

**字段**:
- `type` (string, 必需): 度量类型
- `value` (float, 必需): 度量值
- `vendor` (string): 度量供应商
- `ext` (object): 扩展字段

**示例**:
```json
{
  "type": "viewability",
  "value": 85.5,
  "vendor": "company.com"
}
```

---

### Data (数据)

**描述**: 数据对象(已在Java实现中重命名为DataDto)

**字段**:
- `id` (string): 数据提供者ID
- `name` (string): 数据提供者名称
- `segment` (array): 数据细分数组
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "data123",
  "name": "Data Provider",
  "segment": [
    {
      "id": "seg1",
      "name": "Segment 1",
      "value": "travel"
    }
  ]
}
```

---

### Segment (细分)

**描述**: 数据细分对象

**字段**:
- `id` (string, 必需): 细分ID
- `name` (string): 细分名称
- `value` (string): 细分值
- `parent` (string): 父细分ID
- `ext` (object): 扩展字段

**示例**:
```json
{
  "id": "seg1",
  "name": "Travel Intenders",
  "value": "travel"
}
```

---

## 价格说明

所有价格字段使用**微美元**(Micros)为单位:
- $1.00 = 1,000,000 微美元
- $0.75 = 750,000 微美元
- $0.000001 = 1 微美元

示例: price字段值为`750000`表示$0.75

---

## 扩展字段

所有对象都包含`ext`字段用于自定义扩展:
```json
{
  "ext": {
    "custom_field_1": "value1",
    "custom_field_2": 123
  }
}
```

---

## API端点

### 竞价端点

```
POST /openrtb/bid
```

**请求头**:
```
Content-Type: application/json
x-openrtb-version: 2.6
```

**响应**:
- 成功: 200 OK (返回BidResponse)
- 无竞价: 204 No Content
- 无效请求: 400 Bad Request

---

## 合规与最佳实践

### 1. GDPR合规

- 使用`user.ext.consent`字段传递同意字符串
- 遵循GDPR规定的数据处理原则
- 实现用户权利(访问、删除、可移植性)

### 2. COPPA合规

- 使用`regs.coppa`标志识别儿童定向内容
- 不收集13岁以下儿童的个人信息
- 遵守儿童在线隐私保护法

### 3. 广告欺诈预防

- 检测并阻止无效流量(IVT)
- 使用ads.cert验证供应链完整性
- 实现反欺诈检测机制

### 4. 品牌安全

- 使用`Bid.cat`和`Bid.attr`字段确保内容安全
- 实现上下文定向和页面级别过滤
- 使用第三方品牌安全验证服务

### 5. 供应链透明度

- 使用`source.schain`提供完整供应链路径
- 确保所有中间环节都被正确标识
- 遵循IAB Tech Lab的ads.cert标准

---

## OpenRTB 2.6 新特性详解

### 1. Connected TV (CTV) 增强

**新增字段**:
- `Device.ext.carrier`: 运营商详细信息
- `Device.ext.devicemodel`: 设备型号详情
- `Device.ext.ispon`: 隐私VPN标志

**Video放置类型**:
- 更细致的CTV相关放置类型
- 支持流内、插屏、悬停等多种CTV场景

### 2. ID Bridging (身份桥接)

**用途**: 支持多个身份提供商之间的用户ID映射

**实现**:
```json
{
  "user": {
    "ext": {
      "eids": [
        {
          "source": "vendor1.com",
          "uids": [
            {"id": "uid1", "atype": 1}
          ]
        }
      ]
    }
  }
}
```

### 3. Global Privacy Platform (GPP)

**用途**: 统一的全球隐私合规框架

**实现**:
```json
{
  "regs": {
    "gpp": "DBACNYW~CPg...",
    "gpp_sid": [3, 6]
  }
}
```

### 4. 供应链透明度

**用途**: 完整追踪广告库存来源

**实现**:
```json
{
  "source": {
    "schain": {
      "complete": 1,
      "nodes": [...],
      "ver": "1.0"
    }
  }
}
```

---

## 附录

### A. IAB内容类别

参考IAB Tech Lab的OpenRTB分类体系:
- IAB1: 艺术与娱乐
- IAB2: 汽车车辆
- IAB3: 商业
- ...
- IAB24: 电子商务

### B. 设备类型代码

- 1: 手机/手持设备
- 2: 个人电脑
- 3: 平板
- 4: 联网电视
- 5: 机顶盒
- 6: 家庭助理
- 7: 游戏机

### C. 连接类型代码

- 0: 未知
- 1: 以太网
- 2: WiFi
- 3: 2G网络
- 4: 3G网络
- 5: 4G网络
- 6: 5G网络 (OpenRTB 2.6新增)

### D. 操作系统列表

- iOS
- Android
- Windows
- macOS
- Linux
- Roku (OpenRTB 2.6新增)
- tvOS (OpenRTB 2.6新增)
- ChromeOS (OpenRTB 2.6新增)

### E. API框架代码

- 1: VPAID 1.0
- 2: VPAID 2.0
- 3: MRAID-1
- 4: ORMMA
- 5: MRAID-2
- 6: MRAID-3

### F. 创意属性代码

- 1: 音频(用户自发动作)
- 2: 用户自发(展开)
- 3: 鼠标悬停效果
- 4: 多个创意
- 5: 自动播放音频
- 6: 展开效果
- ...

### G. 不竞价原因代码

- 0: 未知错误
- 1: 技术错误
- 2: 无效请求
- 3: 已知网络蜘蛛
- 4: 疑似不诚实流量
- 5: 每小时/每天预算不足
- 6: 每天预算不足
- 7: 无匹配内容
- 8: 开始时间或结束时间不匹配
- 9: 无匹配广告
- 10: 地域过滤

---

## 参考文档

- [OpenRTB 2.6规范PDF](OpenRTB-2-6_FINAL.pdf)
- [OpenRTB 2.5规范中文版](OpenRTB-API规范-2.5版本-中文版.md)
- [IAB Tech Lab官方网站](https://iabtechlab.com/)
- [OpenRTB GitHub仓库](https://github.com/InteractiveAdvertisingBureau/openrtb2.x)

---

## 版本历史

- **2022-04**: OpenRTB 2.6正式发布
- **2016-12**: OpenRTB 2.5发布
- **2014-06**: OpenRTB 2.3发布
- **2012-11**: OpenRTB 2.0发布
- **2010-10**: OpenRTB 1.0发布

---

**翻译说明**:
- 本文档基于OpenRTB API Specification Version 2.6 Final翻译
- 保留所有技术术语的英文原文以便参考
- 翻译日期: 2025年1月20日
- 如有疑问,请参考官方PDF文档

---

**文档结束**
