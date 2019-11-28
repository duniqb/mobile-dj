package cn.duniqb.mobile.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WxUserDto {

    private Integer id;

    /**
     * 小程序用户的openid
     */
    private String openid;

    /**
     * 用户在开放平台的唯一标识符
     */
    private String unionid;

    /**
     * 学号
     */
    private String stuNo;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别  0-男、1-女
     */
    private Boolean gender;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 语种
     */
    private String language;

    /**
     * 创建/注册时间
     */
    private Date ctime;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * sessionId
     */
    private String sessionId;

}
