package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 与校历相关的接口
 *
 * @author duniqb
 */
@Api(value = "与校历相关的接口", tags = {"与校历相关的接口"})
@RestController
@RequestMapping("/calendar")
public class CalendarController {
    /**
     * 日历
     *
     * @return
     */
    @ApiOperation(value = "日历", notes = "日历的接口")
    @GetMapping("/calendar")
    public R calendar() {
        Map<String, Integer> map = new HashMap<>();

        return R.ok().put("日历获取成功", map);
    }

    /**
     * 节假日提示
     *
     * @return
     */
    @ApiOperation(value = "节假日提示", notes = "节假日提示的接口")
    @GetMapping("/festival")
    public R festival() {
        List<String> list = new ArrayList<>();

        // 空
        return R.ok().put("节假日提示成功", list);
    }
}
