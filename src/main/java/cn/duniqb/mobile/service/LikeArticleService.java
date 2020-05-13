package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.LikeArticleEntity;

import java.util.Map;

/**
 * 文章点赞表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface LikeArticleService extends IService<LikeArticleEntity> {

    PageUtils queryPage(Map<String, Object> params, Integer id);

    void saveLikeToDatabase();

    void saveDislikeToDatabase();
}

