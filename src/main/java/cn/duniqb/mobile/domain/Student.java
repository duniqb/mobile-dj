package cn.duniqb.mobile.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "student")
public class Student implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 学号
     */
    @Column(name = "stu_no")
    private String stuNo;

    /**
     * 姓名
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 性别  0-男、1-女
     */
    @Column(name = "gender")
    private Boolean gender;

    /**
     * 出生日期
     */
    @Column(name = "birthday")
    private Date birthday;

    /**
     * 照片
     */
    @Column(name = "img")
    private String img;

    /**
     * 籍贯
     */
    @Column(name = "hometown")
    private String hometown;

    /**
     * 证件号码
     */
    @Column(name = "certificate")
    private String certificate;

    /**
     * 民族
     */
    @Column(name = "nation")
    private String nation;

    /**
     * 外语语种
     */
    @Column(name = "foreign_languages")
    private String foreignLanguages;

    /**
     * 学生来源
     */
    @Column(name = "student_source")
    private String studentSource;

    /**
     * 考区
     */
    @Column(name = "exam_area")
    private String examArea;

    /**
     * 考生号
     */
    @Column(name = "exam_stu_no")
    private String examStuNo;

    /**
     * 高考总分
     */
    @Column(name = "cee_score")
    private Integer ceeScore;

    /**
     * 入学准考证号
     */
    @Column(name = "exam_no")
    private String examNo;

    /**
     * 入学录取证号
     */
    @Column(name = "offer_no")
    private String offerNo;

    /**
     * 中学名
     */
    @Column(name = "middle_school")
    private String middleSchool;

    /**
     * 入学日期
     */
    @Column(name = "enrollment_date")
    private Date enrollmentDate;

    /**
     * 入学方式
     */
    @Column(name = "enrollment_type")
    private String enrollmentType;

    /**
     * 培养方式
     */
    @Column(name = "train_mode")
    private String trainMode;

    /**
     * 院系
     */
    @Column(name = "college")
    private String college;

    /**
     * 专业
     */
    @Column(name = "major")
    private String major;

    /**
     * 年级
     */
    @Column(name = "grade")
    private String grade;

    /**
     * 校区
     */
    @Column(name = "campus_area")
    private String campusArea;

    /**
     * 是否有学籍
     */
    @Column(name = "student_status")
    private String studentStatus;

    /**
     * 学习形式
     */
    @Column(name = "learn_type")
    private String learnType;

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

    private static final long serialVersionUID = 1L;
}