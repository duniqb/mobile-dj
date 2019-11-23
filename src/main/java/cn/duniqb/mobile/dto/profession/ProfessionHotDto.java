package cn.duniqb.mobile.dto.profession;

import lombok.Data;

import java.util.List;

/**
 * 专业热点 DTO
 *
 * @author duniqb
 */
@Data
public class ProfessionHotDto {
    /**
     * 专业名/课程名
     */
    private String title;

    List<Item> list;
}
