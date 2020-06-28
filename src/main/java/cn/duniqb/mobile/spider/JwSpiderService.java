package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.jw.*;
import cn.duniqb.mobile.entity.StudentEntity;
import cn.duniqb.mobile.utils.HttpUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 爬取教务
 *
 * @author duniqb
 */
@Service
public class JwSpiderService {
    /**
     * 教务主机 ip
     */
    @Value("${jw.host}")
    private String host;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * Cookie 在 Redis 里的前缀
     */
    private static final String COOKIE = "COOKIE";

    /**
     * 获取个人信息与学分信息
     *
     * @return
     * @throws Exception
     */
    public Map<Integer, Object> getInfo(String sessionId) {
        String url = "http://" + host + "/academic/showPersonalInfo.do";
        // 初始化Cookie管理器
        CookieJar cookieJar = new CookieJar() {
            // Cookie缓存区
            private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
                String host = arg0.host();
                List<Cookie> cookiesList = cookiesMap.get(host);
                if (cookiesList != null) {
                    cookiesMap.remove(host);
                }
                //再重新天添加
                cookiesMap.put(host, arg1);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl arg0) {
                // TODO Auto-generated method stub
                List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                return cookiesList != null ? cookiesList : new ArrayList<>();
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                // 超时时间
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();
        String cookieFromRedis = redisUtil.get(COOKIE + ":" + sessionId);
        if (cookieFromRedis == null) {
            redisUtil.del("JW_LOGIN:" + sessionId);
            return null;
        }

        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookieFromRedis)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();
        Call loginCall = okHttpClient.newCall(request);
        try (Response response = loginCall.execute()) {
            if (response.code() == 200) {
                //获取返回数据的头部
                Headers headers = response.headers();
                HttpUrl loginUrl = request.url();
                List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
//                     存储到Cookie管理器中
                okHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);

//                    从缓存中获取Cookie
                List<Cookie> cookieOld = okHttpClient.cookieJar().loadForRequest(request.url());
                for (Cookie cookie : cookieOld) {
                    redisUtil.set(COOKIE + ":" + sessionId, cookie.toString(), 60 * 60 * 24);
                }
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                if ("登录".equals(doc.select("body form div h3").text().trim())) {
                    redisUtil.del("JW_LOGIN:" + sessionId);
                    return null;
                }
                String imgUrl = doc.select("table.form td img").attr("src");

                Elements elements = doc.select("table.form tr");
                Map<Integer, Object> map = new HashMap<>();
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
                map.put(1, student);

                // 学分
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
                map.put(2, credit);
                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询成绩
     *
     * @param stuNo
     * @return
     * @throws Exception
     */
    public List<Score> getScoreParam(String stuNo, String sessionId, String year, String term) {
        String url = "http://" + host + "/academic/manager/score/studentOwnScore.do";
        CookieJar cookieJar = new CookieJar() {
            // Cookie缓存区
            private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
                String host = arg0.host();
                List<Cookie> cookiesList = cookiesMap.get(host);
                if (cookiesList != null) {
                    cookiesMap.remove(host);
                }
                //再重新添加
                cookiesMap.put(host, arg1);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl arg0) {
                // TODO Auto-generated method stub
                List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                return cookiesList != null ? cookiesList : new ArrayList<>();
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                // 超时时间
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();
        String cookieFromRedis = redisUtil.get(COOKIE + ":" + sessionId);
        if (cookieFromRedis == null) {
            redisUtil.del("JW_LOGIN:" + sessionId);
            return null;
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("year", year)
                .add("term", term)
                .add("para", "0")
                .add("sortColumn", "")
                .add("Submit", "查询")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Cookie", cookieFromRedis)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        Call loginCall = okHttpClient.newCall(request);
        List<Score> scoreList = new ArrayList<>();
        try (Response response = loginCall.execute()) {
            if (response.code() == 200) {
                //获取返回数据的头部
                Headers headers = response.headers();
                HttpUrl loginUrl = request.url();
                List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
//                     存储到Cookie管理器中
                okHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);

//                    从缓存中获取Cookie
                List<Cookie> cookieOld = okHttpClient.cookieJar().loadForRequest(request.url());
                for (Cookie cookie : cookieOld) {
                    redisUtil.set(COOKIE + ":" + sessionId, cookie.toString(), 60 * 60 * 24);
                }
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                if ("登录".equals(doc.select("body form div h3").text().trim())) {
                    redisUtil.del("JW_LOGIN:" + sessionId);
                    return null;
                }
                Element element = doc.select("table.datalist").first();
                if (element != null) {
                    Elements tr = element.select("tr");
                    for (int i = 1; i < tr.size(); i++) {
                        Elements trd = tr.get(i).select("td");
                        Score score = new Score();

                        score.setStuNo(stuNo);
                        score.setYear(Integer.valueOf(trd.get(0).text()));
                        score.setTerm(trd.get(1).text());
                        score.setCourseId(trd.get(2).text());
                        score.setCourseName(trd.get(4).text());
                        score.setCredit(trd.get(7).text());
                        score.setTeacherName(trd.get(14).text());
                        score.setUsualScore("".equals(trd.get(8).text()) ? null : trd.get(8).text());
                        score.setEndScore("".equals(trd.get(9).text()) ? null : trd.get(9).text());
                        score.setTotalScore("".equals(trd.get(10).text()) ? null : trd.get(10).text());
                        score.setSlowExam("是".equals(trd.get(11).text()));
                        score.setExamType(trd.get(12).text());
                        score.setComment(trd.get(13).text());

                        scoreList.add(score);
                    }
                    return scoreList;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scoreList;
    }

    /**
     * 等级考试
     *
     * @param stuNo
     * @return
     * @throws Exception
     */
    public List<GradeExam> getGradeExam(String stuNo, String sessionId) {
        String url = "http://" + host + "/academic/student/skilltest/skilltest.jsdo";
        CookieJar cookieJar = new CookieJar() {
            // Cookie缓存区
            private final Map<String, List<Cookie>> cookiesMap = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl arg0, List<Cookie> arg1) {
                String host = arg0.host();
                List<Cookie> cookiesList = cookiesMap.get(host);
                if (cookiesList != null) {
                    cookiesMap.remove(host);
                }
                //再重新添加
                cookiesMap.put(host, arg1);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl arg0) {
                // TODO Auto-generated method stub
                List<Cookie> cookiesList = cookiesMap.get(arg0.host());
                //原因：当Request 连接到网络的时候，OkHttp会调用loadForRequest()
                return cookiesList != null ? cookiesList : new ArrayList<>();
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                // 超时时间
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();
        String cookieFromRedis = redisUtil.get(COOKIE + ":" + sessionId);
        if (cookieFromRedis == null) {
            redisUtil.del("JW_LOGIN:" + sessionId);
            return null;
        }
        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookieFromRedis)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();
        Call loginCall = okHttpClient.newCall(request);
        List<GradeExam> gradeExamList = new ArrayList<>();

        try (Response response = loginCall.execute()) {
            if (response.code() == 200) {
                //获取返回数据的头部
                Headers headers = response.headers();
                HttpUrl loginUrl = request.url();
                List<Cookie> cookies = Cookie.parseAll(loginUrl, headers);
//                     存储到Cookie管理器中
                okHttpClient.cookieJar().saveFromResponse(loginUrl, cookies);

//                    从缓存中获取Cookie
                List<Cookie> cookieOld = okHttpClient.cookieJar().loadForRequest(request.url());
                for (Cookie cookie : cookieOld) {
                    redisUtil.set(COOKIE + ":" + sessionId, cookie.toString(), 60 * 60 * 24);
                }

                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                if ("登录".equals(doc.select("body form div h3").text().trim())) {
                    redisUtil.del("JW_LOGIN:" + sessionId);
                    return null;
                }
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
                    gradeExamList.add(gradeExam);
                }
                return gradeExamList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gradeExamList;
    }

    /**
     * 获取通知列表
     *
     * @param page
     * @return
     */
    public NoticeList noticeList(String page) {
//        String url = "http://" + host + "/homepage/infoArticleList.do?sortColumn=publicationDate&pagingNumberPer=10&columnId=10182&sortDirection=-1&pagingPage=" + page;
        String url = "http://" + host + "/homepage/infoArticleList.do";
        Map<String, String> map = new HashMap<>();
        map.put("sortColumn", "publicationDate");
        map.put("pagingNumberPer", "10");
        map.put("columnId", "10182");
        map.put("sortDirection", "-1");
        map.put("pagingPage", page);

        try (Response response = HttpUtils.get(url, map)) {
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
