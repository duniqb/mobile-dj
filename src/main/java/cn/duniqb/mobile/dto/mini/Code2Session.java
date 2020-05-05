package cn.duniqb.mobile.dto.mini;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 登录凭证校验返回的 JSON 数据包
 *
 * @author duniqb
 */
@Data
public class Code2Session {
    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 会话密钥
     */
    @JsonIgnore
    private String session_key;

    /**
     * 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
     */
    private String unionid;

    /**
     * 错误码
     */
    private String errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
