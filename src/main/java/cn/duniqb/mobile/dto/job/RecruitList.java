package cn.duniqb.mobile.dto.job;

import lombok.Data;

import java.util.List;

@Data
public class RecruitList {
    /**
     * 当前页数
     */
    private String page;

    /**
     * 总共记录
     */
    private String total;

    /**
     * 总共页数
     */
    private String totalPage;

    /**
     * 单位需求列表
     */
    private List<Recruit> list;
}
