package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

/**
 * Banner素材扩展表DO 对应OpenRTB的Bid.Banner对象
 *
 * @author OpenRTB
 * @version 2.6
 */
@Data
@TableName("rtb_material_banner")
public class RtbMaterialBannerDO {

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
   * 位置:1=首屏/2=次屏
   */
  private Integer pos;

  /**
   * 横幅类型,JSON数组格式
   */
  private String btype;

  /**
   * 窗口模式:1=正常/2=全屏
   */
  private Integer wmode;

  /**
   * 扩展字段,JSON格式
   */
  private String ext;
}
