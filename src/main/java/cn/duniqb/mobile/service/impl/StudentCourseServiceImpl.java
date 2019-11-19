package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.StudentCourse;
import cn.duniqb.mobile.mapper.StudentCourseMapper;
import cn.duniqb.mobile.service.StudentCourseService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

@Service
public class StudentCourseServiceImpl implements StudentCourseService {

    @Resource
    private StudentCourseMapper studentCourseMapper;

    /**
     * 根据学号清空选课信息
     *
     * @param stuNo
     * @return
     */
    @Override
    public int deleteByStuNo(String stuNo) {
        Example example = new Example(StudentCourse.class);
        example.createCriteria().andEqualTo("stuNo", stuNo);
        return studentCourseMapper.deleteByExample(example);
    }
}















