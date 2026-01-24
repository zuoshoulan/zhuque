package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出价策略枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum BidStrategyEnum {

    FIXED_CPM(1, "固定CPM"),
    SMART_BID(2, "智能出价"),
    TARGET_CPA(3, "目标CPA"),
    MAX_WIN(4, "最高赢价");

    private final Integer code;
    private final String name;

    public static String getNameByCode(Integer code) {
        for (BidStrategyEnum strategy : values()) {
            if (strategy.getCode().equals(code)) {
                return strategy.getName();
            }
        }
        return "未知";
    }

    public static BidStrategyEnum getByCode(Integer code) {
        for (BidStrategyEnum strategy : values()) {
            if (strategy.getCode().equals(code)) {
                return strategy;
            }
        }
        return null;
    }
}
