package cn.duniqb.mobile.dto.jw;

import lombok.Data;

/**
 * 学分
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/5/1 20:11
 * @since 1.0
 */
@Data
public class Credit {
    /**
     * 学号
     */
    private String stuNo;
    /**
     * 专业
     */
    private String major;
    /**
     * 教学计划学分要求
     */
    private Double requirements;
    /**
     * 已获必修学分
     */
    private Double requiredCredits;
    /**
     * 已获选修学分
     */
    private Double electiveCredits;
    /**
     * 已获任选学分
     */
    private Double optionalCredits;

}
