package cn.duniqb.mobile.dao;

import cn.duniqb.mobile.entity.ImgUrlEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图片表，存储在oss
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Mapper
public interface ImgUrlDao extends BaseMapper<ImgUrlEntity> {
	
}
