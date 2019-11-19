package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.User;
import cn.duniqb.mobile.service.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
 * 教务
 *
 * @author duniqb
 */
@Scope("session")
@RestController
@RequestMapping("/api/v1/")
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

    /**
     * 此处有问题，虽然将 Controller 实例改为 Session，但此处仍然是静态的，所有实例共享。
     * 导致两次 Session 共享，从而让第一次的失效
     */
    private static CloseableHttpClient client = HttpClients.createDefault();

    private final String URL = "http://localhost:8080/";

    /**
     * 进入登录页面时或点击刷新，返回一个验证码
     */
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
    @PostMapping("loginjw")
    public JSONResult login(@RequestBody User user) {
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

            if (!response.toString().contains("error")) {
                Map<Integer, String> info = spiderService.getInfo(client);
                map.put(1, info);
                Map<Integer, String> scoreParam = spiderService.getScoreParam(client, user.getUsername());
                map.put(2, scoreParam);
                Map<Integer, String> gradeExam = spiderService.getGradeExam(client, user.getUsername());
                map.put(3, gradeExam);
                return JSONResult.build(map, "教务登录成功", 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONResult.build(null, "教务登录失败", 400);
    }

    /**
     * 从教务获取验证码，并保存到本地
     *
     * @return 保存的唯一名字
     */
    private String saveVerifyCode() {
        HttpGet getVerifyCode = new HttpGet("http://202.199.128.21/academic/getCaptcha.do");
        FileOutputStream fileOutputStream = null;
        client = HttpClients.createDefault();
        String fileName = System.currentTimeMillis() + "";
        // 把验证码图片保存到本地
        try {
            HttpResponse response = client.execute(getVerifyCode);
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
     * 清空该学生的已存在所有数据
     *
     * @param user
     * @return
     */
    @PostMapping("clear")
    public JSONResult clear(@RequestBody User user) {
        Student student = studentService.selectOneByNo(user.getUsername());
        if (student == null) {
            return JSONResult.build(user.getUsername(), "学生不存在", 400);
        }
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
