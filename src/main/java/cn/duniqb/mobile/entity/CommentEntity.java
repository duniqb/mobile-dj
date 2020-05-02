package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 对文章的评论表
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_comment")
public class CommentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 评论id
	 */
	@TableId
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

}
