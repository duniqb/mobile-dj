package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.WxUserEntity;

import java.util.Map;

/**
 * 小程序用户表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface WxUserService extends IService<WxUserEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

