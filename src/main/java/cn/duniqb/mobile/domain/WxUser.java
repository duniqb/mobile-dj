package cn.duniqb.mobile.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "wx_user")
public class WxUser implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 小程序用户的openid
     */
    @Column(name = "openid")
    private String openid;

    /**
     * 学号
     */
    @Column(name = "stu_no")
    private String stuNo;

    /**
     * 用户昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 用户头像
     */
    @Column(name = "avatarUrl")
    private String avatarurl;

    /**
     * 性别  0-男、1-女
     */
    @Column(name = "gender")
    private Boolean gender;

    /**
     * 所在国家
     */
    @Column(name = "country")
    private String country;

    /**
     * 省份
     */
    @Column(name = "province")
    private String province;

    /**
     * 城市
     */
    @Column(name = "city")
    private String city;

    /**
     * 语种
     */
    @Column(name = "`language`")
    private String language;

    /**
     * 创建/注册时间
     */
    @Column(name = "ctime")
    private Date ctime;

    /**
     * 手机号码
     */
    @Column(name = "mobile")
    private String mobile;

    private static final long serialVersionUID = 1L;
}