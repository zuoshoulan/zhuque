package wake.su.zhuque.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wake.su.zhuque.common.core.result.Result;
import wake.su.zhuque.dao.mapper.RtbFileMapper;
import wake.su.zhuque.model.dto.file.FileUploadResponse;
import wake.su.zhuque.model.entity.RtbFileDO;
import wake.su.zhuque.service.RtbFileService;

import java.util.concurrent.TimeUnit;

/**
 * 文件管理Controller
 * 用于文件的存储在数据库中的上传和下载
 * 支持 HTTP 缓存，适合竞价场景
 */
@Tag(name = "文件管理", description = "文件上传下载接口")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final RtbFileMapper fileMapper;
    private final RtbFileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件并返回文件信息（含尺寸），支持MD5去重")
    public Result<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        String fileId = fileService.upload(file);

        // 查询完整的文件信息
        RtbFileDO fileRecord = fileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RtbFileDO>()
                .eq(RtbFileDO::getFileId, fileId)
        );

        if (fileRecord == null) {
            return Result.error("文件上传失败");
        }

        // 构建响应
        FileUploadResponse response = FileUploadResponse.builder()
            .fileId(fileRecord.getFileId())
            .fileName(fileRecord.getFileName())
            .fileSize(fileRecord.getFileSize())
            .fileType(fileRecord.getFileType())
            .width(fileRecord.getWidth())
            .height(fileRecord.getHeight())
            .build();

        return Result.success(response);
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "根据文件ID下载文件")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileId) {
        // 根据fileId查询文件
        RtbFileDO fileRecord = fileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RtbFileDO>()
                .eq(RtbFileDO::getFileId, fileId)
        );

        if (fileRecord == null || fileRecord.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }

        // 构建响应，添加缓存头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileRecord.getFileType()));
        headers.setContentDispositionFormData("inline", fileRecord.getFileName()); // inline 支持浏览器预览
        headers.setContentLength(fileRecord.getFileData().length);

        // 添加缓存控制
        // 公共缓存：1小时（适合静态素材）
        headers.setCacheControl("public, max-age=3600");
        // ETag：使用 MD5 作为 ETag，支持客户端缓存验证
        headers.setETag("\"" + fileRecord.getFileMd5() + "\"");
        // Last-Modified：使用文件创建时间
        headers.setLastModified(fileRecord.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant());

        return new ResponseEntity<>(
            fileRecord.getFileData(),
            headers,
            HttpStatus.OK
        );
    }

    @GetMapping("/{fileId}/info")
    @Operation(summary = "根据文件ID获取文件信息（不含文件内容）")
    public ResponseEntity<RtbFileDO> getFileInfo(@PathVariable String fileId) {
        // 根据fileId查询文件信息
        RtbFileDO fileRecord = fileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RtbFileDO>()
                .eq(RtbFileDO::getFileId, fileId)
        );

        if (fileRecord == null) {
            return ResponseEntity.notFound().build();
        }

        // 清空文件数据，只返回元信息
        fileRecord.setFileData(null);

        return ResponseEntity.ok(fileRecord);
    }
}
