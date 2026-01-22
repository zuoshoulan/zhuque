package wake.su.zhuque.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface RtbFileService {

    /**
     * 上传文件
     * 如果文件已存在（相同MD5），则返回已有的id
     * 如果文件不存在，则创建新记录并返回新的id
     *
     * @param file 上传的文件
     * @return 文件ID（rtb_file.id）
     */
    Long upload(MultipartFile file);

    /**
     * 增加文件引用计数
     *
     * @param fileId 文件ID
     */
    void incrementUploadCount(String fileId);

    /**
     * 减少文件引用计数
     *
     * @param fileId 文件ID
     */
    void decrementUploadCount(String fileId);
}
