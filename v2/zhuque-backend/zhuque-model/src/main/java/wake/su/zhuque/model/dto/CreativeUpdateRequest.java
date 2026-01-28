package wake.su.zhuque.model.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

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

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endTime;
}
