package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.job.*;
import cn.duniqb.mobile.spider.JobSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 与就业相关的接口
 *
 * @author duniqb
 */
@Api(tags = {"与就业相关的接口"})
@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobSpiderService jobSpiderService;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 招聘会列表在 Redis 里的前缀
     */
    private static final String RECRUIT_LIST = "RECRUIT_LIST";


    /**
     * 招聘会在 Redis 里的前缀
     */
    private static final String RECRUIT = "RECRUIT";

    /**
     * 单位需求列表在 Redis 里的前缀
     */
    private static final String DEMAND_LIST = "DEMAND_LIST";


    /**
     * 单位需求在 Redis 里的前缀
     */
    private static final String DEMAND = "DEMAND";

    /**
     * 招聘日历在 Redis 里的前缀
     */
    private static final String CALENDAR = "CALENDAR";

    /**
     * 招聘会列表
     *
     * @param page
     * @return
     */
    @GetMapping("/recruitList")
    @ApiOperation(value = "获取招聘会列表", notes = "获取招聘会列表，请求参数是 page")
    public R recruitList(@RequestParam String page) {
        String res = redisUtil.get(RECRUIT_LIST + ":" + page);
        if (res != null) {
            return R.ok("招聘会列表 - 缓存获取成功").put("data", JSON.parseObject(res, RecruitList.class));
        }
        RecruitList recruitList = jobSpiderService.recruitList(page);
        if (recruitList != null) {
            redisUtil.set(RECRUIT_LIST + ":" + page, JSON.toJSONString(recruitList), 60 * 60 * 12);
            return R.ok("招聘会列表 - 获取成功").put("data", recruitList);
        }
        return R.error(400, "招聘会列表 - 获取失败");
    }

    /**
     * 招聘会详情
     *
     * @param id
     * @return
     */
    @GetMapping("/recruit")
    @ApiOperation(value = "获取招聘会详情", notes = "获取招聘会详情，请求参数是 id")
    public R recruit(@RequestParam String id) {
        String res = redisUtil.get(RECRUIT + ":" + id);
        if (res != null) {
            return R.ok("招聘会详情 - 缓存获取成功").put("data", JSON.parseObject(res, Recruit.class));
        }
        Recruit recruit = jobSpiderService.recruit(id);
        if (recruit != null) {
            redisUtil.set(RECRUIT + ":" + id, JSON.toJSONString(recruit), 60 * 60 * 12);
            return R.ok("招聘会详情 - 获取成功").put("data", recruit);
        }
        return R.error(400, "招聘会详情 - 获取失败");
    }

    /**
     * 单位需求列表
     *
     * @param page
     * @return
     */
    @GetMapping("/demandList")
    @ApiOperation(value = "获取单位需求列表", notes = "获取单位需求列表，请求参数是 page")
    public R demandList(@RequestParam String page) {
        String res = redisUtil.get(DEMAND_LIST + ":" + page);
        if (res != null) {
            return R.ok("单位需求列表 - 缓存获取成功").put("data", JSON.parseObject(res, RecruitList.class));
        }
        DemandList demandList = jobSpiderService.demandList(page);
        if (demandList != null) {
            redisUtil.set(DEMAND_LIST + ":" + page, JSON.toJSONString(demandList), 60 * 60 * 12);
            return R.ok("单位需求列表 - 获取成功").put("data", demandList);
        }
        return R.error(400, "单位需求列表 - 获取失败");
    }

    /**
     * 单位需求详情
     *
     * @param id
     * @return
     */
    @GetMapping("/demand")
    @ApiOperation(value = "获取单位需求详情", notes = "获取单位需求详情，请求参数是 id")
    public R demand(@RequestParam String id) {
        String res = redisUtil.get(DEMAND + ":" + id);
        if (res != null) {
            return R.ok("单位需求详情 - 缓存获取成功").put("data", JSON.parseObject(res, Demand.class));
        }
        Demand demand = jobSpiderService.demand(id);
        if (demand != null) {
            redisUtil.set(DEMAND + ":" + id, JSON.toJSONString(demand), 60 * 60 * 12);
            return R.ok("单位需求详情 - 获取成功").put("data", demand);
        }
        return R.error(400, "单位需求详情 - 获取失败");
    }

    /**
     * 招聘日历
     *
     * @param year
     * @return
     */
    @GetMapping("/calendar")
    @ApiOperation(value = "获取招聘日历", notes = "获取招聘日历")
    public R calendar(@RequestParam String year, @RequestParam String month) {
        String res = redisUtil.get(CALENDAR + ":" + year + ":" + month);
        if (res != null) {
            return R.ok("招聘日历 - 缓存获取成功").put("data", JSON.parseArray(res, Calendar.class));
        }
        List<Calendar> calendar = jobSpiderService.calendar(year, month);
        if (!calendar.isEmpty()) {
            redisUtil.set(CALENDAR + ":" + year + ":" + month, JSON.toJSONString(calendar), 60 * 60 * 12);
            return R.ok("招聘日历 - 获取成功").put("data", calendar);
        }
        return R.error(400, "招聘日历 - 获取失败");
    }
}