package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.TeacherCourse;

public interface TeacherCourseService {

    /**
     * 根据学号删除关联信息
     *
     * @param stuNo
     * @return
     */
    int deleteByStuNo(String stuNo);

    /**
     * 根据学号查找课程信息
     *
     * @param stuNo
     * @param courseId
     * @return
     */
    TeacherCourse searchByStuNoCourseId(String stuNo, String courseId);
}












