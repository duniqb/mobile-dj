package cn.duniqb.mobile.dto.feed;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 对文章的评论表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
public class Comment implements Serializable {
    /**
     * 评论id
     */
    private Integer commentId;
    /**
     * 被评论文章id
     */
    private Integer articleId;
    /**
     * 评论人
     */
    private String openId;
    /**
     * 评论人
     */
    private String name;

    /**
     * 头像链接
     */
    private String avatar;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 状态，0：显示，1：隐藏
     */
    private Integer status;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 评论时间
     */
    private Date time;

    /**
     * 此评论的回复列表
     */
    private List<CommentReply> replyList;

}
