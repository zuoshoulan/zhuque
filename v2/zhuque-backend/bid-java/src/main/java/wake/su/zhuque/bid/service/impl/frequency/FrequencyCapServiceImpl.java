package wake.su.zhuque.bid.service.impl.frequency;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import wake.su.zhuque.bid.service.frequency.FrequencyCapService;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import lombok.RequiredArgsConstructor;

/**
 * 频次控制服务实现 使用 Redis Lua 脚本实现原子操作
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class FrequencyCapServiceImpl implements FrequencyCapService {

  private static final Logger log = LoggerFactory.getLogger(FrequencyCapServiceImpl.class);

  private final StringRedisTemplate redisTemplate;

  private static final String FREQ_CHECK_AND_INCR_SCRIPT = """
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
      """;

  private static final String FREQ_CHECK_SCRIPT = """
      local key = KEYS[1]
      local current = tonumber(redis.call('GET', key)) or 0
      return current
      """;

  // 频次周期: 1=小时, 2=天, 3=周, 4=月
  private static final int PERIOD_HOUR = 1;
  private static final int PERIOD_DAY = 2;
  private static final int PERIOD_WEEK = 3;
  private static final int PERIOD_MONTH = 4;

  @Override
  public boolean checkFrequency(String userId, RtbAdGroupDO adGroup) {
    Integer cap = adGroup.getFrequencyCap();
    Integer period = adGroup.getFrequencyCapPeriod();

    if(cap == null || cap <= 0 || period == null) {
      return true; // 未设置频次限制
    }

    String key = buildKey(adGroup.getId(), userId, period);
    String current = redisTemplate.opsForValue().get(key);
    int count = current != null ? Integer.parseInt(current) : 0;

    return count < cap;
  }

  @Override
  public boolean tryRecord(String userId, RtbAdGroupDO adGroup) {
    Integer cap = adGroup.getFrequencyCap();
    Integer period = adGroup.getFrequencyCapPeriod();

    if(cap == null || cap <= 0 || period == null) {
      return true; // 未设置频次限制
    }

    String key = buildKey(adGroup.getId(), userId, period);
    int ttl = calculateTtl(period);

    // 执行 Lua 脚本
    Long result = redisTemplate
        .execute(
            org.springframework.data.redis.core.script.RedisScript.of(FREQ_CHECK_AND_INCR_SCRIPT,
                Long.class),
            Collections.singletonList(key), String.valueOf(cap), String.valueOf(ttl));

    return result != null && result == 1;
  }

  @Override
  public String buildKey(Long adGroupId, String userId) {
    // 默认按天计算
    return buildKey(adGroupId, userId, PERIOD_DAY);
  }

  /** 构建频次 Key */
  private String buildKey(Long adGroupId, String userId, Integer period) {
    LocalDate today = LocalDate.now();
    String dateStr;

    return switch(period) {
    case PERIOD_HOUR ->
      "freq:hour:" + today + ":" + LocalDateTime.now().getHour() + ":" + adGroupId + ":" + userId;
    case PERIOD_DAY -> "freq:day:" + today + ":" + adGroupId + ":" + userId;
    case PERIOD_WEEK -> "freq:week:" + getWeekKey(today) + ":" + adGroupId + ":" + userId;
    case PERIOD_MONTH -> "freq:month:" + today.getYear() + ":" + today.getMonthValue() + ":"
        + adGroupId + ":" + userId;
    default -> "freq:day:" + today + ":" + adGroupId + ":" + userId;
    };
  }

  @Override
  public void rollback(String userId, RtbAdGroupDO adGroup) {
    Integer period = adGroup.getFrequencyCapPeriod();
    if(period == null) {
      period = PERIOD_DAY;
    }

    String key = buildKey(adGroup.getId(), userId, period);
    try {
      redisTemplate.opsForValue().decrement(key);
    } catch(Exception e) {
      log.error("回滚频次失败, key={}", key, e);
    }
  }

  /** 计算TTL（秒） */
  private int calculateTtl(Integer period) {
    LocalDateTime now = LocalDateTime.now();
    return switch(period) {
    case PERIOD_HOUR ->
      (int) Duration.between(now, now.plusHours(1).withMinute(0).withSecond(0)).getSeconds();
    case PERIOD_DAY -> (int) Duration
        .between(now, now.plusDays(1).withHour(0).withMinute(0).withSecond(0)).getSeconds();
    case PERIOD_WEEK -> {
      int daysUntilMonday = 7 - now.getDayOfWeek().getValue();
      if(daysUntilMonday == 0)
        daysUntilMonday = 7;
      yield (int) Duration
          .between(now, now.plusDays(daysUntilMonday).withHour(0).withMinute(0).withSecond(0))
          .getSeconds();
    }
    case PERIOD_MONTH -> {
      int daysUntilMonthEnd = now.getMonth().length(now.toLocalDate().isLeapYear())
          - now.getDayOfMonth() + 1;
      yield (int) Duration
          .between(now, now.plusDays(daysUntilMonthEnd).withHour(0).withMinute(0).withSecond(0))
          .getSeconds();
    }
    default -> 86400; // 默认1天
    };
  }

  /** 获取周Key（格式: YYYY-Www） */
  private String getWeekKey(LocalDate date) {
    int weekOfYear = date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
    return date.getYear() + "-W" + weekOfYear;
  }
}
