package wake.su.zhuque.common.enums;

/**
 * 操作系统类型枚举
 *
 * @author zhuque
 */
public enum OSType {

  /** iOS */
  IOS("iOS"),

  /** Android */
  ANDROID("Android"),

  /** Windows */
  WINDOWS("Windows"),

  /** macOS */
  MACOS("macOS"),

  /** Linux */
  LINUX("Linux"),

  /** Roku OS */
  ROKU("ROKU"),

  /** Chrome OS */
  CHROME_OS("Chrome OS"),

  /** Tizen (Samsung) */
  TIZEN("Tizen"),

  /** WebOS (LG) */
  WEBOS("WebOS"),

  /** Firefox OS */
  FIREFOX_OS("Firefox OS"),

  /** Symbian */
  SYMBIAN("Symbian");

  private final String name;

  OSType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * 根据 name 获取 OS 类型
   */
  public static OSType fromName(String name) {
    if (name == null) {
      return null;
    }
    for(OSType os : values()) {
      if (os.name.equalsIgnoreCase(name)) {
        return os;
      }
    }
    return null;
  }
}
