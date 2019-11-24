package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.ImgUrl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.ImgUrlMapper;
import cn.duniqb.mobile.service.ImgUrlService;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ImgUrlServiceImpl implements ImgUrlService {

    @Resource
    private ImgUrlMapper imgUrlMapper;

    /**
     * 根据文章 id 查询图片地址
     *
     * @return
     */
    @Override
    public List<ImgUrl> findByNewsId(String newsId) {
        Example example = new Example(ImgUrl.class);
        example.createCriteria().andEqualTo("newsId", newsId);
        List<ImgUrl> imgUrls = imgUrlMapper.selectByExample(example);
        return imgUrls;
    }

    /**
     * 插入记录
     *
     * @param newsId
     * @param url
     * @return
     */
    @Override
    public int insert(String newsId, String url) {
        ImgUrl imgUrl = new ImgUrl();
        imgUrl.setNewsId(newsId);
        imgUrl.setUrl(url);
        return imgUrlMapper.insert(imgUrl);
    }
}


