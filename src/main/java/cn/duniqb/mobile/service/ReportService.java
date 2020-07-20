package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.ReportEntity;

import java.util.Map;

/**
 * 举报文章/评论
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface ReportService extends IService<ReportEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

