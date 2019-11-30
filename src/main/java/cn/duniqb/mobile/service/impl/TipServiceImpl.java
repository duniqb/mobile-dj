package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.Tip;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.TipMapper;
import cn.duniqb.mobile.service.TipService;

@Service
public class TipServiceImpl implements TipService {

    @Resource
    private TipMapper tipMapper;

    /**
     * 根据 id 查询
     *
     * @param id
     * @return
     */
    @Override
    public Tip selectById(Integer id) {
        return tipMapper.selectByPrimaryKey(id);
    }

    /**
     * 插入
     *
     * @param tip
     * @return
     */
    @Override
    public int insert(Tip tip) {
        return tipMapper.insert(tip);
    }

    /**
     * 更新
     *
     * @param tip
     * @return
     */
    @Override
    public int update(Tip tip) {
        return tipMapper.updateByPrimaryKey(tip);
    }
}





