# 基于OpenRTB 2.6标准的创意和素材数据库设计

**版本**: 1.0
**日期**: 2025-01-20
**基于标准**: OpenRTB API Specification Version 2.6

---

## 目录

1. [设计原则](#设计原则)
2. [表结构设计](#表结构设计)
3. [与OpenRTB的映射关系](#与openrtb的映射关系)
4. [索引设计](#索引设计)
5. [使用示例](#使用示例)

---

## 设计原则

### 核心理念

按照OpenRTB 2.6标准,严格区分**创意(Creative)**和**素材(Material)**:

- **创意(Creative)**: 对应OpenRTB的 `Bid.crid`,是抽象的广告概念
- **素材(Material)**: 对应OpenRTB的 `Bid.adm`中的具体文件,是物理的创意单元

### 关系定义

```
1个创意(Creative) = N个素材(Material)
1个素材(Material) = 1个文件(图片/视频/音频)
```

**为什么分开设计?**

1. ✅ **符合OpenRTB标准**: `crid`和`adm`是两个不同的概念
2. ✅ **便于管理**: 一个创意可以包含多个不同尺寸的素材
3. ✅ **灵活扩展**: 新增尺寸只需新增素材记录
4. ✅ **清晰追踪**: 可以追踪每个素材的展示效果

---

## 表结构设计

### 一、创意表 (rtb_creative)

对应OpenRTB的:
- `Bid.crid` (Creative ID)
- `Bid.adid` (Advertiser ID)
- `Bid.landing_page_url`
- `Bid.adomain`
- `Bid.cat`
- `Bid.attr`

```sql
CREATE TABLE rtb_creative (
    -- === 主键 ===
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    creative_id VARCHAR(64) UNIQUE NOT NULL COMMENT '创意ID,对应OpenRTB的crid',

    -- === 归属 (对应Bid.adid) ===
    advertiser_id BIGINT NOT NULL COMMENT '广告主ID,对应OpenRTB的adid',

    -- === 基础信息 ===
    name VARCHAR(255) NOT NULL COMMENT '创意名称',
    description TEXT COMMENT '创意描述',

    -- === 格式类型 ===
    format TINYINT NOT NULL COMMENT '格式:1=Banner/2=Video/3=Audio/4=Native',

    -- === 落地页 (对应Bid.adm中的链接) ===
    landing_page_url VARCHAR(1024) NOT NULL COMMENT '落地页URL',
    display_url VARCHAR(512) NOT NULL COMMENT '展示URL',

    -- === 品牌安全 (对应Bid.adomain) ===
    advertiser_domain VARCHAR(255) COMMENT '广告主域名,对应OpenRTB的adomain',

    -- === 分类 (对应Bid.cat) ===
    cat JSON COMMENT 'IAB内容类别,JSON数组格式:["IAB1","IAB2"]',

    -- === 属性 (对应Bid.attr) ===
    attr JSON COMMENT '创意属性,JSON数组格式:[1,2,3]',

    -- === 语言 (对应Bid.language) ===
    language VARCHAR(8) COMMENT '创意语言,ISO-639-1-alpha-2',

    -- === 状态控制 ===
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0=草稿/1=启用/2=停用',
    start_time DATETIME COMMENT '生效时间',
    end_time DATETIME COMMENT '失效时间',

    -- === 扩展字段 (对应Bid.ext) ===
    ext JSON COMMENT '扩展字段',
    remark VARCHAR(512) COMMENT '备注',

    -- === 审核追溯 ===
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',

    -- === 逻辑删除 ===
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0=正常/1=删除',

    -- === 索引 ===
    INDEX idx_creative_id (creative_id),
    INDEX idx_advertiser (advertiser_id),
    INDEX idx_format (format),
    INDEX idx_status_time (status, start_time, end_time, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='创意表-对应OpenRTB的Creative';
```

---

### 二、素材表 (rtb_material)

对应OpenRTB的:
- `Bid.adm` (Ad Markup中的文件)
- `Bid.w`, `Bid.h` (尺寸)
- `Bid.w`, `Bid.h` (宽高)
- `Bid.dur` (视频/音频时长)
- `Bid.mime` (MIME类型)

```sql
CREATE TABLE rtb_material (
    -- === 主键 ===
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    material_id VARCHAR(64) UNIQUE NOT NULL COMMENT '素材唯一ID',

    -- === 归属 ===
    creative_id BIGINT NOT NULL COMMENT '关联创意ID,rtb_creative.id',
    advertiser_id BIGINT NOT NULL COMMENT '广告主ID',

    -- === 基础信息 ===
    name VARCHAR(255) NOT NULL COMMENT '素材名称',

    -- === 格式和尺寸 (对应Bid.w, Bid.h) ===
    format TINYINT NOT NULL COMMENT '格式:1=Banner/2=Video/3=Audio/4=Native',
    width INT NOT NULL COMMENT '素材宽度(像素),对应Bid.w',
    height INT NOT NULL COMMENT '素材高度(像素),对应Bid.h',
    ratio INT COMMENT '宽高比(例如100表示1:1),对应Bid.ratio',

    -- === 文件信息 ===
    file_id VARCHAR(128) NOT NULL COMMENT 'OSS文件ID',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_type VARCHAR(64) COMMENT '文件MIME类型',
    thumbnail_url VARCHAR(512) COMMENT '缩略图URL(视频/音频用)',

    -- === 媒体时长 (对应Bid.dur, Video/Audio专用) ===
    dur INT COMMENT '视频或音频持续时间(秒),对应Bid.dur',

    -- === MIME类型 (对应Imp.mimes) ===
    mimes JSON COMMENT '支持的MIME类型,JSON数组格式:["image/jpeg","image/png"]',

    -- === 协议 (对应Video.protocol) ===
    protocol INT COMMENT '视频响应协议:1=VAST1.0/2=VAST2.0/3=VAST3.0/4=VAST4.0',

    -- === 扩展字段 (对应Bid.ext) ===
    ext JSON COMMENT '扩展字段',
    remark VARCHAR(512) COMMENT '备注',

    -- === 审核追溯 ===
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',

    -- === 逻辑删除 ===
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0=正常/1=删除',

    -- === 索引 ===
    INDEX idx_material_id (material_id),
    INDEX idx_creative (creative_id),
    INDEX idx_advertiser (advertiser_id),
    INDEX idx_format_size (format, width, height),
    INDEX idx_status (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='素材表-对应OpenRTB的Ad Markup中的文件';
```

---

### 三、Banner素材扩展表 (rtb_material_banner)

对应OpenRTB的 `Bid.Banner` 对象

```sql
CREATE TABLE rtb_material_banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    material_id BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Banner字段 ===
    pos INT COMMENT '位置:1=首屏/2=次屏',
    btype JSON COMMENT '横幅类型,JSON数组格式:[1,2,3]',
    wmode INT COMMENT '窗口模式:1=正常/2=全屏',

    -- === 扩展字段 ===
    ext JSON COMMENT '扩展字段',

    -- === 索引 ===
    UNIQUE KEY uk_material (material_id),
    INDEX idx_pos (pos)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner素材扩展表';
```

---

### 四、Video素材扩展表 (rtb_material_video)

对应OpenRTB的 `Bid.Video` 对象

```sql
CREATE TABLE rtb_material_video (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    material_id BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Video字段 ===
    linearity TINYINT NOT NULL COMMENT '1=线性/2=非线性',
    sequence INT COMMENT '视频序列号,从1开始',
    min_duration INT COMMENT '最小视频时长(秒)',
    max_duration INT COMMENT '最大视频时长(秒)',
    startdelay INT COMMENT '前贴片:0,中贴片:-1,后贴片:>0',
    skip INT COMMENT '跳过按钮:0=不可跳过/1=可跳过',
    skipmin INT COMMENT '最少播放多少秒后可跳过',
    skipafter INT COMMENT '多少秒后显示跳过按钮',
    placement TINYINT COMMENT '1=流内/2=插屏/3=悬停',
    playbackend INT COMMENT '播放方法:1=自动播放有声/2=自动播放静音/3=点击播放/4=鼠标悬停',
    playableafter INT COMMENT '可播放的最长秒数 (OpenRTB 2.6新增)',
    podid VARCHAR(64) COMMENT '广告组ID (OpenRTB 2.6新增)',
    podsize INT COMMENT '广告组大小 (OpenRTB 2.6新增)',
    podseq INT COMMENT '广告组序列号 (OpenRTB 2.6新增)',
    mincpmpersec DECIMAL(10,2) COMMENT '每秒最低CPM (OpenRTB 2.6新增)',
    maxseq INT COMMENT '最大序列号 (OpenRTB 2.6新增)',
    render INT COMMENT '渲染方式 (OpenRTB 2.6新增)',

    -- === API框架 (对应Video.api) ===
    api JSON COMMENT '支持的API框架,JSON数组格式:[1,2,3,4,5,6]',

    -- === 扩展字段 ===
    ext JSON COMMENT '扩展字段',

    -- === 索引 ===
    UNIQUE KEY uk_material (material_id),
    INDEX idx_linearity (linearity),
    INDEX idx_podid (podid) -- OpenRTB 2.6新增
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Video素材扩展表';
```

---

### 五、Audio素材扩展表 (rtb_material_audio)

对应OpenRTB的 `Bid.Audio` 对象

```sql
CREATE TABLE rtb_material_audio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    material_id BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Audio字段 ===
    sequence INT COMMENT '音频序列号,从1开始',
    min_duration INT COMMENT '最小音频时长(秒)',
    max_duration INT COMMENT '最大音频时长(秒)',
    startdelay INT COMMENT '开始延迟',

    -- === API框架 ===
    api JSON COMMENT '支持的API框架,JSON数组格式:[1,2,3,4]',

    -- === 扩展字段 ===
    ext JSON COMMENT '扩展字段',

    -- === 索引 ===
    UNIQUE KEY uk_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Audio素材扩展表';
```

---

### 六、Native素材扩展表 (rtb_material_native)

对应OpenRTB的 `Bid.Native` 对象

```sql
CREATE TABLE rtb_material_native (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    material_id BIGINT NOT NULL COMMENT '关联素材ID,rtb_material.id',

    -- === 对应OpenRTB Native字段 ===
    request_json TEXT COMMENT '原生广告请求JSON字符串',
    ver VARCHAR(16) COMMENT '原生API版本',

    -- === 扩展字段 ===
    ext JSON COMMENT '扩展字段',

    -- === 索引 ===
    UNIQUE KEY uk_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Native素材扩展表';
```

---

## 与OpenRTB的映射关系

### 竞价请求映射

| OpenRTB字段 | 数据库表 | 数据库字段 | 说明 |
|------------|---------|-----------|------|
| **BidRequest层面** |
| BidRequest.imp | rtb_material | 查询匹配的素材 |
| BidRequest.device | rtb_device | 设备信息表(未设计) |
| BidRequest.user | rtb_user | 用户信息表(未设计) |
| **BidResponse层面** |
| Bid.id | rtb_creative | creative_id | 对应crid |
| Bid.adid | rtb_creative | advertiser_id | 对应adid |
| Bid.impid | - | - | 来自请求 |
| Bid.price | - | - | 竞价价格(实时计算) |
| Bid.crid | rtb_creative | creative_id | 创意ID |
| Bid.adm | rtb_material | file_id | 生成广告标记 |
| Bid.w | rtb_material | width | 创意宽度 |
| Bid.h | rtb_material | height | 创意高度 |
| Bid.adomain | rtb_creative | advertiser_domain | 广告主域名 |
| Bid.cat | rtb_creative | cat | IAB分类 |
| Bid.attr | rtb_creative | attr | 创意属性 |
| Bid.language | rtb_creative | language | 语言 |
| Bid.dur | rtb_material | dur | 视频/音频时长 |
| Bid.mime | rtb_material | file_type | MIME类型 |

### 查询流程

```
1. 收到OpenRTB BidRequest
   ├─ imp.banner.w = 728
   ├─ imp.banner.h = 90
   └─ imp.banner.mimes = ["image/jpeg"]

2. 查询素材表
   SELECT * FROM rtb_material
   WHERE format = 1
     AND width = 728
     AND height = 90
     AND deleted = 0
   LIMIT 1

3. 查询创意表
   SELECT * FROM rtb_creative
   WHERE id = ?
     AND status = 1
     AND deleted = 0

4. 查询Banner扩展表
   SELECT * FROM rtb_material_banner
   WHERE material_id = ?

5. 构建BidResponse
   {
     "id": "bid1",
     "impid": "imp1",
     "adid": "123",              // 来自rtb_creative.advertiser_id
     "crid": "C001",             // 来自rtb_creative.creative_id
     "price": 0.75,
     "w": 728,                   // 来自rtb_material.width
     "h": 90,                    // 来自rtb_material.height
     "adm": "<a href='...'><img src='...'/></a>",  // 基于file_id生成
     "adomain": ["example.com"]  // 来自rtb_creative.advertiser_domain
   }
```

---

## 索引设计

### 核心查询场景

**场景1: 竞价查询 (最高频)**
```sql
-- 查询条件: format + width + height + status
SELECT m.*, c.*
FROM rtb_material m
JOIN rtb_creative c ON m.creative_id = c.id
WHERE m.format = 1
  AND m.width = 728
  AND m.height = 90
  AND c.status = 1
  AND c.deleted = 0
  AND m.deleted = 0
LIMIT 1;

-- 需要的索引
INDEX idx_format_size (format, width, height)
INDEX idx_status_time (status, start_time, end_time, deleted)
```

**场景2: 广告主查询素材列表**
```sql
-- 查询条件: advertiser_id + deleted
SELECT * FROM rtb_creative
WHERE advertiser_id = 123
  AND deleted = 0
ORDER BY create_time DESC;

-- 需要的索引
INDEX idx_advertiser (advertiser_id)
```

**场景3: 创意关联查询**
```sql
-- 查询某个创意的所有素材
SELECT * FROM rtb_material
WHERE creative_id = 123
  AND deleted = 0;

-- 需要的索引
INDEX idx_creative (creative_id)
```

---

## 使用示例

### 1. 插入创意和素材

```sql
-- Step 1: 插入创意(Banner)
INSERT INTO rtb_creative (
    creative_id, advertiser_id, name, format,
    landing_page_url, display_url, advertiser_domain,
    cat, language, status, ext
) VALUES (
    'C001', 123, '双11促销Banner活动', 1,
    'https://example.com/promo11',
    'example.com',
    'example.com',
    '["IAB24","IAB24-1"]',  -- IAB分类: 电子商务 > 零售
    'zh-CN',
    1,
    '{"campaign": "双11", "theme": "促销"}'
);

-- Step 2: 插入素材(728x90 Banner)
INSERT INTO rtb_material (
    material_id, creative_id, advertiser_id, name,
    format, width, height, file_id, file_type,
    mimes, ext
) VALUES (
    'M001', 1, 123, '双11促销 728x90 版本A',
    1, 728, 90, 'oss_banner_728x90_v1.jpg',
    'image/jpeg',
    '["image/jpeg","image/png"]',
    '{"quality": "high", "version": "A"}'
);

-- Step 3: 插入素材(300x250 Banner)
INSERT INTO rtb_material (
    material_id, creative_id, advertiser_id, name,
    format, width, height, file_id, file_type,
    mimes, ext
) VALUES (
    'M002', 1, 123, '双11促销 300x250 版本A',
    1, 300, 250, 'oss_banner_300x250_v1.jpg',
    'image/jpeg',
    '["image/jpeg","image/png"]',
    '{"quality": "high", "version": "A"}'
);

-- Step 4: 插入Banner扩展属性(728x90)
INSERT INTO rtb_material_banner (
    material_id, pos, btype, wmode
) VALUES (
    1, 1, '[2,3]', 1
);

-- Step 5: 插入Banner扩展属性(300x250)
INSERT INTO rtb_material_banner (
    material_id, pos, btype, wmode
) VALUES (
    2, 2, '[2]', 1
);
```

### 2. 竞价查询

```sql
-- 场景: 收到728x90的Banner竞价请求
SELECT
    m.material_id,
    m.width,
    m.height,
    m.file_id,
    m.mimes,
    c.creative_id,
    c.landing_page_url,
    c.display_url,
    c.advertiser_domain,
    c.cat,
    c.attr,
    b.pos,
    b.btype
FROM rtb_material m
JOIN rtb_creative c ON m.creative_id = c.id
LEFT JOIN rtb_material_banner b ON b.material_id = m.id
WHERE m.format = 1          -- Banner
  AND m.width = 728         -- 宽度728
  AND m.height = 90         -- 高度90
  AND c.status = 1          -- 启用状态
  AND c.deleted = 0         -- 未删除
  AND m.deleted = 0
  AND (c.start_time IS NULL OR c.start_time <= NOW())  -- 已到生效时间
  AND (c.end_time IS NULL OR c.end_time >= NOW())      -- 未到失效时间
LIMIT 1;
```

### 3. 构建OpenRTB响应

```java
// 基于查询结果构建Bid对象
Bid bid = new Bid();
bid.setId("bid1");
bid.setImpid("imp1");
bid.setAdid("123");           // rtb_creative.advertiser_id
bid.setCrid("C001");          // rtb_creative.creative_id
bid.setPrice(0.75f);
bid.setW(728);                // rtb_material.width
bid.setH(90);                 // rtb_material.height

// 构建adm (Ad Markup)
String adm = String.format(
    "<a href='%s'><img src='%s' width='%d' height='%d'/></a>",
    creative.getLandingPageUrl(),
    material.getFileId(),
    material.getWidth(),
    material.getHeight()
);
bid.setAdm(adm);

bid.setAdomain(Arrays.asList(creative.getAdvertiserDomain().split(",")));
```

---

## 数据示例

### 创意表数据

```
id | creative_id | advertiser_id | name           | format | landing_page_url      | advertiser_domain
---|-------------|---------------|----------------|--------|----------------------|------------------
1  | C001        | 123           | 双11促销Banner  | 1      | https://example.com/ | example.com
2  | C002        | 123           | 产品介绍Video   | 2      | https://example.com/ | example.com
```

### 素材表数据

```
id | material_id | creative_id | width | height | file_id             | format
---|-------------|-------------|-------|--------|---------------------|--------
1  | M001        | 1           | 728   | 90     | oss_banner_728x90   | 1
2  | M002        | 1           | 300   | 250    | oss_banner_300x250  | 1
3  | M003        | 1           | 320   | 50     | oss_banner_320x50   | 1
4  | M004        | 2           | 640   | 480    | oss_video_640x480   | 2
```

### Banner扩展表数据

```
id | material_id | pos | btype | wmode
---|-------------|-----|-------|-------
1  | 1           | 1   | [2,3] | 1
2  | 2           | 2   | [2]   | 1
3  | 3           | 1   | [2]   | 1
```

---

## 扩展性设计

### 未来可扩展的功能

1. **A/B测试**
   ```sql
   ALTER TABLE rtb_material ADD COLUMN ab_test_id VARCHAR(64);
   ALTER TABLE rtb_material ADD COLUMN ab_test_variant VARCHAR(10);
   ```

2. **预算控制**
   ```sql
   ALTER TABLE rtb_creative ADD COLUMN daily_budget DECIMAL(10,2);
   ALTER TABLE rtb_creative ADD COLUMN total_budget DECIMAL(10,2);
   ```

3. **频次控制**
   ```sql
   ALTER TABLE rtb_creative ADD COLUMN freq_cap INT;
   ALTER TABLE rtb_creative ADD COLUMN freq_cap_duration INT;
   ```

4. **定向条件**
   ```sql
   ALTER TABLE rtb_creative ADD COLUMN targeting JSON;
   -- {"geo":["CN","US"], "device":[1,2], "os":["iOS","Android"]}
   ```

---

## 总结

这个设计完全基于OpenRTB 2.6标准,具有以下特点:

✅ **严格遵循标准**: 每个字段都有对应的OpenRTB定义
✅ **清晰的关系**: 创意和素材1对N的关系
✅ **灵活扩展**: 通过JSON字段支持自定义扩展
✅ **高性能**: 合理的索引设计支持高并发竞价查询
✅ **易于维护**: 表结构清晰,便于理解和维护

---

**参考文档**:
- [OpenRTB 2.6规范中文版](../../参考文档/OpenRTB-2.6规范-中文版.md)
- [OpenRTB 2.6规范PDF](../../参考文档/OpenRTB-2-6_FINAL.pdf)
