package cn.duniqb.mobile.dto;

import lombok.Data;

import java.util.List;

/**
 * 图书 DTO
 *
 * @author duniqb
 */
@Data
public class Book {
    /**
     * 当页序号
     */
    private String curNo;
    /**
     * 图书 id
     */
    private String id;

    /**
     * 中/西文图书
     */
    private String type;

    /**
     * ISBN号
     */
    private String ISBN;

    /**
     * CALIS号
     */
    private String CALIS;

    /**
     * 题名
     */
    private String bookName;

    /**
     * 责任者
     */
    private String author;

    /**
     * 出版发行项
     */
    private String publisher;

    /**
     * 提要文摘
     */
    private String summary;

    /**
     * 复本情况
     */
    private List<String> status;

    /**
     * 索书号
     */
    private String index;

    /**
     * 热度
     */
    private String hot;
}
