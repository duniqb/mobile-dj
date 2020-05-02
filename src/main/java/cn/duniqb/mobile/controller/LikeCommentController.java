package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.LikeCommentEntity;
import cn.duniqb.mobile.service.LikeCommentService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 点赞评论表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("mobile/likecomment")
public class LikeCommentController {
    @Autowired
    private LikeCommentService likeCommentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:likecomment:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = likeCommentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:likecomment:info")
    public R info(@PathVariable("id") Integer id) {
        LikeCommentEntity likeComment = likeCommentService.getById(id);

        return R.ok().put("likeComment", likeComment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:likecomment:save")
    public R save(@RequestBody LikeCommentEntity likeComment) {
        likeCommentService.save(likeComment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:likecomment:update")
    public R update(@RequestBody LikeCommentEntity likeComment) {
        likeCommentService.updateById(likeComment);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:likecomment:delete")
    public R delete(@RequestBody Integer[] ids) {
        likeCommentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
