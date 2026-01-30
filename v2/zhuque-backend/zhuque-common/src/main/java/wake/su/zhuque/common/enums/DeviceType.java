package wake.su.zhuque.common.enums;

/**
 * OpenRTB 2.6 设备类型标准
 *
 * @see <a href="https://www.iab.com/guidelines/openrtb-2-6-specification/">OpenRTB 2.6 Specification</a>
 * @author zhuque
 */
public enum DeviceType {

  /** 手机/手持设备 */
  PHONE(1, "手机"),

  /** 个人电脑 */
  PC(2, "个人电脑"),

  /** 平板 */
  TABLET(3, "平板"),

  /** 联网电视 */
  TV(4, "联网电视"),

  /** 机顶盒 */
  SET_TOP(5, "机顶盒");

  private final int code;
  private final String description;

  DeviceType(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  /**
   * 根据 code 获取设备类型
   */
  public static DeviceType fromCode(int code) {
    for (DeviceType type : values()) {
      if (type.code == code) {
        return type;
      }
    }
    return null;
  }
}
