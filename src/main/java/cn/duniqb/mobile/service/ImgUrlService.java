package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.ImgUrl;

import java.util.List;

public interface ImgUrlService {

    /**
     * 根据文章 id 查询图片地址
     *
     * @param newsId
     * @return
     */
    List<ImgUrl> findByNewsId(String newsId);

    /**
     * 插入记录
     *
     * @param newsId
     * @param url
     * @return
     */
    int insert(String newsId, String url);
}


