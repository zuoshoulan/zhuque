package wake.su.zhuque.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建素材Request
 */
@Data
public class MaterialCreateRequest {
    private Long creativeId;
    private Integer format;
    private String name;
    private Integer width;
    private Integer height;
    private String fileId;
    private String fileType;
    private List<String> mimes;
    private Integer dur;
    private Integer pos;
    private List<Integer> btype;
    private Integer linearity;
    private Integer startdelay;
    private Integer playbackend;
    private Integer audioSequence;
    private String nativeRequestJson;
}
