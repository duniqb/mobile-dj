package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.TopArticleDao;
import cn.duniqb.mobile.entity.TopArticleEntity;
import cn.duniqb.mobile.service.TopArticleService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("topArticleService")
public class TopArticleServiceImpl extends ServiceImpl<TopArticleDao, TopArticleEntity> implements TopArticleService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TopArticleEntity> page = this.page(
                new Query<TopArticleEntity>().getPage(params),
                new QueryWrapper<TopArticleEntity>()
        );

        return new PageUtils(page);
    }

}