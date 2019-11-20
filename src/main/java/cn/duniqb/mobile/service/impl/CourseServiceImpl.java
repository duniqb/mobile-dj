package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.Course;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.CourseMapper;
import cn.duniqb.mobile.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    /**
     * 根据课程 id 查询课程
     *
     * @param courseId
     * @return
     */
    @Override
    public Course selectOneByCourseId(String courseId) {

        return courseMapper.selectByPrimaryKey(courseId);
    }
}
















