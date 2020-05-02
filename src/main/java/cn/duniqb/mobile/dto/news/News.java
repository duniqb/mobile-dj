package cn.duniqb.mobile.dto.news;

import lombok.Data;

import java.util.List;

/**
 * 新闻详情
 */
@Data
public class News {
    /**
     * 当前序号
     */
    private Integer curNo;

    /**
     * 唯一 id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 日期：从列表中获取的
     */
    private String date;

    /**
     * 新闻类型
     */
    private String type;

    /**
     * 发布时间：从详情中获取的
     */
    private String time;

    /**
     * 来源
     */
    private String from;

    /**
     * 浏览数
     */
    private String browse;

    /**
     * 内容
     */
    private List<String> content;

    /**
     * 图片地址
     */
    private List<String> image;
}
