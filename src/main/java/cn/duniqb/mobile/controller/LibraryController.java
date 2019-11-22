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
     * 读者热点-近2年入藏复本平均量的 url
     */
    @Value("${lib.readerHotAvg}")
    private String readerHotAvg;

    /**
     * 读者热点-近2年入藏复本总借量的 url
     */
    @Value("${lib.readerHotSum}")
    private String readerHotSum;

    /**
     * 荐购热点-近5年入藏复本平均量的 url
     */
    @Value("${lib.recommendHotAvg}")
    private String recommendHotAvg;

    /**
     * 荐购热点-近5年入藏复本总借量的 url
     */
    @Value("${lib.recommendHotSum}")
    private String recommendHotSum;

    /**
     * 新书热度-近90天内入藏复本总借量的 url
     */
    @Value("${lib.newHotSum}")
    private String newHotSum;


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

    /**
     * 图书详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "图书详情", notes = "图书详情的接口，请求参数是图书 id")
    @ApiImplicitParam(name = "id", value = "查询参数，图书 id", required = true, dataType = "String", paramType = "query")
    @GetMapping("show")
    public JSONResult show(@RequestParam String id) {
        if (id == null || "".equals(id.trim())) {
            return JSONResult.build(null, "id 不能为空", 400);
        }
        Book book = librarySpiderService.show(id);
        return JSONResult.build(book, "查询成功", 200);
    }

    /**
     * 热点图书
     *
     * @return
     */
    @ApiOperation(value = "热点图书", notes = "热点图书的接口")
    @ApiImplicitParam(name = "type", value = "查询参数，热度类型，1：读者热点-近2年入藏复本平均量，2：读者热点-近2年入藏复本总借量，" +
            "3：荐购热点-近5年入藏复本平均量，4：荐购热点-近5年入藏复本总借量，5：新书热度-近90天内入藏复本总借量)，",
            required = true, dataType = "String", paramType = "query")
    @GetMapping("hot")
    public JSONResult score(@RequestParam String type) {
        if ("1".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(readerHotAvg);
            return JSONResult.build(bookList, "读者热点-近2年入藏复本平均量，查询成功", 200);
        } else if ("2".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(readerHotSum);
            return JSONResult.build(bookList, "读者热点-近2年入藏复本总借量，查询成功", 200);
        } else if ("3".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(recommendHotAvg);
            return JSONResult.build(bookList, "荐购热点-近5年入藏复本平均量，查询成功", 200);
        } else if ("4".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(recommendHotSum);
            return JSONResult.build(bookList, "荐购热点-近5年入藏复本总借量，查询成功", 200);
        } else if ("5".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(newHotSum);
            return JSONResult.build(bookList, "新书热度-近90天内入藏复本总借量，查询成功", 200);
        }

        return JSONResult.build(null, "查询失败", 400);
    }

}

