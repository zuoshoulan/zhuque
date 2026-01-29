package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 素材格式枚举
 */
@Getter
@AllArgsConstructor
public enum MaterialFormatEnum {

  BANNER(1, "Banner"), VIDEO(2, "Video"), AUDIO(3, "Audio"), NATIVE(4, "Native");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(MaterialFormatEnum format : values()) {
      if (format.getCode().equals(code)) {
        return format.getName();
      }
    }
    return "未知";
  }
}
