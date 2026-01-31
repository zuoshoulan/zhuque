-- 频次控制 Lua 脚本
-- 功能: 检查频次是否超限，如果未超限则累加计数
-- 参数:
--   KEYS[1]: 频次 Key
--   ARGV[1]: 频次上限 cap
--   ARGV[2]: TTL 秒数
-- 返回:
--   1: 成功 (未超限，已累加)
--   0: 失败 (已超限)

local key = KEYS[1]
local cap = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])

-- 获取当前计数
local current = tonumber(redis.call('GET', key)) or 0

-- 检查是否超限
if current < cap then
    -- 未超限，累加计数
    redis.call('INCR', key)

    -- 设置过期时间
    if ttl > 0 then
        redis.call('EXPIRE', key, ttl)
    end

    return 1  -- 成功
else
    -- 已超限
    return 0  -- 失败
end
