package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "teacher")
public class Teacher implements Serializable {
    /**
     * 教师
     */
    @Id
    @Column(name = "teacher_id")
    private String teacherId;

    /**
     * 教师姓名
     */
    @Column(name = "teacher_name")
    private String teacherName;

    private static final long serialVersionUID = 1L;
}