package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "credit")
public class Credit implements Serializable {
    /**
     * 学号/用户名
     */
    @Id
    @Column(name = "stu_no")
    private String stuNo;

    /**
     * 专业
     */
    @Column(name = "major")
    private String major;

    /**
     * 教学计划学分要求
     */
    @Column(name = "`credit requirements`")
    private Double creditRequirements;

    /**
     * 已获必修学分
     */
    @Column(name = "required_credits")
    private Double requiredCredits;

    /**
     * 已获选修学分
     */
    @Column(name = "elective_credits")
    private Double electiveCredits;

    /**
     * 已获任选学分
     */
    @Column(name = "optional_credits")
    private Double optionalCredits;

    private static final long serialVersionUID = 1L;
}