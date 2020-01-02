package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.BookCate;
import cn.duniqb.mobile.domain.LikeBook;
import cn.duniqb.mobile.mapper.LikeBookMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.BookCateMapper;
import cn.duniqb.mobile.service.LibraryService;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class LibraryServiceImpl implements LibraryService {

    @Resource
    private BookCateMapper bookCateMapper;

    @Autowired
    private LikeBookMapper likeBookMapper;

    /**
     * 查询所有图书分类
     *
     * @return
     */
    @Override
    public List<BookCate> selectAll() {

        return bookCateMapper.selectAll();
    }

    /**
     * 收藏图书
     *
     * @param openid
     * @param bookId
     * @return
     */
    @Override
    public int likeBook(String openid, String bookId, String bookName, String author) {
        if (isLikeBook(openid, bookId)) {
            return -1;
        }
        LikeBook likeBook = new LikeBook();
        likeBook.setOpenid(openid);
        likeBook.setBookId(bookId);
        likeBook.setBookName(bookName);
        likeBook.setAuthor(author);
        likeBook.setTime(LocalDateTime.now());
        return likeBookMapper.insert(likeBook);
    }

    /**
     * 取消收藏图书
     *
     * @param openid
     * @param bookId
     * @return
     */
    @Override
    public int unlikeBook(String openid, String bookId) {
        if (!isLikeBook(openid, bookId)) {
            return -1;
        }
        Example example = new Example(LikeBook.class);
        example.createCriteria().andEqualTo("openid", openid).andEqualTo("bookId", bookId);
        return likeBookMapper.deleteByExample(example);
    }

    /**
     * 查询是否收藏图书
     *
     * @param openid
     * @param bookId
     * @return
     */
    @Override
    public boolean isLikeBook(String openid, String bookId) {
        Example example = new Example(LikeBook.class);
        example.createCriteria().andEqualTo("openid", openid).andEqualTo("bookId", bookId);
        LikeBook likeBook = likeBookMapper.selectOneByExample(example);
        return likeBook != null;
    }

    /**
     * 分页查询收藏图书列表
     *
     * @param openid
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<LikeBook> likeList(String openid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<LikeBook> likeBookList = likeBookMapper.selectAll();


        PageInfo<LikeBook> pageList = new PageInfo<>(likeBookList);

        return pageList;
    }
}
