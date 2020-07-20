package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.ImgUrlDao;
import cn.duniqb.mobile.entity.ImgUrlEntity;
import cn.duniqb.mobile.service.ImgUrlService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("imgUrlService")
public class ImgUrlServiceImpl extends ServiceImpl<ImgUrlDao, ImgUrlEntity> implements ImgUrlService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ImgUrlEntity> page = this.page(
                new Query<ImgUrlEntity>().getPage(params),
                new QueryWrapper<ImgUrlEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ImgUrlEntity> listById(String id) {

        return null;
    }
}