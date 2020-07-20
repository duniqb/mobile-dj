package cn.duniqb.mobile.utils;

/**
 * 返回状态码枚举
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/4/21 14:08
 * @since 1.0
 */
public enum BizCodeEnum {
    /**
     * 状态码
     */
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    LOGGED_IN(10003, "已登录"),
    ERROR_EXCEPTION(10004, "错误"),
    TIMEOUT_EXCEPTION(10005, "请求超时");


    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {

        return code;
    }

    public String getMsg() {

        return msg;
    }
}
