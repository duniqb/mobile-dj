package cn.duniqb.mobile.service;

import cn.duniqb.mobile.entity.AdminEntity;
import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-06-27 16:27:31
 */
public interface AdminService extends IService<AdminEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

