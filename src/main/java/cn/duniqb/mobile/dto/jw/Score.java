package cn.duniqb.mobile.dto.jw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class Score implements Serializable {
    private Integer id;

    /**
     * 学号
     */
    private String stuNo;

    /**
     * 课程号
     */
    @JsonIgnore
    private String courseId;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 学分
     */
    private Double credit;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 学年
     */
    private Integer year;

    /**
     * 学期  0-春、1-秋
     */
    private String term;
    /**
     * 平时成绩
     */
    private String usualScore;

    /**
     * 期末成绩
     */
    private String endScore;

    /**
     * 总评
     */
    private String totalScore;

    /**
     * 是否缓考  0-是、1-否
     */
    private Boolean slowExam;

    /**
     * 考试性质
     */
    private String examType;

    /**
     * 备注
     */
    private String comment;

    private static final long serialVersionUID = 1L;
}