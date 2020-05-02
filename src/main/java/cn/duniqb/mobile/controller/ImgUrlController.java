package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.ImgUrlEntity;
import cn.duniqb.mobile.service.ImgUrlService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 图片表，存储在oss
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("/imgurl")
public class ImgUrlController {
    @Autowired
    private ImgUrlService imgUrlService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:imgurl:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = imgUrlService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:imgurl:info")
    public R info(@PathVariable("id") Integer id) {
        ImgUrlEntity imgUrl = imgUrlService.getById(id);

        return R.ok().put("imgUrl", imgUrl);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:imgurl:save")
    public R save(@RequestBody ImgUrlEntity imgUrl) {
        imgUrlService.save(imgUrl);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:imgurl:update")
    public R update(@RequestBody ImgUrlEntity imgUrl) {
        imgUrlService.updateById(imgUrl);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:imgurl:delete")
    public R delete(@RequestBody Integer[] ids) {
        imgUrlService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
