package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.SeekDao;
import cn.duniqb.mobile.entity.SeekEntity;
import cn.duniqb.mobile.service.SeekService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("seekService")
public class SeekServiceImpl extends ServiceImpl<SeekDao, SeekEntity> implements SeekService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeekEntity> page = this.page(
                new Query<SeekEntity>().getPage(params),
                new QueryWrapper<SeekEntity>()
        );
        for (SeekEntity record : page.getRecords()) {
            if (record.getContent().length() > 50) {
                record.setContent(record.getContent().substring(0, 50) + "...");
            }
        }
        return new PageUtils(page);
    }

}