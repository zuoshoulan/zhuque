package wake.su.zhuque.model.dto;

import lombok.Data;

/**
 * 素材查询Request
 */
@Data
public class MaterialQueryRequest {
  private Long creativeId;
  private Long advertiserId;
  private Integer format;
  private Integer width;
  private Integer height;
  private Integer current = 1;
  private Integer size = 10;
}
