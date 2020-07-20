package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 小程序用户表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与小程序用户相关的接口"})
@RestController
@RequestMapping("/wxuser")
public class WxUserController {
    @Autowired
    private WxUserService wxUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:wxuser:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wxUserService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:wxuser:info")
    public R info(@PathVariable("id") Integer id) {
        WxUserEntity wxUser = wxUserService.getById(id);

        return R.ok().put("wxUser", wxUser);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:wxuser:save")
    public R save(@RequestBody WxUserEntity wxUser) {
        wxUserService.save(wxUser);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:wxuser:update")
    public R update(@RequestBody WxUserEntity wxUser) {
        wxUserService.updateById(wxUser);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:wxuser:delete")
    public R delete(@RequestBody Integer[] ids) {
        wxUserService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
