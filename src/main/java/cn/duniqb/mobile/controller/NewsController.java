package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.news.News;
import cn.duniqb.mobile.dto.news.NewsList;
import cn.duniqb.mobile.spider.NewsSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 与新闻相关的接口
 *
 * @author duniqb
 */
@Api(tags = {"与新闻相关的接口"})
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsSpiderService newsSpiderService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 新闻列表在 Redis 里的前缀
     */
    private static final String NEWS_LIST = "NEWS_LIST";

    /**
     * 新闻详情在 Redis 里的前缀
     */
    private static final String NEWS_DETAIL = "NEWS_DETAIL";

    /**
     * 获取新闻列表
     *
     * @param type 新闻类型 type，1：交大要闻 2：综合报道 ，3：通知公告
     * @param page 页数
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取新闻列表", notes = "获取新闻列表的接口，请求参数是 type，page")
    public R list(@RequestParam String type, @RequestParam(required = false) String page) {
        String res = redisUtil.get(NEWS_LIST + ":" + type + ":" + page);
        if (res != null) {
            return R.ok("新闻列表 - 缓存获取成功").put("data", JSON.parseObject(res, NewsList.class));
        }
        NewsList list = newsSpiderService.list(type, page);
        if (list != null) {
            redisUtil.set(NEWS_LIST + ":" + type + ":" + page, JSON.toJSONString(list), 60 * 60 * 24);
            return R.ok(list.getType() + " - 获取成功").put("data", list);
        }
        return R.error(400, "获取失败");
    }

    /**
     * 获取新闻详情
     *
     * @param type 新闻类型 type，1：交大要闻 2：综合报道 ，3：通知公告
     * @param id   新闻 id
     * @return
     */
    @GetMapping("/detail")
    @ApiOperation(value = "获取新闻详情", notes = "获取新闻详情的接口，请求参数是 type，id")
    public R detail(@RequestParam String type, @RequestParam Integer id) {
        String res = redisUtil.get(NEWS_DETAIL + ":" + type + ":" + id);
        if (res != null) {
            return R.ok("新闻详情 - 缓存获取成功").put("data", JSON.parseObject(res, News.class));
        }
        News detail = newsSpiderService.detail(type, id);
        if (detail != null) {
            redisUtil.set(NEWS_DETAIL + ":" + type + ":" + id, JSON.toJSONString(detail), 60 * 60 * 24);
            return R.ok(detail.getType() + " - 获取成功").put("data", detail);
        }
        return R.error(400, "获取失败");
    }
}
