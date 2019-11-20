package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.Course;

public interface CourseService {
    /**
     * 根据课程 id 查询课程
     *
     * @param courseId
     * @return
     */
    Course selectOneByCourseId(String courseId);

}
















