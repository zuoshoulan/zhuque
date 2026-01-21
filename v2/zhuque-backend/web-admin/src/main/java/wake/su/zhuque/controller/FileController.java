package wake.su.zhuque.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wake.su.zhuque.dao.mapper.RtbFileMapper;
import wake.su.zhuque.model.entity.RtbFileDO;

/**
 * 文件下载Controller
 * 用于获取存储在数据库中的文件
 */
@Tag(name = "文件管理", description = "文件下载接口")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final RtbFileMapper fileMapper;

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

        // 构建响应
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileRecord.getFileType()));
        headers.setContentDispositionFormData("attachment", fileRecord.getFileName());
        headers.setContentLength(fileRecord.getFileData().length);

        return new ResponseEntity<>(
            fileRecord.getFileData(),
            headers,
            HttpStatus.OK
        );
    }
}
