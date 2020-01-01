package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.domain.WxUser;
import cn.duniqb.mobile.dto.Code2Session;
import cn.duniqb.mobile.dto.json.JSONResult;
import cn.duniqb.mobile.dto.tip.Tip;
import cn.duniqb.mobile.dto.tip.TipDto;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.spider.MiniSpiderService;
import cn.duniqb.mobile.utils.MobileUtil;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 与小程序相关的接口
 *
 * @author duniqb
 */
@Api(value = "与小程序相关的接口", tags = {"与小程序相关的接口"})
@RestController
@RequestMapping("/api/v1/mini/")
public class MiniController {
    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MiniSpiderService miniSpiderService;

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
     * Session_key 在 Redis 里前缀
     */
    private static final String SESSION_ID = "SESSION_ID";

    /**
     * 提示信息 在 Redis 里前缀
     */
    private static final String TIP = "TIP";

    /**
     * 此登录接口只有在首次使用或登录过期时才使用
     * <p>
     * 自定义登录态，使用 Redis 的随机字符串来作为 SessionId
     * 调用 auth.code2Session 接口，换取 用户唯一标识 OpenID 和 会话密钥 session_key
     * 以后每次调用业务接口，都根据 sessionId 的值 sessionKey 是否存在，不存在提示重新登录
     *
     * @param code
     * @return
     */
    @GetMapping("login")
    @ApiOperation(value = "登录小程序", notes = "获取登录态的接口，请求参数是 code")
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

            // 查询是否第一次登录，是则插入用户，并添加 Session_key
            WxUser wxUser = wxUserService.selectByOpenid(code2Session.getOpenid());
            String sessionId = SESSION_ID + ":" + UUID.randomUUID().toString().replace("-", "");
            if (wxUser == null) {
                WxUser wxUserNew = new WxUser();
                wxUserNew.setOpenid(code2Session.getOpenid());
                wxUserNew.setUnionid(code2Session.getUnionid());
                int i = wxUserService.insertWxUser(wxUserNew);
                if (i > 0) {
                    // 以 随机串 为 key，openid:session_key 为 value 组成键值对并存到缓存当中，24 小时过期
                    String value = code2Session.getOpenid() + ":" + code2Session.getSession_key();
                    redisUtil.set(sessionId, value, 60 * 60 * 24);
                    return JSONResult.build(sessionId, "首次登录成功", 200);
                }
            }
            // 否则更新 Session_key
            else {
                String value = code2Session.getOpenid() + ":" + code2Session.getSession_key();
                redisUtil.set(sessionId, value, 60 * 60 * 24);
                return JSONResult.build(sessionId, "登录成功，已更新 Session_key", 200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONResult.build(null, "登录失败", 400);
    }

    /**
     * 解密微信加密数据
     *
     * @param sessionId
     * @param iv
     * @param encryptData
     * @return
     */
    @PostMapping("decrypt")
    @ApiOperation(value = "解密数据", notes = "解密数据的接口，请求参数是 sessionId，iv，encryptData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "登录态保持 id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "iv", value = "偏移向量", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "encryptData", value = "被加密的数据", required = true, dataType = "String", paramType = "query")
    })
    public JSONResult decrypt(String sessionId, String iv, String encryptData) {
        String value = redisUtil.get(sessionId);
        if (value == null) {
            return JSONResult.build(null, "解密失败", 400);
        }
        String sessionKey = value.split(":")[1];
        String decrypt = MobileUtil.decrypt(sessionKey, iv, encryptData);
        return JSONResult.build(decrypt, "解密成功", 200);
    }

    /**
     * 检查登录状态
     *
     * @param sessionId
     * @return
     */
    @GetMapping("session")
    @ApiOperation(value = "检查登录是否有效", notes = "检查登录是否有效的接口，请求参数是 sessionId")
    @ApiImplicitParam(name = "sessionId", value = "sessionId", required = true, dataType = "String", paramType = "query")
    public JSONResult session(@RequestParam String sessionId) {
        String value = redisUtil.get(sessionId);
        if (value == null) {
            return JSONResult.build(null, "未登录", 400);
        }
        return JSONResult.build(null, "已登录", 200);
    }

    /**
     * 提示信息，天气等
     * 女性返回化妆信息
     *
     * @param sessionId 用于判断男女
     * @param province
     * @param city
     * @return
     */
    @GetMapping("tip")
    @ApiOperation(value = "提示信息", notes = "提示信息的接口，请求参数是 sessionId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "sessionId", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "市", required = true, dataType = "String", paramType = "query"),
    })
    public JSONResult tip(@RequestParam(required = false) String sessionId, @RequestParam String province, @RequestParam String city) {
        if ("undefined".equals(sessionId) || "undefined".equals(province) || "undefined".equals(city)) {
            return JSONResult.build(null, "获取提示失败", 400);
        }
        String res = redisUtil.get(TIP + ":" + province + ":" + city);
        if (res != null) {
            // 根据 sessionId 查询是男是女
            String sessionIdValue = redisUtil.get(sessionId);
            if (sessionIdValue != null) {
                String openid = sessionIdValue.split(":")[0];
                WxUser wxUser = wxUserService.selectByOpenid(openid);
                Tip tip = JSON.parseObject(res, Tip.class);

                TipDto tipDto = new TipDto();
                BeanUtils.copyProperties(tip, tipDto);

                List<String> list = new ArrayList<>();
                list.add(tip.getChill());
                list.add(tip.getClod());
                list.add(tip.getTip1());
                list.add(tip.getTip2());
                if (wxUser != null) {
                    Boolean gender = wxUser.getGender();
                    // false 男，true 女，女性增加化妆信息
                    if (gender != null && !gender) {
                        list.add(tip.getMakeup());
                    }
                }
                tipDto.setTips(list);
                return JSONResult.build(tipDto, "提示信息 - 缓存获取成功", 200);
            }

        }

        Tip tip = miniSpiderService.tip(province, city);
        TipDto tipDto = new TipDto();
        BeanUtils.copyProperties(tip, tipDto);

        List<String> list = new ArrayList<>();
        list.add(tip.getChill());
        list.add(tip.getClod());
        list.add(tip.getTip1());
        list.add(tip.getTip2());
        // 根据 sessionId 查询是男是女
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            WxUser wxUser = wxUserService.selectByOpenid(openid);

            if (wxUser != null) {
                Boolean gender = wxUser.getGender();
                // false 男，true 女，女性增加化妆信息
                if (gender != null && !gender) {
                    list.add(tip.getMakeup());
                }
            }
            tipDto.setTips(list);
            redisUtil.set(TIP + ":" + province + ":" + city, JSON.toJSONString(tip), 60 * 60);
            return JSONResult.build(tipDto, "获取提示成功", 200);
        } else {
            tipDto.setTips(list);
            return JSONResult.build(tipDto, "获取提示成功", 200);
        }
    }

    /**
     * 添加/更新用户
     * 要携带 SessionId
     *
     * @param
     * @return
     */
    @PostMapping("add")
    @ApiOperation(value = "添加用户", notes = "添加用户的接口，请求参数是 wxUser")
    @ApiImplicitParam(name = "wxUser", value = "微信账号信息", required = true, dataType = "WxUserDto", paramType = "body")
    public JSONResult add(@RequestParam String sessionId, @RequestParam String avatarUrl,
                          @RequestParam String city, @RequestParam String country,
                          @RequestParam Boolean gender, @RequestParam String language,
                          @RequestParam String nickName, @RequestParam String province) {
        // 根据 sessionId 获取 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            // 检测是否存在，存在则更新
            WxUser wxUser = wxUserService.selectByOpenid(openid);
            if (wxUser != null) {
                wxUser.setAvatarUrl(avatarUrl);
                wxUser.setCity(city);
                wxUser.setCountry(country);
                wxUser.setGender(gender);
                wxUser.setLanguage(language);
                wxUser.setNickname(nickName);
                wxUser.setProvince(province);
                wxUser.setCtime(new Date());

                int i = wxUserService.updateWxUser(wxUser);
                if (i > 0) {
                    return JSONResult.build(wxUser, "更新用户成功", 200);
                }
            }
            // 不存在则插入
            else {
                WxUser newWxUser = new WxUser();
                newWxUser.setOpenid(openid);
                newWxUser.setAvatarUrl(avatarUrl);
                newWxUser.setCity(city);
                newWxUser.setCountry(country);
                newWxUser.setGender(gender);
                newWxUser.setLanguage(language);
                newWxUser.setNickname(nickName);
                newWxUser.setProvince(province);
                newWxUser.setCtime(new Date());

                int i = wxUserService.insertWxUser(newWxUser);
                if (i > 0) {
                    return JSONResult.build(newWxUser, "新增用户成功", 200);
                }
            }
        }
        return JSONResult.build(null, "添加/更新用户失败", 400);
    }

    /**
     * 获取用户信息
     *
     * @param sessionId
     * @return
     */
    @GetMapping("query")
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息的接口，请求参数是 sessionId")
    @ApiImplicitParam(name = "sessionId", value = "sessionId", required = true, dataType = "String", paramType = "query")
    public JSONResult query(@RequestParam String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            WxUser wxUser = wxUserService.selectByOpenid(openid);
            if (wxUser != null) {
                return JSONResult.build(wxUser, "获取用户信息成功", 200);
            }
        }
        return JSONResult.build(null, "获取用户信息失败", 400);
    }

    /**
     * 检查是否真正的在数据库注册，而不仅是登录态
     *
     * @param sessionId
     * @return
     */
    @GetMapping("register")
    @ApiOperation(value = "检查是否真正的在数据库注册", notes = "检查是否真正的在数据库注册的接口，请求参数是 sessionId")
    @ApiImplicitParam(name = "sessionId", value = "sessionId", required = true, dataType = "String", paramType = "query")
    public JSONResult register(@RequestParam String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            WxUser wxUser = wxUserService.selectByOpenid(openid);
            if (wxUser != null) {
                // 已注册
                if (wxUser.getCtime() != null) {
                    return JSONResult.build(wxUser, "用户已注册", 200);
                }
            }
        }
        return JSONResult.build(null, "用户未注册", 400);
    }
}
