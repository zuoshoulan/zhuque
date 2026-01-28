package wake.su.zhuque.common.core.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wake.su.zhuque.common.core.result.OldResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice(basePackages = "wake.su.zhuque.controller")
public class GlobalExceptionHandler {

  /**
   * 业务异常
   */
  @ExceptionHandler(BusinessException.class)
  public OldResult<Void> handleBusinessException(BusinessException e) {
    log.error("业务异常：{}", e.getMessage());
    return OldResult.error(e.getCode(), e.getMessage());
  }

  /**
   * 参数校验异常
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public OldResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    FieldError fieldError = e.getBindingResult().getFieldError();
    String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
    log.error("参数校验异常：{}", message);
    return OldResult.error(400, message);
  }

  /**
   * 参数绑定异常
   */
  @ExceptionHandler(BindException.class)
  public OldResult<Void> handleBindException(BindException e) {
    FieldError fieldError = e.getFieldError();
    String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
    log.error("参数绑定异常：{}", message);
    return OldResult.error(400, message);
  }

  /**
   * 系统异常
   */
  @ExceptionHandler(Exception.class)
  public OldResult<Void> handleException(Exception e) {
    log.error("系统异常", e);
    return OldResult.error("系统异常，请联系管理员");
  }

}
