package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.jw.*;
import cn.duniqb.mobile.entity.StudentEntity;
import cn.duniqb.mobile.utils.HttpUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * 爬取教务
 *
 * @author duniqb
 */
@Service
public class JWSpiderService {
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    /**
     * 教务主机 ip
     */
    @Value("${jw.host}")
    private String host;

    /**
     * 获取个人信息与学分信息
     *
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<Integer, String> getInfo() {
        String url = "http://" + host + "/academic/showPersonalInfo.do";

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @NotNull
            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<>();
            }
        });

        OkHttpClient okHttpClient = builder.build();


        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));

                String imgUrl = doc.select("table.form td img").attr("src");

                Elements elements = doc.select("table.form tr");
                Map<Integer, String> map = new HashMap<>();
                StudentEntity student = new StudentEntity();
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
                        credit.setRequirements(Double.valueOf(td.get(i).text()));
                    } else if (th.get(i).text().contains("已获必修学分")) {
                        credit.setRequiredCredits(Double.valueOf(td.get(i).text()));
                    } else if (th.get(i).text().contains("已获选修学分")) {
                        credit.setElectiveCredits(Double.valueOf(td.get(i).text()));
                    } else if (th.get(i).text().contains("已获任选学分")) {
                        credit.setOptionalCredits(Double.valueOf(td.get(i).text()));
                    }
                }
                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询成绩
     * 在此处总是查询所有的成绩
     *
     * @param stuNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Score getScoreParam(String stuNo) {
        String url = "http://" + host + "/academic/manager/score/studentOwnScore.do";

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("year", null)
                .add("term", null)
                .add("para", "0")
                .add("sortColumn", "")
                .add("Submit", "查询")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element element = doc.select("table.datalist").first();
                Elements tr = element.select("tr");

                Score score = new Score();

                for (int i = 1; i < tr.size(); i++) {
                    Elements trd = tr.get(i).select("td");

                    score.setStuNo(stuNo);
                    score.setYear(Integer.valueOf(trd.get(0).text()));
                    score.setTerm(trd.get(1).text());
                    score.setCourseId(trd.get(2).text());
                    score.setUsualScore("".equals(trd.get(8).text()) ? null : trd.get(8).text());
                    score.setEndScore("".equals(trd.get(9).text()) ? null : trd.get(9).text());
                    score.setTotalScore("".equals(trd.get(10).text()) ? null : trd.get(10).text());
                    score.setSlowExam("是".equals(trd.get(11).text()));
                    score.setExamType(trd.get(12).text());
                    score.setComment(trd.get(13).text());
                }
                return score;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 等级考试
     *
     * @param stuNo
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public GradeExam getGradeExam(String stuNo) {
        String url = "http://" + host + "/academic/student/skilltest/skilltest.jsdo";

        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element element = doc.select("table.infolist_tab").last();
                Elements tr = element.select("tr");

                for (int i = 1; i < tr.size() - 1; i++) {
                    Elements trd = tr.get(i).select("td");

                    GradeExam gradeExam = new GradeExam();
                    gradeExam.setStuNo(stuNo);
                    gradeExam.setExamName(trd.get(0).text());
                    if (trd.get(0).text().contains("4")) {
                        gradeExam.setGrade("4");
                    } else if (trd.get(0).text().contains("6")) {
                        gradeExam.setGrade("6");
                    }
                    gradeExam.setTicketNumber(trd.get(2).text());
                    gradeExam.setExamDate(trd.get(1).text().split(" ")[0]);
                    gradeExam.setExamTime(trd.get(1).text().split(" ")[1]);
                    gradeExam.setScore(trd.get(4).text());
                    gradeExam.setApproved(trd.get(5).text());

                    return gradeExam;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取通知列表
     *
     * @param page
     * @return
     */
    public NoticeList noticeList(String page) {
        String url = "http://" + host + "/homepage/infoArticleList.do?sortColumn=publicationDate&pagingNumberPer=10&columnId=10182&sortDirection=-1&pagingPage=" + page;

        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
                    for (Element element : elements) {
                        Notice notice = new Notice();
                        notice.setTitle(element.select("a").text());
                        notice.setReleaseDate(element.select("span").text());
                        if (element.select("a").attr("href").contains("articleId")) {
                            notice.setId(element.select("a").attr("href").split("articleId=")[1].split("&")[0]);
                        }
                        list.add(notice);
                    }
                    noticeList.setList(list);
                }
                return noticeList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取通知详情
     *
     * @throws Exception
     */
    public Notice notice(String id) {
        String url = "http://" + host + "/homepage/infoSingleArticle.do?articleId=" + id + "&columnId=313";

        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
