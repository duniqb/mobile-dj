package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.CardInfo;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.utils.spider.CardSpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 与一卡通相关的接口
 *
 * @author duniqb
 */
@Api(value = "与一卡通相关的接口", tags = {"与一卡通相关的接口"})
@Scope("session")
@RestController
@RequestMapping("/api/v1/card/")
public class CardController {
    @Autowired
    private CardSpiderService cardSpiderService;

    private CookieStore cookieStore = null;

    /**
     * 验证码存放的本机文件夹
     */
    @Value("${local.verifyPath}")
    private String verifyPath;


    /**
     * 本机 url，以供回传验证码地址
     */
    @Value("${local.host}")
    private String localhost;


    /**
     * 进入登录页面时或点击刷新，返回一个验证码
     */
    @ApiOperation(value = "获取验证码", notes = "无需参数，但获取验证码的客户端应当和登录的客户端一致，否则无效，即同一个 Session")
    @GetMapping("verify")
    public JSONResult getVerifyCode() {
        // 获取验证码并保存到本地
        String fileName = saveVerifyCode();
        String imgUrl = "verify/" + fileName + ".jpg";
        return JSONResult.build(localhost + imgUrl, "验证码获取成功", 200);
    }

    /**
     * 登录一卡通
     *
     * @return
     */
    @ApiOperation(value = "登录一卡通", notes = "登录一卡通的接口，请求体是 User，包含学号，密码和验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stuNo", value = "学号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("login")
    public JSONResult login(@RequestParam String stuNo, @RequestParam String password, @RequestParam String verifyCode) {
        try {
            HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair("signtype", "SynSno"));
            postData.add(new BasicNameValuePair("username", stuNo));
            postData.add(new BasicNameValuePair("password", password));
            postData.add(new BasicNameValuePair("checkcode", verifyCode));
            postData.add(new BasicNameValuePair("isUsedKeyPad", "false"));

            HttpPost post = new HttpPost("http://ykt.djtu.edu.cn/Account/MiniCheckIn");
            post.setEntity(new UrlEncodedFormEntity(postData));

            // 重要的 Header
            post.setHeader("Accept", "*/*");
            post.setHeader("Accept-Encoding", "gzip, deflate");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Host", "ykt.djtu.edu.cn");
            post.setHeader("Origin", "http://ykt.djtu.edu.cn");
            post.setHeader("Referer", "http://ykt.djtu.edu.cn");
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");
            post.setHeader("X-Requested-With", "XMLHttpRequest");

            HttpResponse response = client.execute(post);
            System.out.println(response);
            if (response.toString().contains("200")) {
                CardInfo info = cardSpiderService.info(cookieStore);
                if (info != null) {
                    return JSONResult.build(info, "一卡通登录成功", 200);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONResult.build(null, "一卡通登录失败", 400);
    }

    /**
     * 从一卡通获取验证码，并保存到本地
     *
     * @param
     */
    private String saveVerifyCode() {
        HttpGet getVerifyCode = new HttpGet("http://ykt.djtu.edu.cn/Account/GetCheckCodeImg" + "/Flag=" + System.currentTimeMillis());
        FileOutputStream fileOutputStream = null;
        String fileName = System.currentTimeMillis() + "";
        // 把验证码图片保存到本地
        try {
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(getVerifyCode);
            setCookieStore(response);
            fileOutputStream = new FileOutputStream(new File(verifyPath + fileName + ".jpg"));
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
        String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
        String sessionId = setCookie.substring("ASP.NET_SessionId=".length(), setCookie.indexOf(";"));
        BasicClientCookie cookie = new BasicClientCookie("ASP.NET_SessionId", sessionId);
        cookie.setVersion(0);
        cookie.setDomain("ykt.djtu.edu.cn");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
    }
}
