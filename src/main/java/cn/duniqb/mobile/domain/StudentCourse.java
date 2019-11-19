package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "student_course")
public class StudentCourse implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 学号
     */
    @Column(name = "stu_no")
    private String stuNo;

    /**
     * 课程号
     */
    @Column(name = "course_id")
    private String courseId;

    /**
     * 选课属性
     */
    @Column(name = "course_attr")
    private String courseAttr;

    /**
     * 上课时间/地点
     */
    @Column(name = "time_date")
    private String timeDate;

    /**
     * 学年
     */
    @Column(name = "`year`")
    private Integer year;

    /**
     * 学期  0-春、1-秋
     */
    @Column(name = "term")
    private Boolean term;

    private static final long serialVersionUID = 1L;
}