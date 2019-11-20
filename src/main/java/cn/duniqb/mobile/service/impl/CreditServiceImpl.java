package cn.duniqb.mobile.service.impl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.CreditMapper;
import cn.duniqb.mobile.service.CreditService;

@Service
public class CreditServiceImpl implements CreditService {

    @Resource
    private CreditMapper creditMapper;

    /**
     * 根据学号清空学分
     *
     * @param stuNo
     * @return
     */
    @Override
    public int deleteByStuNo(String stuNo) {
        return creditMapper.deleteByPrimaryKey(stuNo);
    }
}













