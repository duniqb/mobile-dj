package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.BookCate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.BookCateMapper;
import cn.duniqb.mobile.service.BookCateService;

import java.util.List;

@Service
public class BookCateServiceImpl implements BookCateService {

    @Resource
    private BookCateMapper bookCateMapper;

    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<BookCate> selectAll() {

        return bookCateMapper.selectAll();
    }
}
