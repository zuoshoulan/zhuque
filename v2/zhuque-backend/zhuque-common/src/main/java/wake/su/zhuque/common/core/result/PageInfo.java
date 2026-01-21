package wake.su.zhuque.common.core.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页信息
 */
@Data
@Schema(description = "分页信息")
public class PageInfo implements Serializable {

    @Schema(description = "当前页码", example = "1")
    private Number current;

    @Schema(description = "每页数量", example = "10")
    private Number size;

    @Schema(description = "总记录数", example = "100")
    private Number total;

    @Schema(description = "总页数", example = "10")
    private Number pages;

    /**
     * 默认构造函数
     */
    public PageInfo() {
    }

    /**
     * 构造函数
     */
    public PageInfo(Number current, Number size, Number total) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = (total.intValue() + size.intValue() - 1) / size.intValue();
    }

    /**
     * 构建分页信息
     */
    public static PageInfo of(Number current, Number size, Number total) {
        return new PageInfo(current, size, total);
    }

}
