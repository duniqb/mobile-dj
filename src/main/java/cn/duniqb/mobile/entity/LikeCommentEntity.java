package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 点赞评论表
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_like_comment")
public class LikeCommentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 点赞id
	 */
	@TableId
	private Integer id;
	/**
	 * 点赞类型，0：评论，1：回复
	 */
	private Integer type;
	/**
	 * 点赞对象id
	 */
	private Integer commentOrReplyId;

	/**
	 * 点赞人
	 */
	private String openId;

}
