package wake.su.zhuque.model.dto.material;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Video素材扩展属性
 * 对应OpenRTB的Bid.Video对象
 */
@Data
public class VideoExt {
    /**
     * 播放方式:1=线性播放/2=非线性播放
     */
    private Integer linearity;

    /**
     * 视频序列号,从1开始
     */
    private Integer sequence;

    /**
     * 最小视频时长(秒)
     */
    private Integer minDuration;

    /**
     * 最大视频时长(秒)
     */
    private Integer maxDuration;

    /**
     * 前贴片:0,中贴片:-1,后贴片:>0
     */
    private Integer startdelay;

    /**
     * 跳过按钮:0=不可跳过/1=可跳过
     */
    private Integer skip;

    /**
     * 最少播放多少秒后可跳过
     */
    private Integer skipmin;

    /**
     * 多少秒后显示跳过按钮
     */
    private Integer skipafter;

    /**
     * 1=流内/2=插屏/3=悬停
     */
    private Integer placement;

    /**
     * 播放方法:1=自动播放有声/2=自动播放静音/3=点击播放/4=鼠标悬停
     */
    private Integer playbackend;

    /**
     * 可播放的最长秒数
     */
    private Integer playableafter;

    /**
     * 广告组ID
     */
    private String podid;

    /**
     * 广告组大小
     */
    private Integer podsize;

    /**
     * 广告组序列号
     */
    private Integer podseq;

    /**
     * 每秒最低CPM
     */
    private BigDecimal mincpmpersec;

    /**
     * 最大序列号
     */
    private Integer maxseq;

    /**
     * 渲染方式
     */
    private Integer render;

    /**
     * 支持的API框架列表
     */
    private List<Integer> api;

    /**
     * 扩展字段，JSON格式
     */
    private String ext;
}
