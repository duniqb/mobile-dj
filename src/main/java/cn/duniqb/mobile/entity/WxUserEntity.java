package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 小程序用户表
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_wx_user")
public class WxUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
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
	private Integer gender;
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
	private Date time;
	/**
	 * 手机号码
	 */
	private String mobile;
	/**
	 * 禁言，0：正常，1：禁言
	 */
	private Integer forbid;

}
