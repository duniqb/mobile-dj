package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.SeekEntity;
import cn.duniqb.mobile.service.SeekService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 失物招领
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 10:21:25
 */
@RestController
@RequestMapping("/seek")
public class SeekController {
    @Autowired
    private SeekService seekService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:seek:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = seekService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:seek:info")
    public R info(@PathVariable("id") Integer id) {
        SeekEntity seek = seekService.getById(id);

        return R.ok().put("seek", seek);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:seek:save")
    public R save(@RequestBody SeekEntity seek) {
        seekService.save(seek);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:seek:update")
    public R update(@RequestBody SeekEntity seek) {
        seekService.updateById(seek);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:seek:delete")
    public R delete(@RequestBody Integer[] ids) {
        seekService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
