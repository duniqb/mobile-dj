package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.Student;
import cn.duniqb.mobile.dto.User;
import cn.duniqb.mobile.mapper.StudentMapper;
import cn.duniqb.mobile.service.StudentService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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

    /**
     * 根据学号密码查询学生
     *
     * @param user
     * @return
     */
    @Override
    public Student selectOneByStudent(User user) {
        Example example = new Example(Student.class);
        example.createCriteria().andEqualTo("stuNo", user.getUsername());
        example.createCriteria().andEqualTo("password", user.getPassword());
        return studentMapper.selectOneByExample(example);
    }
}


















