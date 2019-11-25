package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.repair.Buildings;
import cn.duniqb.mobile.dto.repair.Detail;
import cn.duniqb.mobile.dto.repair.Equipments;
import cn.duniqb.mobile.dto.repair.Rooms;
import cn.duniqb.mobile.utils.LogisticsService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

/**
 * 与后勤相关的接口
 *
 * @author duniqb
 */
@Api(value = "与后勤相关的接口", tags = {"与后勤相关的接口"})
@RestController
@RequestMapping("/api/v1/logistics/")
public class LogisticsController {
    @Autowired
    private LogisticsService logisticsService;

    /**
     * 故障报修 查询各项数据清单
     */
    @GetMapping("detail")
    @ApiOperation(value = "查询各项数据清单", notes = "查询各项数据清单的接口，请求参数是 id，value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "value", value = "id 的值", required = true, dataType = "String", paramType = "query")
    })
    public JSONResult detail(@RequestParam String id, @RequestParam String value) {
        String string = logisticsService.detail(id, value);

        if ("distinctId".equals(id)) {
            Buildings buildings = JSON.parseObject(string, Buildings.class);
            if (!buildings.getBuildings().isEmpty()) {
                return JSONResult.build(buildings, "查询数据成功", 200);
            }
        }
        else if ("buildingId".equals(id)) {
            Rooms rooms = JSON.parseObject(string, Rooms.class);
            if (!rooms.getRooms().isEmpty()) {
                return JSONResult.build(rooms, "查询数据成功", 200);
            }
        }
        else if ("roomId".equals(id)) {
            Equipments equipments = JSON.parseObject(string, Equipments.class);
            if (!equipments.getEquipments().isEmpty()) {
                return JSONResult.build(equipments, "查询数据成功", 200);
            }
        }
        else if ("equipmentId".equals(id)) {
            Detail detail = JSON.parseObject(string, Detail.class);
            if (detail != null) {
                return JSONResult.build(detail, "查询数据成功", 200);
            }
        }
        return JSONResult.build(null, "查询数据失败", 400);
    }
}
