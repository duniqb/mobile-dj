package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.Score;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.ScoreMapper;
import cn.duniqb.mobile.service.ScoreService;
import tk.mybatis.mapper.entity.Example;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Resource
    private ScoreMapper scoreMapper;

    /**
     * 根据学号清空成绩
     *
     * @param stuNo
     * @return
     */
    @Override
    public int deleteByStuNo(String stuNo) {
        Example example = new Example(Score.class);
        example.createCriteria().andEqualTo("stuNo", stuNo);
        return scoreMapper.deleteByExample(example);
    }
}
















