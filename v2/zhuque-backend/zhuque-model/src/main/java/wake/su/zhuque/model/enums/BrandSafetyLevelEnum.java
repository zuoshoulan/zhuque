package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 品牌安全级别枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum BrandSafetyLevelEnum {

  LOW(1, "宽松"), MEDIUM(2, "中等"), HIGH(3, "严格");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(BrandSafetyLevelEnum level : values()) {
      if(level.getCode().equals(code)) {
        return level.getName();
      }
    }
    return "未知";
  }

  public static BrandSafetyLevelEnum getByCode(Integer code) {
    for(BrandSafetyLevelEnum level : values()) {
      if(level.getCode().equals(code)) {
        return level;
      }
    }
    return null;
  }
}
