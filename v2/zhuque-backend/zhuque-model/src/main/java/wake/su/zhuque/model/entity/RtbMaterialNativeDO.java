package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

/**
 * Native素材扩展表DO 对应OpenRTB的Bid.Native对象
 *
 * @author OpenRTB
 * @version 2.6
 */
@Data
@TableName("rtb_material_native")
public class RtbMaterialNativeDO {

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
   * 原生广告请求JSON字符串
   */
  private String requestJson;

  /**
   * 原生API版本
   */
  private String ver;

  /**
   * 扩展字段,JSON格式
   */
  private String ext;
}
