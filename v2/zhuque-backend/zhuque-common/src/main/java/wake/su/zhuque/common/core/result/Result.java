package wake.su.zhuque.common.core.result;

import lombok.Data;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    private String beijingTime;

    private static final DateTimeFormatter BEIJING_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("Asia/Shanghai"));

    public Result() {
        this.timestamp = System.currentTimeMillis();
        this.beijingTime = BEIJING_TIME_FORMATTER.format(java.time.Instant.ofEpochMilli(this.timestamp));
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.beijingTime = BEIJING_TIME_FORMATTER.format(java.time.Instant.ofEpochMilli(this.timestamp));
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.beijingTime = BEIJING_TIME_FORMATTER.format(java.time.Instant.ofEpochMilli(this.timestamp));
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 自定义响应
     */
    public static <T> Result<T> build(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

}
