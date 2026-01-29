package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告组状态枚举
 *
 * @author zhuque
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum AdGroupStatusEnum {

  DRAFT(0, "草稿"), RUNNING(1, "进行中"), PAUSED(2, "暂停");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(AdGroupStatusEnum status : values()) {
      if (status.getCode().equals(code)) {
        return status.getName();
      }
    }
    return "未知";
  }

  public static AdGroupStatusEnum getByCode(Integer code) {
    for(AdGroupStatusEnum status : values()) {
      if (status.getCode().equals(code)) {
        return status;
      }
    }
    return null;
  }
}
