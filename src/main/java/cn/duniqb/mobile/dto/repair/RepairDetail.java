package cn.duniqb.mobile.dto.repair;

import lombok.Data;

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
     * 链接
     */
    private String url;
    /**
     * 房间号
     */
    private String room;
    /**
     * 故障描述
     */
    private String description;
}
