package wake.su.zhuque.common.core.constant;

/**
 * 通用常量
 */
public class Constants {

  /**
   * UTF-8 字符集
   */
  public static final String UTF8 = "UTF-8";

  /**
   * GBK 字符集
   */
  public static final String GBK = "GBK";

  /**
   * 成功标记
   */
  public static final Integer SUCCESS = 200;

  /**
   * 失败标记
   */
  public static final Integer ERROR = 500;

  /**
   * 登录成功
   */
  public static final Integer LOGIN_SUCCESS = 200;

  /**
   * 登录失败
   */
  public static final Integer LOGIN_FAIL = 401;

  /**
   * Token有效期（2小时）
   */
  public static final Long TOKEN_EXPIRE_TIME = 7200L;

  /**
   * RefreshToken有效期（7天）
   */
  public static final Long REFRESH_TOKEN_EXPIRE_TIME = 604800L;

  /**
   * JWT用户IDkey
   */
  public static final String JWT_USER_ID = "userId";

  /**
   * JWT用户名key
   */
  public static final String JWT_USERNAME = "username";

}
