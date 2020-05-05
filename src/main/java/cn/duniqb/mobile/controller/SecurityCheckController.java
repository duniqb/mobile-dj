package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.mini.SecurityCheck;
import cn.duniqb.mobile.spider.MiniSpiderService;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/4 15:05
 * @since 1.8
 */
@Slf4j
@Api(tags = {"与内容安全相关的接口"})
@RestController
@RequestMapping("/security")
public class SecurityCheckController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MiniSpiderService miniSpiderService;

    /**
     * AccessToken 在 Redis 里前缀
     */
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    /**
     * 文本安全校验
     * 此功能保留，应该在小程序端调用云函数校验文本和图片
     *
     * @return
     */
    @GetMapping("/msg")
    @ApiOperation(value = "文本安全校验", notes = "文本安全校验的接口")
    public R msgSecCheck(@RequestParam String content) {
        String accessToken = redisUtil.get(ACCESS_TOKEN);
        if (accessToken == null) {
            accessToken = Objects.requireNonNull(miniSpiderService.getAccessToken()).getAccess_token();
            redisUtil.set(ACCESS_TOKEN, accessToken, 60 * 60);
        }

        SecurityCheck securityCheck = miniSpiderService.msgSecCheck(content, accessToken);
        if (securityCheck != null) {
            return R.ok("校验请求发送成功").put("data", securityCheck);
        }
        return R.error(400, "校验请求发送失败");
    }

}
