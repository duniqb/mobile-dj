package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.LikeArticleDao;
import cn.duniqb.mobile.entity.LikeArticleEntity;
import cn.duniqb.mobile.service.LikeArticleService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import cn.duniqb.mobile.utils.redis.EntityType;
import cn.duniqb.mobile.utils.redis.RedisKeyUtil;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Set;


@Service("likeArticleService")
public class LikeArticleServiceImpl extends ServiceImpl<LikeArticleDao, LikeArticleEntity> implements LikeArticleService {


    /**
     * 被点赞过的文档 id
     */
    private final String ARTICLE_ID_LIKE_LIST = "ARTICLE_ID_LIKE_LIST";

    /**
     * 被取消点赞过的文档 id
     */
    private final String ARTICLE_ID_DISLIKE_LIST = "ARTICLE_ID_DISLIKE_LIST";

    @Autowired
    private LikeArticleService likeArticleService;

    @Resource
    private LikeArticleDao likeArticleDao;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Integer id) {

        QueryWrapper<LikeArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);

        IPage<LikeArticleEntity> page = this.page(
                new Query<LikeArticleEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 定时任务
     * Redis 中的点赞数据定时保存到数据库
     *
     * @return
     */
    @Scheduled(cron = "0/60 * * * * ?")
    @Override
    public void saveLikeToDatabase() {
        Set<String> articleIdList = redisUtil.ssmembers(ARTICLE_ID_LIKE_LIST);

        // 遍历文章列表
        for (String articleId : articleIdList) {
            // 某个文章对应的点赞键，以便找到谁点的赞
            String likeKey = RedisKeyUtil.getLikeKey(EntityType.ENTITY_ARTICLE, Integer.parseInt(articleId));
            Set<String> openidList = redisUtil.ssmembers(likeKey);
            // 遍历此文章下的点赞数据
            for (String openid : openidList) {

                QueryWrapper<LikeArticleEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("article_id", articleId);
                queryWrapper.eq("open_id", openid);
                LikeArticleEntity selectOne = likeArticleDao.selectOne(queryWrapper);

                // 保存到数据库
                if (selectOne == null) {
                    LikeArticleEntity likeArticleEntity = new LikeArticleEntity();
                    likeArticleEntity.setArticleId(Integer.parseInt(articleId));
                    likeArticleEntity.setTime(new Date());
                    likeArticleEntity.setOpenId(openid);
                    likeArticleService.save(likeArticleEntity);
                }
            }
            // 从集合中移除该 id
            redisUtil.srem(ARTICLE_ID_LIKE_LIST, articleId);
        }
    }

    /**
     * 定时任务
     * Redis 中的取消点赞数据定时保存到数据库
     *
     * @return
     */
    @Scheduled(cron = "0/60 * * * * ?")
    @Override
    public void saveDislikeToDatabase() {
        Set<String> articleIdList = redisUtil.ssmembers(ARTICLE_ID_DISLIKE_LIST);

        // 遍历文章列表
        for (String articleId : articleIdList) {
            // 某个文章对应的点赞键，以便找到谁点的赞
            String dislikeKey = RedisKeyUtil.getDislikeKey(EntityType.ENTITY_ARTICLE, Integer.parseInt(articleId));
            Set<String> openidList = redisUtil.ssmembers(dislikeKey);
            // 遍历此文章下的点赞数据
            for (String openid : openidList) {

                QueryWrapper<LikeArticleEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("article_id", articleId);
                queryWrapper.eq("open_id", openid);
                LikeArticleEntity selectOne = likeArticleDao.selectOne(queryWrapper);

                // 有记录则删除
                if (selectOne != null) {
                    likeArticleService.remove(queryWrapper);
                }
            }
            // 从集合中移除该 id
            redisUtil.srem(ARTICLE_ID_DISLIKE_LIST, articleId);
        }
    }
}