package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.GradeExam;

import java.util.List;

public interface GradeExamService {

    /**
     * 根据学号清空等级成绩
     *
     * @param stuNo
     * @return
     */
    int deleteByStuNo(String stuNo);

    /**
     * 根据学号查询等级成绩
     *
     * @param stuNo
     * @return
     */
    List<GradeExam> selectOneByStuNo(String stuNo);
}


