package cn.duniqb.mobile.dto.job;

import lombok.Data;

import java.util.List;

/**
 * 招聘会信息
 */
@Data
public class Recruit {
    /**
     * 当前序号
     */
    private Integer curNo;

    /**
     * id：链接中的数字
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 来源
     */
    private String from;

    /**
     * 浏览次数
     */
    private String browser;

    /**
     * 发布日期
     */
    private String releaseDate;

    /**
     * 企业信息 - 企业名称
     */
    private String companyName;

    /**
     * 企业信息 - 单位性质
     */
    private String companyProperties;

    /**
     * 企业信息 - 主管部门
     */
    private String competentDepartment;

    /**
     * 企业信息 - 单位地区
     */
    private String companyRegion;

    /**
     * 企业信息 - 详细地址
     */
    private String address;

    /**
     * 邮政编码
     */
    private String zipCode;

    /**
     * 招聘信息 - 地点
     */
    private String place;

    /**
     * 招聘信息 - 日期
     */
    private String date;

    /**
     * 招聘信息 - 时间
     */
    private String time;

    /**
     * 内容
     */
    private List<String> content;
}
