package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.CommentReplyEntity;

import java.util.Map;

/**
 * 评论回复表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface CommentReplyService extends IService<CommentReplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

