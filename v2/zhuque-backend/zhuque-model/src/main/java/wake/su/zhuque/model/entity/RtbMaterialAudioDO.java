package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * Audio素材扩展表DO
 * 对应OpenRTB的Bid.Audio对象
 *
 * @author OpenRTB
 * @version 2.6
 */
@Data
@TableName("rtb_material_audio")
public class RtbMaterialAudioDO {

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
     * 音频序列号,从1开始
     */
    private Integer sequence;

    /**
     * 最小音频时长(秒)
     */
    private Integer minDuration;

    /**
     * 最大音频时长(秒)
     */
    private Integer maxDuration;

    /**
     * 开始延迟
     */
    private Integer startdelay;

    /**
     * 支持的API框架,JSON数组格式
     */
    private String api;

    /**
     * 扩展字段,JSON格式
     */
    private String ext;
}
