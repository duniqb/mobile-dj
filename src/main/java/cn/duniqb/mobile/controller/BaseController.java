package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.dto.JSONResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;

@RestController
public class BaseController implements ErrorController {

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public JSONResult error() {
        return JSONResult.build(null, "无效的请求", 404);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}


