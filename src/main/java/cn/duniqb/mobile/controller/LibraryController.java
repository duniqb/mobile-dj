package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.Book;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.utils.LibrarySpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 与图书馆相关的接口
 *
 * @author duniqb
 */
@Api(value = "与图书馆相关的接口", tags = {"与图书馆相关的接口"})
@Scope("session")
@RestController
@RequestMapping("/api/v1/library/")
public class LibraryController {
    @Autowired
    private LibrarySpiderService librarySpiderService;

    /**
     * 发起馆藏查询的 url
     */
    @Value("${lib.searchUrl}")
    private String searchUrl;

    /**
     * 显示图书详情的 url
     */
    @Value("${lib.showBookUrl}")
    private String showBookUrl;

    /**
     * 设置 Cookie 的参数 domain
     */
    @Value("${lib.cookie.domain}")
    private String domain;

    /**
     * 设置 Cookie 的参数 path
     */
    @Value("${lib.cookie.path}")
    private String path;

    /**
     * 馆藏查询
     *
     * @param name
     * @return
     */
    @ApiOperation(value = "馆藏查询", notes = "馆藏查询的接口，请求参数是图书名")
    @ApiImplicitParam(name = "name", value = "查询参数，书名", required = true, dataType = "String", paramType = "query")
    @GetMapping("query")
    public JSONResult query(@RequestParam String name) {
        if (name == null || "".equals(name.trim())) {
            return JSONResult.build(null, "书名不能为空", 400);
        }
        List<Book> bookList = librarySpiderService.query(name);
        return JSONResult.build(bookList, "查询成功", 200);
    }

}
