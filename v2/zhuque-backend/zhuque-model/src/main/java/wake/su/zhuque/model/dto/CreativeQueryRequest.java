package wake.su.zhuque.model.dto;

import lombok.Data;

/**
 * 创意查询Request
 */
@Data
public class CreativeQueryRequest {
    private Long advertiserId;
    private String name;
    private Integer status;
    private Integer current = 1;
    private Integer size = 10;
}
