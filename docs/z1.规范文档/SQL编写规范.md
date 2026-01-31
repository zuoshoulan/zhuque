# SQL 编写规范

## 1. 字符集设置

**所有 SQL 文件必须显式设置字符集**，防止中文乱码：

```sql
-- 表创建前必须添加
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE `table_name` (
    ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表说明';
```

**关键点**：
- `SET NAMES utf8mb4;` - 设置客户端字符集
- `SET CHARACTER SET utf8mb4;` - 设置连接字符集
- `DEFAULT CHARSET=utf8mb4` - 设置表默认字符集
- `COLLATE=utf8mb4_unicode_ci` - 设置排序规则（不区分大小写）

## 2. 表命名规范

- 小写字母 + 下划线
- 前缀：`rtb_`（RTB相关表）、`sys_`（系统表）
- 示例：`rtb_campaign`、`rtb_ad_group`、`sys_user`

## 3. 字段命名规范

- 小写字母 + 下划线
- 主键统一使用 `id`（BIGINT 自增）
- 不再创建冗余的业务 ID 字段（如 `campaign_id` VARCHAR）

```sql
-- 正确
CREATE TABLE `rtb_campaign` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投放活动ID（主键）',
    ...
);

-- 错误（不要双重 ID）
CREATE TABLE `rtb_campaign` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `campaign_id` VARCHAR(64) NOT NULL,  -- 冗余字段，不要
    ...
);
```

## 4. 注释规范

- 所有字段必须添加 COMMENT
- 复杂字段需要说明取值范围

```sql
`status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=草稿/1=进行中/2=暂停/3=已完成',
```

## 5. 索引规范

- 索引命名：`idx_字段名`
- 联合索引：`idx_字段1_字段2`

```sql
KEY `idx_advertiser_status` (`advertiser_id`, `status`)
```

## 6. 时间字段规范

- 创建时间：`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
- 更新时间：`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- 逻辑删除：`deleted` TINYINT DEFAULT 0

## 7. 模板

```sql
-- 表说明
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE `table_name` (
    -- 主键
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',

    -- 外键
    `ref_id` BIGINT NOT NULL COMMENT '关联表ID（ref_table.id）',

    -- 业务字段
    `name` VARCHAR(255) NOT NULL COMMENT '名称',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=禁用/1=启用',

    -- 审计字段
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常/1=删除',

    PRIMARY KEY (`id`),
    KEY `idx_ref_status` (`ref_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表说明';
```
