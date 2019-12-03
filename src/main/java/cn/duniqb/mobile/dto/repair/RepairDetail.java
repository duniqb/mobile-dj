package cn.duniqb.mobile.dto.repair;

import lombok.Data;

import java.util.List;

/**
 * 报修详情
 */
@Data
public class RepairDetail {
    /**
     * 报修手机
     */
    private String phone;
    /**
     * 报修时间
     */
    private String date;
    /**
     * 标题
     */
    private String title;
    /**
     * 报修单号
     */
    private String id;
    /**
     * 提交状态
     */
    private String state;
    /**
     * 序列号
     */
    private String listNumber;
    /**
     * 房间号
     */
    private String room;
    /**
     * 故障描述
     */
    private String description;
    /**
     * 是否已评价
     */
    private Boolean showEvaluate;

    /**
     * 时间线
     */
    private List<TimeLine> timeLineList;
}
