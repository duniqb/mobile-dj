package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 举报文章/评论
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_report")
public class ReportEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Integer id;
	/**
	 * 举报类型，0：文章，1：评论
	 */
	private Integer type;
	/**
	 * 举报内容的id
	 */
	private Integer reportId;
	/**
	 * 举报时间
	 */
	private Date time;

}
