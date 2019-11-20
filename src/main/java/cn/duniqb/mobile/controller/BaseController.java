package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.JSONResult;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController implements ErrorController {

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONResult error() {
        return JSONResult.build(null, "无效的路径", 404);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
