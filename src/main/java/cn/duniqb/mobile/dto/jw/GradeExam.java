package cn.duniqb.mobile.dto.jw;

import lombok.Data;

/**
 * 等级考试
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/5/1 20:26
 * @since 1.0
 */
@Data
public class GradeExam {
    /**
     * 学号
     */
    private String stuNo;
    /**
     * 考试名称
     */
    private String examName;
    /**
     * 考试日期
     */
    private String examDate;
    /**
     * 考试时间
     */
    private String examTime;
    /**
     * 准考证号
     */
    private String ticketNumber;
    /**
     * 成绩
     */
    private String score;
    /**
     * 是否已批准考试
     */
    private String approved;
    /**
     * 等级
     */
    private String grade;
}
