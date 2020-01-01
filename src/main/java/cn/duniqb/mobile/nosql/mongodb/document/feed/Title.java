package cn.duniqb.mobile.nosql.mongodb.document.feed;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章
 *
 * @author duniqb
 * @date 2019/12/30 22:21
 */
@Data
@Document
public class Title implements Serializable {
    @Id
    private String _id;

    /**
     * 用户名
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 文章发表人的 openid
     */
    private String openid;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片
     */
    private List<String> images;

    /**
     * 创建时间
     */
    private Date date;

    /**
     * 标记是否是本人,默认是非本人
     */
    private boolean flag = false;

    /**
     * 点赞
     */
    private List<Like> likeList;

    /**
     * 状态 0:正常，1：删除，2：禁止评论
     */
    private Integer state;

    /**
     * 问题的回答列表
     */
    private List<Comment> commentList;
}
