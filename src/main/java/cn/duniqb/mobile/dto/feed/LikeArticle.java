package cn.duniqb.mobile.dto.feed;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章点赞表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
public class LikeArticle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 点赞id
     */
    private Integer id;
    /**
     * 被点赞文章
     */
    private Integer articleId;
    /**
     * 点赞人
     */
    private String openId;

    /**
     * 点赞人昵称
     */
    private String name;

    /**
     * 头像链接
     */
    private String avatar;

    /**
     * 点赞时间
     */
    private Date time;
}
