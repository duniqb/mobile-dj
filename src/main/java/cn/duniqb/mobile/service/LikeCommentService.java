package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.LikeCommentEntity;

import java.util.Map;

/**
 * 点赞评论表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface LikeCommentService extends IService<LikeCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

