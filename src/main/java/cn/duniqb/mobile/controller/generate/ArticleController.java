package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.dao.ImgUrlDao;
import cn.duniqb.mobile.dto.feed.Article;
import cn.duniqb.mobile.entity.*;
import cn.duniqb.mobile.service.ArticleService;
import cn.duniqb.mobile.service.CommentService;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 文章表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 09:28:06
 */
@Api(tags = {"与信息流文章相关的接口"})
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private LikeArticleService likeArticleService;

    @Autowired
    private CommentService commentService;

    @Resource
    private ImgUrlDao imgUrlDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LikeService likeService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:article:list")
    public R list(@RequestParam Map<String, Object> params, @RequestParam String sessionId) {
        PageUtils page = articleService.queryPage(params);

        List<ArticleEntity> articleEntityList = (List<ArticleEntity>) page.getList();

        List<Article> articleList = new ArrayList<>();

        for (ArticleEntity articleEntity : articleEntityList) {
            Article article = new Article();

            // 复制已有属性
            BeanUtils.copyProperties(articleEntity, article);

            // 过长的文字截断
            if (article.getContent().length() > 90) {
                article.setContent(article.getContent().substring(0, 90) + "...");
            }

            // 查找该文章可能关联的图片
            QueryWrapper<ImgUrlEntity> queryWrapperImg = new QueryWrapper<>();
            queryWrapperImg.eq("article_id", article.getId());
            List<ImgUrlEntity> imgUrlEntityList = imgUrlDao.selectList(queryWrapperImg);

            List<String> imgList = new ArrayList<>();
            for (ImgUrlEntity imgUrlEntity : imgUrlEntityList) {
                imgList.add(imgUrlEntity.getUrl());
            }
            if (imgList.isEmpty()) {
                article.setBlankImage(0);
            } else if (imgList.size() < 3) {
                article.setBlankImage(3 - imgList.size());
            } else if (imgList.size() < 6) {
                article.setBlankImage(6 - imgList.size());
            } else if (imgList.size() < 9) {
                article.setBlankImage(9 - imgList.size());
            }
            article.setImgUrlList(imgList);

            // 查找文章的作者名
            QueryWrapper<WxUserEntity> queryWrapperName = new QueryWrapper<>();
            queryWrapperName.eq("openid", article.getOpenId());
            WxUserEntity wxUser = wxUserService.getOne(queryWrapperName);
            article.setAuthor(wxUser.getNickname());
            article.setAvatar(wxUser.getAvatarUrl());

            // 点赞数量
            QueryWrapper<LikeArticleEntity> queryWrapperLike = new QueryWrapper<>();
            queryWrapperLike.eq("article_id", article.getId());
            List<LikeArticleEntity> likeArticleEntityList = likeArticleService.list(queryWrapperLike);
            article.setLikeCount(likeArticleEntityList.size());

            // 该用户是否对该文章点赞
            String sessionIdValue = redisUtil.get(sessionId);
            if (sessionIdValue != null) {
                String openid = sessionIdValue.split(":")[0];

                Integer likeStatus = likeService.getLikeStatus(openid, EntityType.ENTITY_ARTICLE, article.getId());
                if (likeStatus == 1) {
                    article.setIsLike(true);
                } else if (likeStatus == -1) {
                    article.setIsLike(false);
                } else {
                    QueryWrapper<LikeArticleEntity> queryWrapperIsLike = new QueryWrapper<>();
                    queryWrapperIsLike.eq("article_id", article.getId());
                    queryWrapperIsLike.eq("open_id", openid);
                    LikeArticleEntity likeArticleEntity = likeArticleService.getOne(queryWrapperIsLike);
                    if (likeArticleEntity != null) {
                        article.setIsLike(true);
                    }
                }
            }

            // 查找评论数量
            QueryWrapper<CommentEntity> queryWrapperComment = new QueryWrapper<>();
            queryWrapperComment.eq("article_id", articleEntity.getId());
            List<CommentEntity> commentEntityList = commentService.list(queryWrapperComment);
            article.setCommentCount(commentEntityList.size());

            article.setTimestamp(articleEntity.getTime().getTime());

            articleList.add(article);
        }

        page.setList(articleList);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info")
    // @RequiresPermissions("mobile:article:info")
    public R info(@RequestParam String sessionId, @RequestParam Integer id) {
        ArticleEntity articleEntity = articleService.getById(id);

        Article article = new Article();

        // 复制已有属性
        BeanUtils.copyProperties(articleEntity, article);

        // 查找该文章可能关联的图片
        Integer articleId = articleEntity.getId();
        QueryWrapper<ImgUrlEntity> queryWrapperImg = new QueryWrapper<>();
        queryWrapperImg.eq("article_id", articleId);
        List<ImgUrlEntity> imgUrlEntityList = imgUrlDao.selectList(queryWrapperImg);

        List<String> imgList = new ArrayList<>();
        for (ImgUrlEntity imgUrlEntity : imgUrlEntityList) {
            imgList.add(imgUrlEntity.getUrl());
        }
        article.setImgUrlList(imgList);

        // 查找文章的作者名
        QueryWrapper<WxUserEntity> queryWrapperName = new QueryWrapper<>();
        queryWrapperName.eq("openid", articleEntity.getOpenId());
        WxUserEntity wxUser = wxUserService.getOne(queryWrapperName);
        article.setAuthor(wxUser.getNickname());
        article.setAvatar(wxUser.getAvatarUrl());

        // 点赞数量
        QueryWrapper<LikeArticleEntity> queryWrapperLike = new QueryWrapper<>();
        queryWrapperLike.eq("article_id", article.getId());
        List<LikeArticleEntity> likeArticleEntityList = likeArticleService.list(queryWrapperLike);
        article.setLikeCount(likeArticleEntityList.size());

        // 该用户是否对该文章点赞
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            QueryWrapper<LikeArticleEntity> queryWrapperIsLike = new QueryWrapper<>();
            queryWrapperIsLike.eq("article_id", article.getId());
            queryWrapperIsLike.eq("open_id", openid);
            LikeArticleEntity likeArticleEntity = likeArticleService.getOne(queryWrapperIsLike);
            if (likeArticleEntity != null) {
                article.setIsLike(true);
            }
        }

        // 查找评论数量
        QueryWrapper<CommentEntity> queryWrapperComment = new QueryWrapper<>();
        queryWrapperComment.eq("article_id", articleEntity.getId());
        List<CommentEntity> commentEntityList = commentService.list(queryWrapperComment);
        article.setCommentCount(commentEntityList.size());

        return R.ok().put("article", article);
    }

    /**
     * 保存
     */
    @RequestMapping("/save/{sessionId}")
    // @RequiresPermissions("mobile:article:save")
    public R save(@RequestBody ArticleEntity article, @PathVariable String sessionId) {

        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            article.setOpenId(openid);
            article.setStatus(0);
//            article.setTime(LocalDateTime.now());
            int articleId = articleService.saveArticle(article);

            return R.ok("文章保存成功").put("data", articleId);
        }
        return R.error(400, "文章保存失败");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:article:update")
    public R update(@RequestBody ArticleEntity article) {
        articleService.updateById(article);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:article:delete")
    public R delete(@RequestBody Integer[] ids) {
        articleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
