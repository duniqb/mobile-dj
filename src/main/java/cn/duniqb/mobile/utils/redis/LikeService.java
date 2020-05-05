package cn.duniqb.mobile.utils.redis;

import jdk.nashorn.internal.ir.ReturnNode;
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

    /**
     * 获取某实体的点赞数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return redisUtil.scard(likeKey);
    }

    /**
     * 某用户对某元素是否 like 的状态
     *
     * @param openid     用户
     * @param entityType 实体类型
     * @param entityId   实体 id
     * @return 是否喜欢：喜欢是 0，不喜欢是 1
     */
    public Boolean getLikeStatus(String openid, int entityType, int entityId) {
        // 生成喜欢某个实体 id 的键
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 该用户对该实体 id 点过赞吗？（以该实体 id 为键的集合，是否包含该用户）
        return redisUtil.sismember(likeKey, openid);
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
        // likekey 某具体实体的键
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 在该实体 id 为键的集合里，加入用户
        redisUtil.sadd(likeKey, openid);

        // 返回该实体集合中，点赞用户的数量
        return redisUtil.scard(likeKey);
    }

    /**
     * 用户对某实体进行了取消点赞操作
     *
     * @param openid
     * @param entityType
     * @param entityId
     * @return
     */
    public Long disLike(String openid, int entityType, int entityId) {
        // 将用户从点赞集合里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        // 在该实体 id 为键的集合里，移除用户
        redisUtil.srem(likeKey, openid);

        // 返回该实体集合中，点赞用户的数量
        return redisUtil.scard(likeKey);
    }
}
