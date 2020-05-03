package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.LikeArticleEntity;
import cn.duniqb.mobile.service.LikeArticleService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 文章点赞表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与文章点赞相关的接口"})
@RestController
@RequestMapping("/likearticle")
public class LikeArticleController {
    @Autowired
    private LikeArticleService likeArticleService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:likearticle:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = likeArticleService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:likearticle:info")
    public R info(@PathVariable("id") Integer id) {
        LikeArticleEntity likeArticle = likeArticleService.getById(id);

        return R.ok().put("likeArticle", likeArticle);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:likearticle:save")
    public R save(@RequestBody LikeArticleEntity likeArticle) {
        likeArticleService.save(likeArticle);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:likearticle:update")
    public R update(@RequestBody LikeArticleEntity likeArticle) {
        likeArticleService.updateById(likeArticle);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:likearticle:delete")
    public R delete(@RequestBody Integer[] ids) {
        likeArticleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
