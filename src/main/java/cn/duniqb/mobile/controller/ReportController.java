package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.ReportEntity;
import cn.duniqb.mobile.service.ReportService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 举报文章/评论
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("mobile/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:report:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = reportService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:report:info")
    public R info(@PathVariable("id") Integer id) {
        ReportEntity report = reportService.getById(id);

        return R.ok().put("report", report);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:report:save")
    public R save(@RequestBody ReportEntity report) {
        reportService.save(report);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:report:update")
    public R update(@RequestBody ReportEntity report) {
        reportService.updateById(report);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:report:delete")
    public R delete(@RequestBody Integer[] ids) {
        reportService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
