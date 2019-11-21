package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.WxUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.WxUserMapper;
import cn.duniqb.mobile.service.WxUserService;
import tk.mybatis.mapper.entity.Example;

@Service
public class WxUserServiceImpl implements WxUserService {

    @Resource
    private WxUserMapper wxUserMapper;

    /**
     * 更新 session_key
     *
     * @param openid
     * @param sessionKey
     * @return
     */
    @Override
    public int updateSessionKeyByOpenid(String openid, String sessionKey) {
        Example example = new Example(WxUser.class);
        example.createCriteria().andEqualTo("openid", openid);

        WxUser wxUser = new WxUser();
        wxUser.setSessionKey(sessionKey);
        return wxUserMapper.updateByExampleSelective(wxUser, example);
    }

    /**
     * 根据 openid 查找
     *
     * @param openid
     * @return
     */
    @Override
    public WxUser selectByOpenid(String openid) {
        Example example = new Example(WxUser.class);
        example.createCriteria().andEqualTo("openid", openid);
        return wxUserMapper.selectOneByExample(example);
    }

    /**
     * 插入
     *
     * @param wxUser
     * @return
     */
    @Override
    public int insertWxUser(WxUser wxUser) {
        return wxUserMapper.insert(wxUser);
    }
}





















