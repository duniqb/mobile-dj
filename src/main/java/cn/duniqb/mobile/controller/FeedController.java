package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.WxUser;
import cn.duniqb.mobile.dto.json.JSONResult;
import cn.duniqb.mobile.nosql.mongodb.document.feed.Comment;
import cn.duniqb.mobile.nosql.mongodb.document.feed.Title;
import cn.duniqb.mobile.service.FeedService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.RedisUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.DeleteResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * 信息流，此处需要校验用户信息
 *
 * @author duniqb
 * @date 2019/12/30 22:46
 */
@Api(value = "与信息流相关的接口", tags = {"与信息流相关的接口"})
@RestController
@RequestMapping("/api/v1/feed/")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "查询文章详情", notes = "查询文章详情")
    @GetMapping("find")
    public JSONResult find(@RequestParam String id) {
        Title title = feedService.findById(id);
        if (title != null) {
            return JSONResult.build(title, "查询文章详情成功", 200);
        }
        return JSONResult.build(null, "查询文章详情失败", 200);
    }

    @ApiOperation(value = "分页查询文章", notes = "分页查询文章")
    @GetMapping("list")
    public JSONResult listDesc(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        List<Title> titles = feedService.listDesc(pageNum, pageSize);
        if (!titles.isEmpty()) {
            return JSONResult.build(titles, "分页查询文章成功", 200);
        }
        return JSONResult.build(null, "分页查询文章失败", 200);
    }

    @ApiOperation(value = "新增文章", notes = "新增文章")
    @PostMapping("create")
    public JSONResult create(@RequestBody Title title) {
        title.set_id(System.currentTimeMillis() + "");
        title.setDate(new Date());
        Title res = feedService.save(title);
        if (res != null) {
            return JSONResult.build(res, "新增文章成功", 200);
        }
        return JSONResult.build(null, "新增文章失败", 400);
    }

    @ApiOperation(value = "删除文章", notes = "删除文章")
    @DeleteMapping("delete")
    public JSONResult delete(@RequestParam String sessionId, @RequestParam String titleId) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            // 找出 mongoDB 中的文章
            Title title = feedService.findById(titleId);
            if (title != null && StrUtil.equals(title.getOpenid(), openidFromRedis)) {
                DeleteResult deleteResult = feedService.delete(titleId);
                if (deleteResult != null) {
                    return JSONResult.build(deleteResult, "删除文章成功", 200);
                }
            }
        }
        return JSONResult.build(null, "删除文章失败", 400);
    }

    @ApiOperation(value = "添加评论", notes = "添加评论")
    @PutMapping("comment")
    public JSONResult comment(@RequestParam String sessionId, @RequestParam String titleId, @RequestBody String content) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            WxUser wxUser = wxUserService.selectByOpenid(openidFromRedis);
            if (wxUser != null) {
                // 找出 mongoDB 中的文章
                Title title = feedService.findById(titleId);
                if (title != null) {
                    Comment comment = new Comment();
                    comment.setAvatar(wxUser.getAvatarUrl());
                    comment.setContent(content);
                    comment.setOpenid(openidFromRedis);
                    comment.setDate(new Date());
                    comment.setGroup(openidFromRedis);
                    comment.setState(0);
                    comment.setNickname(wxUser.getNickname());

                    title.getCommentList().add(comment);
                    Title res = feedService.save(title);
                    if (res != null) {
                        return JSONResult.build(res, "添加评论成功", 200);
                    }
                }
            }
        }
        return JSONResult.build(null, "添加评论失败", 200);
    }
}

