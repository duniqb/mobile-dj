package cn.duniqb.mobile.dto.seek;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/6/24 11:38
 * @since 1.8
 */
@Data
public class Seek {

    /**
     * 失物招领id
     */
    private Integer id;
    /**
     * 发布人
     */
    private String openid;

    /**
     * 发布者姓名
     */
    private String author;

    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 该文章可能带有的图片
     */
    private List<String> imgUrlList;
    /**
     * 发布时间
     */
    private LocalDateTime time;
    /**
     * 显示状态，0：正常，1：删除
     */
    private Integer status;
    /**
     * 地点
     */
    private String place;
    /**
     * 类型：0：寻物，1：招领
     */
    private Integer type;
    /**
     * 联系方式
     */
    private String contact;
    /**
     * 日期
     */
    private LocalDate date;
}
