package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.BookCateEntity;
import cn.duniqb.mobile.service.BookCateService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 图书分类法总类
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与图书分类相关的接口"})
@RestController
@RequestMapping("/bookcate")
public class BookCateController {
    @Autowired
    private BookCateService bookCateService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 图书列表在 Redis 里的前缀
     */
    private static final String BOOK_CATE_LIST = "BOOK_CATE_LIST";

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:bookcate:list")
    public R list(@RequestParam Map<String, Object> params) {
        String res = redisUtil.get(BOOK_CATE_LIST);
        if (res != null) {
            return R.ok().put("图书列表 - 缓存获取成功", JSON.parseObject(res, PageUtils.class));
        }
        PageUtils page = bookCateService.queryPage(params);
        if (page != null) {
            redisUtil.set(BOOK_CATE_LIST, JSON.toJSONString(page), 60 * 60 * 24);
            return R.ok().put("图书列表 - 获取成功", page);
        }
        return R.ok().put("图书列表 - 获取失败", 400);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:bookcate:info")
    public R info(@PathVariable("id") Integer id) {
        BookCateEntity bookCate = bookCateService.getById(id);

        return R.ok().put("bookCate", bookCate);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:bookcate:save")
    public R save(@RequestBody BookCateEntity bookCate) {
        bookCateService.save(bookCate);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:bookcate:update")
    public R update(@RequestBody BookCateEntity bookCate) {
        bookCateService.updateById(bookCate);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:bookcate:delete")
    public R delete(@RequestBody Integer[] ids) {
        bookCateService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
