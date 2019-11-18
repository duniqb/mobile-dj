package cn.duniqb.mobile.service;

import cn.duniqb.mobile.domain.*;
import cn.duniqb.mobile.mapper.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class SpiderService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CreditMapper creditMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    /**
     * 获取个人信息与学分信息
     *
     * @param client
     * @return
     * @throws Exception
     */
    public int getInfo(HttpClient client) throws Exception {
        HttpResponse response = client.execute(new HttpGet("http://202.199.128.21/academic/showPersonalInfo.do"));
        Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        String imgUrl = doc.select("table.form td img").attr("src");
        System.out.println("图片地址" + "http://202.199.128.21" + imgUrl);

        Elements elements = doc.select("table.form tr");
        Student student = new Student();
        for (Element element : elements) {
            Elements tit = element.select("th");
            Elements info = element.select("td");
            for (int i = 0; i < tit.size(); i++) {
                if (tit.get(i).text().contains("用户名")) {
                    student.setStuNo(info.get(i).text());
                } else if (tit.get(i).text().contains("真实姓名")) {
                    student.setName(info.get(i).text());
                } else if (tit.get(i).text().contains("所在院系")) {
                    student.setCollege(info.get(i).text());
                } else if (tit.get(i).text().contains("专业")) {
                    student.setMajor(info.get(i).text());
                } else if (tit.get(i).text().contains("方向")) {
                    student.setDirection(info.get(i).text());
                } else if (tit.get(i).text().contains("学生类别")) {
                    student.setStudentType(info.get(i).text());
                } else if (tit.get(i).text().contains("年级")) {
                    student.setGrade(info.get(i).text());
                } else if (tit.get(i).text().contains("班级")) {
                    student.setClazz(info.get(i).text());
                } else if (tit.get(i).text().contains("证件类型")) {
                    student.setCertificateType(info.get(i).text());
                } else if (tit.get(i).text().contains("证件号码")) {
                    student.setCertificate(info.get(i).text());
                } else if (tit.get(i).text().contains("电子邮箱")) {
                    student.setEmail(info.get(i).text());
                } else if (tit.get(i).text().contains("联系电话")) {
                    student.setPhone(info.get(i).text());
                } else if (tit.get(i).text().contains("通讯地址")) {
                    student.setAddress(info.get(i).text());
                } else if (tit.get(i).text().contains("邮政编码")) {
                    student.setZipCode(info.get(i).text());
                }
            }
        }

        // 插入学分
        Elements credits = doc.select("table.datalist tr");
        Elements th = credits.select("th");
        Elements td = credits.select("td");
        Credit credit = new Credit();
        for (int i = 0; i < th.size(); i++) {
            if (th.get(i).text().contains("专业")) {
                credit.setMajor(td.get(i).text());
            } else if (th.get(i).text().contains("教学计划学分")) {
                credit.setCreditRequirements(Double.valueOf(td.get(i).text()));
            } else if (th.get(i).text().contains("已获必修学分")) {
                credit.setRequiredCredits(Double.valueOf(td.get(i).text()));
            } else if (th.get(i).text().contains("已获选修学分")) {
                credit.setElectiveCredits(Double.valueOf(td.get(i).text()));
            } else if (th.get(i).text().contains("已获任选学分")) {
                credit.setOptionalCredits(Double.valueOf(td.get(i).text()));
            }
        }
        credit.setStuNo(student.getStuNo());
        creditMapper.insert(credit);
        return studentMapper.insert(student);
    }

    /**
     * 查询成绩
     * 在此处总是查询所有的成绩
     *
     * @return
     */
    public Map<Integer, String> getScoreParam(HttpClient client, String stuNo) {
        try {
            HttpPost post = new HttpPost("http://202.199.128.21/academic/manager/score/studentOwnScore.do");
            ArrayList<NameValuePair> postData = new ArrayList<>();

            postData.add(new BasicNameValuePair("year", null));
            postData.add(new BasicNameValuePair("term", null));
            postData.add(new BasicNameValuePair("para", "0"));
            postData.add(new BasicNameValuePair("sortColumn", ""));
            postData.add(new BasicNameValuePair("Submit", "查询"));

            post.setEntity(new UrlEncodedFormEntity(postData));

            HttpResponse response = client.execute(post);
            Map<Integer, String> map = new HashMap<>();
            try {
                Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
                Element element = doc.select("table.datalist").first();
                Elements th = element.select("th");
                Elements tr = element.select("tr");

                // 保存成绩，课程，选课，授课
                for (int i = 1; i < tr.size(); i++) {
                    Elements trd = tr.get(i).select("td");

                    Score score = new Score();
                    score.setStuNo(stuNo);
                    score.setYear(Integer.valueOf(trd.get(0).text()));
                    score.setTerm("秋".equals(trd.get(1).text()));
                    score.setCourseId(trd.get(2).text());
                    score.setUsualScore("".equals(trd.get(8).text()) ? null : trd.get(8).text());
                    score.setEndScore("".equals(trd.get(9).text()) ? null : trd.get(9).text());
                    score.setTotalScore("".equals(trd.get(10).text())? null : trd.get(10).text());
                    score.setSlowExam("是".equals(trd.get(11).text()));
                    score.setExamType(trd.get(12).text());
                    score.setComment(trd.get(13).text());
                    int i1 = scoreMapper.insert(score);
                    if (i1 > 0) {
                        map.put(1, "保存成绩成功");
                    }

                    Course course = new Course();
                    course.setCourseId(trd.get(2).text());
                    course.setCourseSerialNo(trd.get(3).text());
                    course.setCourseName(trd.get(4).text());
                    course.setCourseGroup(trd.get(6).text());
                    course.setCredit(Double.valueOf(trd.get(7).text()));
                    course.setCourseType(trd.get(15).text());
                    int i2 = courseMapper.insert(course);
                    if (i2 > 0) {
                        map.put(2, "保存课程成功");
                    }

                    StudentCourse studentCourse = new StudentCourse();
                    studentCourse.setStuNo(stuNo);
                    studentCourse.setCourseAttr(trd.get(5).text());
                    studentCourse.setCourseId(trd.get(2).text());
                    int i3 = studentCourseMapper.insert(studentCourse);
                    if (i3 > 0) {
                        map.put(3, "保存选课成功");
                    }

                    TeacherCourse teacherCourse = new TeacherCourse();
                    teacherCourse.setTeacherName(trd.get(14).text());
                    teacherCourse.setCourseId(trd.get(2).text());
                    int i4 = teacherCourseMapper.insert(teacherCourse);
                    if (i4 > 0) {
                        map.put(4, "保存授课成功");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据学期查询课表
     * 在此处总是查询所有的成绩
     */
    public HttpResponse getTimeTable(HttpClient client, Integer year, Integer term) {
        try {
            String url = "http://202.199.128.21/academic/student/currcourse/currcourse.jsdo";

            // 2.输入网址,发起get请求创建HttpGet对象
            HttpGet httpGet = new HttpGet(url + "year=" + year + "&term=" + term);

            // 3.返回响应
            HttpResponse response = client.execute(httpGet);

            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            Element element = doc.select("table.infolist_tab").first();

            // 标题集合
            Elements th = element.select("tr th");
            // 总行数
            Elements tr = element.select("tr.infolist_common");

            for (int i = 0; i < tr.size(); i++) {
                Elements trd = tr.get(i).select("td");
                for (int j = 0; j < th.size() - 2; j++) {
                    System.out.println(th.get(j).text() + ": " + trd.get(j).text());
                }
                System.out.println("-------------------------------------------------");
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
