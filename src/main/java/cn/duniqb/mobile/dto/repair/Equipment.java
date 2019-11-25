package cn.duniqb.mobile.dto.repair;

import lombok.Data;

/**
 * 设备详情
 */
@Data
public class Equipment {
    private String equipmentId;
    private String equipmentName;
    private String equipmentNumber;
    private String hiddenEquipmentNumber;
    private Integer sort;
    private String repairGroupId;
    private String repairGroupName;
    private String repairGroupNumber;
    private String sets;
    private String setId;
    private String buildingId;
    private String buildingName;
    private String roomId;
    private String roomName;
    private String distinctId;
    private String distinctName;
    private String equipmentNames;
    private String newEquipmentName;
    private Integer deleteFlag;
    private String roomIds;
    private String equipmentIds;
    private String comment;
    private String materiels;
    private Integer bigFlag;
    private String providerId;
    private String contactId;
    private String providerName;
    private String contactName;
    private String contactTel;
    private String providerAddr;
    private String providerCompany;

}
