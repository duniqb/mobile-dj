package cn.duniqb.mobile.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * 失物招领
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 10:21:25
 */
@Data
@TableName("dj_seek")
public class SeekEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 失物招领id
     */
    @TableId
    private Integer id;
    /**
     * 发布人
     */
    private String openid;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
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
