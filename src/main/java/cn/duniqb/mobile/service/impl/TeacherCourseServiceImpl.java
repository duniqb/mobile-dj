package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.TeacherCourse;
import cn.duniqb.mobile.mapper.TeacherCourseMapper;
import cn.duniqb.mobile.service.TeacherCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class TeacherCourseServiceImpl implements TeacherCourseService {

    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    /**
     * 根据学号查找课程信息
     *
     * @param stuNo
     * @param courseId
     * @return
     */
    @Override
    public TeacherCourse searchByStuNoCourseId(String stuNo, String courseId) {
        Example example = new Example(TeacherCourse.class);
        example.createCriteria().andEqualTo("stuNo", stuNo).andEqualTo("courseId", courseId);
        return teacherCourseMapper.selectOneByExample(example);
    }

    /**
     * 根据学号删除关联信息
     *
     * @param stuNo
     * @return
     */
    @Override
    public int deleteByStuNo(String stuNo) {
        Example example = new Example(TeacherCourse.class);
        example.createCriteria().andEqualTo("stuNo", stuNo);
        return teacherCourseMapper.deleteByExample(example);
    }
}












