package wake.su.zhuque.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 投放活动状态枚举
 *
 * 简化设计：只有三种状态 - 草稿：新建未发布 - 进行中：正在投放（用户点击启动） - 暂停：手动暂停
 *
 * 时间到期/预算耗尽后自动停止投放，但不改变状态字段 前端根据 end_time 和 budget_used 判断是否"完成"
 */
@Getter
@AllArgsConstructor
public enum CampaignStatusEnum {

  DRAFT(0, "草稿"), RUNNING(1, "进行中"), PAUSED(2, "暂停");

  private final Integer code;
  private final String name;

  public static String getNameByCode(Integer code) {
    for(CampaignStatusEnum status : values()) {
      if (status.getCode().equals(code)) {
        return status.getName();
      }
    }
    return "未知";
  }

  public static CampaignStatusEnum getByCode(Integer code) {
    for(CampaignStatusEnum status : values()) {
      if (status.getCode().equals(code)) {
        return status;
      }
    }
    return null;
  }
}
