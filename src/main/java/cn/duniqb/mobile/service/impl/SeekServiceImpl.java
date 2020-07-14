package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.SeekDao;
import cn.duniqb.mobile.entity.ArticleEntity;
import cn.duniqb.mobile.entity.SeekEntity;
import cn.duniqb.mobile.service.SeekService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("seekService")
public class SeekServiceImpl extends ServiceImpl<SeekDao, SeekEntity> implements SeekService {

    @Resource
    private SeekDao seekDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeekEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        queryWrapper.orderByDesc("time");

        IPage<SeekEntity> page = this.page(
                new Query<SeekEntity>().getPage(params),
                queryWrapper
        );
        for (SeekEntity record : page.getRecords()) {
            if (record.getContent().length() > 50) {
                record.setContent(record.getContent().substring(0, 50) + "...");
            }
        }
        return new PageUtils(page);
    }

    @Override
    public int saveSeek(SeekEntity seekEntity) {
        seekDao.saveSeek(seekEntity);
        return seekEntity.getId();
    }
}