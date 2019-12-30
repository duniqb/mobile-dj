package cn.duniqb.mobile.dto.feed;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 评论详情
 *
 * @author duniqb
 * @date 2019/12/28 13:21
 */
@Data
public class Comment {
    private String commentId;

    private String openid;

    private Date time;

    private Integer like;

    private Integer state;

    private String content;

    private List<Reply> replyList;
}
