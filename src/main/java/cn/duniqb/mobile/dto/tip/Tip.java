package cn.duniqb.mobile.dto.tip;

import lombok.Data;

import java.io.Serializable;

@Data
public class Tip implements Serializable {
    /**
     * 温度
     */
    private String degree;

    /**
     * 天气
     */
    private String weather;

    /**
     * 风寒
     */
    private String chill;

    /**
     * 化妆
     */
    private String makeup;

    /**
     * 感冒
     */
    private String clod;

    /**
     * 提示1
     */
    private String tip1;

    /**
     * 提示2
     */
    private String tip2;

    /**
     * 提示3
     */
    private String tip3;
}