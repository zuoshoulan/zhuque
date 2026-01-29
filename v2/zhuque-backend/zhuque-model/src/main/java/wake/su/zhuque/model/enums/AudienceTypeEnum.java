package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 受众类型枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum AudienceTypeEnum {

  ALL(1, "全部"), NEW(2, "新客"), RETURNING(3, "老客");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(AudienceTypeEnum type : values()) {
      if (type.getCode().equals(code)) {
        return type.getName();
      }
    }
    return "未知";
  }

  public static AudienceTypeEnum getByCode(Integer code) {
    for(AudienceTypeEnum type : values()) {
      if (type.getCode().equals(code)) {
        return type;
      }
    }
    return null;
  }
}
