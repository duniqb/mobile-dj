package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.Book;
import cn.duniqb.mobile.dto.profession.ProfessionHot;
import cn.duniqb.mobile.entity.BookCateEntity;
import cn.duniqb.mobile.service.BookCateService;
import cn.duniqb.mobile.spider.LibrarySpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
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
@Api(tags = {"与图书馆相关的接口"})
@Scope("session")
@RestController
@RequestMapping("/library")
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
    @GetMapping("/query")
    public R query(@RequestParam String name) {
        if (name == null || "".equals(name.trim())) {
            return R.error(400, "书名不能为空");
        }
        String res = redisUtil.get(LIBRARY_QUERY + ":" + name);
        if (res != null) {
            return R.ok("馆藏查询 - 缓存获取成功").put("data", JSON.parseArray(res, Book.class));
        }
        List<Book> bookList = librarySpiderService.query(name);
        if (!bookList.isEmpty()) {
            redisUtil.set(LIBRARY_QUERY + ":" + name, JSON.toJSONString(bookList), 60 * 60 * 24);
            return R.ok("查询成功").put("data", bookList);
        }
        return R.error(400, "查询失败");
    }

    /**
     * 图书详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "图书详情", notes = "图书详情的接口，请求参数是图书 id")
    @GetMapping("/show")
    public R show(@RequestParam String id) {
        if (id == null || "".equals(id.trim())) {
            return R.error(400, "id 不能为空");
        }
        String res = redisUtil.get(BOOK_DETAIL + ":" + id);
        if (res != null) {
            return R.ok("图书详情 - 缓存获取成功").put("data", JSON.parseObject(res, Book.class));
        }
        Book book = librarySpiderService.show(id);
        redisUtil.set(BOOK_DETAIL + ":" + id, JSON.toJSONString(book), 60 * 60 * 24);
        return R.ok("图书详情 - 缓存获取成功").put("data", book);
    }

    /**
     * 热点图书
     *
     * @param type 查询参数，热度类型，
     *             1：读者热点-近2年入藏复本平均量，2：读者热点-近2年入藏复本总借量，" +
     *             3：荐购热点-近5年入藏复本平均量，4：荐购热点-近5年入藏复本总借量，
     *             5：新书热度-近90天内入藏复本总借量)，6：专业热点
     * @param sq   查询参数，6.专业热点的查询参数
     * @return
     */
    @ApiOperation(value = "热点图书 - 读者热点，荐购热点，新书热度，专业热点", notes = "热点图书的接口")
    @GetMapping("/hot")
    public R score(@RequestParam String type, @RequestParam(required = false) String sq) {
        String res = redisUtil.get(HOT_BOOK + ":" + type);
        if (res != null) {
            return R.ok("热点图书 - 缓存获取成功").put("data", JSON.parseArray(res, Book.class));
        }
        if ("1".equals(type) && sq == null) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderScoreHot.aspx");
            if (!bookList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("读者热点-近2年入藏复本平均量，查询成功").put("data", bookList);
            }
        } else if ("2".equals(type) && sq == null) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderScoreHot2.aspx");
            if (!bookList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("读者热点-近2年入藏复本总借量，查询成功").put("data", bookList);
            }
        } else if ("3".equals(type) && sq == null) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderRecommanded.aspx");
            if (!bookList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("荐购热点-近5年入藏复本平均量，查询成功").put("data", bookList);
            }
        } else if ("4".equals(type) && sq == null) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderRecommanded2.aspx");
            if (!bookList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("荐购热点-近5年入藏复本总借量，查询成功").put("data", bookList);
            }
        } else if ("5".equals(type) && sq == null) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderNewBook.aspx");
            if (!bookList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("新书热度-近90天内入藏复本总借量，查询成功").put("data", bookList);
            }
        } else if ("6".equals(type) && sq != null) {
            String res6 = redisUtil.get(HOT_BOOK + ":" + type + ":" + sq);
            if (res6 != null) {
                return R.ok("热点图书 - 缓存获取成功").put("data", JSON.parseArray(res6, Book.class));
            }
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderHot3.aspx" + "?sq=" + sq);
            if (!bookList.isEmpty()) {
                redisUtil.set(HOT_BOOK + ":" + type + ":" + sq, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("专业热点-近2年内入藏复本总借量，查询成功").put("data", bookList);
            }
        }
        return R.error(400, "查询失败");
    }


    /**
     * 分类热点
     *
     * @param type 查询参数，type=1 近2年入藏复本平均量，type=2 近2年入藏复本总借量
     * @param cate 1.马克思主义、列宁主义、毛泽东思...A  \n  2.哲学、宗教...B  \n 3.社会科学总论...C  \n   " +
     *             "4.政治、法律...D  \n 5.军事...E  \n   6.经济...F  \n   7.文化、科学、教育、体育...G \n   8.语言、文字...H \n 9.文学...I  \n   " +
     *             "10.艺术...J  \n  11.历史、地理...K \n    12.自然科学总论...N  \n  13.数理科学和化学...O \n  14.天文学、地球科学...P  \n   " +
     *             "15.生物科学...Q  \n   16.医药、卫生...R  \n   17.农业科学...S \n  18.工业技术...T  \n 19.交通运输...U \n  20.航空、航天...V  \n   " +
     *             "21.环境科学、安全科学...X \n    22.综合性图书...Z
     * @return
     */
    @ApiOperation(value = "热点图书 - 分类热点", notes = "热点图书的接口")
    @GetMapping("/category")
    public R category(@RequestParam String type, @RequestParam String cate) {
        if (type == null || cate == null) {
            return R.error(400, "参数不能为空");
        }
        String res = redisUtil.get(CATE_HOT_BOOK + ":" + type + ":" + cate);
        if (res != null) {
            return R.ok("分类热点 - 缓存获取成功").put("data", JSON.parseArray(res, Book.class));
        }
        if ("1".equals(type)) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderHot4.aspx" + "?sq=" + cate);
            if (!bookList.isEmpty()) {
                redisUtil.set(CATE_HOT_BOOK + ":" + type + ":" + cate, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("分类热点-近2年入藏复本平均量，查询成功").put("data", bookList);
            }
        } else if ("2".equals(type)) {
            List<Book> bookList = librarySpiderService.hot("http://wxlib.djtu.edu.cn/br/ReaderHot.aspx" + "?sq=" + cate);
            if (!bookList.isEmpty()) {
                redisUtil.set(CATE_HOT_BOOK + ":" + type + ":" + cate, JSON.toJSONString(bookList), 60 * 60 * 24 * 3);
                return R.ok("分类热点-近2年入藏复本总借量，查询成功").put("data", bookList);
            }
        }
        return R.error(400, "查询失败");
    }

    /**
     * 图书分类法总类
     *
     * @return
     */
    @ApiOperation(value = "图书分类法总类", notes = "图书分类法总类的接口")
    @GetMapping("/bookCate")
    public R bookCate() {
        String res = redisUtil.get(BOOK_CATE);
        if (res != null) {
            return R.ok("图书分类法总类 - 缓存获取成功").put("data", JSON.parseArray(res, BookCateEntity.class));
        }
        List<BookCateEntity> bookCateList = bookCateService.list();
        if (!bookCateList.isEmpty()) {
            redisUtil.set(BOOK_CATE, JSON.toJSONString(bookCateList), 60 * 60 * 24 * 3);
            return R.ok("查询成功").put("data", bookCateList);
        }
        return R.error(400, "查询失败");
    }

    /**
     * 学院列表
     */
    @ApiOperation(value = "学院列表", notes = "学院列表的接口")
    @GetMapping("/college")
    public R college() {
        String res = redisUtil.get(COLLEGE_LIST);
        if (res != null) {
            return R.ok("学院列表 - 缓存获取成功").put("data", JSON.parseObject(res, ProfessionHot.class));
        }
        ProfessionHot college = librarySpiderService.college();
        if (!college.getList().isEmpty()) {
            redisUtil.set(COLLEGE_LIST, JSON.toJSONString(college), 60 * 60 * 24 * 3);
            return R.ok("查询成功").put("data", college);
        }
        return R.error(400, "查询失败");
    }

    /**
     * 专业热点里的专业/课程列表
     * major 为空则 http://wxlib.djtu.edu.cn/br/ReaderProfession.aspx?sq=材料科学
     * 否则 http://wxlib.djtu.edu.cn/br/ReaderFenLeiHao.aspx?zy=材料焊接&xy=材料科学
     *
     * @param college 查询参数，学院，如 材料科学
     * @param major   查询参数，专业，如 材料焊接
     * @return
     */
    @ApiOperation(value = "专业热点里的专业/课程列表", notes = "专业/课程列表的接口")
    @GetMapping("/major")
    public R major(@RequestParam String college, @RequestParam(required = false) String major) {
        if (college == null) {
            return R.error(400, "学院不能为空");
        }
        if (major == null) {
            String res = redisUtil.get(MAJOR_HOT + ":" + college);
            if (res != null) {
                return R.ok("专业热点里的专业/课程列表 - 缓存获取成功").put("data", JSON.parseObject(res, ProfessionHot.class));
            }
        } else {
            String res = redisUtil.get(MAJOR_HOT + ":" + college + ":" + major);
            if (res != null) {
                return R.ok("专业热点里的专业/课程列表 - 缓存获取成功").put("data", JSON.parseObject(res, ProfessionHot.class));
            }
        }

        ProfessionHot professionHot = librarySpiderService.major(college, major);
        if (!professionHot.getList().isEmpty()) {
            if (major == null) {
                redisUtil.set(MAJOR_HOT + ":" + college, JSON.toJSONString(professionHot), 60 * 60 * 24 * 3);
            } else {
                redisUtil.set(MAJOR_HOT + ":" + college + ":" + major, JSON.toJSONString(professionHot), 60 * 60 * 24 * 3);
            }
            return R.ok("查询成功").put("data", professionHot);
        }
        return R.error(400, "查询失败");
    }
}