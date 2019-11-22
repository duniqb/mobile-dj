package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.WxUser;

public interface WxUserService {
    /**
     * 根据 openid 查找
     *
     * @param openid
     * @return
     */
    WxUser selectByOpenid(String openid);

    /**
     * 插入
     *
     * @param wxUser
     * @return
     */
    int insertWxUser(WxUser wxUser);
}






















