package cn.duniqb.mobile.dto.repair;

import lombok.Data;

@Data
public class Room {
    private String roomId;
    private String roomName;
    private String buildingId;
    private String setId;
    private String roomNumber;
    private String hiddenRoomNumber;
    private String distinctId;
    private String distinctName;
    private String buildingName;
    private String setName;
    private String roomTemplateId;
    private Integer isRoomFlag;
    private Integer sort;
    private Integer deleteFlag;
}
