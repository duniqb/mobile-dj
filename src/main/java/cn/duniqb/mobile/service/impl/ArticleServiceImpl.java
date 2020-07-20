package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.ArticleDao;
import cn.duniqb.mobile.entity.ArticleEntity;
import cn.duniqb.mobile.service.ArticleService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {
    @Resource
    private ArticleDao articleDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        queryWrapper.orderByDesc("time");

        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public int saveArticle(ArticleEntity articleEntity) {
        articleDao.saveArticle(articleEntity);
        return articleEntity.getId();
    }
}