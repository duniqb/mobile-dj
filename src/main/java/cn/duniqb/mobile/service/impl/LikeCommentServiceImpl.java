package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.LikeCommentDao;
import cn.duniqb.mobile.entity.LikeCommentEntity;
import cn.duniqb.mobile.service.LikeCommentService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("likeCommentService")
public class LikeCommentServiceImpl extends ServiceImpl<LikeCommentDao, LikeCommentEntity> implements LikeCommentService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LikeCommentEntity> page = this.page(
                new Query<LikeCommentEntity>().getPage(params),
                new QueryWrapper<LikeCommentEntity>()
        );

        return new PageUtils(page);
    }

}