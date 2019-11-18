package cn.duniqb.mobile.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import cn.duniqb.mobile.mapper.WxUserMapper;
import cn.duniqb.mobile.service.WxUserService;

@Service
public class WxUserServiceImpl implements WxUserService {

    @Resource
    private WxUserMapper wxUserMapper;

}

