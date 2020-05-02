package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.TopArticleEntity;

import java.util.Map;

/**
 * 置顶文章
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface TopArticleService extends IService<TopArticleEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

