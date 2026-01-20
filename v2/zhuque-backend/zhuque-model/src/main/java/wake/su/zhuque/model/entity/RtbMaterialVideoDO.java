package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Video素材扩展表DO
 * 对应OpenRTB的Bid.Video对象
 *
 * @author OpenRTB
 * @version 2.6
 */
@Data
@TableName("rtb_material_video")
public class RtbMaterialVideoDO {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联素材ID,rtb_material.id
     */
    private Long materialId;

    /**
     * 1=线性/2=非线性
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
     * 可播放的最长秒数 (OpenRTB 2.6新增)
     */
    private Integer playableafter;

    /**
     * 广告组ID (OpenRTB 2.6新增)
     */
    private String podid;

    /**
     * 广告组大小 (OpenRTB 2.6新增)
     */
    private Integer podsize;

    /**
     * 广告组序列号 (OpenRTB 2.6新增)
     */
    private Integer podseq;

    /**
     * 每秒最低CPM (OpenRTB 2.6新增)
     */
    private BigDecimal mincpmpersec;

    /**
     * 最大序列号 (OpenRTB 2.6新增)
     */
    private Integer maxseq;

    /**
     * 渲染方式 (OpenRTB 2.6新增)
     */
    private Integer render;

    /**
     * 支持的API框架,JSON数组格式
     */
    private String api;

    /**
     * 扩展字段,JSON格式
     */
    private String ext;
}
