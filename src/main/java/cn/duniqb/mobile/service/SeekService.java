package cn.duniqb.mobile.service;

import cn.duniqb.mobile.entity.SeekEntity;
import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 失物招领
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 10:21:25
 */
public interface SeekService extends IService<SeekEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

