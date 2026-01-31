package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 营销目标枚举
 */
@Getter
@AllArgsConstructor
public enum CampaignObjectiveEnum {

  BRAND_AWARENESS(1, "品牌曝光"), TRAFFIC(2, "流量"), CONVERSION(3, "转化"), ROI(4, "ROI");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(CampaignObjectiveEnum objective : values()) {
      if (objective.getCode().equals(code)) {
        return objective.getName();
      }
    }
    return "未知";
  }
}
