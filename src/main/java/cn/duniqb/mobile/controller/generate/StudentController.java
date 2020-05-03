package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.StudentEntity;
import cn.duniqb.mobile.service.StudentService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 学籍信息表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与学籍信息相关的接口"})
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:student:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = studentService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{stuNo}")
    // @RequiresPermissions("mobile:student:info")
    public R info(@PathVariable("stuNo") String stuNo) {
        StudentEntity student = studentService.getById(stuNo);

        return R.ok().put("student", student);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:student:save")
    public R save(@RequestBody StudentEntity student) {
        studentService.save(student);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:student:update")
    public R update(@RequestBody StudentEntity student) {
        studentService.updateById(student);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:student:delete")
    public R delete(@RequestBody String[] stuNos) {
        studentService.removeByIds(Arrays.asList(stuNos));

        return R.ok();
    }

}
