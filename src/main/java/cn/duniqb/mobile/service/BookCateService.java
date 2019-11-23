package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.BookCate;

import java.util.List;

public interface BookCateService {

    /**
     * 查询所有
     *
     * @return
     */
    List<BookCate> selectAll();
}
