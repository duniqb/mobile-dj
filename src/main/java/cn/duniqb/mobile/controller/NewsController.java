package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.news.NewsDto;
import cn.duniqb.mobile.dto.news.NewsList;
import cn.duniqb.mobile.spider.NewsSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(value = "与新闻相关的接口", tags = {"与新闻相关的接口"})
@RestController
@RequestMapping("/api/v2/news/")
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
     * @param type
     * @param page
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "获取新闻列表", notes = "获取新闻列表的接口，请求参数是 type，page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "新闻类型 type，1：交大要闻 2：综合报道 ，3：通知公告", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数 page", dataType = "String", paramType = "query")
    })
    public R list(@RequestParam String type, @RequestParam(required = false) String page) {
        String res = redisUtil.get(NEWS_LIST + ":" + type + ":" + page);
        if (res != null) {
            return R.ok().put("新闻列表 - 缓存获取成功", JSON.parseObject(res, NewsList.class));
        }
        NewsList list = newsSpiderService.list(type, page);
        if (list != null) {
            redisUtil.set(NEWS_LIST + ":" + type + ":" + page, JSON.toJSONString(list), 60 * 60 * 24);
            return R.ok().put(list.getType() + " - 获取成功", list);
        }
        return R.ok().put("获取失败", 400);
    }

    /**
     * 获取新闻详情
     *
     * @param type
     * @param id
     * @return
     */
    @GetMapping("detail")
    @ApiOperation(value = "获取新闻详情", notes = "获取新闻详情的接口，请求参数是 type，id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "新闻类型 type，1：交大要闻 2：综合报道 ，3：通知公告", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "新闻 id", required = true, dataType = "String", paramType = "query")
    })
    public R detail(@RequestParam String type, @RequestParam Integer id) {
        String res = redisUtil.get(NEWS_DETAIL + ":" + type + ":" + id);
        if (res != null) {
            return R.ok().put("新闻详情 - 缓存获取成功", JSON.parseObject(res, NewsDto.class));
        }
        NewsDto detail = newsSpiderService.detail(type, id);
        if (detail != null) {
            redisUtil.set(NEWS_DETAIL + ":" + type + ":" + id, JSON.toJSONString(detail), 60 * 60 * 24);
            return R.ok().put(detail.getType() + " - 获取成功", detail);
        }
        return R.ok().put("获取失败", 400);
    }
}
