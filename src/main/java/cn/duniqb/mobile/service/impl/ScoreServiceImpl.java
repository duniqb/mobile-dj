package cn.duniqb.mobile.service.impl;

import cn.duniqb.mobile.domain.Score;
import cn.duniqb.mobile.domain.TeacherCourse;
import cn.duniqb.mobile.dto.ScoreDto;
import cn.duniqb.mobile.mapper.CourseMapper;
import cn.duniqb.mobile.service.TeacherCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import cn.duniqb.mobile.mapper.ScoreMapper;
import cn.duniqb.mobile.service.ScoreService;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Resource
    private ScoreMapper scoreMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeacherCourseService teacherCourseService;

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

    /**
     * 按照学号查询全部成绩
     *
     * @param stuNo
     * @return
     */
    @Override
    public List<ScoreDto> selectOneByStuNo(String stuNo) {
        Example example = new Example(Score.class);
        example.createCriteria().andEqualTo("stuNo", stuNo);
        return getScoreDto(example);
    }

    /**
     * 按照学号 + 学年查询成绩
     *
     * @param stuNo
     * @param year
     * @return
     */
    @Override
    public List<ScoreDto> selectOneByStuNoYear(String stuNo, Integer year) {
        Example example = new Example(Score.class);
        example.createCriteria().andEqualTo("stuNo", stuNo)
                .andEqualTo("year", year);
        return getScoreDto(example);
    }

    /**
     * 按照学号 + 学年 + 学期查询成绩
     *
     * @param stuNo
     * @param year
     * @param term
     * @return
     */
    @Override
    public List<ScoreDto> selectOneByStuNoYearTerm(String stuNo, Integer year, Integer term) {
        Example example = new Example(Score.class);
        example.createCriteria().andEqualTo("stuNo", stuNo)
                .andEqualTo("year", year)
                .andEqualTo("term", term);
        return getScoreDto(example);
    }

    /**
     * 把 Score 转为 ScoreDto
     *
     * @param example
     * @return
     */
    private List<ScoreDto> getScoreDto(Example example) {
        List<Score> scoreList = scoreMapper.selectByExample(example);
        List<ScoreDto> scoreDtoList = new ArrayList<>();
        for (Score score : scoreList) {
            ScoreDto scoreDto = new ScoreDto();
            BeanUtils.copyProperties(score, scoreDto);
            scoreDto.setCourseName(courseMapper.selectByPrimaryKey(score.getCourseId()).getCourseName());
            scoreDto.setTerm(score.getTerm() ? "秋" : "春");
            scoreDto.setCredit(courseMapper.selectByPrimaryKey(score.getCourseId()).getCredit());
            TeacherCourse teacherCourse = teacherCourseService.searchByStuNoCourseId(score.getStuNo(), score.getCourseId());
            scoreDto.setTeacherName(teacherCourse.getTeacherName());
            scoreDtoList.add(scoreDto);
        }
        return scoreDtoList;
    }
}
















