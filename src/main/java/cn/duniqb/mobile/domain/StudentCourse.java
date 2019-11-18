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
     * 教师
     */
    @Column(name = "teacher_id")
    private String teacherId;

    /**
     * 课程号
     */
    @Column(name = "course_id")
    private String courseId;

    private static final long serialVersionUID = 1L;
}