package cn.duniqb.mobile.utils;

/**
 * redis 的键生成器
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/4/23 10:05
 * @since 1.0
 */

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String BIZ_EVENT_QUEUE = "EVENT_QUEUE";
    private static final String BIZ_EVENT = "EVENT";


    public static String getEventQueueKey() {
        return BIZ_EVENT_QUEUE;
    }
}
