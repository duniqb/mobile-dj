package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.BookDto;
import cn.duniqb.mobile.dto.profession.ProfessionHotDto;
import cn.duniqb.mobile.entity.BookCateEntity;
import cn.duniqb.mobile.service.BookCateService;
import cn.duniqb.mobile.spider.LibrarySpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v2/library/")
public class LibraryController {
    @Autowired
    private LibrarySpiderService librarySpiderService;

    @Autowired
    private BookCateService bookCateService;


    @Autowired
    private RedisUtil redisUtil;


    /**
     * 馆藏查询在 Redis 里的前缀
     */
    private static final String LIBRARY_QUERY = "LIBRARY_QUERY";

    /**
     * 图书详情在 Redis 里的前缀
     */
    private static final String BOOK_DETAIL = "BOOK_DETAIL";

    /**
     * 热点图书在 Redis 里的前缀
     */
    private static final String HOT_BOOK = "HOT_BOOK";

    /**
     * 分类热点在 Redis 里的前缀
     */
    private static final String CATE_HOT_BOOK = "CATE_HOT_BOOK";

    /**
     * 图书分类法总类在 Redis 里的前缀
     */
    private static final String BOOK_CATE = "BOOK_CATE";

    /**
     * 学院列表在 Redis 里的前缀
     */
    private static final String COLLEGE_LIST = "COLLEGE_LIST";

    /**
     * 专业热点里的专业/课程列表在 Redis 里的前缀
     */
    private static final String MAJOR_HOT = "MAJOR_HOT";

    /**
     * 馆藏查询
     *
     * @param name
     * @return
     */
    @ApiOperation(value = "馆藏查询", notes = "馆藏查询的接口，请求参数是图书名")
    @ApiImplicitParam(name = "name", value = "查询参数，书名", required = true, dataType = "String", paramType = "query")
    @GetMapping("query")
    public R query(@RequestParam String name) {
        if (name == null || "".equals(name.trim())) {
            return R.ok().put("书名不能为空", null);
        }
        String res = redisUtil.get(LIBRARY_QUERY + ":" + name);
        if (res != null) {
            return R.ok().put("馆藏查询 - 缓存获取成功", JSON.parseArray(res, BookDto.class));
        }
        List<BookDto> bookDtoList = librarySpiderService.query(name);
        if (!bookDtoList.isEmpty()) {
            redisUtil.set(LIBRARY_QUERY + ":" + name, JSON.toJSONString(bookDtoList), 60 * 60 * 24);
            return R.ok().put("查询成功", bookDtoList);
        }
        return R.ok().put("查询失败", null);
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
    public R show(@RequestParam String id) {
        if (id == null || "".equals(id.trim())) {
            return R.ok().put("id 不能为空", null);
        }
        String res = redisUtil.get(BOOK_DETAIL + ":" + id);
        if (res != null) {
            return R.ok().put("图书详情 - 缓存获取成功", JSON.parseObject(res, BookDto.class));
        }
        BookDto bookDto = librarySpiderService.show(id);
        redisUtil.set(BOOK_DETAIL + ":" + id, JSON.toJSONString(bookDto), 60 * 60 * 24);
        return R.ok().put("查询成功", bookDto);
    }

    /**
     * 热点图书
     *
     * @return
     */
    @ApiOperation(value = "热点图书 - 读者热点，荐购热点，新书热度，专业热点", notes = "热点图书的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询参数，热度类型，1：读者热点-近2年入藏复本平均量，2：读者热点-近2年入藏复本总借量，" +
                    "3：荐购热点-近5年入藏复本平均量，4：荐购热点-近5年入藏复本总借量，5：新书热度-近90天内入藏复本总借量)，6：专业热点",
                    required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sq", value = "查询参数，6.专业热点的查询参数", dataType = "String", paramType = "query")
    })
    @GetMapping("hot")
    public R score(@RequestParam String type, @RequestParam(required = false) String sq) {
        String res = redisUtil.get(HOT_BOOK + ":" + type);
        if (res != null) {
            return R.ok().put("热点图书 - 缓存获取成功", JSON.parseArray(res, BookDto.class));
        }
        if ("1".equals(type) && sq == null) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderScoreHot.aspx");
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("读者热点-近2年入藏复本平均量，查询成功", bookDtoList);
            }
        } else if ("2".equals(type) && sq == null) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderScoreHot2.aspx");
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("读者热点-近2年入藏复本总借量，查询成功", bookDtoList);
            }
        } else if ("3".equals(type) && sq == null) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderRecommanded.aspx");
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("荐购热点-近5年入藏复本平均量，查询成功", bookDtoList);
            }
        } else if ("4".equals(type) && sq == null) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderRecommanded2.aspx");
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("荐购热点-近5年入藏复本总借量，查询成功", bookDtoList);
            }
        } else if ("5".equals(type) && sq == null) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderNewBook.aspx");
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("新书热度-近90天内入藏复本总借量，查询成功", bookDtoList);
            }
        } else if ("6".equals(type) && sq != null) {
            String res6 = redisUtil.get(HOT_BOOK + ":" + type + ":" + sq);
            if (res6 != null) {
                return R.ok().put("热点图书 - 缓存获取成功", JSON.parseArray(res6, BookDto.class));
            }
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderHot3.aspx" + "?sq=" + sq);
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type + ":" + sq, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("专业热点-近2年内入藏复本总借量，查询成功", bookDtoList);
            }
        }
        return R.ok().put("查询失败", null);
    }


    /**
     * 分类热点
     *
     * @param type
     * @param cate
     * @return
     */
    @ApiOperation(value = "热点图书 - 分类热点", notes = "热点图书的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询参数，type=1 近2年入藏复本平均量，type=2 近2年入藏复本总借量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cate", value = "1.马克思主义、列宁主义、毛泽东思...A  \n  2.哲学、宗教...B  \n 3.社会科学总论...C  \n   " +
                    "4.政治、法律...D  \n 5.军事...E  \n   6.经济...F  \n   7.文化、科学、教育、体育...G \n   8.语言、文字...H \n 9.文学...I  \n   " +
                    "10.艺术...J  \n  11.历史、地理...K \n    12.自然科学总论...N  \n  13.数理科学和化学...O \n  14.天文学、地球科学...P  \n   " +
                    "15.生物科学...Q  \n   16.医药、卫生...R  \n   17.农业科学...S \n  18.工业技术...T  \n 19.交通运输...U \n  20.航空、航天...V  \n   " +
                    "21.环境科学、安全科学...X \n    22.综合性图书...Z", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("category")
    public R category(@RequestParam String type, @RequestParam String cate) {
        if (type == null || cate == null) {
            return R.ok().put("参数不能为空", null);
        }
        String res = redisUtil.get(CATE_HOT_BOOK + ":" + type + ":" + cate);
        if (res != null) {
            return R.ok().put("分类热点 - 缓存获取成功", JSON.parseArray(res, BookDto.class));
        }
        if ("1".equals(type)) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderHot4.aspx" + "?sq=" + cate);
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(CATE_HOT_BOOK + ":" + type + ":" + cate, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("分类热点-近2年入藏复本平均量，查询成功", bookDtoList);
            }
        } else if ("2".equals(type)) {
            List<BookDto> bookDtoList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderHot.aspx" + "?sq=" + cate);
            if (!bookDtoList.isEmpty()) {
                redisUtil.set(CATE_HOT_BOOK + ":" + type + ":" + cate, JSON.toJSONString(bookDtoList), 60 * 60 * 24 * 3);
                return R.ok().put("分类热点-近2年入藏复本总借量，查询成功", bookDtoList);
            }
        }
        return R.ok().put("查询失败", null);
    }

    /**
     * 图书分类法总类
     *
     * @return
     */
    @ApiOperation(value = "图书分类法总类", notes = "图书分类法总类的接口")
    @GetMapping("bookCate")
    public R bookCate() {
        String res = redisUtil.get(BOOK_CATE);
        if (res != null) {
            return R.ok().put("图书分类法总类 - 缓存获取成功", JSON.parseArray(res, BookCateEntity.class));
        }
        List<BookCateEntity> bookCateList = bookCateService.list();
        if (!bookCateList.isEmpty()) {
            redisUtil.set(BOOK_CATE, JSON.toJSONString(bookCateList), 60 * 60 * 24 * 3);
            return R.ok().put("查询成功", bookCateList);
        }
        return R.ok().put("查询失败", null);
    }

    /**
     * 学院列表
     */
    @ApiOperation(value = "学院列表", notes = "学院列表的接口")
    @GetMapping("college")
    public R college() {
        String res = redisUtil.get(COLLEGE_LIST);
        if (res != null) {
            return R.ok().put("学院列表 - 缓存获取成功", JSON.parseObject(res, ProfessionHotDto.class));
        }
        ProfessionHotDto college = librarySpiderService.college();
        if (!college.getList().isEmpty()) {
            redisUtil.set(COLLEGE_LIST, JSON.toJSONString(college), 60 * 60 * 24 * 3);
            return R.ok().put("查询成功", college);
        }
        return R.ok().put("查询失败", null);
    }

    /**
     * 专业热点里的专业/课程列表
     * major 为空则 http://wxlib.djtu.edu.cn/br/ReaderProfession.aspx?sq=材料科学
     * 否则 http://wxlib.djtu.edu.cn/br/ReaderFenLeiHao.aspx?zy=材料焊接&xy=材料科学
     *
     * @return
     */
    @ApiOperation(value = "专业热点里的专业/课程列表", notes = "专业/课程列表的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "college", value = "查询参数，学院，如 材料科学", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "major", value = "查询参数，专业，如 材料焊接", dataType = "String", paramType = "query")
    })
    @GetMapping("major")
    public R major(@RequestParam String college, @RequestParam(required = false) String major) {
        if (college == null) {
            return R.ok().put("学院不能为空", null);
        }
        if (major == null) {
            String res = redisUtil.get(MAJOR_HOT + ":" + college);
            if (res != null) {
                return R.ok().put("专业热点里的专业/课程列表 - 缓存获取成功", JSON.parseObject(res, ProfessionHotDto.class));
            }
        } else {
            String res = redisUtil.get(MAJOR_HOT + ":" + college + ":" + major);
            if (res != null) {
                return R.ok().put("专业热点里的专业/课程列表 - 缓存获取成功", JSON.parseObject(res, ProfessionHotDto.class));
            }
        }

        ProfessionHotDto professionHotDto = librarySpiderService.major(college, major);
        if (!professionHotDto.getList().isEmpty()) {
            if (major == null) {
                redisUtil.set(MAJOR_HOT + ":" + college, JSON.toJSONString(professionHotDto), 60 * 60 * 24 * 3);
            } else {
                redisUtil.set(MAJOR_HOT + ":" + college + ":" + major, JSON.toJSONString(professionHotDto), 60 * 60 * 24 * 3);
            }
            return R.ok().put("查询成功", professionHotDto);
        }
        return R.ok().put("查询失败", null);
    }
}