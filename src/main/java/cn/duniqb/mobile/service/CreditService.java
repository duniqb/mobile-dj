package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.Credit;

public interface CreditService {

    /**
     * 根据学号清空学分
     *
     * @param stuNo
     * @return
     */
    int deleteByStuNo(String stuNo);

    /**
     * 根据学号查询学分
     *
     * @param no
     * @return
     */
    Credit selectOneByNo(String no);
}













