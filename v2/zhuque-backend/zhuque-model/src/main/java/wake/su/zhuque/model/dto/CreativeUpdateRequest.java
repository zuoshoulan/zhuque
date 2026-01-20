package wake.su.zhuque.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 更新创意Request
 */
@Data
public class CreativeUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private String landingPageUrl;
    private String displayUrl;
    private String advertiserDomain;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
