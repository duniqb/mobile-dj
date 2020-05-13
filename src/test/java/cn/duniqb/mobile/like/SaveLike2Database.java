package cn.duniqb.mobile.like;

import cn.duniqb.mobile.utils.redis.EntityType;
import cn.duniqb.mobile.utils.redis.RedisKeyUtil;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/8 12:19
 * @since 1.8
 */
public class SaveLike2Database {

    @Autowired
    private RedisUtil redisUtil;


    String likeKey = RedisKeyUtil.getLikeKey(EntityType.ENTITY_ARTICLE, 1);


}
