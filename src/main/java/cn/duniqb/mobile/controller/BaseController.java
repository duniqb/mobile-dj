package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.utils.R;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发生错误时返回
 *
 * @author zhang
 */
@RestController
public class BaseController implements ErrorController {

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public R error() {

        return R.error(404, "无效的请求");
    }

    @Override
    public String getErrorPath() {

        return "/error";
    }
}


