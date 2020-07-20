package cn.duniqb.mobile.dao;

import cn.duniqb.mobile.entity.BookCateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书分类法总类
 * 
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Mapper
public interface BookCateDao extends BaseMapper<BookCateEntity> {
	
}
