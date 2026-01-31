package wake.su.zhuque.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 创意表DO 对应OpenRTB的Bid.crid, Bid.adid等
 *
 * @author OpenRTB
 * @version 2.6
 */
@Data
@TableName("rtb_creative")
public class RtbCreativeDO {

  /**
   * 主键ID
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * 创意ID,对应OpenRTB的crid
   */
  private String creativeId;

  /**
   * 广告主ID,对应OpenRTB的adid
   */
  private Long advertiserId;

  /**
   * 创意名称
   */
  private String name;

  /**
   * 创意描述
   */
  private String description;

  /**
   * 落地页URL
   */
  private String landingPageUrl;

  /**
   * 展示URL
   */
  private String displayUrl;

  /**
   * 广告主域名,对应OpenRTB的adomain
   */
  private String advertiserDomain;

  /**
   * IAB内容类别,JSON数组格式
   */
  private String cat;

  /**
   * 创意属性,JSON数组格式
   */
  private String attr;

  /**
   * 创意语言,ISO-639-1-alpha-2
   */
  private String language;

  /**
   * 状态:0=草稿/1=启用/2=停用
   */
  private Integer status;

  /**
   * 生效时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startTime;

  /**
   * 失效时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endTime;

  /**
   * 扩展字段,JSON格式
   */
  private String ext;

  /**
   * 备注
   */
  private String remark;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  private LocalDateTime updateTime;

  /**
   * 创建人
   */
  private String createBy;

  /**
   * 更新人
   */
  private String updateBy;

  /**
   * 逻辑删除:0=正常/1=删除
   */
  @TableLogic
  private Integer deleted;
}
