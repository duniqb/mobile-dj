package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.dto.feed.LikeArticle;
import cn.duniqb.mobile.entity.LikeArticleEntity;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.LikeArticleService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.EntityType;
import cn.duniqb.mobile.utils.redis.LikeService;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 文章点赞表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Slf4j
@Api(tags = {"与文章点赞相关的接口"})
@RestController
@RequestMapping("/likearticle")
public class LikeArticleController {
    @Autowired
    private LikeArticleService likeArticleService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LikeService likeService;

    @Autowired
    private WxUserService wxUserService;

    /**
     * 被点赞过的文档 id
     */
    private final String ARTICLE_ID_LIKE_LIST = "ARTICLE_ID_LIKE_LIST";

    /**
     * 被取消点赞过的文档 id
     */
    private final String ARTICLE_ID_DISLIKE_LIST = "ARTICLE_ID_DISLIKE_LIST";

    /**
     * 列表
     */
    @RequestMapping("/list/{id}")
    // @RequiresPermissions("mobile:likearticle:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Integer id) {

        PageUtils page = likeArticleService.queryPage(params, id);

        List<LikeArticleEntity> likeArticleEntityList = (List<LikeArticleEntity>) page.getList();
        List<LikeArticle> likeArticleList = new ArrayList<>();
        for (LikeArticleEntity likeArticleEntity : likeArticleEntityList) {
            LikeArticle likeArticle = new LikeArticle();
            BeanUtils.copyProperties(likeArticleEntity, likeArticle);

            // 昵称与头像
            QueryWrapper<WxUserEntity> queryWrapperName = new QueryWrapper<>();
            queryWrapperName.eq("openid", likeArticleEntity.getOpenId());
            WxUserEntity wxUser = wxUserService.getOne(queryWrapperName);
            likeArticle.setName(wxUser.getNickname());
            likeArticle.setAvatar(wxUser.getAvatarUrl());

            likeArticleList.add(likeArticle);
        }

        page.setList(likeArticleList);

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
    public R update(@RequestParam Integer articleId, @RequestParam(required = false) String sessionId) {
        // 使用 sessionId 查找用户
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];

            Integer likeStatus = likeService.getLikeStatus(openid, EntityType.ENTITY_ARTICLE, articleId);

            // 用户对该实体点过赞
            if (likeStatus == 1) {
                // 记录出现变化的 articleId
                redisUtil.sadd(ARTICLE_ID_DISLIKE_LIST, String.valueOf(articleId));

                Long likeCount = likeService.dislike(openid, EntityType.ENTITY_ARTICLE, articleId);
                // 做定时任务序列化
                return R.ok(1, "取消点赞成功").put("likeCount", likeCount);
            }
            // 取消点赞
            else if (likeStatus == -1) {
                // 记录出现变化的 articleId
                redisUtil.sadd(ARTICLE_ID_LIKE_LIST, String.valueOf(articleId));
                Long likeCount = likeService.like(openid, EntityType.ENTITY_ARTICLE, articleId);
                // 做定时任务序列化
                return R.ok(0, "点赞成功").put("likeCount", likeCount);
            }
        }
        return R.error(400, "请求失败");
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
