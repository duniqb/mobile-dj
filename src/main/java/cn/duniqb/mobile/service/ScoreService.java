package cn.duniqb.mobile.service;

import cn.duniqb.mobile.dto.ScoreDto;

import java.util.List;

public interface ScoreService {

    /**
     * 根据学号清空成绩
     *
     * @param stuNo
     * @return
     */
    int deleteByStuNo(String stuNo);

    /**
     * 按照学号查询全部成绩
     *
     * @param stuNo
     * @return
     */
    List<ScoreDto> selectOneByStuNo(String stuNo);

    /**
     * 按照学号 + 学年查询成绩
     *
     * @param stuNo
     * @param year
     * @return
     */
    List<ScoreDto> selectOneByStuNoYear(String stuNo, Integer year);

    /**
     * 按照学号 + 学年 + 学期查询成绩
     *
     * @param stuNo
     * @param year
     * @param term
     * @return
     */
    List<ScoreDto> selectOneByStuNoYearTerm(String stuNo, Integer year, Integer term);
}
















