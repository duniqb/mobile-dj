package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.CommentReplyEntity;
import cn.duniqb.mobile.service.CommentReplyService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 评论回复表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@RestController
@RequestMapping("/commentreply")
public class CommentReplyController {
    @Autowired
    private CommentReplyService commentReplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:commentreply:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = commentReplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{replyId}")
    // @RequiresPermissions("mobile:commentreply:info")
    public R info(@PathVariable("replyId") Integer replyId) {
        CommentReplyEntity commentReply = commentReplyService.getById(replyId);

        return R.ok().put("commentReply", commentReply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:commentreply:save")
    public R save(@RequestBody CommentReplyEntity commentReply) {
        commentReplyService.save(commentReply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:commentreply:update")
    public R update(@RequestBody CommentReplyEntity commentReply) {
        commentReplyService.updateById(commentReply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:commentreply:delete")
    public R delete(@RequestBody Integer[] replyIds) {
        commentReplyService.removeByIds(Arrays.asList(replyIds));

        return R.ok();
    }

}
