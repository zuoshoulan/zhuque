package wake.su.zhuque.model.dto;

import lombok.Data;

import java.util.List;
import wake.su.zhuque.model.dto.material.AudioExt;
import wake.su.zhuque.model.dto.material.BannerExt;
import wake.su.zhuque.model.dto.material.NativeExt;
import wake.su.zhuque.model.dto.material.VideoExt;

/**
 * 更新素材Request
 *
 * 注意：素材类型（format）不可变，如需更换类型请删除后重新创建
 */
@Data
public class MaterialUpdateRequest {
    // ========== 主表字段 ==========

    /**
     * 素材ID
     */
    private Long id;

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
     * 文件ID（如需更换文件）
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
