package cn.duniqb.mobile.dao;

import cn.duniqb.mobile.entity.ReportEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 举报文章/评论
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Mapper
public interface ReportDao extends BaseMapper<ReportEntity> {
	
}
