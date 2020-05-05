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

    /**
     * 生成 like 的键
     *
     * @param entityId
     * @param entityType
     * @return
     */
    public static String getLikeKey(int entityType, int entityId) {
        // 类型：序号
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
