package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 收藏图书表
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_like_book")
public class LikeBookEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * openid
	 */
	@TableId
	private String openid;
	/**
	 * 图书id
	 */
	private String bookId;
	/**
	 * 收藏时间
	 */
	private Date time;
	/**
	 * 图书名
	 */
	private String bookName;
	/**
	 * 作者
	 */
	private String author;

}
