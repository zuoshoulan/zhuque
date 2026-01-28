package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 创意格式枚举
 */
@Getter
@AllArgsConstructor
public enum CreativeFormatEnum {

  BANNER(1, "Banner"), VIDEO(2, "Video"), AUDIO(3, "Audio"), NATIVE(4, "Native");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(CreativeFormatEnum format : values()) {
      if(format.getCode().equals(code)) {
        return format.getName();
      }
    }
    return "未知";
  }
}
