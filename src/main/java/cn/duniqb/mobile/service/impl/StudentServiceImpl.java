package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.mapper.StudentMapper;
import cn.duniqb.mobile.service.StudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    /**
     * 根据学号查询学生
     *
     * @param no
     * @return
     */
    @Override
    public Student selectOneByNo(String no) {

        return studentMapper.selectByPrimaryKey(no);
    }

    /**
     * 根据学号删除学生
     *
     * @param stuNo
     * @return
     */
    @Override
    public int deleteByStuNo(String stuNo) {
        return studentMapper.deleteByPrimaryKey(stuNo);
    }
}

















