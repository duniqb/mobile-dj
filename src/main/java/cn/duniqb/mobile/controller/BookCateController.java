package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.entity.BookCateEntity;
import cn.duniqb.mobile.service.BookCateService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
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
@RestController
@RequestMapping("mobile/bookcate")
public class BookCateController {
    @Autowired
    private BookCateService bookCateService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:bookcate:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = bookCateService.queryPage(params);

        return R.ok().put("page", page);
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
