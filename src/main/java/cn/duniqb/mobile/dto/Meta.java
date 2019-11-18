package cn.duniqb.mobile.dto;

import lombok.Data;

@Data
public class Meta {
    private String msg;
    private Integer status;

    public Meta() {
    }

    public Meta(String msg, Integer status) {
        this.msg = msg;
        this.status = status;
    }
}