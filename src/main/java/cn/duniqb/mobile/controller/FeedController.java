package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.WxUser;
import cn.duniqb.mobile.dto.json.JSONResult;
import cn.duniqb.mobile.nosql.mongodb.document.feed.Title;
import cn.duniqb.mobile.service.FeedService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.RedisUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("detail")
    public JSONResult detail(@RequestParam String id) {
        Title title = feedService.findById(id);
        if (title != null) {
            return JSONResult.build(title, "查询文章详情成功", 200);
        }
        return JSONResult.build(null, "查询文章详情失败", 400);
    }

    @ApiOperation(value = "分页倒序查询文章", notes = "分页倒序查询文章")
    @GetMapping("list")
    public JSONResult listDesc(@RequestParam Integer page, @RequestParam Integer size) {
        List<Title> titles = feedService.listDesc(page, size);
        if (!titles.isEmpty()) {
            return JSONResult.build(titles, "分页倒序查询文章成功", 200);
        }
        return JSONResult.build(null, "分页倒序查询文章失败", 400);
    }

    @ApiOperation(value = "新增文章", notes = "新增文章")
    @PostMapping("create")
    public JSONResult create(@RequestParam String sessionId, @RequestBody Title title) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            WxUser wxUser = wxUserService.selectByOpenid(openidFromRedis);
            if (wxUser != null) {
                Title res = feedService.save(wxUser, title);
                if (res != null) {
                    return JSONResult.build(res, "新增文章成功", 200);
                }
            }
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
                if (deleteResult.getDeletedCount() > 0) {
                    return JSONResult.build(deleteResult, "删除文章成功", 200);
                }
            }
        }
        return JSONResult.build(null, "删除文章失败", 400);
    }

    @ApiOperation(value = "添加评论", notes = "添加评论")
    @PutMapping("addComment")
    public JSONResult addComment(@RequestParam String sessionId, @RequestParam String titleId, @RequestParam String content) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            WxUser wxUser = wxUserService.selectByOpenid(openidFromRedis);
            if (wxUser != null) {
                UpdateResult updateResult = feedService.addComment(openidFromRedis, titleId, content, wxUser);
                if (updateResult != null && updateResult.getModifiedCount() > 0) {
                    return JSONResult.build(null, "添加评论成功", 200);
                }
            }
        }
        return JSONResult.build(null, "添加评论失败", 400);
    }

    @ApiOperation(value = "删除评论", notes = "删除添加评论")
    @DeleteMapping("delComment")
    public JSONResult delComment(@RequestParam String sessionId, @RequestParam String titleId, @RequestParam String commentId) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            if (openidFromRedis != null) {
                UpdateResult updateResult = feedService.delComment(openidFromRedis, titleId, commentId);
                if (updateResult != null && updateResult.getModifiedCount() > 0) {
                    return JSONResult.build(null, "删除评论成功", 200);
                }
            }
        }
        return JSONResult.build(null, "删除评论失败", 400);
    }

    @ApiOperation(value = "点赞评论", notes = "点赞评论")
    @PutMapping("likeComment")
    public JSONResult likeComment(@RequestParam String sessionId, @RequestParam String titleId, @RequestParam String commentId) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            // 找出 mongoDB 中的文章
            Title title = feedService.findById(titleId);
            if (title != null) {
                UpdateResult updateResult = feedService.likeComment(openidFromRedis, titleId, commentId);
                if (updateResult != null && updateResult.getModifiedCount() > 0) {
                    return JSONResult.build(updateResult, "点赞评论成功", 200);
                } else if (updateResult == null) {
                    return JSONResult.build(null, "重复点赞", 401);
                }
            }
        }
        return JSONResult.build(null, "点赞评论失败", 400);
    }

    @ApiOperation(value = "取消点赞评论", notes = "取消点赞评论")
    @PutMapping("unlikeComment")
    public JSONResult unlikeComment(@RequestParam String sessionId, @RequestParam String titleId, @RequestParam String commentId) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            // 找出 mongoDB 中的文章
            Title title = feedService.findById(titleId);
            if (title != null) {
                UpdateResult updateResult = feedService.unlikeComment(openidFromRedis, titleId, commentId);
                if (updateResult != null && updateResult.getModifiedCount() > 0) {
                    return JSONResult.build(updateResult, "取消点赞评论成功", 200);
                }
            }
        }
        return JSONResult.build(null, "取消点赞评论失败", 400);
    }

    @ApiOperation(value = "点赞文章", notes = "点赞文章")
    @PutMapping("likeTitle")
    public JSONResult likeTitle(@RequestParam String sessionId, @RequestParam String titleId) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            // 找出 mongoDB 中的文章
            Title title = feedService.findById(titleId);
            if (title != null) {
                UpdateResult updateResult = feedService.likeTitle(titleId, openidFromRedis);
                if (updateResult != null && updateResult.getModifiedCount() > 0) {
                    return JSONResult.build(updateResult, "点赞文章成功", 200);
                } else if (updateResult == null) {
                    return JSONResult.build(null, "重复点赞", 401);
                }
            }
        }
        return JSONResult.build(null, "点赞文章失败", 400);
    }

    @ApiOperation(value = "取消点赞文章", notes = "取消点赞文章")
    @PutMapping("unlikeTitle")
    public JSONResult unlikeTitle(@RequestParam String sessionId, @RequestParam String titleId) {
        // 找出 Redis 中映射的 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openidFromRedis = sessionIdValue.split(":")[0];
            // 找出 mongoDB 中的文章
            Title title = feedService.findById(titleId);
            if (title != null) {
                UpdateResult updateResult = feedService.unlikeTitle(titleId, openidFromRedis);
                if (updateResult != null && updateResult.getModifiedCount() > 0) {
                    return JSONResult.build(updateResult, "取消点赞文章成功", 200);
                }
            }
        }
        return JSONResult.build(null, "取消点赞文章失败", 400);
    }
}

