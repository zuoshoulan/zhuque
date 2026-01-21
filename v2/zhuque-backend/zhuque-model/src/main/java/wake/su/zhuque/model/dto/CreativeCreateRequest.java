package wake.su.zhuque.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建创意Request
 */
@Data
public class CreativeCreateRequest {
    private Long advertiserId;
    private String name;
    private String description;
    private String landingPageUrl;
    private String displayUrl;
    private String advertiserDomain;
    private List<String> cat;
    private List<Integer> attr;
    private String language;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
