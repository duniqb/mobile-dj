package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.WxUser;
import cn.duniqb.mobile.dto.Code2Session;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.MobileUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 与小程序相关的接口
 *
 * @author duniqb
 */
@Api(value = "与小程序相关的接口", tags = {"与小程序相关的接口"})
@Scope("session")
@RestController
@RequestMapping("/api/v1/mini/")
public class MiniController {
    @Autowired
    private WxUserService wxUserService;

    /**
     * 小程序配置
     */
    @Value("${mini.appId}")
    private String appId;

    /**
     * 小程序配置
     */
    @Value("${mini.secret}")
    private String secret;

    /**
     * 调用 auth.code2Session 接口，换取 用户唯一标识 OpenID 和 会话密钥 session_key
     *
     * @param code
     * @return
     */
    @GetMapping("login")
    @ApiImplicitParam(name = "code", value = "认证 code", required = true, dataType = "String", paramType = "query")
    public JSONResult login(@RequestParam String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HttpGet httpGet = new HttpGet(url + "?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code");
        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(httpGet);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            String json = doc.select("html body").first().text();
            Code2Session code2Session = JSON.parseObject(json, Code2Session.class);

            // 查询是否第一次登录，是则插入
            WxUser wxUser = wxUserService.selectByOpenid(code2Session.getOpenid());
            if (wxUser == null) {
                WxUser wxUserNew = new WxUser();
                wxUserNew.setOpenid(code2Session.getOpenid());
                wxUserNew.setUnionid(code2Session.getUnionid());
                wxUserNew.setSessionKey(code2Session.getSession_key());
                int i = wxUserService.insertWxUser(wxUserNew);
                if (i > 0) {
                    return JSONResult.build(code2Session, "首次登录成功", 200);
                }
            }
            // 否则更新 Session_key
            else {
                int i = wxUserService.updateSessionKeyByOpenid(code2Session.getOpenid(), code2Session.getSession_key());
                if (i > 0) {
                    return JSONResult.build(code2Session, "登录成功，已更新 Session_key", 200);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONResult.build(null, "登录失败", 400);
    }

    /**
     * 解密微信开放数据
     *
     * @param openid
     * @param iv
     * @param encryptData
     * @return
     */
    @PostMapping("decrypt")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openid", value = "openid", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iv", value = "偏移向量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "encryptData", value = "被加密的数据", required = true, dataType = "String", paramType = "query")
    })
    public JSONResult decrypt(String openid, String iv, String encryptData) {
        if (openid != null) {
            WxUser wxUser = wxUserService.selectByOpenid(openid);
            System.out.println("wxUser: " + wxUser);
            if (wxUser != null) {
                String sessionKey = wxUser.getSessionKey();
                if (sessionKey != null) {
                    String decrypt = MobileUtil.decrypt(sessionKey, iv, encryptData);
                    return JSONResult.build(decrypt, "解密成功", 200);
                }
            }
        }
        return JSONResult.build(null, "解密失败", 400);
    }
}
