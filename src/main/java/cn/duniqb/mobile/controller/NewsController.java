package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.news.NewsList;
import cn.duniqb.mobile.utils.NewsSpiderService;
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
@RequestMapping("/api/v1/news/")
public class NewsController {

    @Autowired
    private NewsSpiderService newsSpiderService;

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
    public JSONResult list(@RequestParam String type, @RequestParam(required = false) String page) {
        NewsList list = newsSpiderService.list(type, page);
        if (list != null) {
            return JSONResult.build(list, list.getType() + " - 获取成功", 200);
        }
        return JSONResult.build(null, "获取失败", 400);
    }
}
