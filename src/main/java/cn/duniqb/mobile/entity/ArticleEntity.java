package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 文章表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 09:28:06
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
     * 发布地点
     */
    private String address;
}
