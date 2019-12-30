package cn.duniqb.mobile.dto.feed;

import lombok.Data;

import java.util.Date;

/**
 * 评论的回复
 *
 * @author duniqb
 * @date 2019/12/28 13:24
 */
@Data
public class Reply {
    private String replyId;

    private String fromOpenId;

    private String toOpenId;

    private Date time;

    private Integer like;

    private Integer state;

    private String content;
}
