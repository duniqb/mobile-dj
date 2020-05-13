package cn.duniqb.mobile.dto.feed;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/4 21:30
 * @since 1.8
 */
@Data
public class Article {
    /**
     * 文章id
     */
    private Integer id;
    /**
     * 发布者openid
     */
    private String openId;

    /**
     * 发布者姓名
     */
    private String author;

    /**
     * 头像
     */
    private String avatar;

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
     * 该文章可能带有的图片
     */
    private List<String> imgUrlList;

    /**
     * 点赞数量
     */
    private Integer likeCount = 0;

    /**
     * 评论数量
     */
    private Integer commentCount = 0;

    /**
     * 当前登录用户是否点赞
     */
    private Boolean isLike = false;

    /**
     * 空白区域的数量，用以点击进入详情页
     */
    private Integer blankImage;

    /**
     * 发布地点
     */
    private String address;

    /**
     * 发表时间戳
     */
    private Long timestamp;
}
