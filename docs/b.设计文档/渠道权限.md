# 渠道权限控制设计（简化版）

> **设计原则**: 简单实用，够用就好

---

## 一、核心思路

### 1.1 需求场景

- 渠道管理员只能管理自己负责的渠道（如头条、百度）
- 普通运营可能负责多个渠道
- 超级管理员可以管理所有渠道

### 1.2 解决方案

**最简单的方式**：用户-渠道关联表 + 查询时过滤

```
用户表 ←→ 用户-渠道关联表 ←→ 渠道表
```

---

## 二、数据库设计

### 2.1 渠道表

```sql
CREATE TABLE sys_channel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '渠道ID',
  channel_name VARCHAR(50) NOT NULL COMMENT '渠道名称',
  channel_code VARCHAR(50) UNIQUE NOT NULL COMMENT '渠道编码',
  status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_channel_code (channel_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='渠道表';

-- 初始化数据
INSERT INTO sys_channel (channel_name, channel_code) VALUES
('巨量引擎（头条）', 'toutiao'),
('百度营销', 'baidu'),
('腾讯广告', 'tencent');
```

### 2.2 用户-渠道关联表

```sql
CREATE TABLE sys_user_channel (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  channel_id BIGINT NOT NULL COMMENT '渠道ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_channel (user_id, channel_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户渠道关联表';

-- 示例：用户100可以访问渠道1和渠道2
INSERT INTO sys_user_channel (user_id, channel_id) VALUES
(100, 1), (100, 2);
```

### 2.3 业务表增加渠道字段

```sql
-- 广告主表
ALTER TABLE advertiser ADD COLUMN channel_id BIGINT COMMENT '渠道ID';

-- 推广活动表
ALTER TABLE campaign ADD COLUMN channel_id BIGINT COMMENT '渠道ID';

-- 广告组表
ALTER TABLE adgroup ADD COLUMN channel_id BIGINT COMMENT '渠道ID';
```

---

## 三、后端实现

### 3.1 用户服务

```java
/**
 * 获取用户可访问的渠道ID列表
 */
@Service
public class UserService {

    @Autowired
    private UserChannelMapper userChannelMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户可访问的渠道ID列表
     * @return null表示所有渠道，空列表表示无权限
     */
    public List<Long> getUserChannelIds(Long userId) {
        // 1. 超级管理员返回null（表示所有渠道）
        if (isAdmin(userId)) {
            return null;
        }

        // 2. 从缓存获取
        String cacheKey = "user:channels:" + userId;
        List<Long> channelIds = (List<Long>) redisTemplate.opsForValue().get(cacheKey);

        if (channelIds != null) {
            return channelIds;
        }

        // 3. 从数据库查询
        channelIds = userChannelMapper.selectChannelIdsByUserId(userId);

        // 4. 写入缓存（30分钟）
        if (channelIds != null && !channelIds.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, channelIds, 30, TimeUnit.MINUTES);
        }

        return channelIds;
    }

    /**
     * 为用户分配渠道权限
     */
    @Transactional
    public void assignUserChannels(Long userId, List<Long> channelIds) {
        // 1. 删除原有权限
        userChannelMapper.deleteByUserId(userId);

        // 2. 插入新权限
        if (channelIds != null && !channelIds.isEmpty()) {
            userChannelMapper.batchInsert(userId, channelIds);
        }

        // 3. 清除缓存
        redisTemplate.delete("user:channels:" + userId);
    }
}
```

### 3.2 广告主服务

```java
/**
 * 广告主服务 - 带渠道权限过滤
 */
@Service
public class AdvertiserService {

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertiserMapper advertiserMapper;

    /**
     * 查询广告主列表（自动过滤渠道权限）
     */
    public List<Advertiser> listAdvertiser(Long userId, AdvertiserQuery query) {
        // 1. 获取用户可访问的渠道
        List<Long> channelIds = userService.getUserChannelIds(userId);

        // 2. 设置查询条件
        query.setChannelIds(channelIds);

        // 3. 查询数据
        return advertiserMapper.selectList(query);
    }

    /**
     * 查询广告主详情（验证权限）
     */
    public Advertiser getAdvertiser(Long userId, Long advertiserId) {
        Advertiser advertiser = advertiserMapper.selectById(advertiserId);

        if (advertiser == null) {
            throw new BusinessException("广告主不存在");
        }

        // 验证渠道权限
        List<Long> channelIds = userService.getUserChannelIds(userId);
        if (channelIds != null && !channelIds.contains(advertiser.getChannelId())) {
            throw new BusinessException("无权访问该广告主");
        }

        return advertiser;
    }
}
```

### 3.3 MyBatis Mapper

```java
/**
 * 用户渠道关联Mapper
 */
public interface UserChannelMapper {

    /**
     * 查询用户可访问的渠道ID列表
     */
    @Select("SELECT channel_id FROM sys_user_channel WHERE user_id = #{userId}")
    List<Long> selectChannelIdsByUserId(Long userId);

    /**
     * 删除用户的所有渠道权限
     */
    @Delete("DELETE FROM sys_user_channel WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入用户渠道权限
     */
    @Insert("<script>" +
            "INSERT INTO sys_user_channel (user_id, channel_id) VALUES " +
            "<foreach collection='channelIds' item='channelId' separator=','>" +
            "(#{userId}, #{channelId})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("userId") Long userId, @Param("channelIds") List<Long> channelIds);
}
```

```xml
<!-- AdvertiserMapper.xml -->
<select id="selectList" resultType="Advertiser">
    SELECT * FROM advertiser
    WHERE status = 1
    <if test="channelIds != null">
        AND channel_id IN
        <foreach collection="channelIds" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </if>
    ORDER BY create_time DESC
</select>
```

---

## 四、前端实现

### 4.1 分配渠道权限页面

```vue
<template>
  <el-dialog v-model="dialogVisible" title="分配渠道权限" width="500px">
    <el-form label-width="100px">
      <el-form-item label="用户名称">
        <span>{{ userName }}</span>
      </el-form-item>

      <el-form-item label="选择渠道">
        <el-select
          v-model="selectedChannelIds"
          multiple
          placeholder="请选择渠道"
          style="width: 100%"
        >
          <el-option
            v-for="channel in channels"
            :key="channel.id"
            :label="channel.channelName"
            :value="channel.id"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSave">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { getUserChannels, assignUserChannels } from '@/api/user'
import { getChannelList } from '@/api/channel'

const dialogVisible = ref(false)
const userName = ref('')
const selectedChannelIds = ref([])
const channels = ref([])

const open = async (user) => {
  dialogVisible.value = true
  userName.value = user.realName

  // 加载所有渠道
  channels.value = await getChannelList()

  // 加载用户已有渠道
  selectedChannelIds.value = await getUserChannels(user.id)
}

const handleSave = async () => {
  await assignUserChannels(userId, selectedChannelIds.value)
  dialogVisible.value = false
  ElMessage.success('分配成功')
}

defineExpose({ open })
</script>
```

---

## 五、使用示例

### 5.1 为用户分配渠道权限

```java
/**
 * 用户管理Controller
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 为用户分配渠道权限
     */
    @PostMapping("/{userId}/channels")
    public Result<Void> assignChannels(
            @PathVariable Long userId,
            @RequestBody List<Long> channelIds) {
        userService.assignUserChannels(userId, channelIds);
        return Result.success();
    }

    /**
     * 获取用户的渠道权限
     */
    @GetMapping("/{userId}/channels")
    public Result<List<Long>> getUserChannels(@PathVariable Long userId) {
        List<Long> channelIds = userService.getUserChannelIds(userId);
        return Result.success(channelIds);
    }
}
```

### 5.2 查询时自动过滤

```java
/**
 * 广告主Controller
 */
@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    @Autowired
    private AdvertiserService advertiserService;

    /**
     * 查询广告主列表（自动过滤渠道权限）
     */
    @GetMapping
    public Result<List<Advertiser>> listAdvertisers(
            @RequestParam(required = false) String keyword) {
        Long userId = SecurityContextHolder.getCurrentUserId();

        AdvertiserQuery query = new AdvertiserQuery();
        query.setKeyword(keyword);

        List<Advertiser> advertisers = advertiserService.listAdvertiser(userId, query);

        return Result.success(advertisers);
    }
}
```

---

## 六、缓存管理

### 6.1 缓存策略

```java
/**
 * 渠道权限缓存
 */
@Service
public class ChannelPermissionCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "user:channels:";
    private static final long CACHE_EXPIRE_TIME = 30; // 30分钟

    /**
     * 清除用户渠道权限缓存
     */
    public void clearUserChannelCache(Long userId) {
        String cacheKey = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 批量清除缓存（渠道变更时）
     */
    public void clearChannelCache(Long channelId) {
        // 查询所有拥有该渠道的用户
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
```

### 6.2 缓存刷新时机

- ✅ 分配渠道权限时清除缓存
- ✅ 修改渠道权限时清除缓存
- ✅ 渠道被删除时清除所有用户缓存
- ✅ 用户角色变更时清除缓存

---

## 七、总结

### 7.1 方案特点

| 特点 | 说明 |
|-----|------|
| ✅ **简单** | 只有1张关联表，没有复杂的规则引擎 |
| ✅ **实用** | 满足90%的渠道权限场景 |
| ✅ **高效** | 查询时直接用 IN 条件，性能好 |
| ✅ **灵活** | 可以扩展到其他数据权限场景 |

### 7.2 适用场景

- ✅ 渠道管理员权限控制
- ✅ 广告主权限控制
- ✅ 部门数据权限控制
- ✅ 代理商权限控制

### 7.3 不适用场景

- ❌ 复杂的多维度权限组合（如：渠道+地区+产品线）
- ❌ 动态权限规则（如：金额大于10000才能看）

### 7.4 扩展建议

如果将来需要更复杂的权限，可以考虑：
1. 引入数据权限规则表
2. 使用MyBatis拦截器自动追加SQL条件
3. 参考之前的复杂方案

---

**文档版本**: v2.0 (简化版)
**最后更新**: 2025-01-07
**维护者**: 开发团队
