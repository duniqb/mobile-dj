package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.jw.GradeExam;
import cn.duniqb.mobile.dto.jw.Notice;
import cn.duniqb.mobile.dto.jw.NoticeList;
import cn.duniqb.mobile.dto.jw.Score;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.spider.JwSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
     * 查询教务登录是否过期
     */
    @ApiOperation(value = "查询教务登录是否过期")
    @GetMapping("/jwIsLogin")
    public R jwIsLogin(@RequestParam String sessionId) {
        String isLogin = redisUtil.get("JW_LOGIN:" + sessionId);
        if (isLogin == null) {
            return R.error(400, "登录过期");
        }
        return R.ok("教务已经登录").put("data", true);
    }

    /**
     * 查询等级考试
     */
    @ApiOperation(value = "查询等级考试")
    @GetMapping("/grade")
    public R getGradeExam(@RequestParam String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            QueryWrapper<WxUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", openid);
            WxUserEntity wxUser = wxUserService.getOne(queryWrapper);
            String stuNo = wxUser.getStuNo();
            List<GradeExam> gradeExamList = jWSpiderService.getGradeExam(stuNo, sessionId);
            if (gradeExamList == null) {
                return R.error(400, "验证码失效");
            }
            return R.ok("查询等级考试成功").put("data", gradeExamList);
        }
        return R.error(400, "查询等级考试失败");
    }

    /**
     * 查询成绩
     */
    @ApiOperation(value = "查询成绩")
    @GetMapping("/score")
    public R getScoreParam(@RequestParam String sessionId, @RequestParam(required = false) String year, @RequestParam(required = false) String term) {
        if (year == null || "-1".equals(year)) {
            year = "";
        } else {
            year = String.valueOf(Integer.parseInt(year) - 1980);
        }
        if (term == null || "-1".equals(term) || "0".equals(term)) {
            term = "";
        }
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            QueryWrapper<WxUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", openid);
            WxUserEntity wxUser = wxUserService.getOne(queryWrapper);
            String stuNo = wxUser.getStuNo();
            List<Score> scoreList = jWSpiderService.getScoreParam(stuNo, sessionId, year, term);
            if (scoreList == null) {
                return R.error(400, "验证码失效");
            }
            return R.ok("查询成绩成功").put("data", scoreList);
        }
        return R.error(400, "查询成绩失败");
    }

    /**
     * 获取个人信息与学分信息
     */
    @ApiOperation(value = "获取个人信息与学分信息")
    @GetMapping("/info")
    public R getInfo(@RequestParam String sessionId) {

        Map<Integer, Object> map = jWSpiderService.getInfo(sessionId);
        if (map != null) {
            return R.ok("个人信息与学分信息获取成功").put("data", map);

        }
        return R.error(400, "个人信息与学分信息获取失败");

    }

    /**
     * 登录教务获取学生信息与 WxUser 关联
     *
     * @return
     */
    @ApiOperation(value = "登录教务", notes = "登录教务的接口，包含学号，密码和验证码")
    @GetMapping("/login")
    public R login(@RequestParam String stuNo, @RequestParam String password, @RequestParam String verifyCode, @RequestParam String sessionId) {
        String url = "http://" + host + "/academic/j_acegi_security_check";
        // 初始化Cookie管理器
        CookieJar cookieJar = new CookieJar() {
            // Cookie缓存区
            private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
                String host = arg0.host();
                List<Cookie> cookiesList = cookiesMap.get(host);
                if (cookiesList != null) {
                    cookiesMap.remove(host);
                }
                //再重新天添加
                cookiesMap.put(host, arg1);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl arg0) {
                // TODO Auto-generated method stub
                List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                return cookiesList != null ? cookiesList : new ArrayList<>();
            }
        };
        String cookieFromRedis = redisUtil.get(COOKIE + ":" + sessionId);
        if (cookieFromRedis == null) {
            return R.error(400, "验证码无效");
        }

        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    // 禁止重定向
                    .followRedirects(false)
                    .followSslRedirects(false)

                    .cookieJar(cookieJar)
                    // 超时时间
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS).build();
            FormBody requestBody = new FormBody.Builder()
                    .add("j_username", stuNo)
                    .add("j_password", password)
                    .add("j_captcha", verifyCode)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Cookie", cookieFromRedis)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                    .addHeader("Connection", "keep-alive")

                    .build();
            //上传
            Call loginCall = okHttpClient.newCall(request);
            try (Response response = loginCall.execute()) {
                if (response.code() == 302) {
                    Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                    //获取返回数据的头部
                    Headers headers = response.headers();
                    System.out.println(headers.toString());
                    if (headers.get("Location").contains("error")) {
                        redisUtil.del(COOKIE + ":" + sessionId);
                        return R.error(400, "登录失败");
                    }
                    HttpUrl loginUrl = request.url();
                    List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
//                     存储到Cookie管理器中
                    okHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);

//                    从缓存中获取Cookie
                    List<Cookie> cookieOld = okHttpClient.cookieJar().loadForRequest(request.url());
                    for (Cookie cookie : cookieOld) {
                        redisUtil.set(COOKIE + ":" + sessionId, cookie.toString(), 60 * 60 * 24);
                    }

                    // 教务账号和微信账号关联
                    String sessionIdValue = redisUtil.get(sessionId);
                    if (sessionIdValue != null) {
                        String openid = sessionIdValue.split(":")[0];
                        // 检测是否存在，存在则更新
                        QueryWrapper<WxUserEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("openid", openid);
                        WxUserEntity wxUser = wxUserService.getOne(queryWrapper);
                        if (wxUser != null) {
                            wxUser.setStuNo(stuNo);
                            boolean update = wxUserService.updateById(wxUser);
                            if (update) {
                                redisUtil.set("JW_LOGIN:" + sessionId, LocalDateTime.now().toString(), 60 * 60 * 24);
                                return R.ok("教务账号和微信账号关联成功");
                            } else {
                                return R.error(400, "教务账号和微信账号关联失败");
                            }
                        }
                    }
                    return R.ok("教务登录成功");
                }
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

        // 初始化Cookie管理器
        CookieJar cookieJar = new CookieJar() {
            // Cookie缓存区
            private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
                String host = arg0.host();
                List<Cookie> cookiesList = cookiesMap.get(host);
                if (cookiesList != null) {
                    cookiesMap.remove(host);
                }
                //再重新天添加
                cookiesMap.put(host, arg1);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl arg0) {
                // TODO Auto-generated method stub
                List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                return cookiesList != null ? cookiesList : new ArrayList<>();
            }
        };

        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    // 超时时间
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS).build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                    .addHeader("Connection", "keep-alive")
                    .build();
            //上传
            Call loginCall = okHttpClient.newCall(request);
            try (Response response = loginCall.execute()) {
                if (response.code() == 200) {
                    //获取返回数据的头部
                    Headers headers = response.headers();
                    HttpUrl loginUrl = request.url();
                    List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
//                     存储到Cookie管理器中
                    okHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);

//                    从缓存中获取Cookie
                    List<Cookie> cookieOld = okHttpClient.cookieJar().loadForRequest(request.url());
                    for (Cookie cookie : cookieOld) {
                        redisUtil.set(COOKIE + ":" + sessionId, cookie.toString(), 60 * 60 * 24);
                    }

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
        if ("null".equals(id)) {
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

