package cn.duniqb.mobile.utils.redis;

/**
 * Redis 的键生成器
 *
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/5 13:16
 * @since 1.8
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String BIZ_LIKE = "LIKE";
    private static final String BIZ_DISLIKE = "DISLIKE";

    /**
     * 生成 like 的键
     *
     * @param entityType
     * @return
     */
    public static String getLikeKey(int entityType, int entityId) {
        // 事件类型：实体类型
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成 dislike 的键
     *
     * @param entityType
     * @return
     */
    public static String getDislikeKey(int entityType, int entityId) {
        // 事件类型：实体类型
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
