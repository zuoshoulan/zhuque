package wake.su.zhuque.model.dto.material;

import lombok.Data;

import java.util.List;

/**
 * Audio素材扩展属性
 * 对应OpenRTB的Bid.Audio对象
 */
@Data
public class AudioExt {
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
     * 支持的API框架列表
     */
    private List<Integer> api;

    /**
     * 扩展字段，JSON格式
     */
    private String ext;
}
