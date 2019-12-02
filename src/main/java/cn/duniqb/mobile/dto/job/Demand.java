package cn.duniqb.mobile.dto.job;

import lombok.Data;

import java.util.List;

/**
 * 单位需求
 */
@Data
public class Demand {
    /**
     * 当前序号
     */
    private Integer curNo;

    /**
     * id：链接中的数字
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 来源
     */
    private String from;

    /**
     * 日期 - 列表中的日期
     */
    private String date;

    /**
     * 浏览次数
     */
    private String browser;

    /**
     * 发布日期
     */
    private String releaseDate;

    /**
     * 内容
     */
    private List<String> content;
}
