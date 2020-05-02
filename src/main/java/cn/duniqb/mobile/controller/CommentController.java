package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.CommentEntity;
import cn.duniqb.mobile.service.CommentService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 对文章的评论表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("mobile/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:comment:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = commentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{commentId}")
    // @RequiresPermissions("mobile:comment:info")
    public R info(@PathVariable("commentId") Integer commentId) {
        CommentEntity comment = commentService.getById(commentId);

        return R.ok().put("comment", comment);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:comment:save")
    public R save(@RequestBody CommentEntity comment) {
        commentService.save(comment);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:comment:update")
    public R update(@RequestBody CommentEntity comment) {
        commentService.updateById(comment);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:comment:delete")
    public R delete(@RequestBody Integer[] commentIds) {
        commentService.removeByIds(Arrays.asList(commentIds));

        return R.ok();
    }

}
