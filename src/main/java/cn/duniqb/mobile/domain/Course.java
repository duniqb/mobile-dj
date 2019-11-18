package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "course")
public class Course implements Serializable {
    /**
     * 课程号
     */
    @Id
    @Column(name = "course_id")
    private String courseId;

    /**
     * 课程序号
     */
    @Column(name = "course_serial_no")
    private String courseSerialNo;

    /**
     * 课程名
     */
    @Column(name = "course_name")
    private String courseName;

    /**
     * 学分
     */
    @Column(name = "credit")
    private Double credit;

    /**
     * 上课时间/地点
     */
    @Column(name = "time_date")
    private String timeDate;

    private static final long serialVersionUID = 1L;
}