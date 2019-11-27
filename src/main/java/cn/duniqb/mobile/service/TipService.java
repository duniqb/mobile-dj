package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.Tip;

public interface TipService {

    /**
     * 根据 id 查询
     *
     * @param id
     * @return
     */
    Tip selectById(Integer id);

    /**
     * 插入
     *
     * @param tip
     * @return
     */
    int insert(Tip tip);

    /**
     * 更新
     *
     * @param tip
     * @return
     */
    int update(Tip tip);
}

