package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.WxUserDao;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wxUserService")
public class WxUserServiceImpl extends ServiceImpl<WxUserDao, WxUserEntity> implements WxUserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WxUserEntity> page = this.page(
                new Query<WxUserEntity>().getPage(params),
                new QueryWrapper<WxUserEntity>()
        );

        return new PageUtils(page);
    }

}