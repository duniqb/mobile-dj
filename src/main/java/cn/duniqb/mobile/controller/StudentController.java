package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.User;
import cn.duniqb.mobile.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 与学生相关的接口
 *
 * @author duniqb
 */
@Api(value = "与学生相关的接口", tags = {"与学生相关的接口 Controller"})
@RestController
@RequestMapping("/api/v1/student/")
public class StudentController {
    @Autowired
    private StudentService studentService;

    /**
     * 根据学号获取学生信息
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "查询学生信息", notes = "查询学生信息的接口，请求体是 User，包含学号和密码")
    @ApiImplicitParam(name = "user", value = "请求对象 user，包含学号和密码", required = true, dataType = "User", paramType = "body")
    @PostMapping("info")
    public JSONResult info(@RequestBody User user) {
        Student student = studentService.selectOneByNo(user.getUsername());
        if (student != null) {
            return JSONResult.build(student, "获取成功", 200);
        }

        return JSONResult.build(null, "获取失败", 400);
    }
}
