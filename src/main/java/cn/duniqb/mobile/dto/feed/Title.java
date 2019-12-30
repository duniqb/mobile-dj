package cn.duniqb.mobile.dto.feed;

import lombok.Data;

import java.util.List;

/**
 * 信息流文章
 *
 * @author duniqb
 * @date 2019/12/28 13:15
 */
@Data
public class Title {
    private String _id;

    private String titleId;

    private String openid;

    private List<Image> imageList;

    private String content;

    private Integer like;

    private Integer state;

    private List<Comment> commentList;
}
