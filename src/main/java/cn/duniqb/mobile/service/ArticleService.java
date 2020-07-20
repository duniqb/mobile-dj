package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.ArticleEntity;

import java.util.Map;

/**
 * 文章表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 09:28:06
 */
public interface ArticleService extends IService<ArticleEntity> {

    PageUtils queryPage(Map<String, Object> params);

    int saveArticle(ArticleEntity articleEntity);
}

