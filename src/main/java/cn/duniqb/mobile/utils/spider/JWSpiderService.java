package cn.duniqb.mobile.utils.spider;

import cn.duniqb.mobile.domain.*;
import cn.duniqb.mobile.dto.JSONResult;
import cn.duniqb.mobile.dto.jw.Notice;
import cn.duniqb.mobile.dto.jw.NoticeList;
import cn.duniqb.mobile.mapper.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 爬取教务
 *
 * @author duniqb
 */
@Service
public class JWSpiderService {

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

    @Autowired
    private GradeExamMapper gradeExamMapper;

    /**
     * 获取个人信息的 url
     */
    @Value("${jw.getInfoUrl}")
    private String getInfoUrl;

    /**
     * 获取成绩的 url
     */
    @Value("${jw.getScoreParamUrl}")
    private String getScoreParamUrl;

    /**
     * 获取等级考试的 url
     */
    @Value("${jw.getGradeExam}")
    private String getGradeExam;

    /**
     * 获取等级考试的 url
     */
    @Value("${jw.noticeListUrl}")
    private String noticeListUrl;

    /**
     * 获取等级考试的 url
     */
    @Value("${jw.noticeUrl}")
    private String noticeUrl;

    /**
     * 获取个人信息与学分信息
     *
     * @param cookieStore
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<Integer, String> getInfo(CookieStore cookieStore, String password) {
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(getInfoUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            if (response != null) {
                doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        String imgUrl = doc.select("table.form td img").attr("src");

        Elements elements = doc.select("table.form tr");
        Map<Integer, String> map = new HashMap<>();
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
//                student.setSalt(UUID.randomUUID().toString().substring(0, 5));
//                student.setPassword(MobileUtil.MD5(password) + student.getSalt());
                student.setPassword(password);
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
        int i1 = creditMapper.insert(credit);
        if (i1 > 0) {
            map.put(1, "保存学分成功");
        } else {
            map.put(1, "保存学分失败");
        }
        int i2 = studentMapper.insert(student);
        if (i2 > 0) {
            map.put(2, "保存学籍成功");
        } else {
            map.put(2, "保存学籍失败");
        }
        return map;
    }

    /**
     * 查询成绩
     * 在此处总是查询所有的成绩
     *
     * @param cookieStore
     * @param stuNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<Integer, String> getScoreParam(CookieStore cookieStore, String stuNo) {
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost post = new HttpPost(getScoreParamUrl);
        ArrayList<NameValuePair> postData = new ArrayList<>();

        postData.add(new BasicNameValuePair("year", null));
        postData.add(new BasicNameValuePair("term", null));
        postData.add(new BasicNameValuePair("para", "0"));
        postData.add(new BasicNameValuePair("sortColumn", ""));
        postData.add(new BasicNameValuePair("Submit", "查询"));

        try {
            post.setEntity(new UrlEncodedFormEntity(postData));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Integer, String> map = new HashMap<>();

        Document doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Element element = doc.select("table.datalist").first();
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
            score.setTotalScore("".equals(trd.get(10).text()) ? null : trd.get(10).text());
            score.setSlowExam("是".equals(trd.get(11).text()));
            score.setExamType(trd.get(12).text());
            score.setComment(trd.get(13).text());
            int i1 = scoreMapper.insert(score);
            if (i1 > 0) {
                map.put(1, "保存学生成绩成功");
            } else {
                map.put(1, "保存学生成绩失败");
            }

            Course courseExist = courseMapper.selectByPrimaryKey(trd.get(2).text());
            if (courseExist == null) {
                Course course = new Course();
                course.setCourseId(trd.get(2).text());
                course.setCourseSerialNo(trd.get(3).text());
                course.setCourseName(trd.get(4).text());
                course.setCourseGroup(trd.get(6).text());
                course.setCredit(Double.valueOf(trd.get(7).text()));
                course.setCourseType(trd.get(15).text());
                int i2 = courseMapper.insert(course);
                if (i2 > 0) {
                    map.put(2, "保存学生课程成功");
                } else {
                    map.put(2, "保存学生成绩失败");
                }
            }

            StudentCourse studentCourse = new StudentCourse();
            studentCourse.setStuNo(stuNo);
            studentCourse.setCourseAttr(trd.get(5).text());
            studentCourse.setCourseId(trd.get(2).text());
            studentCourse.setYear(Integer.valueOf(trd.get(0).text()));
            studentCourse.setTerm("秋".equals(trd.get(1).text()));
            int i3 = studentCourseMapper.insert(studentCourse);
            if (i3 > 0) {
                map.put(3, "保存学生选课成功");
            } else {
                map.put(3, "保存学生选课失败");
            }

            Example example = new Example(TeacherCourse.class);
            example.createCriteria().andEqualTo("courseId", trd.get(2).text());
            List<TeacherCourse> teacherCourses = teacherCourseMapper.selectByExample(example);
            if (teacherCourses.isEmpty()) {
                TeacherCourse teacherCourse = new TeacherCourse();
                teacherCourse.setStuNo(stuNo);
                teacherCourse.setTeacherName(trd.get(14).text());
                teacherCourse.setCourseId(trd.get(2).text());
                int i4 = teacherCourseMapper.insert(teacherCourse);
                if (i4 > 0) {
                    map.put(4, "保存教师授课成功");
                } else {
                    map.put(4, "保存教师授课失败");
                }
            }
        }
        return map;
    }

    /**
     * 根据学期查询课表
     * 在此处总是查询所有的成绩
     * 暂未使用
     *
     * @param client
     * @param year
     * @param term
     * @return
     */
    public HttpResponse getTimeTable(HttpClient client, Integer year, Integer term) {
        try {
            String url = "http://202.199.128.21/academic/student/currcourse/currcourse.jsdo";

            HttpGet httpGet = new HttpGet(url + "year=" + year + "&term=" + term);

            HttpResponse response = client.execute(httpGet);

            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            Element element = doc.select("table.infolist_tab").first();

            Elements th = element.select("tr th");
            Elements tr = element.select("tr.infolist_common");

            for (int i = 0; i < tr.size(); i++) {
                Elements trd = tr.get(i).select("td");
                for (int j = 0; j < th.size() - 2; j++) {
                    System.out.println(th.get(j).text() + ": " + trd.get(j).text());
                }
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 等级考试
     *
     * @param cookieStore
     * @param stuNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<Integer, String> getGradeExam(CookieStore cookieStore, String stuNo) {
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(getGradeExam));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Integer, String> map = new HashMap<>();
        assert doc != null;
        Element element = doc.select("table.infolist_tab").last();
        Elements tr = element.select("tr");

        for (int i = 1; i < tr.size() - 1; i++) {
            Elements trd = tr.get(i).select("td");

            GradeExam gradeExam = new GradeExam();
            gradeExam.setStuNo(stuNo);
            gradeExam.setExamName(trd.get(0).text());
            if (trd.get(0).text().contains("4")) {
                gradeExam.setGrade(4);
            } else if (trd.get(0).text().contains("6")) {
                gradeExam.setGrade(6);
            }
            gradeExam.setTicketNumber(trd.get(2).text());
            gradeExam.setExamDate(trd.get(1).text().split(" ")[0]);
            gradeExam.setExamTime(trd.get(1).text().split(" ")[1]);
            gradeExam.setScore(trd.get(4).text());
            gradeExam.setApproved(trd.get(5).text());
            int insert = gradeExamMapper.insert(gradeExam);
            if (insert > 0) {
                map.put(1, "保存等级考试成功");
            } else {
                map.put(1, "保存等级考试失败");
            }
        }
        return map;
    }

    /**
     * 获取通知列表
     *
     * @param page
     * @return
     */
    public NoticeList noticeList(String page) {
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(noticeListUrl + page));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            if (response != null) {
                doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("&amp;", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements = null;
        if (doc != null) {
            elements = doc.select("body .thirdBody #thirdmiddle #thirdcontent ul.articleList li");
        }
        NoticeList noticeList = new NoticeList();
        if (doc != null) {
            noticeList.setPage(doc.select("body .thirdBody #thirdmiddle #thirdcontent .navigation ul li.curpage").text());
        }

        List<Notice> list = new ArrayList<>();
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                Notice notice = new Notice();
                notice.setTitle(elements.get(i).select("a").text());
                notice.setReleaseDate(elements.get(i).select("span").text());
                if (elements.get(i).select("a").attr("href").contains("articleId")) {
                    notice.setId(elements.get(i).select("a").attr("href").split("articleId=")[1].split("&")[0]);
                }
                list.add(notice);
            }
            noticeList.setList(list);
        }
        return noticeList;
    }

    /**
     * 获取通知详情
     *
     * @throws Exception
     */
    public Notice notice(String id) {
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(noticeUrl + id + "&columnId=313");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            if (response != null) {
                doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("&amp;", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element element = null;
        if (doc != null) {
            element = doc.select("#article div.body input").get(0);
        }

        String string = null;
        if (element != null) {
            string = element.select("input").attr("value");
        }

        Document input = Jsoup.parse(string != null ? string.replace("nbsp;", "").replace("amp;", "") : null);
        Elements elements = input.select("p");
        Notice notice = new Notice();

        if (elements.size() == 0) {
            List<String> list = new ArrayList<>();
            elements = input.select("div");
            for (int i = 1; i < elements.size() - 1; i++) {
                if (!"".equals(elements.get(i).text().trim())) {
                    list.add(elements.get(i).text());
                }
            }
            notice.setContent(list);
            notice.setTitle(elements.get(0).text());
        } else {
            List<String> list = new ArrayList<>();
            for (int i = 1; i < elements.size() - 1; i++) {
                if (!"".equals(elements.get(i).text().trim())) {
                    list.add(elements.get(i).text());
                }
            }
            notice.setContent(list);
            notice.setTitle(elements.get(0).text());
        }
        return notice;
    }
}
