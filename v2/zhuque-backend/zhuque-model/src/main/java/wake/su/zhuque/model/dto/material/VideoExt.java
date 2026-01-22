package wake.su.zhuque.model.dto.material;

import lombok.Data;

/**
 * Video素材扩展属性
 */
@Data
public class VideoExt {
    /**
     * 播放方式:1=线性播放/2=非线性播放
     */
    private Integer linearity;

    /**
     * 开始延迟(秒)
     */
    private Integer startdelay;

    /**
     * 播放结束行为
     */
    private Integer playbackend;
}
