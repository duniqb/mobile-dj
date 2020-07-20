package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学籍信息表
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_student")
public class StudentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 学号/用户名
	 */
	@TableId
	private String stuNo;
	/**
	 * 真实姓名
	 */
	private String name;
	/**
	 * 照片
	 */
	private String img;
	/**
	 * 所在院系
	 */
	private String college;
	/**
	 * 专业
	 */
	private String major;
	/**
	 * 方向
	 */
	private String direction;
	/**
	 * 学生类别
	 */
	private String studentType;
	/**
	 * 年级
	 */
	private String grade;
	/**
	 * 班级
	 */
	private String clazz;
	/**
	 * 证件类型
	 */
	private String certificateType;
	/**
	 * 证件号码
	 */
	private String certificate;
	/**
	 * 电子邮箱
	 */
	private String email;
	/**
	 * 联系电话
	 */
	private String phone;
	/**
	 * 通讯地址
	 */
	private String address;

}
