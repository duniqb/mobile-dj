package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.Credit;
import cn.duniqb.mobile.domain.GradeExam;
import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.ScoreDto;
import cn.duniqb.mobile.service.CreditService;
import cn.duniqb.mobile.service.GradeExamService;
import cn.duniqb.mobile.service.ScoreService;
import cn.duniqb.mobile.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 与学生相关的接口
 *
 * @author duniqb
 */
@Api(value = "与学生相关的接口", tags = {"与学生相关的接口"})
@RestController
@RequestMapping("/api/v1/student/")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private GradeExamService gradeExamService;

    @Autowired
    private ScoreService scoreService;

    /**
     * 根据学号获取学生信息
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "查询学生信息", notes = "查询学生信息的接口，请求参数是学号")
    @ApiImplicitParam(name = "username", value = "学号", required = true, dataType = "String", paramType = "query")
    @GetMapping("info")
    public JSONResult info(@RequestParam String username) {
        Student student = studentService.selectOneByNo(username);
        if (student != null) {
            return JSONResult.build(student, "获取信息成功", 200);
        }
        return JSONResult.build(null, "获取信息失败", 400);
    }

    /**
     * 根据学号获取学生学分
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "查询学生学分", notes = "查询学生学分的接口，请求参数是学号")
    @ApiImplicitParam(name = "username", value = "学号", required = true, dataType = "String", paramType = "query")
    @GetMapping("credit")
    public JSONResult credit(@RequestParam String username) {
        Credit credit = creditService.selectOneByNo(username);
        if (credit != null) {
            return JSONResult.build(credit, "获取学分成功", 200);
        }
        return JSONResult.build(null, "获取学分失败", 400);
    }

    /**
     * 根据学号获取等级考试
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "查询学生等级考试", notes = "查询学生等级考试的接口，请求参数是学号")
    @ApiImplicitParam(name = "username", value = "学号", required = true, dataType = "String", paramType = "query")
    @GetMapping("grade")
    public JSONResult gradeExam(@RequestParam String username) {
        List<GradeExam> gradeExamList = gradeExamService.selectOneByStuNo(username);
        if (!gradeExamList.isEmpty()) {
            return JSONResult.build(gradeExamList, "获取等级考试成功", 200);
        }
        return JSONResult.build(null, "获取等级考试失败", 400);
    }

    /**
     * 根据学号，学年，学期获取分数
     *
     * @param username
     * @param year
     * @param term
     * @return
     */
    @ApiOperation(value = "查询学生分数", notes = "查询学生分数的接口，请求参数是学号，学年，学期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "学号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "year", value = "学年", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "term", value = "学期 0-春、1-秋", dataType = "Integer", paramType = "query")
    })
    @GetMapping("score")
    public JSONResult score(@RequestParam String username,
                            @RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer term) {
        // 按照学号查询全部成绩
        if (year == null && term == null) {
            List<ScoreDto> scoreDtoList = scoreService.selectOneByStuNo(username);
            if (!scoreDtoList.isEmpty()) {
                return JSONResult.build(scoreDtoList, "获取学生分数成功", 200);
            }
        }
        // 按照学号 + 学年查询成绩
        if (year != null && term == null) {
            List<ScoreDto> scoreDtoList = scoreService.selectOneByStuNoYear(username, year);
            if (!scoreDtoList.isEmpty()) {
                return JSONResult.build(scoreDtoList, "获取学生分数成功", 200);
            }
        }
        // 按照学号 + 学年 + 学期查询成绩
        if (year != null) {
            List<ScoreDto> scoreDtoList = scoreService.selectOneByStuNoYearTerm(username, year, term);
            if (!scoreDtoList.isEmpty()) {
                return JSONResult.build(scoreDtoList, "获取学生分数成功", 200);
            }
        }
        return JSONResult.build(null, "获取学生分数失败", 400);
    }
}
