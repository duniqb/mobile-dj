package cn.duniqb.mobile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;

@Data
public class TipDto {
    /**
     * 上次更新时间
     */
    private Integer id;

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
    @JsonIgnore
    private String chill;

    /**
     * 化妆
     */
    @JsonIgnore
    private String makeup;

    /**
     * 提示1
     */
    @JsonIgnore
    private String tip1;

    /**
     * 提示2
     */
    @JsonIgnore
    private String tip2;

    private List<String> tips;
}
