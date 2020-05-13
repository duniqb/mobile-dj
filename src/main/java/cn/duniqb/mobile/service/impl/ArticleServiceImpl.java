package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.ArticleDao;
import cn.duniqb.mobile.entity.ArticleEntity;
import cn.duniqb.mobile.service.ArticleService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {
    @Autowired
    private ArticleDao articleDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
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