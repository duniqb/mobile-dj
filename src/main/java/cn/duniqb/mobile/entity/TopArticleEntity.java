package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 置顶文章
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_top_article")
public class TopArticleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 被置顶文章id
	 */
	@TableId
	private Integer articleId;
	/**
	 * 被置顶时间
	 */
	private Date createTime;
	/**
	 * 结束时间
	 */
	private Date endTime;

}
