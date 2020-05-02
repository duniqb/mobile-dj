package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.LikeArticleDao;
import cn.duniqb.mobile.entity.LikeArticleEntity;
import cn.duniqb.mobile.service.LikeArticleService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("likeArticleService")
public class LikeArticleServiceImpl extends ServiceImpl<LikeArticleDao, LikeArticleEntity> implements LikeArticleService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LikeArticleEntity> page = this.page(
                new Query<LikeArticleEntity>().getPage(params),
                new QueryWrapper<LikeArticleEntity>()
        );

        return new PageUtils(page);
    }

}