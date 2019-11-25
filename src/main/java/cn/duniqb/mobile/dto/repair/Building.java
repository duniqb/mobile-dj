package cn.duniqb.mobile.dto.repair;

import lombok.Data;

@Data
public class Building {
    private String buildingId;
    private String buildingName;
    private String buildingShortName;
    private String distinctId;
    private String hiddenBuildingNumber;
    private String buildingTemplateId;
    private String leaderId;
    private String distinctName;
    private Integer sort;
    private Integer deleteFlag;
    private String hiddenBuildingName;
    private String hiddenDistinctId;
    private Integer isRoomFlag;
    private String distinctNumber;
    private String buildingNumber;
}
