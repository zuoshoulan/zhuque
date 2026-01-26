package wake.su.zhuque.bid.service.impl.matcher;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wake.su.zhuque.bid.context.BidContext;
import wake.su.zhuque.bid.service.matcher.ScheduleMatcher;
import wake.su.zhuque.model.entity.RtbAdGroupDO;

import java.time.LocalTime;
import java.util.List;

/**
 * 时段匹配器实现
 *
 * @author zhuque
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ScheduleMatcherImpl implements ScheduleMatcher {

    private static final Logger log = LoggerFactory.getLogger(ScheduleMatcherImpl.class);

    // 时段类型: 1=全天, 2=工作日, 3=自定义
    private static final int SCHEDULE_ALL_DAY = 1;
    private static final int SCHEDULE_WEEKDAY = 2;
    private static final int SCHEDULE_CUSTOM = 3;

    @Override
    public boolean matches(BidContext context, RtbAdGroupDO adGroup) {
        Integer scheduleType = adGroup.getScheduleType();
        if (scheduleType == null) {
            scheduleType = SCHEDULE_ALL_DAY;
        }

        return switch (scheduleType) {
            case SCHEDULE_ALL_DAY -> true;  // 全天投放
            case SCHEDULE_WEEKDAY -> isWeekday(context.getDayOfWeek());
            case SCHEDULE_CUSTOM -> isCustomScheduleMatch(context, adGroup);
            default -> true;
        };
    }

    /**
     * 检查是否是工作日 (1-5 = 周一到周五)
     */
    private boolean isWeekday(Integer dayOfWeek) {
        return dayOfWeek != null && dayOfWeek >= 1 && dayOfWeek <= 5;
    }

    /**
     * 检查自定义时段
     */
    private boolean isCustomScheduleMatch(BidContext context, RtbAdGroupDO adGroup) {
        String scheduleConfig = adGroup.getScheduleConfig();
        if (scheduleConfig == null || scheduleConfig.isEmpty()) {
            return true;
        }

        JSONObject config = JSONUtil.parseObj(scheduleConfig);

        // 检查星期
        if (config.containsKey("weekdays")) {
            JSONArray weekdays = config.getJSONArray("weekdays");
            List<Integer> allowedDays = weekdays.toList(Integer.class);
            if (!allowedDays.contains(context.getDayOfWeek())) {
                return false;
            }
        }

        // 检查时间段
        if (config.containsKey("time_ranges")) {
            JSONArray timeRanges = config.getJSONArray("time_ranges");
            List<String> ranges = timeRanges.toList(String.class);

            int currentHour = context.getHourOfDay();
            boolean inRange = false;
            for (String range : ranges) {
                if (isTimeInRange(currentHour, range)) {
                    inRange = true;
                    break;
                }
            }
            if (!inRange) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查当前小时是否在时间段内
     * 支持 "09:00-12:00" 格式
     */
    private boolean isTimeInRange(int currentHour, String range) {
        try {
            String[] parts = range.split("-");
            int startHour = Integer.parseInt(parts[0].split(":")[0]);
            int endHour = Integer.parseInt(parts[1].split(":")[0]);
            return currentHour >= startHour && currentHour < endHour;
        } catch (Exception e) {
            log.warn("解析时间段失败: {}", range, e);
            return false;
        }
    }
}
