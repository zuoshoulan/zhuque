package wake.su.zhuque.common.core.result;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.Data;

/**
 * 统一响应结果
 *
 * @param <T>
 *          数据类型
 */
@Data
public class OldResult<T> {

  private Integer code;
  private String message;
  private T data;
  private Long timestamp;
  private String beijingTime;

  private static final DateTimeFormatter BEIJING_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      .withZone(ZoneId.of("Asia/Shanghai"));

  public OldResult() {
    this.timestamp = System.currentTimeMillis();
    this.beijingTime = BEIJING_TIME_FORMATTER.format(java.time.Instant.ofEpochMilli(this.timestamp));
  }

  public OldResult(Integer code, String message) {
    this.code = code;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
    this.beijingTime = BEIJING_TIME_FORMATTER.format(java.time.Instant.ofEpochMilli(this.timestamp));
  }

  public OldResult(Integer code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
    this.beijingTime = BEIJING_TIME_FORMATTER.format(java.time.Instant.ofEpochMilli(this.timestamp));
  }

  /**
   * 成功响应
   */
  public static <T> OldResult<T> success() {
    return new OldResult<>(200, "操作成功");
  }

  public static <T> OldResult<T> success(T data) {
    return new OldResult<>(200, "操作成功", data);
  }

  public static <T> OldResult<T> success(String message, T data) {
    return new OldResult<>(200, message, data);
  }

  /**
   * 失败响应
   */
  public static <T> OldResult<T> error(String message) {
    return new OldResult<>(500, message);
  }

  public static <T> OldResult<T> error(Integer code, String message) {
    return new OldResult<>(code, message);
  }

  /**
   * 自定义响应
   */
  public static <T> OldResult<T> build(Integer code, String message, T data) {
    return new OldResult<>(code, message, data);
  }

}
