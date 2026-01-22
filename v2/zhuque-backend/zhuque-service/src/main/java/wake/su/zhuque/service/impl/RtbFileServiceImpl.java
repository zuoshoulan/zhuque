package wake.su.zhuque.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wake.su.zhuque.common.util.ImageUtils;
import wake.su.zhuque.dao.mapper.RtbFileMapper;
import wake.su.zhuque.model.entity.RtbFileDO;
import wake.su.zhuque.service.RtbFileService;

import java.time.LocalDateTime;

/**
 * 文件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RtbFileServiceImpl implements RtbFileService {

    private final RtbFileMapper fileMapper;

    @Override
    @Transactional
    public Long upload(MultipartFile file) {
        try {
            // 读取文件内容
            byte[] fileData = file.getBytes();

            // 计算文件MD5
            String fileMd5 = calculateMD5(fileData);

            // 检查文件是否已存在（通过MD5去重）
            RtbFileDO existingFile = fileMapper.selectOne(
                new LambdaQueryWrapper<RtbFileDO>()
                    .eq(RtbFileDO::getFileMd5, fileMd5)
            );

            if (existingFile != null) {
                // 文件已存在，直接返回已有的id
                log.info("文件已存在，复用文件: id={}, fileUuid={}, fileName={}",
                    existingFile.getId(), existingFile.getFileUuid(), file.getOriginalFilename());

                return existingFile.getId();
            }

            // 文件不存在，创建新记录
            String fileUuid = "FILE_" + IdUtil.getSnowflakeNextId();

            RtbFileDO fileRecord = new RtbFileDO();
            fileRecord.setFileUuid(fileUuid);
            fileRecord.setFileName(file.getOriginalFilename());
            fileRecord.setFileData(fileData);
            fileRecord.setFileSize((long) fileData.length);
            fileRecord.setFileMd5(fileMd5);
            fileRecord.setFileType(file.getContentType());
            fileRecord.setCreateTime(LocalDateTime.now());
            fileRecord.setUpdateTime(LocalDateTime.now());

            // 如果是图片，解析尺寸
            if (ImageUtils.isImage(file.getContentType())) {
                ImageUtils.ImageDimension dimension = ImageUtils.getImageDimension(fileData);
                if (dimension != null) {
                    fileRecord.setWidth(dimension.getWidth());
                    fileRecord.setHeight(dimension.getHeight());
                    log.info("解析图片尺寸: {}x{}", dimension.getWidth(), dimension.getHeight());
                }
            }

            // 如果是视频，解析尺寸
            if (ImageUtils.isVideo(file.getContentType())) {
                ImageUtils.ImageDimension dimension = ImageUtils.getVideoDimension(fileData);
                if (dimension != null) {
                    fileRecord.setWidth(dimension.getWidth());
                    fileRecord.setHeight(dimension.getHeight());
                    log.info("解析视频尺寸: {}x{}", dimension.getWidth(), dimension.getHeight());
                }
            }

            fileMapper.insert(fileRecord);

            log.info("文件上传成功: id={}, fileUuid={}, fileName={}, size={}",
                fileRecord.getId(), fileUuid, file.getOriginalFilename(), fileData.length);

            return fileRecord.getId();

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void incrementUploadCount(String fileId) {
        // 不再需要，可以通过查询素材表统计
    }

    @Override
    @Transactional
    public void decrementUploadCount(String fileId) {
        // 不再需要，可以通过查询素材表判断是否可以删除
    }

    /**
     * 计算字节数组的MD5值
     */
    private String calculateMD5(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算MD5失败", e);
        }
    }
}
