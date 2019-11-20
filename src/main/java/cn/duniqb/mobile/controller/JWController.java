package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.User;
import cn.duniqb.mobile.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 与登录教务相关的接口
 *
 * @author duniqb
 */
@Api(value = "与登录教务相关的接口", tags = {"与登录教务相关的接口 Controller"})
@Scope("session")
@RestController
@RequestMapping("/api/v1/jw")
public class JWController {
    @Autowired
    private SpiderService spiderService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentCourseService studentCourseService;

    private CookieStore cookieStore = null;
    private final String URL = "http://localhost:8080/";

    /**
     * 进入登录页面时或点击刷新，返回一个验证码
     */
    @ApiOperation(value = "获取验证码", notes = "无需参数，但获取验证码的客户端应当和登录的客户端一致，否则无效，即同一个 Session")
    @GetMapping("verify")
    public JSONResult getVerifyCode() {
        // 获取验证码并保存到本地
        String fileName = saveVerifyCode();
        String imgUrl = "verify/" + fileName + ".jpg";
        return JSONResult.build(URL + imgUrl, "验证码获取成功", 200);
    }

    /**
     * 登录教务
     * 此登录总是获取最新的信息，以便在前端要求清空缓存时使用
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "登录教务", notes = "登录教务的接口，请求体是 User，包含学号，密码和验证码")
    @ApiImplicitParam(name = "user", value = "请求对象 user，包含学号，密码和验证码", required = true, dataType = "User", paramType = "body")
    @PostMapping("login")
    public JSONResult login(@RequestBody User user) {
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        Map<Integer, Object> map = new HashMap<>();
        Student student = studentService.selectOneByNo(user.getUsername());
        if (student != null) {
            return JSONResult.build(user.getUsername(), "学生已存在", 400);
        }
        HttpGet getLoginPage = new HttpGet("http://202.199.128.21/academic/common/security/login.jsp");

        getLoginPage.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getLoginPage.setHeader("Accept-Encoding", "gzip, deflate");
        getLoginPage.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        getLoginPage.setHeader("Connection", "keep-alive");
        getLoginPage.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        try {
            client.execute(getLoginPage);
            // 构造 POST 参数
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("groupId", null));
            postData.add(new BasicNameValuePair("j_username", user.getUsername()));
            postData.add(new BasicNameValuePair("j_password", user.getPassword()));
            postData.add(new BasicNameValuePair("j_captcha", user.getVerifyCode()));

            HttpPost post = new HttpPost("http://202.199.128.21/academic/j_acegi_security_check");
            post.setEntity(new UrlEncodedFormEntity(postData));
            HttpResponse response = client.execute(post);

            // 响应中不包含 error 字符，认为是成功
            if (!response.toString().contains("error")) {
                Map<Integer, String> info = spiderService.getInfo(cookieStore, user.getPassword());
                map.put(1, info);
                Map<Integer, String> scoreParam = spiderService.getScoreParam(cookieStore, user.getUsername());
                map.put(2, scoreParam);
                Map<Integer, String> gradeExam = spiderService.getGradeExam(cookieStore, user.getUsername());
                map.put(3, gradeExam);
                cookieStore.clear();
                return JSONResult.build(map, "教务登录成功", 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cookieStore != null) {
            cookieStore.clear();
        }
        return JSONResult.build(null, "教务登录失败", 400);
    }

    /**
     * 清空该学生的已存在所有数据
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "清空学生信息", notes = "清空学生信息的接口，请求体是 User，包含学号，密码")
    @ApiImplicitParam(name = "user", value = "请求对象 user，包含学号，密码", required = true, dataType = "User", paramType = "body")
    @PostMapping("clear")
    public JSONResult clear(@RequestBody User user) {
        if (user.getUsername() != null) {
            Student student = studentService.selectOneByNo(user.getUsername());
            if (student == null) {
                return JSONResult.build(user.getUsername(), "学生不存在", 400);
            }
        }

        if (user.getUsername() != null && user.getPassword() != null) {
            Student studentUser = studentService.selectOneByStudent(user);
            if (studentUser == null) {
                return JSONResult.build(user.getUsername(), "学号/密码错误", 400);
            } else {
                Map<Integer, Object> map = new HashMap<>();

                int i1 = creditService.deleteByStuNo(user.getUsername());
                map.put(1, "清空了 " + i1 + " 条学分信息");
                int i2 = scoreService.deleteByStuNo(user.getUsername());
                map.put(2, "清空了 " + i2 + " 条成绩信息");
                int i3 = studentCourseService.deleteByStuNo(user.getUsername());
                map.put(3, "清空了 " + i3 + " 条选课信息");
                int i4 = studentService.deleteByStuNo(user.getUsername());
                map.put(4, "清空了 " + i4 + " 条学生信息");

                return JSONResult.build(map, "清空成功", 200);
            }
        }

        return JSONResult.build(null, "清空失败", 400);
    }

    /**
     * 从教务获取验证码，并保存到本地
     *
     * @return 保存的唯一名字
     */
    private String saveVerifyCode() {
        HttpGet getVerifyCode = new HttpGet("http://202.199.128.21/academic/getCaptcha.do");
        FileOutputStream fileOutputStream = null;
        String fileName = System.currentTimeMillis() + "";
        // 把验证码图片保存到本地
        try {
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(getVerifyCode);
            setCookieStore(response);
            fileOutputStream = new FileOutputStream(new File("D:\\verify\\" + fileName + ".jpg"));
            response.getEntity().writeTo(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileOutputStream != null;
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 设置 Cookie
     *
     * @param httpResponse
     */
    private void setCookieStore(HttpResponse httpResponse) {
        cookieStore = new BasicCookieStore();
        // JSESSIONID
        String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
        String JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
        cookie.setVersion(0);
        cookie.setDomain("202.199.128.21");
        cookie.setPath("/academic");
        cookieStore.addCookie(cookie);
    }
}
