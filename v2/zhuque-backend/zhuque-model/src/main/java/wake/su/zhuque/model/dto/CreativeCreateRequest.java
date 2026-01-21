package wake.su.zhuque.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建创意Request
 */
@Data
public class CreativeCreateRequest {
    private Long advertiserId;  // 超级管理员必填，普通用户由后端自动设置
    private String name;
    private String description;
    private String landingPageUrl;
    private String displayUrl;
    private String advertiserDomain;
    private List<String> cat;
    private List<Integer> attr;
    private String language;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
