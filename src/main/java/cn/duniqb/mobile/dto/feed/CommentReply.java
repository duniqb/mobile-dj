package cn.duniqb.mobile.dto.feed;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论回复表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
public class CommentReply implements Serializable {
    /**
     * 回复id
     */
    private Integer replyId;
    /**
     * 评论id
     */
    private Integer commentId;
    /**
     * 回复人
     */
    private String openIdFrom;
    /**
     * 被回复人
     */
    private String openIdTo;
    /**
     * 回复人
     */
    private String fromName;
    /**
     * 被回复人
     */
    private String toName;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 回复时间
     */
    private Date time;
    /**
     * 状态，0：显示，1：隐藏
     */
    private Integer status;
}
