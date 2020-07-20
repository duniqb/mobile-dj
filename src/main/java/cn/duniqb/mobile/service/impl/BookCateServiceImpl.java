package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.BookCateDao;
import cn.duniqb.mobile.entity.BookCateEntity;
import cn.duniqb.mobile.service.BookCateService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("bookCateService")
public class BookCateServiceImpl extends ServiceImpl<BookCateDao, BookCateEntity> implements BookCateService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BookCateEntity> page = this.page(
                new Query<BookCateEntity>().getPage(params),
                new QueryWrapper<BookCateEntity>()
        );

        return new PageUtils(page);
    }

}