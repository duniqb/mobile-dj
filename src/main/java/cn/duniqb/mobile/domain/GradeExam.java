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
     * 考试日期
     */
    @Column(name = "exam_date")
    private String examDate;

    /**
     * 考试时间
     */
    @Column(name = "exam_time")
    private String examTime;

    /**
     * 准考证号
     */
    @Column(name = "ticket_number")
    private String ticketNumber;

    /**
     * 成绩
     */
    @Column(name = "score")
    private String score;

    /**
     * 是否已批准考试
     */
    @Column(name = "approved")
    private String approved;

    /**
     * 等级
     */
    @Column(name = "grade")
    private Integer grade;

    private static final long serialVersionUID = 1L;
}