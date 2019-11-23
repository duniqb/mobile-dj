package cn.duniqb.mobile.dto;

import lombok.Data;

/**
 * 校园卡管理-基本信息
 */
@Data
public class CardInfo {
    /**
     * 姓名
     */
    private String name;

    /**
     * 学号
     */
    private String stuNo;

    /**
     * 校园卡号
     */
    private String id;

    /**
     * 校园卡余额
     */
    private String balance;

    /**
     * 过渡余额
     */
    private String transition;

    /**
     * 挂失状态
     */
    private String lossState;

    /**
     * 冻结状态
     */
    private String frozen;
}
