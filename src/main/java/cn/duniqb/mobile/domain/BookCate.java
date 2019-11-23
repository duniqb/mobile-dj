package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "book_cate")
public class BookCate implements Serializable {
    /**
     * id
     */
    @Column(name = "id")
    private Integer id;

    /**
     * 类别
     */
    @Column(name = "cate")
    private String cate;

    /**
     * 参数/代码
     */
    @Column(name = "param")
    private String param;

    private static final long serialVersionUID = 1L;
}