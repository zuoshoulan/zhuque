package wake.su.zhuque.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 素材表DO
 * 对应OpenRTB的Bid.adm中的文件
 *
 * @author OpenRTB
 * @version 2.6
 */
@Data
@TableName("rtb_material")
public class RtbMaterialDO {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 素材唯一ID
     */
    private String materialId;

    /**
     * 关联创意ID,rtb_creative.id
     */
    private Long creativeId;

    /**
     * 广告主ID
     */
    private Long advertiserId;

    /**
     * 素材名称
     */
    private String name;

    /**
     * 格式:1=Banner/2=Video/3=Audio/4=Native
     */
    private Integer format;

    /**
     * 素材宽度(像素),对应Bid.w
     */
    private Integer width;

    /**
     * 素材高度(像素),对应Bid.h
     */
    private Integer height;

    /**
     * 宽高比(例如100表示1:1),对应Bid.ratio
     */
    private Integer ratio;

    /**
     * 关联文件ID,rtb_file.id
     */
    private Long fileId;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 缩略图URL(视频/音频用)
     */
    private String thumbnailUrl;

    /**
     * 视频或音频持续时间(秒),对应Bid.dur
     */
    private Integer dur;

    /**
     * 支持的MIME类型,JSON数组格式
     */
    private String mimes;

    /**
     * 视频响应协议:1=VAST1.0/2=VAST2.0/3=VAST3.0/4=VAST4.0
     */
    private Integer protocol;

    /**
     * 扩展字段,JSON格式
     */
    private String ext;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 逻辑删除:0=正常/1=删除
     */
    @TableLogic
    private Integer deleted;
}
