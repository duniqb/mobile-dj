package cn.duniqb.mobile.dto.mini;

import lombok.Data;

/**
 * 小程序全局唯一后台接口调用凭据
 *
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/4 15:18
 * @since 1.8
 */
@Data
public class AccessToken {
    /**
     * 获取到的凭证
     */
    private String access_token;

    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private Integer expires_in;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
