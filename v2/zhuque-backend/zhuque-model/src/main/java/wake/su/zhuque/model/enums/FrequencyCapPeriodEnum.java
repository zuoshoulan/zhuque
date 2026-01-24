package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 频次周期枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum FrequencyCapPeriodEnum {

    HOURLY(1, "小时"),
    DAILY(2, "天"),
    WEEKLY(3, "周"),
    MONTHLY(4, "月");

    private final Integer code;
    private final String name;

    public static String getNameByCode(Integer code) {
        for (FrequencyCapPeriodEnum period : values()) {
            if (period.getCode().equals(code)) {
                return period.getName();
            }
        }
        return "未知";
    }

    public static FrequencyCapPeriodEnum getByCode(Integer code) {
        for (FrequencyCapPeriodEnum period : values()) {
            if (period.getCode().equals(code)) {
                return period;
            }
        }
        return null;
    }
}
