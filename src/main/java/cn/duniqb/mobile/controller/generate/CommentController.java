package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.dto.feed.Comment;
import cn.duniqb.mobile.dto.feed.CommentReply;
import cn.duniqb.mobile.entity.CommentEntity;
import cn.duniqb.mobile.entity.CommentReplyEntity;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.CommentReplyService;
import cn.duniqb.mobile.service.CommentService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 对文章的评论表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与文章评论相关的接口"})
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentReplyService commentReplyService;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 查询某个文章对应的评论和回复列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:comment:list")
    public R list(@RequestParam Map<String, Object> params, @RequestParam String id) {
        PageUtils page = commentService.queryPage(params);

        List<Comment> commentList = new ArrayList<>();

        // 找到该文章对应的评论
        QueryWrapper<CommentEntity> queryWrapperComment = new QueryWrapper<>();
        queryWrapperComment.eq("article_id", id);
        List<CommentEntity> commentEntityList = commentService.list(queryWrapperComment);

        // 找到每个评论对应的回复
        for (CommentEntity commentEntity : commentEntityList) {
            Comment comment = new Comment();
            BeanUtils.copyProperties(commentEntity, comment);

            // 查询每条评论人的昵称，头像
            QueryWrapper<WxUserEntity> queryWrapperName = new QueryWrapper<>();
            queryWrapperName.eq("openid", commentEntity.getOpenId());
            WxUserEntity commentUser = wxUserService.getOne(queryWrapperName);
            comment.setName(commentUser.getNickname());
            comment.setAvatar(commentUser.getAvatarUrl());

            // 查到每个回复列表
            Integer commentId = commentEntity.getCommentId();
            QueryWrapper<CommentReplyEntity> queryWrapperReply = new QueryWrapper<>();
            queryWrapperReply.eq("comment_id", commentId);
            List<CommentReplyEntity> commentReplyEntityList = commentReplyService.list(queryWrapperReply);

            List<CommentReply> commentReplyList = new ArrayList<>();
            for (CommentReplyEntity commentReplyEntity : commentReplyEntityList) {
                CommentReply commentReply = new CommentReply();
                BeanUtils.copyProperties(commentReplyEntity, commentReply);

                // 查询每个回复人的昵称，头像
                QueryWrapper<WxUserEntity> queryWrapperReplyFromName = new QueryWrapper<>();
                queryWrapperReplyFromName.eq("openid", commentReplyEntity.getOpenIdFrom());
                WxUserEntity from = wxUserService.getOne(queryWrapperReplyFromName);
                if (from != null) {
                    commentReply.setFromName(from.getNickname());
                }

                // 查询每个被回复人的昵称，头像
                QueryWrapper<WxUserEntity> queryWrapperReplyToName = new QueryWrapper<>();
                queryWrapperReplyToName.eq("openid", commentReplyEntity.getOpenIdTo());
                WxUserEntity to = wxUserService.getOne(queryWrapperReplyToName);
                if (to != null) {
                    commentReply.setToName(to.getNickname());
                }
                commentReplyList.add(commentReply);
            }
            comment.setReplyList(commentReplyList);

            commentList.add(comment);
        }

        page.setList(commentList);

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
    @RequestMapping("/save/{sessionId}")
    // @RequiresPermissions("mobile:comment:save")
    public R save(@RequestBody CommentEntity comment, @PathVariable String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            comment.setOpenId(openid);
            commentService.save(comment);
            return R.ok("评论成功");
        }

        return R.error(400, "评论失败");
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
