package cn.duniqb.mobile.dto.repair;

import lombok.Data;

/**
 * 报修返回的数据
 */
@Data
public class Report {
    /**
     * 报修单号
     */
    private String id;

    /**
     * 报修时间
     */
    private String time;

    /**
     * 您的手机
     */
    private String phone;

    /**
     * 您所在校区的待办报修单数
     */
    private String pending;

    /**
     * 您所在校区的在办报修单数
     */
    private String repairing;
}
