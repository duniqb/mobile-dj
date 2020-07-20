package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.dao.ReportDao;
import cn.duniqb.mobile.entity.ReportEntity;
import cn.duniqb.mobile.service.ReportService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("reportService")
public class ReportServiceImpl extends ServiceImpl<ReportDao, ReportEntity> implements ReportService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ReportEntity> page = this.page(
                new Query<ReportEntity>().getPage(params),
                new QueryWrapper<ReportEntity>()
        );

        return new PageUtils(page);
    }

}