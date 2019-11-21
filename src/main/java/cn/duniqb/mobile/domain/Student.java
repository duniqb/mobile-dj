package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "student")
public class Student implements Serializable {
    /**
     * 学号/用户名
     */
    @Id
    @Column(name = "stu_no")
    private String stuNo;

    /**
     * 真实姓名
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 密码
     */
    @Column(name = "`password`")
    private String password;

    /**
     * 密码混淆
     */
    @Column(name = "salt")
    private String salt;

    /**
     * 照片
     */
    @Column(name = "img")
    private String img;

    /**
     * 所在院系
     */
    @Column(name = "college")
    private String college;

    /**
     * 专业
     */
    @Column(name = "major")
    private String major;

    /**
     * 方向
     */
    @Column(name = "direction")
    private String direction;

    /**
     * 学生类别
     */
    @Column(name = "student_type")
    private String studentType;

    /**
     * 年级
     */
    @Column(name = "grade")
    private String grade;

    /**
     * 班级
     */
    @Column(name = "clazz")
    private String clazz;

    /**
     * 证件类型
     */
    @Column(name = "certificate_type")
    private String certificateType;

    /**
     * 证件号码
     */
    @Column(name = "certificate")
    private String certificate;

    /**
     * 电子邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 联系电话
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 通讯地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 邮政编码
     */
    @Column(name = "zip_code")
    private String zipCode;

    private static final long serialVersionUID = 1L;
}