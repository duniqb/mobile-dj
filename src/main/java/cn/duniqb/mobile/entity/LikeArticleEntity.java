package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文章点赞表
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_like_article")
public class LikeArticleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 点赞id
	 */
	@TableId
	private Integer id;
	/**
	 * 被点赞文章
	 */
	private Integer articleId;
	/**
	 * 点赞时间
	 */
	private Date time;
	/**
	 * 点赞人
	 */
	private String openId;

}
