package cn.duniqb.mobile.domain;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "tip")
public class Tip implements Serializable {
    /**
     * 唯一 id
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     * 上次更新时间
     */
    @Column(name = "`time`")
    private Integer time;

    /**
     * 温度
     */
    @Column(name = "`degree`")
    private String degree;

    /**
     * 天气
     */
    @Column(name = "weather")
    private String weather;

    /**
     * 风寒
     */
    @Column(name = "chill")
    private String chill;

    /**
     * 化妆
     */
    @Column(name = "makeup")
    private String makeup;

    /**
     * 提示1
     */
    @Column(name = "tip1")
    private String tip1;

    /**
     * 提示2
     */
    @Column(name = "tip2")
    private String tip2;

    private static final long serialVersionUID = 1L;
}