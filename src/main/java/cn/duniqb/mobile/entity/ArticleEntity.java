package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Data
@TableName("dj_article")
public class ArticleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文章id
     */
    @TableId
    private Integer id;
    /**
     * 发布者
     */
    private String openId;
    /**
     * 标题
     */
    private String title;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 发表时间
     */
    private Date time;
    /**
     * 显示状态，0：正常，1：删除
     */
    private Integer status;
    /**
     * 类型，0：信息流，1：失物招领
     */
    private Integer type;

}
