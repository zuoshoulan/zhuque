package wake.su.zhuque.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    private String fileType;

    /**
     * 宽度(像素) - 图片/视频有效
     */
    private Integer width;

    /**
     * 高度(像素) - 图片/视频有效
     */
    private Integer height;
}
