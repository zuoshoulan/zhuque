package wake.su.zhuque.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 广告列表VO
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@Schema(description = "广告列表")
public class AdListVO {

    @Schema(description = "广告ID")
    private Long id;

    @Schema(description = "投放活动ID")
    private Long campaignId;

    @Schema(description = "广告组ID")
    private Long adGroupId;

    @Schema(description = "广告组名称")
    private String adGroupName;

    @Schema(description = "广告主ID")
    private Long advertiserId;

    @Schema(description = "创意ID")
    private Long creativeId;

    @Schema(description = "创意名称")
    private String creativeName;

    @Schema(description = "广告名称")
    private String name;

    @Schema(description = "落地页URL")
    private String landingPageUrl;

    @Schema(description = "权重")
    private Integer weight;

    @Schema(description = "状态：0=草稿/1=进行中/2=暂停")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "显示状态类型：info/success/warning")
    private String displayStatusType;

    @Schema(description = "今日展现量")
    private Long todayImpressions;

    @Schema(description = "今日点击量")
    private Long todayClicks;

    @Schema(description = "今日CTR（%）")
    private BigDecimal todayCtr;

    @Schema(description = "今日消耗（元）")
    private BigDecimal todayCost;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
