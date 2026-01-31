package wake.su.zhuque.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 广告表DO 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@TableName("rtb_ad")
public class RtbAdDO {

  /**
   * 广告ID（主键）
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * 投放活动ID
   */
  private Long campaignId;

  /**
   * 广告组ID
   */
  private Long adGroupId;

  /**
   * 广告主ID
   */
  private Long advertiserId;

  /**
   * 创意ID（rtb_creative.id）
   */
  private Long creativeId;

  /**
   * 广告名称
   */
  private String name;

  /**
   * 落地页URL（可覆盖创意中的设置）
   */
  private String landingPageUrl;

  /**
   * 展示URL（可覆盖创意中的设置）
   */
  private String displayUrl;

  /**
   * 追踪参数（JSON）：{"utm_source":"rtb"}
   */
  private String trackingParams;

  /**
   * 权重，值越大分配流量越多
   */
  private Integer weight;

  /**
   * 状态：0=草稿/1=进行中/2=暂停
   */
  private Integer status;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  /**
   * 逻辑删除：0=正常/1=删除
   */
  @TableLogic
  private Integer deleted;
}
