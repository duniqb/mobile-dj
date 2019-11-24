package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "img_url")
public class ImgUrl implements Serializable {
    /**
     * id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 文章 id
     */
    @Column(name = "news_id")
    private String newsId;

    /**
     * 图片路径
     */
    @Column(name = "url")
    private String url;

    private static final long serialVersionUID = 1L;
}