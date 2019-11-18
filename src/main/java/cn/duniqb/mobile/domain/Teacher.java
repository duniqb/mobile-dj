package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "teacher")
public class Teacher implements Serializable {
    @Id
    @Column(name = "teacher_id")
    @GeneratedValue(generator = "JDBC")
    private Integer teacherId;

    /**
     * 教师姓名
     */
    @Column(name = "teacher_name")
    private String teacherName;

    private static final long serialVersionUID = 1L;
}