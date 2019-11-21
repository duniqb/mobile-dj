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
     * 根据账户密码查询学生
     *
     * @param no
     * @param password
     * @return
     */
    @Override
    public Student selectOneByStuNoPwd(String no, String password) {
        Example example = new Example(Student.class);
        example.createCriteria().andEqualTo("stuNo", no).andEqualTo("password", password);
        return studentMapper.selectOneByExample(example);
    }
}



















