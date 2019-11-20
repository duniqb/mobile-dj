package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "grade_exam")
public class GradeExam implements Serializable {
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
     * 考试名称
     */
    @Column(name = "exam_name")
    private String examName;

    /**
     * 考试时间
     */
    @Column(name = "exam_time")
    private String examTime;

    /**
     * 成绩
     */
    @Column(name = "score")
    private String score;

    private static final long serialVersionUID = 1L;
}