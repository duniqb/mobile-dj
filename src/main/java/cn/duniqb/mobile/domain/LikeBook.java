package cn.duniqb.mobile.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;

import lombok.Data;

@Data
@Table(name = "like_book")
public class LikeBook implements Serializable {
    /**
     * openid
     */
    @Column(name = "openid")
    private String openid;

    /**
     * 图书
     */
    @Column(name = "book_id")
    private String bookId;

    /**
     * 收藏时间
     */
    @Column(name = "`time`")
    private LocalDateTime time;

    /**
     * 图书名
     */
    @Column(name = "book_name")
    private String bookName;

    /**
     * 作者
     */
    @Column(name = "author")
    private String author;

    private static final long serialVersionUID = 1L;
}