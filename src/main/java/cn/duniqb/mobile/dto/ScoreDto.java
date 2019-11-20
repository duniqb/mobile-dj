package cn.duniqb.mobile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
public class ScoreDto implements Serializable {
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

    private String courseName;

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