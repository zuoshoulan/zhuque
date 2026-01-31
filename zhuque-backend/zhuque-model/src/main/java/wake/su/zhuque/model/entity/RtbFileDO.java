package wake.su.zhuque.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

/**
 * 文件表DO 用于存储上传的文件内容，实现文件复用
 *
 * @author zhuque
 * @version 1.0
 */
@Data
@TableName("rtb_file")
public class RtbFileDO {

  /**
   * 主键ID
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * 文件唯一UUID
   */
  @TableField("file_uuid")
  private String fileUuid;

  /**
   * 原始文件名
   */
  private String fileName;

  /**
   * 文件二进制数据
   */
  private byte[] fileData;

  /**
   * 文件大小(字节)
   */
  private Long fileSize;

  /**
   * 文件MD5值
   */
  private String fileMd5;

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

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  private LocalDateTime updateTime;
}
