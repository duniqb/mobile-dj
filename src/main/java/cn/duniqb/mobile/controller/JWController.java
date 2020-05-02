package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.jw.Notice;
import cn.duniqb.mobile.dto.jw.NoticeList;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.StudentService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.spider.JWSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

/**
 * 与登录教务相关的接口
 *
 * @author duniqb
 */
@Api(value = "与登录教务相关的接口", tags = {"与登录教务相关的接口"})
@Scope("session")
@RestController
@RequestMapping("/jw")
public class JWController {
    @Autowired
    private JWSpiderService jWSpiderService;

    @Autowired
    private StudentService studentService;

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
     * 本机 url，以供回传验证码地址
     */
    @Value("${local.host}")
    private String localhost;

    /**
     * 教务主机 ip
     */
    @Value("${jw.host}")
    private String host;

    /**
     * 设置验证码存放路径
     */
    @Value("${local.verifyPath}")
    private String verifyPath;

    /**
     * 进入登录页面时或点击刷新，返回一个验证码
     */
    @ApiOperation(value = "获取验证码", notes = "无需参数，但获取验证码的客户端应当和登录的客户端一致，否则无效，即同一个 Session")
    @GetMapping("/verify")
    public R getVerifyCode() {
        // 获取验证码并保存到本地
        String fileName = saveVerifyCode();
        String imgUrl = "verify/" + fileName + ".jpg";
        return R.ok().put("验证码获取成功", localhost + imgUrl);
    }

    /**
     * 登录教务
     * 此登录总是获取最新的信息，以便在前端要求清空缓存时使用
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @ApiOperation(value = "登录教务", notes = "登录教务的接口，包含学号，密码和验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stuNo", value = "学号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("/login")
    public R login(@RequestParam String stuNo, @RequestParam String password, @RequestParam String verifyCode, @RequestParam String sessionId) {

        String url = "http://" + host + "/academic/j_acegi_security_check";

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("groupId", null)
                .add("j_username", stuNo)
                .add("j_password", password)
                .add("j_captcha", verifyCode)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                // 此处应该存储cookie


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
                            return R.ok().put("教务账号和微信账号关联失败", 400);
                        }
                    }
                }
                return R.ok("教务登录成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.ok().put("教务登录失败", null);
    }


    /**
     * 从教务获取验证码，并保存到本地
     *
     * @return 保存的唯一名字
     */
    private String saveVerifyCode() {
//        HttpGet getVerifyCode = new HttpGet("http://" + host + "/academic/getCaptcha.do");
//        FileOutputStream fileOutputStream = null;
//        String fileName = System.currentTimeMillis() + "";
//        // 把验证码图片保存到本地
//        try {
//            HttpClient client = HttpClients.createDefault();
//            HttpResponse response = client.execute(getVerifyCode);
//            setCookieStore(response);
//            fileOutputStream = new FileOutputStream(new File(verifyPath + fileName + ".jpg"));
//            response.getEntity().writeTo(fileOutputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                assert fileOutputStream != null;
//                fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return fileName;
        return null;
    }


    /**
     * 获取通知列表
     *
     * @throws Exception
     */
    @ApiOperation(value = "获取通知列表", notes = "获取通知列表")
    @ApiImplicitParam(name = "/page", value = "页数", required = true, dataType = "String", paramType = "query")
    @GetMapping("noticeList")
    public R noticeList(String page) {
        String res = redisUtil.get(NOTICE_LIST + ":" + page);
        if (res != null) {
            return R.ok().put("通知列表 - 缓存获取成功", JSON.parseObject(res, NoticeList.class));
        }
        NoticeList noticeList = jWSpiderService.noticeList(page);
        if (!noticeList.getList().isEmpty()) {
            redisUtil.set(NOTICE_LIST + ":" + page, JSON.toJSONString(noticeList), 60 * 60 * 12);
            return R.ok().put("获取通知列表成功", noticeList);
        }
        return R.ok().put("获取通知列表失败", null);
    }

    /**
     * 获取通知详情
     *
     * @throws Exception
     */
    @ApiOperation(value = "获取通知详情", notes = "获取通知详情")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query")
    @GetMapping("/notice")
    public R notice(String id) {
        if ("null".equals(id)) {
            return R.ok().put("获取通知详情失败", null);
        }
        String res = redisUtil.get(NOTICE_DETAIL + ":" + id);
        if (res != null) {
            return R.ok().put("通知详情 - 缓存获取成功", JSON.parseObject(res, Notice.class));
        }
        Notice notice = jWSpiderService.notice(id);
        if (notice != null && !notice.getContent().isEmpty()) {
            redisUtil.set(NOTICE_DETAIL + ":" + id, JSON.toJSONString(notice), 60 * 60 * 24);
            return R.ok().put("获取通知详情成功", notice);
        }
        return R.ok().put("获取通知详情失败", 400);
    }
}

