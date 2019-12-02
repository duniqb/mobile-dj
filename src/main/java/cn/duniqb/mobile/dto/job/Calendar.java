package cn.duniqb.mobile.dto.job;

import lombok.Data;

/**
 * 招聘日历
 */
@Data
public class Calendar {
    private Integer id;
    private String date;
    private Integer day;
    private String title;
    private Boolean badge;
    private Integer num;
}
