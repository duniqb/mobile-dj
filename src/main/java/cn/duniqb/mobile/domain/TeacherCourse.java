package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "teacher_course")
public class TeacherCourse implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 教师姓名
     */
    @Column(name = "teacher_name")
    private String teacherName;

    /**
     * 课程号
     */
    @Column(name = "course_id")
    private String courseId;

    private static final long serialVersionUID = 1L;
}