package cn.duniqb.mobile.dto.repair;

import lombok.Data;

/**
 * 最近 30 天维修数量
 */
@Data
public class Recent {
    /**
     * 区域
     */
    private String area;

    /**
     * 已报修
     */
    private String reported;

    /**
     * 已维修
     */
    private String repaired;

    /**
     * 待维修
     */
    private String pending;
}
