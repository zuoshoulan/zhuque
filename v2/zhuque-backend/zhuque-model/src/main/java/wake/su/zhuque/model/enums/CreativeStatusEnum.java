package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 创意状态枚举
 */
@Getter
@AllArgsConstructor
public enum CreativeStatusEnum {

  DRAFT(0, "草稿"), ENABLED(1, "启用"), DISABLED(2, "停用");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(CreativeStatusEnum status : values()) {
      if (status.getCode().equals(code)) {
        return status.getName();
      }
    }
    return "未知";
  }
}
