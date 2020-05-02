package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.TopArticleEntity;
import cn.duniqb.mobile.service.TopArticleService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 置顶文章
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("/toparticle")
public class TopArticleController {
    @Autowired
    private TopArticleService topArticleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:toparticle:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = topArticleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{articleId}")
    // @RequiresPermissions("mobile:toparticle:info")
    public R info(@PathVariable("articleId") Integer articleId) {
        TopArticleEntity topArticle = topArticleService.getById(articleId);

        return R.ok().put("topArticle", topArticle);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:toparticle:save")
    public R save(@RequestBody TopArticleEntity topArticle) {
        topArticleService.save(topArticle);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:toparticle:update")
    public R update(@RequestBody TopArticleEntity topArticle) {
        topArticleService.updateById(topArticle);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:toparticle:delete")
    public R delete(@RequestBody Integer[] articleIds) {
        topArticleService.removeByIds(Arrays.asList(articleIds));

        return R.ok();
    }

}
