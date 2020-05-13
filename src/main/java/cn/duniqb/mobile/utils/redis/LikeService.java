package cn.duniqb.mobile.utils.redis;

import cn.duniqb.mobile.entity.LikeArticleEntity;
import cn.duniqb.mobile.service.LikeArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 点赞服务
 *
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/5 14:12
 * @since 1.8
 */
@Service
public class LikeService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LikeArticleService likeArticleService;

    /**
     * 某用户对某元素是否 like 的状态
     *
     * @param openid     用户
     * @param entityType 实体类型
     * @param entityId   实体 id
     * @return like 返回 1，不 like 返回 -1，否则返回 0
     */
    public Integer getLikeStatus(String openid, int entityType, int entityId) {
        // 生成喜欢某个实体 id 的键
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 该用户对该实体 id 点过赞吗？（以该实体 id 为键的集合，是否包含该用户）
        if (redisUtil.sismember(likeKey, openid)) {
            return 1;
        }

        // 生成不喜欢某个实体 id 的键
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        if (redisUtil.sismember(dislikeKey, openid)) {
            return -1;
        }

        QueryWrapper<LikeArticleEntity> queryWrapperIsLike = new QueryWrapper<>();
        queryWrapperIsLike.eq("article_id", entityId);
        queryWrapperIsLike.eq("open_id", openid);
        LikeArticleEntity likeArticleEntity = likeArticleService.getOne(queryWrapperIsLike);
        if (likeArticleEntity != null) {
            return 1;
        }
        return -1;
    }

    /**
     * 用户对某实体进行了点赞操作
     *
     * @param openid
     * @param entityType 实体类型，文章，评论等
     * @param entityId   实体 id
     * @return
     */
    public Long like(String openid, int entityType, int entityId) {
        // likeKey 某具体实体的键
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 在该实体 id 为键的集合里，加入用户
        redisUtil.sadd(likeKey, openid);

        // 同时将用户从点踩集合里删除
        String disLikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        redisUtil.srem(disLikeKey, openid);

        // 返回该实体集合中，点赞用户的数量
        return redisUtil.scard(likeKey);
    }

    /**
     * 用户对某实体进行了取消点赞操作
     *
     * @param openid
     * @param entityType 实体类型，文章，评论等
     * @param entityId   实体 id
     * @return
     */
    public Long dislike(String openid, int entityType, int entityId) {
        // likeKey 某具体实体的键
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        // 在该实体 id 为键的集合里，加入用户
        redisUtil.sadd(dislikeKey, openid);

        // 同时将用户从点赞集合里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        redisUtil.srem(likeKey, openid);

        // 返回该实体集合中，点赞用户的数量
        return redisUtil.scard(likeKey);
    }
}
