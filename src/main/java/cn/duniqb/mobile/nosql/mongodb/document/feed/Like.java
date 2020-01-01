package cn.duniqb.mobile.nosql.mongodb.document.feed;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 点赞
 *
 * @author duniqb
 * @date 2019/12/31 11:27
 */
@Data
public class Like implements Serializable {
    /**
     * 点赞人
     */
    private String openid;

    /**
     * 点赞时间
     */
    private Date time;
}
