package cn.duniqb.mobile.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import cn.duniqb.mobile.mapper.GradeExamMapper;
import cn.duniqb.mobile.service.GradeExamService;

@Service
public class GradeExamServiceImpl implements GradeExamService {

    @Resource
    private GradeExamMapper gradeExamMapper;

}

