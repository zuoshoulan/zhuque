package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 投放时段类型枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ScheduleTypeEnum {

  ALL_DAY(1, "全天"), WEEKDAYS(2, "工作日"), CUSTOM(3, "自定义");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(ScheduleTypeEnum type : values()) {
      if(type.getCode().equals(code)) {
        return type.getName();
      }
    }
    return "未知";
  }

  public static ScheduleTypeEnum getByCode(Integer code) {
    for(ScheduleTypeEnum type : values()) {
      if(type.getCode().equals(code)) {
        return type;
      }
    }
    return null;
  }
}
