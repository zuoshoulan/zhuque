package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 投放速度枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum DeliveryModeEnum {

    ACCELERATED(1, "加速"),
    STANDARD(2, "均匀");

    private final Integer code;
    private final String name;

    public static String getNameByCode(Integer code) {
        for (DeliveryModeEnum mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode.getName();
            }
        }
        return "未知";
    }

    public static DeliveryModeEnum getByCode(Integer code) {
        for (DeliveryModeEnum mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        return null;
    }
}
