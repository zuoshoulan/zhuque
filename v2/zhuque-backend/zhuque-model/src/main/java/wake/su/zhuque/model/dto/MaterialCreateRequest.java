package wake.su.zhuque.model.dto;

import lombok.Data;

import java.util.List;
import wake.su.zhuque.model.dto.material.AudioExt;
import wake.su.zhuque.model.dto.material.BannerExt;
import wake.su.zhuque.model.dto.material.NativeExt;
import wake.su.zhuque.model.dto.material.VideoExt;

/**
 * 创建素材Request
 */
@Data
public class MaterialCreateRequest {
    // ========== 主表字段 ==========

    /**
     * 所属创意ID
     */
    private Long creativeId;

    /**
     * 素材格式:1=Banner/2=Video/3=Audio/4=Native
     */
    private Integer format;

    /**
     * 素材名称
     */
    private String name;

    /**
     * 素材宽度(像素)
     */
    private Integer width;

    /**
     * 素材高度(像素)
     */
    private Integer height;

    /**
     * 文件ID（需先通过文件上传接口获取）
     */
    private String fileId;

    /**
     * 支持的MIME类型列表
     */
    private List<String> mimes;

    /**
     * 视频或音频持续时间(秒)
     */
    private Integer dur;

    // ========== 扩展表字段（根据format使用对应的扩展对象） ==========

    /**
     * Banner扩展属性（format=1时使用）
     */
    private BannerExt bannerExt;

    /**
     * Video扩展属性（format=2时使用）
     */
    private VideoExt videoExt;

    /**
     * Audio扩展属性（format=3时使用）
     */
    private AudioExt audioExt;

    /**
     * Native扩展属性（format=4时使用）
     */
    private NativeExt nativeExt;
}
