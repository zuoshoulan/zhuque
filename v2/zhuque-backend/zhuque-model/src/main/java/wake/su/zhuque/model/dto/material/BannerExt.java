package wake.su.zhuque.model.dto.material;

import lombok.Data;

import java.util.List;

/**
 * Banner素材扩展属性
 */
@Data
public class BannerExt {
    /**
     * 广告位置:1=首屏/2=次屏
     */
    private Integer pos;

    /**
     * 浏览器类型列表
     */
    private List<Integer> btype;
}
