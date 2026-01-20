package wake.su.zhuque.api.openrtb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * OpenRTB 视频广告对象
 */
@Data
@Schema(description = "视频广告对象")
public class Video {

    @JsonProperty("mimes")
    @Schema(description = "支持的MIME类型白名单", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> mimes;

    @JsonProperty("w")
    @Schema(description = "视频播放器宽度(设备无关像素)")
    private Integer w;

    @JsonProperty("h")
    @Schema(description = "视频播放器高度(设备无关像素)")
    private Integer h;

    @JsonProperty("linearity")
    @Schema(description = "线性/非线性指示器")
    private Integer linearity;

    @JsonProperty("minduration")
    @Schema(description = "最小广告持续时间(秒)")
    private Integer minduration;

    @JsonProperty("maxduration")
    @Schema(description = "最大广告持续时间(秒)")
    private Integer maxduration;

    @JsonProperty("protocol")
    @Schema(description = "视频响应协议")
    private Integer protocol;

    @JsonProperty("protocols")
    @Schema(description = "视频响应协议数组")
    private List<Integer> protocols;

    @JsonProperty("startdelay")
    @Schema(description = "广告开始延迟")
    private Integer startdelay;

    @JsonProperty("placement")
    @Schema(description = "展示位置")
    private Integer placement;

    @JsonProperty("plcmt")
    @Schema(description = "展示位置")
    private Integer plcmt;

    @JsonProperty("skip")
    @Schema(description = "是否可跳过：0=否，1=是")
    private Integer skip;

    @JsonProperty("skipmin")
    @Schema(description = "跳过按钮出现前需观看的最小秒数")
    private Integer skipmin;

    @JsonProperty("skipafter")
    @Schema(description = "跳过按钮出现前需观看的最小秒数")
    private Integer skipafter;

    @JsonProperty("minbitrate")
    @Schema(description = "最低比特率(Kbps)")
    private Integer minbitrate;

    @JsonProperty("maxbitrate")
    @Schema(description = "最高比特率(Kbps)")
    private Integer maxbitrate;

    @JsonProperty("delivery")
    @Schema(description = "允许的内容传递方法")
    private List<Integer> delivery;

    @JsonProperty("companionad")
    @Schema(description = "伴随广告对象数组")
    private List<Banner> companionad;

    @JsonProperty("companiontype")
    @Schema(description = "允许的伴随广告类型")
    private List<Integer> companiontype;

    @JsonProperty("api")
    @Schema(description = "API框架")
    private List<Integer> api;

    @JsonProperty("playbackend")
    @Schema(description = "播放方法 (OpenRTB 2.6增强)：1=自动播放有声，2=自动播放静音，3=点击播放，4=鼠标悬停播放")
    private Integer playbackend;

    @JsonProperty("playableafter")
    @Schema(description = "可播放的最长秒数 (OpenRTB 2.6新增)")
    private Integer playableafter;

    @JsonProperty("podid")
    @Schema(description = "广告组ID (OpenRTB 2.6新增)")
    private String podid;

    @JsonProperty("podsize")
    @Schema(description = "广告组大小 (OpenRTB 2.6新增)")
    private Integer podsize;

    @JsonProperty("podseq")
    @Schema(description = "广告组序列号 (OpenRTB 2.6新增)")
    private Integer podseq;

    @JsonProperty("mincpmpersec")
    @Schema(description = "每秒最低CPM (OpenRTB 2.6新增)")
    private Float mincpmpersec;

    @JsonProperty("maxseq")
    @Schema(description = "最大序列号 (OpenRTB 2.6新增)")
    private Integer maxseq;

    @JsonProperty("render")
    @Schema(description = "渲染方式 (OpenRTB 2.6新增)")
    private Integer render;

    @JsonProperty("ext")
    @Schema(description = "扩展字段")
    private Object ext;
}
