package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.Code2Session;
import cn.duniqb.mobile.dto.tip.Tip;
import cn.duniqb.mobile.dto.tip.TipDto;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.spider.MiniSpiderService;
import cn.duniqb.mobile.utils.HttpUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * 与小程序相关的接口
 *
 * @author duniqb
 */
@Api(value = "与小程序相关的接口", tags = {"与小程序相关的接口"})
@RestController
@RequestMapping("/api/v2/mini/")
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
    public R login(@RequestParam String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";

        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("secret", secret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        try (Response response = HttpUtils.get(url, map)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                String json = doc.select("html body").first().text();
                Code2Session code2Session = JSON.parseObject(json, Code2Session.class);

                // 查询是否第一次登录，是则插入用户，并添加 Session_key
                WxUserEntity wxUser = wxUserService.getById(code2Session.getOpenid());
                String sessionId = SESSION_ID + ":" + UUID.randomUUID().toString().replace("-", "");
                if (wxUser == null) {
                    WxUserEntity wxUserNew = new WxUserEntity();
                    wxUserNew.setOpenid(code2Session.getOpenid());
                    wxUserNew.setUnionid(code2Session.getUnionid());
                    boolean saved = wxUserService.save(wxUserNew);
                    if (saved) {
                        // 以 随机串 为 key，openid:session_key 为 value 组成键值对并存到缓存当中，24 小时过期
                        String value = code2Session.getOpenid() + ":" + code2Session.getSession_key();
                        redisUtil.set(sessionId, value, 60 * 60 * 24);
                        return R.ok().put("首次登录成功", sessionId);
                    }
                }
                // 否则更新 Session_key
                else {
                    String value = code2Session.getOpenid() + ":" + code2Session.getSession_key();
                    redisUtil.set(sessionId, value, 60 * 60 * 24);
                    return R.ok().put("登录成功，已更新 Session_key", sessionId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.ok().put("登录失败", null);
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
    public R session(@RequestParam String sessionId) {
        String value = redisUtil.get(sessionId);
        if (value == null) {
            return R.ok().put("未登录", 400);
        }
        return R.ok().put("已登录", null);
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
    public R tip(@RequestParam(required = false) String sessionId, @RequestParam String province, @RequestParam String city) {
        if ("undefined".equals(sessionId) || "undefined".equals(province) || "undefined".equals(city)) {
            return R.ok().put("获取提示失败", null);
        }
        String res = redisUtil.get(TIP + ":" + province + ":" + city);
        if (res != null) {
            // 根据 sessionId 查询是男是女
            String sessionIdValue = redisUtil.get(sessionId);
            if (sessionIdValue != null) {
                String openid = sessionIdValue.split(":")[0];
                WxUserEntity wxUser = wxUserService.getById(openid);
                Tip tip = JSON.parseObject(res, Tip.class);

                TipDto tipDto = new TipDto();
                BeanUtils.copyProperties(tip, tipDto);

                List<String> list = new ArrayList<>();
                list.add(tip.getChill());
                list.add(tip.getClod());
                list.add(tip.getTip1());
                list.add(tip.getTip2());
                if (wxUser != null) {
                    Integer gender = wxUser.getGender();
                    // false 男，true 女，女性增加化妆信息
                    if (gender == 1) {
                        list.add(tip.getMakeup());
                    }
                }
                tipDto.setTips(list);
                return R.ok().put("提示信息 - 缓存获取成功", tipDto);
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
            WxUserEntity wxUser = wxUserService.getById(openid);

            if (wxUser != null) {
                Integer gender = wxUser.getGender();
                // false 男，true 女，女性增加化妆信息
                if (gender == 1) {
                    list.add(tip.getMakeup());
                }
            }
            tipDto.setTips(list);
            redisUtil.set(TIP + ":" + province + ":" + city, JSON.toJSONString(tip), 60 * 60);
            return R.ok().put("获取提示成功", tipDto);
        } else {
            tipDto.setTips(list);
            return R.ok().put("获取提示成功", tipDto);
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
    public R add(@RequestParam String sessionId, @RequestParam String avatarUrl,
                 @RequestParam String city, @RequestParam String country,
                 @RequestParam Integer gender, @RequestParam String language,
                 @RequestParam String nickName, @RequestParam String province) {
        // 根据 sessionId 获取 openid
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            // 检测是否存在，存在则更新
            WxUserEntity wxUser = wxUserService.getById(openid);
            if (wxUser != null) {
                wxUser.setAvatarUrl(avatarUrl);
                wxUser.setCity(city);
                wxUser.setCountry(country);
                wxUser.setGender(gender);
                wxUser.setLanguage(language);
                wxUser.setNickname(nickName);
                wxUser.setProvince(province);
                wxUser.setTime(new Date());

                boolean update = wxUserService.updateById(wxUser);
                if (update) {
                    return R.ok().put("更新用户成功", wxUser);
                }
            }
            // 不存在则插入
            else {
                WxUserEntity newWxUser = new WxUserEntity();
                newWxUser.setOpenid(openid);
                newWxUser.setAvatarUrl(avatarUrl);
                newWxUser.setCity(city);
                newWxUser.setCountry(country);
                newWxUser.setGender(gender);
                newWxUser.setLanguage(language);
                newWxUser.setNickname(nickName);
                newWxUser.setProvince(province);
                newWxUser.setTime(new Date());

                boolean update = wxUserService.save(newWxUser);
                if (update) {
                    return R.ok().put("新增用户成功", newWxUser);
                }
            }
        }
        return R.ok().put("添加/更新用户失败", 400);
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
    public R query(@RequestParam String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            WxUserEntity wxUser = wxUserService.getById(openid);
            if (wxUser != null) {
                return R.ok().put("获取用户信息成功", wxUser);
            }
        }
        return R.ok().put("获取用户信息失败", 400);
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
    public R register(@RequestParam String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            WxUserEntity wxUser = wxUserService.getById(openid);
            if (wxUser != null) {
                // 已注册
                if (wxUser.getTime() != null) {
                    return R.ok().put("用户已注册", wxUser);
                }
            }
        }
        return R.ok().put("用户未注册", 400);
    }
}
