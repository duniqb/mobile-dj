package cn.duniqb.mobile.utils.spider;

import cn.duniqb.mobile.dto.job.*;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 就业爬虫
 */
@Service
public class JobSpiderService {

    /**
     * 招聘会列表的 url
     */
    @Value("${job.recruitListUrl}")
    private String recruitListUrl;

    /**
     * 招聘会列表的 url
     */
    @Value("${job.recruitUrl}")
    private String recruitUrl;

    /**
     * 单位需求列表的 url
     */
    @Value("${job.demandListUrl}")
    private String demandListUrl;

    /**
     * 单位需求详情的 url
     */
    @Value("${job.demandUrl}")
    private String demandUrl;

    /**
     * 招聘日历的 url
     */
    @Value("${job.calendarUrl}")
    private String calendarUrl;

    /**
     * 招聘会列表
     * http://jobs.djtu.edu.cn/Recruits.html?page=2
     *
     * @param page
     */
    public RecruitList recruitList(String page) {
        String url = recruitListUrl + "?page=" + page;
        HttpGet getNews = new HttpGet(url);

        getNews.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getNews.setHeader("Accept-Encoding", "gzip, deflate");
        getNews.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        getNews.setHeader("Connection", "keep-alive");
        getNews.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        HttpResponse response;
        HttpClient client = HttpClients.createDefault();

        try {
            response = client.execute(getNews);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));
            Elements elements = doc.select("body section .recruits>ul li");
            RecruitList recruitList = new RecruitList();
            List<Recruit> list = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                Recruit recruit = new Recruit();
                recruit.setCurNo(i);
                // 标题
                recruit.setTitle(elements.get(i).select("a").text());
                // id
                recruit.setId(elements.get(i).select("a").attr("href").split("/")[2].split("\\.")[0]);
                // 浏览次数
                recruit.setBrowser(elements.get(i).select(".stype").text().replace("次", ""));
                // 发布日期
                recruit.setReleaseDate(elements.get(i).select(".sdate").text());
                list.add(recruit);
            }
            recruitList.setList(list);
            recruitList.setPage(page);

            recruitList.setTotal(doc.select("section .pages span").text().split("有")[1].split("条")[0]);
            recruitList.setTotalPage(doc.select("section .pages span").text().split("分")[1].split("页")[0]);
            return recruitList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 招聘会详情
     * http://jobs.djtu.edu.cn/Recruits/408.html
     *
     * @param id
     */
    public Recruit recruit(String id) {
        String url = recruitUrl + id + ".html";
        HttpGet getNews = new HttpGet(url);

        getNews.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getNews.setHeader("Accept-Encoding", "gzip, deflate");
        getNews.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        getNews.setHeader("Connection", "keep-alive");
        getNews.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        HttpResponse response;
        HttpClient client = HttpClients.createDefault();

        try {
            response = client.execute(getNews);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));

            Recruit recruit = new Recruit();
            recruit.setId(id);
            // 标题
            recruit.setTitle(doc.select("body section .minfo .ititle").first().text());

            // 来源，发布日期，浏览数
            String string = doc.select("body section .minfo .ioth").first().toString();
            if (string.contains("来源")) {
                recruit.setFrom(string.split("</span>")[1].split("<span>")[0].trim());
                recruit.setReleaseDate(string.split("</span>")[2].split("<span>")[0].trim());
                recruit.setBrowser(string.split("</span>")[3].split("</h5>")[0].trim());
            } else {
                recruit.setReleaseDate(string.split("</span>")[1].split("<span>")[0].trim());
                recruit.setBrowser(string.split("</span>")[2].split("</h5>")[0].trim());
            }

            // 企业信息
            if (doc.select("body section .minfo .info-company").first() != null) {
                Elements companyInfo = doc.select("body section .minfo .info-company").first().select("ul li");
                for (int i = 0; i < companyInfo.size(); i++) {
                    if (companyInfo.get(i).text().contains("企业名称")) {
                        recruit.setCompanyName(companyInfo.get(i).select("b").text());
                    } else if (companyInfo.get(i).text().contains("单位性质")) {
                        recruit.setCompanyProperties(companyInfo.get(i).select("b").text());
                    } else if (companyInfo.get(i).text().contains("主管部门")) {
                        recruit.setCompetentDepartment(companyInfo.get(i).select("b").text());
                    } else if (companyInfo.get(i).text().contains("单位地区")) {
                        recruit.setCompanyRegion(companyInfo.get(i).select("b").text());
                    } else if (companyInfo.get(i).text().contains("详细地址")) {
                        recruit.setAddress(companyInfo.get(i).select("b").text());
                    } else if (companyInfo.get(i).text().contains("邮政编码")) {
                        recruit.setZipCode(companyInfo.get(i).select("b").text());
                    }
                }
            }

            // 招聘信息
            Elements fairInfo = doc.select("body section .minfo .info-recruits").first().select("ul li");
            for (int i = 0; i < fairInfo.size(); i++) {
                if (fairInfo.get(i).text().contains("地点")) {
                    recruit.setPlace(fairInfo.get(i).select("b").text());
                } else if (fairInfo.get(i).text().contains("日期")) {
                    recruit.setDate(fairInfo.get(i).select("b").text());
                } else if (fairInfo.get(i).text().contains("时间")) {
                    recruit.setTime(fairInfo.get(i).text().replace("时间：", ""));
                }
            }

            // 正文
            Elements elements = doc.select("body section .minfo .content>div");
            List<String> list = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                list.add(elements.get(i).text());
            }
            recruit.setContent(list);

            return recruit;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单位需求 列表
     * http://jobs.djtu.edu.cn/Demands.html?page=2
     *
     * @param page
     */
    public DemandList demandList(String page) {
        String url = demandListUrl + "?page=" + page;
        HttpGet getNews = new HttpGet(url);

        getNews.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getNews.setHeader("Accept-Encoding", "gzip, deflate");
        getNews.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        getNews.setHeader("Connection", "keep-alive");
        getNews.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        HttpResponse response;
        HttpClient client = HttpClients.createDefault();

        try {
            response = client.execute(getNews);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));
            Elements elements = doc.select("body section .demands>ul li");

            DemandList demandList = new DemandList();
            List<Demand> list = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                Demand demand = new Demand();
                demand.setCurNo(i);
                // 标题
                demand.setTitle(elements.get(i).select("a").text());
                // id
                demand.setId(elements.get(i).select("a").attr("href").split("/")[2].split("\\.")[0]);
                // 浏览次数
                demand.setBrowser(elements.get(i).select(".stype").text().replace("次", ""));
                // 发布日期
                demand.setReleaseDate("".equals(elements.get(i).select(".sdate").text()) ? null : elements.get(i).select(".sdate").text());
                list.add(demand);
            }

            // 总标题
            demandList.setList(list);
            demandList.setPage(page);

            demandList.setTotal(doc.select("section .pages span").text().split("有")[1].split("条")[0]);
            demandList.setTotalPage(doc.select("section .pages span").text().split("分")[1].split("页")[0]);
            return demandList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单位需求 详情
     * http://jobs.djtu.edu.cn/Demands/521.html
     *
     * @param id
     */
    public Demand demand(String id) {
        String url = demandUrl + id + ".html";
        HttpGet getNews = new HttpGet(url);

        getNews.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getNews.setHeader("Accept-Encoding", "gzip, deflate");
        getNews.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        getNews.setHeader("Connection", "keep-alive");
        getNews.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");

        HttpResponse response;
        HttpClient client = HttpClients.createDefault();

        try {
            response = client.execute(getNews);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));

            Demand demand = new Demand();
            demand.setId(id);
            // 标题
            demand.setTitle(doc.select("body section .minfo .ititle").first().text());
            // 来源，发布日期，浏览数
            String string = doc.select("body section .minfo .ioth").first().toString();
            if (string.contains("来源")) {
                demand.setFrom(string.split("</span>")[1].split("<span>")[0].trim());
                demand.setReleaseDate(string.split("</span>")[2].split("<span>")[0].trim());
                demand.setBrowser(string.split("</span>")[3].split("</h5>")[0].trim());
            } else {
                demand.setReleaseDate(string.split("</span>")[1].split("<span>")[0].trim());
                demand.setBrowser(string.split("</span>")[2].split("</h5>")[0].trim());
            }

            Elements elements = doc.select("body section .minfo .content>div");
            List<String> list = new ArrayList<>();
            for (int i = 1; i < elements.size(); i++) {
                list.add(elements.get(i).text());
            }
            demand.setContent(list);
            return demand;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 招聘日历
     *
     * @param year
     * @param month
     */
    public List<Calendar> calendar(String year, String month) {
        HttpClient client = HttpClients.createDefault();

        ArrayList<NameValuePair> postData = new ArrayList<>();
        postData.add(new BasicNameValuePair("year", year));
        postData.add(new BasicNameValuePair("month", month));

        HttpPost post = new HttpPost(calendarUrl);
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
        Document doc = null;
        try {
            if (response != null) {
                doc = Jsoup.parse(EntityUtils.toString(response.getEntity()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String string = null;
        if (doc != null) {
            string = doc.select("body").text().replace("\\", "");
        }
        if (string != null) {
            return JSON.parseArray(string.substring(1, string.length() - 1), Calendar.class);
        }
        return null;
    }
}
