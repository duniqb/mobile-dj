package cn.duniqb.mobile.dto.json;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回 JSON 结果
 */
@Data
public class JSONResult implements Serializable {
    private Object data;
    private Meta meta;

    public JSONResult() {
    }

    private JSONResult(Object data, String msg, Integer status) {
        this.data = data;
        this.meta = new Meta(msg, status);
    }

    public static JSONResult build(Object data, String msg, Integer status) {
        return new JSONResult(data, msg, status);
    }
}
