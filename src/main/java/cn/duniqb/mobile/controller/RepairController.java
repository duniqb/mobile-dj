package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.repair.*;
import cn.duniqb.mobile.spider.RepairSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 与后勤相关的接口
 *
 * @author duniqb
 */
@Api(value = "与后勤相关的接口", tags = {"与后勤相关的接口"})
@RestController
@RequestMapping("/api/v2/repair/")
public class RepairController {
    @Autowired
    private RepairSpiderService repairSpiderService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 通知查询在 Redis 里的前缀
     */
    private static final String LOGISTICS_NOTICE = "LOGISTICS_NOTICE";

    /**
     * 最新维修在 Redis 里的前缀
     */
    private static final String LOGISTICS_RECENT = "LOGISTICS_RECENT";

    /**
     * 故障报修数据在 Redis 里的前缀
     */
    private static final String LOGISTICS_DATA = "LOGISTICS_DATA";

    /**
     * 故障报修 查询各项数据清单
     */
    @GetMapping("data")
    @ApiOperation(value = "查询各项数据清单", notes = "查询各项数据清单的接口，请求参数是 id，value")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "value", value = "id 的值", required = true, dataType = "String", paramType = "query")
    })
    public R data(@RequestParam String id, @RequestParam String value) {
        String res = redisUtil.get(LOGISTICS_DATA + ":" + id + ":" + value);
        if (res != null) {
            if ("distinctId".equals(id)) {
                Buildings buildings = JSON.parseObject(res, Buildings.class);
                if (!buildings.getBuildings().isEmpty()) {
                    return R.ok().put("建筑物数据清单 - 缓存获取成功", buildings);
                }
            } else if ("buildingId".equals(id)) {
                Rooms rooms = JSON.parseObject(res, Rooms.class);
                if (!rooms.getRooms().isEmpty()) {
                    return R.ok().put("房间号数据清单 - 缓存获取成功", rooms);
                }
            } else if ("roomId".equals(id)) {
                Equipments equipments = JSON.parseObject(res, Equipments.class);
                if (!equipments.getEquipments().isEmpty()) {
                    return R.ok().put("设备号数据清单 - 缓存获取成功", equipments);
                }
            } else if ("equipmentId".equals(id)) {
                Detail detail = JSON.parseObject(res, Detail.class);
                if (detail != null) {
                    return R.ok().put("设备详情数据清单 - 缓存获取成功", detail);
                }
            }
        }
        String string = repairSpiderService.data(id, value);

        String replace = string.replace("\\", "");
        redisUtil.set(LOGISTICS_DATA + ":" + id + ":" + value, replace.substring(1, replace.length() - 1), 60 * 60 * 24 * 3);
        if ("distinctId".equals(id)) {
            Buildings buildings = JSON.parseObject(replace.substring(1, replace.length() - 1), Buildings.class);
            if (!buildings.getBuildings().isEmpty()) {
                return R.ok().put("查询建筑物数据成功", buildings);
            }
        } else if ("buildingId".equals(id)) {
            Rooms rooms = JSON.parseObject(replace.substring(1, replace.length() - 1), Rooms.class);
            if (!rooms.getRooms().isEmpty()) {
                return R.ok().put("查询房间号数据成功", rooms);
            }
        } else if ("roomId".equals(id)) {
            Equipments equipments = JSON.parseObject(replace.substring(1, replace.length() - 1), Equipments.class);
            if (!equipments.getEquipments().isEmpty()) {
                return R.ok().put("查询设备号数据成功", equipments);
            }
        } else if ("equipmentId".equals(id)) {
            Detail detail = JSON.parseObject(replace.substring(1, replace.length() - 1), Detail.class);
            if (detail != null) {
                return R.ok().put("查询设备详情数据成功", detail);
            }
        }
        return R.ok().put("查询数据失败", null);
    }

    /**
     * 根据报修手机号查询报修列表
     */
    @GetMapping("list")
    @ApiOperation(value = "根据报修手机号查询报修列表", notes = "根据报修手机号查询报修列表的接口，请求参数是 phone")
    @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String", paramType = "query")
    public R list(@RequestParam String phone) {
        List<RepairDetail> list = repairSpiderService.list(phone);
        if (!list.isEmpty()) {
            return R.ok().put("查询报修列表成功", list);
        }
        return R.ok().put("查询报修列表失败", null);
    }

    /**
     * 报修单详情
     */
    @GetMapping("detail")
    @ApiOperation(value = "报修单详情", notes = "报修单详情的接口，请求参数是 listNumber")
    @ApiImplicitParam(name = "listNumber", value = "序列号", required = true, dataType = "String", paramType = "query")
    public R detail(@RequestParam String listNumber) {
        RepairDetail repairDetail = repairSpiderService.detail(listNumber);
        if (repairDetail != null) {
            return R.ok().put("查询报修单详情成功", repairDetail);
        }
        return R.ok().put("查询报修单详情失败", null);
    }

    /**
     * 最新通知
     */
    @GetMapping("notice")
    @ApiOperation(value = "最新通知", notes = "最新通知的接口")
    public R notice() {
        String res = redisUtil.get(LOGISTICS_NOTICE);
        if (res != null) {
            return R.ok().put("最新通知 - 缓存获取成功", JSON.parseObject(res, Notice.class));
        }
        Notice notice = repairSpiderService.notice();
        if (notice != null) {
            redisUtil.set(LOGISTICS_NOTICE, JSON.toJSONString(notice), 60 * 30);
            return R.ok().put("查询最新通知成功", notice);
        }
        return R.ok().put("查询最新通知失败", null);
    }

    /**
     * 最近维修数量
     */
    @GetMapping("recent")
    @ApiOperation(value = "最近维修数量", notes = "最近维修数量的接口")
    public R recent() {
        String res = redisUtil.get(LOGISTICS_RECENT);
        if (res != null) {
            return R.ok().put("最近维修数量 - 缓存获取成功", JSON.parseArray(res, Recent.class));
        }
        List<Recent> recentList = repairSpiderService.recent();
        if (!recentList.isEmpty()) {
            redisUtil.set(LOGISTICS_RECENT, JSON.toJSONString(recentList), 60 * 60 * 24);
            return R.ok().put("查询最近维修数量成功", recentList);
        }
        return R.ok().put("查询最近维修数量失败", null);
    }

    /**
     * 发起报修
     */
    @GetMapping("report")
    @ApiOperation(value = "发起报修", notes = "发起报修的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "报修电话", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "distinctId", value = "校区", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "buildingId", value = "建筑物", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roomId", value = "房间号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "equipmentId", value = "设备", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "place", value = "房间/位置", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "描述信息", required = true, dataType = "String", paramType = "query"),
    })
    public R report(String phone, String distinctId, String buildingId, String roomId, String equipmentId, String place, String description) {
        String listDescription = "房间号 " + place + " " + description;
        Report report = repairSpiderService.report(phone, distinctId, buildingId, roomId, equipmentId, listDescription);
        if (report != null) {
            return R.ok().put("发起报修成功", report);
        }
        return R.ok().put("发起报修失败", null);
    }

    /**
     * 维修评价
     */
    @GetMapping("evaluate")
    @ApiOperation(value = "维修评价", notes = "维修评价的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "listNumber", value = "序列号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "报修电话", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "listScore", value = "打分 1-5", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "listWord", value = "评语", required = true, dataType = "String", paramType = "query")
    })
    public R evaluate(String listNumber, String phone, String listScore, String listWord) {
        repairSpiderService.evaluate(listNumber, phone, listScore, listWord);
        return R.ok().put("维修评价成功", null);
    }
}
