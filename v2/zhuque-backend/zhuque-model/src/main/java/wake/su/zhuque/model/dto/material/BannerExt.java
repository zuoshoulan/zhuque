package wake.su.zhuque.model.dto.material;

import lombok.Data;

/**
 * Banner素材扩展属性 对应OpenRTB的Bid.Banner对象
 */
@Data
public class BannerExt {
  /**
   * 广告位置:1=首屏/2=次屏
   */
  private Integer pos;

  /**
   * 横幅类型（单选）
   */
  private Integer btype;

  /**
   * 窗口模式:1=正常/2=全屏
   */
  private Integer wmode;

  /**
   * 扩展字段，JSON格式
   */
  private String ext;
}
