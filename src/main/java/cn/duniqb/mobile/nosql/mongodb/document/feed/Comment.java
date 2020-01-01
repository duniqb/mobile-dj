package cn.duniqb.mobile.nosql.mongodb.document.feed;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论
 *
 * @author duniqb
 * @date 2019/12/30 22:24
 */
@Data
public class Comment implements Serializable {
    /**
     * 当前评论的唯一标识
     */
    private String id;

    /**
     * 用户名
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 评论的用户的 openid
     */
    private String openid;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 评论时间
     */
    private Date date;

    /**
     * 默认 false.不是本人
     */
    private boolean flag = false;

    /**
     * 点赞
     */
    private List<Like> likeList;

    /**
     * 状态 0:正常，1：删除
     */
    private Integer state;

    /**
     * 分组的标记
     */
    private String group;
}
