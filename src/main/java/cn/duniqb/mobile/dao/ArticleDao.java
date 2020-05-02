package cn.duniqb.mobile.dao;

import cn.duniqb.mobile.entity.ArticleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticleEntity> {

}
