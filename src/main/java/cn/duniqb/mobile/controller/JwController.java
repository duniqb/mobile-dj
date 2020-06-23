package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.jw.Notice;
import cn.duniqb.mobile.dto.jw.NoticeList;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.interceptor.LogInterceptor;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.spider.JwSpiderService;
import cn.duniqb.mobile.utils.HttpUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * 与登录教务相关的接口
 *
 * @author duniqb
 */
@Api(tags = {"与登录教务相关的接口"})
@RestController
@RequestMapping("/jw")
public class JwController {
    @Autowired
    private JwSpiderService jWSpiderService;

    @Autowired
    private WxUserService wxUserService;


    @Autowired
    private RedisUtil redisUtil;

    /**
     * 通知列表在 Redis 里的前缀
     */
    private static final String NOTICE_LIST = "NOTICE_LIST";

    /**
     * 通知详情在 Redis 里的前缀
     */
    private static final String NOTICE_DETAIL = "NOTICE_DETAIL";

    /**
     * Cookie 在 Redis 里的前缀
     */
    private static final String COOKIE = "COOKIE";

    /**
     * 教务主机 ip
     */
    @Value("${jw.host}")
    private String host;

    /**
     * 本机 url，以供回传验证码地址
     */
    @Value("${local.host}")
    private String localhost;

    /**
     * 设置验证码存放路径
     */
    @Value("${local.verifyPath}")
    private String verifyPath;

    /**
     * 检测验证码是否正确
     *
     * @return
     */
    @ApiOperation(value = "登录教务", notes = "登录教务的接口，包含学号，密码和验证码")
    @GetMapping("/check")
    public void checkCaptcha(@RequestParam String verifyCode) {

        String url = "http://" + host + "/academic/checkCaptcha.do?captchaCode=" + verifyCode;

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();
        FormBody requestBody = new FormBody.Builder()
//                .add("captchaCode", verifyCode)
                .build();

        // 构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
//                .addHeader("Cache-Control", "max-age=0")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Content-Length", String.valueOf(requestBody.contentLength()))
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .addHeader("Cookie", cookie)
//                .addHeader("Host", "jw.djtu.edu.cn")
//                .addHeader("Origin", "http://jw.djtu.edu.cn")
//                .addHeader("Upgrade-Insecure-Requests", "1")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                System.out.println(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录教务获取学生信息与 WxUser 关联
     *
     * @return
     */
    @ApiOperation(value = "登录教务", notes = "登录教务的接口，包含学号，密码和验证码")
    @GetMapping("/login")
    public R login(@RequestParam String stuNo, @RequestParam String password, @RequestParam String verifyCode, @RequestParam String sessionId) {
        // 拿到该用户的 Cookie
        String cookie = redisUtil.get(COOKIE + ":" + sessionId);
        System.out.println("Redis 获取到的 cookie:" + cookie);
        System.out.println("stuNo:" + stuNo);
        System.out.println("password:" + password);
        System.out.println("verifyCode:" + verifyCode);
        System.out.println("sessionId:" + sessionId);

        String url = "http://" + host + "/academic/j_acegi_security_check";

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();
        FormBody requestBody = new FormBody.Builder()
                .add("j_username", stuNo)
                .add("j_password", password)
                .add("j_captcha", verifyCode)
                .build();

        // 构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
//                .addHeader("Cache-Control", "max-age=0")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Content-Length", String.valueOf(requestBody.contentLength()))
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", cookie)
//                .addHeader("Host", "jw.djtu.edu.cn")
//                .addHeader("Origin", "http://jw.djtu.edu.cn")
//                .addHeader("Upgrade-Insecure-Requests", "1")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                // 此处应该存储cookie，以便其他请求携带cookie访问
//                System.out.println(doc);
                // 教务账号和微信账号关联
                // 根据 sessionId 获取 openid
                String sessionIdValue = redisUtil.get(sessionId);
                if (sessionIdValue != null) {
                    String openid = sessionIdValue.split(":")[0];
                    // 检测是否存在，存在则更新
                    WxUserEntity wxUser = wxUserService.getById(openid);
                    if (wxUser != null) {
                        wxUser.setStuNo(stuNo);
                        boolean update = wxUserService.updateById(wxUser);
                        if (update) {
                            return R.ok("教务账号和微信账号关联成功");
                        } else {
                            return R.error(400, "教务账号和微信账号关联失败");
                        }
                    }
                }
                return R.ok("教务登录成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.error(400, "教务登录失败");

    }

    /**
     * 进入登录页面时或点击刷新，返回一个验证码
     */
    @ApiOperation(value = "获取验证码")
    @GetMapping("/verify")
    public R getVerifyCode(@RequestParam String sessionId) {
        // 获取验证码并保存到本地
        String fileName = saveVerifyCode(sessionId);
        String imgUrl = "api/v2/verify/" + fileName + ".jpg";
        return R.ok("验证码获取成功").put("url", localhost + imgUrl);
    }

    /**
     * 从教务获取验证码，并保存到本地
     *
     * @return 保存的图片的唯一名字
     */
    private String saveVerifyCode(String sessionId) {
        String url = "http://" + host + "/academic/getCaptcha.do";
        String fileName = System.currentTimeMillis() + "";

        try {
            try (Response response = HttpUtils.get(url, null)) {
                if (response.code() == 200) {
                    // 取得 Cookie 信息并保存
                    Headers headers = response.headers();
                    String cookie = Objects.requireNonNull(headers.get("Set-Cookie")).split(";")[0];
                    redisUtil.set(COOKIE + ":" + sessionId, cookie, 60 * 60);
                    System.out.println(cookie);

                    // 构造文件对象
                    File file = new File(verifyPath + fileName + ".jpg");
                    InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
                    OutputStream outputStream = new FileOutputStream(file);
                    byte[] byteStr = new byte[1024];
                    int len;
                    while ((len = inputStream.read(byteStr)) > 0) {
                        outputStream.write(byteStr, 0, len);
                    }
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 获取通知列表
     *
     * @throws Exception
     */
    @ApiOperation(value = "获取通知列表", notes = "获取通知列表")
    @GetMapping("noticeList")
    public R noticeList(String page) {
        String res = redisUtil.get(NOTICE_LIST + ":" + page);
        if (res != null) {
            return R.ok("通知列表 - 缓存获取成功").put("data", JSON.parseObject(res, NoticeList.class));
        }
        NoticeList noticeList = jWSpiderService.noticeList(page);
        if (!noticeList.getList().isEmpty()) {
            redisUtil.set(NOTICE_LIST + ":" + page, JSON.toJSONString(noticeList), 60 * 60 * 12);
            return R.ok("获取通知列表成功").put("data", noticeList);
        }
        return R.error(400, "获取通知列表失败");
    }

    /**
     * 获取通知详情
     *
     * @throws Exception
     */
    @ApiOperation(value = "获取通知详情", notes = "获取通知详情")
    @GetMapping("/notice")
    public R notice(String id) {
        if ("null" .equals(id)) {
            return R.error(400, "获取通知详情失败");
        }
        String res = redisUtil.get(NOTICE_DETAIL + ":" + id);
        if (res != null) {
            return R.ok("通知详情 - 缓存获取成功").put("notice", JSON.parseObject(res, Notice.class));
        }
        Notice notice = jWSpiderService.notice(id);
        if (notice != null && !notice.getContent().isEmpty()) {
            redisUtil.set(NOTICE_DETAIL + ":" + id, JSON.toJSONString(notice), 60 * 60 * 24);
            return R.ok("获取通知详情成功").put("notice", notice);
        }
        return R.error(400, "获取通知详情失败");
    }
}

