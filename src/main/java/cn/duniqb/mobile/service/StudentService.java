package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.dto.User;

public interface StudentService {

    /**
     * 根据学号查询学生
     *
     * @param no
     * @return
     */
    Student selectOneByNo(String no);

    /**
     * 根据学号删除学生
     *
     * @param stuNo
     * @return
     */
    int deleteByStuNo(String stuNo);


    /**
     * 根据账户密码查询学生
     *
     * @param no
     * @param password
     * @return
     */
    Student selectOneByStuNoPwd(String no, String password);
}



















