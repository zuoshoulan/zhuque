package wake.su.zhuque.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 主题偏好枚举
 */
public enum ThemePreferenceEnum {

  /**
   * 自动切换（根据时间自动切换）- 默认值
   */
  AUTO(0, "auto", "自动"),

  /**
   * 亮色主题
   */
  LIGHT(1, "light", "亮色"),

  /**
   * 暗色主题
   */
  DARK(2, "dark", "暗色");

  /**
   * 数据库存储值
   */
  @EnumValue
  private final Integer code;

  /**
   * 前端使用的标识
   */
  private final String value;

  /**
   * 描述
   */
  private final String description;

  ThemePreferenceEnum(Integer code, String value, String description) {
    this.code = code;
    this.value = value;
    this.description = description;
  }

  public Integer getCode() {
    return code;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  /**
   * 根据数据库code获取枚举
   */
  public static ThemePreferenceEnum fromCode(Integer code) {
    if (code == null) {
      return AUTO;
    }
    for(ThemePreferenceEnum theme : values()) {
      if (theme.code.equals(code)) {
        return theme;
      }
    }
    return AUTO;
  }

  /**
   * 根据前端value获取枚举
   */
  public static ThemePreferenceEnum fromValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      return AUTO;
    }
    for(ThemePreferenceEnum theme : values()) {
      if (theme.value.equals(value)) {
        return theme;
      }
    }
    return AUTO;
  }
}
