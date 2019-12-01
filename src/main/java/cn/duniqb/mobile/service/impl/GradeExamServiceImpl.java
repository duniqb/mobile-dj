package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.GradeExam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.GradeExamMapper;
import cn.duniqb.mobile.service.GradeExamService;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class GradeExamServiceImpl implements GradeExamService {

    @Resource
    private GradeExamMapper gradeExamMapper;

    /**
     * 根据学号清空等级成绩
     *
     * @param stuNo
     * @return
     */
    @Override
    public int deleteByStuNo(String stuNo) {
        Example example = new Example(GradeExam.class);
        example.createCriteria().andEqualTo("stuNo", stuNo);
        return gradeExamMapper.deleteByExample(example);
    }

    /**
     * 根据学号查询等级成绩
     *
     * @param stuNo
     * @return
     */
    @Override
    public List<GradeExam> selectOneByStuNo(String stuNo) {
        Example example = new Example(GradeExam.class);
        example.createCriteria().andEqualTo("stuNo", stuNo);
        return gradeExamMapper.selectByExample(example);
    }
}




