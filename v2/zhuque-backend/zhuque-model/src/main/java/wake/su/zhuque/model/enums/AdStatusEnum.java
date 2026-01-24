package wake.su.zhuque.model.enums;

/**
 * 广告状态枚举
 *
 * @author zhuque
 * @version 1.0
 */
public enum AdStatusEnum {

    /**
     * 草稿
     */
    DRAFT(0, "草稿"),

    /**
     * 进行中
     */
    RUNNING(1, "进行中"),

    /**
     * 暂停
     */
    PAUSED(2, "暂停");

    private final Integer code;
    private final String name;

    AdStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据状态码获取状态名称
     */
    public static String getNameByCode(Integer code) {
        if (code == null) {
            return "未知";
        }
        for (AdStatusEnum statusEnum : values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum.name;
            }
        }
        return "未知";
    }
}
