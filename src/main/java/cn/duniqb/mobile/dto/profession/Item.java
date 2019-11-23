package cn.duniqb.mobile.dto.profession;

import lombok.Data;

/**
 * @author duniqb
 */
@Data
public class Item {
    /**
     * 当前序号
     */
    private Integer curNo;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 后半部分关键词
     */
    private String keyWord;
}
