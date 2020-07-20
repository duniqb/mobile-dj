package cn.duniqb.mobile.dto.mini;

import lombok.Data;

/**
 * 安全校验的返回结果
 *
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/4 16:45
 * @since 1.8
 */
@Data
public class SecurityCheck {
    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errMsg;
}
