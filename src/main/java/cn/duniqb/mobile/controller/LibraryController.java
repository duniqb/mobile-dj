package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.BookCate;
import cn.duniqb.mobile.dto.Book;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.profession.ProfessionHot;
import cn.duniqb.mobile.service.BookCateService;
import cn.duniqb.mobile.utils.LibrarySpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

    @Autowired
    private BookCateService bookCateService;

    /**
     * 读者热点-近2年入藏复本平均量的 url
     */
    @Value("${lib.readerHotAvgUrl}")
    private String readerHotAvgUrl;

    /**
     * 读者热点-近2年入藏复本总借量的 url
     */
    @Value("${lib.readerHotSumUrl}")
    private String readerHotSumUrl;

    /**
     * 荐购热点-近5年入藏复本平均量的 url
     */
    @Value("${lib.recommendHotAvgUrl}")
    private String recommendHotAvgUrl;

    /**
     * 荐购热点-近5年入藏复本总借量的 url
     */
    @Value("${lib.recommendHotSumUrl}")
    private String recommendHotSumUrl;

    /**
     * 新书热度-近90天内入藏复本总借量的 url
     */
    @Value("${lib.newHotSumUrl}")
    private String newHotSumUrl;

    /**
     * 分类热度-近2年入藏复本平均量的 url
     */
    @Value("${lib.cateHotAvgUrl}")
    private String cateHotAvgUrl;

    /**
     * 分类热度-近2年入藏复本总借量的 url
     */
    @Value("${lib.cateHotSumUrl}")
    private String cateHotSumUrl;

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
        if (!bookList.isEmpty()) {
            return JSONResult.build(bookList, "查询成功", 200);
        }
        return JSONResult.build(null, "查询失败", 400);
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
            List<Book> bookList = librarySpiderService.hot(readerHotAvgUrl);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "读者热点-近2年入藏复本平均量，查询成功", 200);
            }
        } else if ("2".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(readerHotSumUrl);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "读者热点-近2年入藏复本总借量，查询成功", 200);
            }
        } else if ("3".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(recommendHotAvgUrl);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "荐购热点-近5年入藏复本平均量，查询成功", 200);
            }
        } else if ("4".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(recommendHotSumUrl);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "荐购热点-近5年入藏复本总借量，查询成功", 200);
            }
        } else if ("5".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(newHotSumUrl);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "新书热度-近90天内入藏复本总借量，查询成功", 200);
            }
        }
        return JSONResult.build(null, "查询失败", 400);
    }


    /**
     * 分类热点
     *
     * @param type
     * @param cate
     * @return
     */
    @ApiOperation(value = "分类热点", notes = "分类热点的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询参数，type=1 近2年入藏复本平均量，type=2 近2年入藏复本总借量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cate", value = "1.马克思主义、列宁主义、毛泽东思...A  \n  2.哲学、宗教...B  \n 3.社会科学总论...C  \n   " +
                    "4.政治、法律...D  \n 5.军事...E  \n   6.经济...F  \n   7.文化、科学、教育、体育...G \n   8.语言、文字...H \n 9.文学...I  \n   " +
                    "10.艺术...J  \n  11.历史、地理...K \n    12.自然科学总论...N  \n  13.数理科学和化学...O \n  14.天文学、地球科学...P  \n   " +
                    "15.生物科学...Q  \n   16.医药、卫生...R  \n   17.农业科学...S \n  18.工业技术...T  \n 19.交通运输...U \n  20.航空、航天...V  \n   " +
                    "21.环境科学、安全科学...X \n    22.综合性图书...Z", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("category")
    public JSONResult category(@RequestParam String type, @RequestParam String cate) {
        if (type == null || cate == null) {
            return JSONResult.build(null, "参数不能为空", 400);
        }

        if ("1".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(cateHotAvgUrl + "?sq=" + cate);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "分类热点-近2年入藏复本平均量，查询成功", 200);
            }
        } else if ("2".equals(type)) {
            List<Book> bookList = librarySpiderService.hot(cateHotSumUrl + "?sq=" + cate);
            if (!bookList.isEmpty()) {
                return JSONResult.build(bookList, "分类热点-近2年入藏复本总借量，查询成功", 200);
            }
        }
        return JSONResult.build(null, "查询失败", 400);
    }

    /**
     * 图书分类法总类
     *
     * @return
     */
    @ApiOperation(value = "图书分类法总类", notes = "图书分类法总类的接口")
    @GetMapping("bookCate")
    public JSONResult bookCate() {
        List<BookCate> bookCateList = bookCateService.selectAll();
        if (!bookCateList.isEmpty()) {
            return JSONResult.build(bookCateList, "查询成功", 200);
        }
        return JSONResult.build(null, "查询失败", 400);
    }

    /**
     * 学院列表
     */
    @ApiOperation(value = "学院列表", notes = "学院列表的接口")
    @GetMapping("college")
    public JSONResult college() {
        ProfessionHot college = librarySpiderService.college();
        if (!college.getList().isEmpty()) {
            return JSONResult.build(college, "查询成功", 200);
        }
        return JSONResult.build(null, "查询失败", 400);
    }
}