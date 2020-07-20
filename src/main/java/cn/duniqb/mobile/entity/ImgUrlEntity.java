package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 图片表，存储在oss
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_img_url")
public class ImgUrlEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Integer id;
	/**
	 * 图片类型，0：新闻图片，1：文章图片，2：失物招领
	 */
	private Integer imgType;
	/**
	 * 图片路径
	 */
	private String url;
	/**
	 * 文章id（新闻，失物招领都算article）
	 */
	private Integer articleId;

}
