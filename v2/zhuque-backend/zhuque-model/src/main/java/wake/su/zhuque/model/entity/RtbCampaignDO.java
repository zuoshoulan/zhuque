package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 投放活动表DO
 * 三层架构：Campaign（投放活动）→ AdGroup（广告组）→ Ad（广告）
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@TableName("rtb_campaign")
public class RtbCampaignDO {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 广告主ID
     */
    private Long advertiserId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 营销目标：1=品牌曝光/2=流量/3=转化/4=ROI
     */
    private Integer campaignObjective;

    /**
     * 目标类型：1=展示/2=点击/3=转化
     */
    private Integer campaignGoalType;

    /**
     * 目标值
     */
    private Long campaignGoalValue;

    /**
     * 总预算（元）
     */
    private BigDecimal lifetimeBudget;

    /**
     * 累计已消耗（元）
     */
    private BigDecimal lifetimeBudgetUsed;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态：0=草稿/1=进行中/2=暂停/3=已完成
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
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 逻辑删除：0=正常/1=删除
     */
    @TableLogic
    private Integer deleted;
}
