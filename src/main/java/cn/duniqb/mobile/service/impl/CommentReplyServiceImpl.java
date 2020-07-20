package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.CommentReplyDao;
import cn.duniqb.mobile.entity.CommentReplyEntity;
import cn.duniqb.mobile.service.CommentReplyService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("commentReplyService")
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyDao, CommentReplyEntity> implements CommentReplyService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentReplyEntity> page = this.page(
                new Query<CommentReplyEntity>().getPage(params),
                new QueryWrapper<CommentReplyEntity>()
        );

        return new PageUtils(page);
    }

}