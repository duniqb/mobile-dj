package cn.duniqb.mobile.dto.news;

import lombok.Data;

import java.util.List;

/**
 * 新闻列表
 */
@Data
public class NewsList {
    /**
     * 新闻类型
     */
    private String type;

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
     * 新闻列表
     */
    private List<News> list;
}
