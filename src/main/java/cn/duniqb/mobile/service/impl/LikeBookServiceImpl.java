package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.LikeBookDao;
import cn.duniqb.mobile.entity.ArticleEntity;
import cn.duniqb.mobile.entity.LikeBookEntity;
import cn.duniqb.mobile.service.LikeBookService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("likeBookService")
public class LikeBookServiceImpl extends ServiceImpl<LikeBookDao, LikeBookEntity> implements LikeBookService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<LikeBookEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");

        IPage<LikeBookEntity> page = this.page(
                new Query<LikeBookEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}