package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.LikeBookEntity;
import cn.duniqb.mobile.service.LikeBookService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 收藏图书表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("/likebook")
public class LikeBookController {
    @Autowired
    private LikeBookService likeBookService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:likebook:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = likeBookService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{openid}")
    // @RequiresPermissions("mobile:likebook:info")
    public R info(@PathVariable("openid") String openid) {
        LikeBookEntity likeBook = likeBookService.getById(openid);

        return R.ok().put("likeBook", likeBook);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:likebook:save")
    public R save(@RequestBody LikeBookEntity likeBook) {
        likeBookService.save(likeBook);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:likebook:update")
    public R update(@RequestBody LikeBookEntity likeBook) {
        likeBookService.updateById(likeBook);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:likebook:delete")
    public R delete(@RequestBody String[] openids) {
        likeBookService.removeByIds(Arrays.asList(openids));

        return R.ok();
    }

}
