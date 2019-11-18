package cn.duniqb.mobile.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import cn.duniqb.mobile.mapper.StudentMapper;
import cn.duniqb.mobile.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;

}



