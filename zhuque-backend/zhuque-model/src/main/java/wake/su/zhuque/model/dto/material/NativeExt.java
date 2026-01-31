package wake.su.zhuque.model.dto.material;

import lombok.Data;

/**
 * Native素材扩展属性 对应OpenRTB的Bid.Native对象
 */
@Data
public class NativeExt {
  /**
   * 原生广告请求JSON字符串
   */
  private String requestJson;

  /**
   * 原生API版本
   */
  private String ver;

  /**
   * 扩展字段，JSON格式
   */
  private String ext;
}
