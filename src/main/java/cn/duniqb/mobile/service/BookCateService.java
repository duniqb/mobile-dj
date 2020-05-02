package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.BookCateEntity;

import java.util.Map;

/**
 * 图书分类法总类
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface BookCateService extends IService<BookCateEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

