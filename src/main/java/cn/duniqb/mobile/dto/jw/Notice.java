package cn.duniqb.mobile.dto.jw;

import lombok.Data;

import java.util.List;

/**
 * 教务通告
 */
@Data
public class Notice {
    /**
     * 文章 id
     */
    private String id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 发布日期，从列表中获取的
     */
    private String releaseDate;

    /**
     * 正文
     */
    List<String> content;
}
