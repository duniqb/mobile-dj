package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.BookCate;
import cn.duniqb.mobile.domain.LikeBook;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface LibraryService {

    /**
     * 查询所有
     *
     * @return
     */
    List<BookCate> selectAll();

    /**
     * 收藏图书
     *
     * @param openid
     * @param bookId
     * @return
     */
    int likeBook(String openid, String bookId, String bookName, String author);

    /**
     * 取消收藏图书
     *
     * @param openid
     * @param bookId
     * @return
     */
    int unlikeBook(String openid, String bookId);

    /**
     * 查询是否收藏图书
     *
     * @param openid
     * @param bookId
     * @return
     */
    boolean isLikeBook(String openid, String bookId);

    /**
     * 分页查询收藏图书列表
     *
     * @param openid
     * @param page
     * @param num
     * @return
     */
    PageInfo<LikeBook> likeList(String openid, Integer page, Integer num);
}