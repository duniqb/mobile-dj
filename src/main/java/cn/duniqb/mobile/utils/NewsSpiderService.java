package cn.duniqb.mobile.utils;

import cn.duniqb.mobile.domain.ImgUrl;
import cn.duniqb.mobile.dto.news.NewsDto;
import cn.duniqb.mobile.dto.news.NewsList;
import cn.duniqb.mobile.service.ImgUrlService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取新闻
 *
 * @author duniqb
 */
@Service
public class NewsSpiderService {

    @Autowired
    private ImgUrlService imgUrlService;
    /**
     * 交大要闻的 url
     */
    @Value("${news.list.newsUrl}")
    private String newsUrl;

    /**
     * 综合报道的 url
     */
    @Value("${news.list.reportUrl}")
    private String reportUrl;

    /**
     * 通知公告的 url
     */
    @Value("${news.list.noticesUrl}")
    private String noticesUrl;

    /**
     * 交大要闻详情的 url
     */
    @Value("${news.detail.newsDetailUrl}")
    private String newsDetailUrl;

    /**
     * 综合报道详情的 url
     */
    @Value("${news.detail.reportDetailUrl}")
    private String reportDetailUrl;

    /**
     * 通知公告详情的 url
     */
    @Value("${news.detail.noticesDetailUrl}")
    private String noticesDetailUrl;

    /**
     * 文章图片存放的本机文件夹
     */
    @Value("${news.imagePath}")
    private String imagePath;

    /**
     * 学校主站地址
     */
    @Value("${news.schoolHost}")
    private String schoolHost;

    /**
     * 本机 url，以供回传图片地址
     */
    @Value("${jw.localhost}")
    private String localhost;


    /**
     * 新闻列表
     *
     * @param type 1：交大要闻 2：综合报道 ，3：通知公告
     * @param page
     */
    public NewsList list(String type, String page) {
        String url = null;
        if ("1".equals(type)) {
            url = newsUrl + "?page=" + page;
        } else if ("2".equals(type)) {
            url = reportUrl + "?page=" + page;
        } else if ("3".equals(type)) {
            url = noticesUrl + "?page=" + page;
        }
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
            if (response.toString().contains("200")) {
                Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));
                Elements elements = doc.select("body section .m .list li");
                NewsList newsList = new NewsList();
                List<NewsDto> list = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    NewsDto newsDto = new NewsDto();
                    // 当前序号
                    newsDto.setCurNo(i);
                    // 唯一 id
                    if (elements.get(i).select("a").attr("href").contains("html")) {
                        newsDto.setId(elements.get(i).select("a").attr("href").split("/")[2].split("\\.")[0]);
                    }
                    // 标题
                    newsDto.setTitle(elements.get(i).select("a").text());
                    // 日期：从列表中获取的
                    newsDto.setDate(elements.get(i).select(".sdate").text());
                    newsDto.setType(type);
                    list.add(newsDto);
                }
                // 新闻类型
                newsList.setType(doc.select("section .totitle a").text().substring(0, 4));
                // 当前页数
                newsList.setPage(doc.select("section .pages ul .active").text());
                newsList.setList(list);
                newsList.setTotal(doc.select("section .pages span").text().split("有")[1].split("条")[0]);
                newsList.setTotalPage(doc.select("section .pages span").text().split("分")[1].split("页")[0]);
                return newsList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新闻详情
     *
     * @param type 1：交大要闻 http://www.djtu.edu.cn/News，2：综合报道 http://www.djtu.edu.cn/Report，3：通知公告：http://www.djtu.edu.cn/Notices
     * @param id
     */
    public NewsDto detail(String type, String id) {
        String url = null;
        if ("1".equals(type)) {
            url = newsDetailUrl + "/" + id + ".html";
        } else if ("2".equals(type)) {
            url = reportDetailUrl + "/" + id + ".html";
        } else if ("3".equals(type)) {
            url = noticesDetailUrl + "/" + id + ".html";
        }
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
            if (response.toString().contains("200")) {
                Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));
                NewsDto newsDto = new NewsDto();
                // 唯一 id
                newsDto.setId(id);
                // 标题
                newsDto.setTitle(doc.select("section .minfo h1").text());
                // 新闻类型
                newsDto.setType(type);
                // 发布时间：从详情中获取的
                newsDto.setTime(doc.select("section .minfo h5").toString().split("<span>")[0].split("ioth\">")[1].trim());
                // 来源
                newsDto.setFrom(doc.select("section .minfo h5").toString().split("</span>")[1].split("<span>")[0].trim());
                // 浏览数
                newsDto.setBrowse(doc.select("section .minfo h5").toString().split("</span>")[2].split("</h5>")[0]);
                // 内容
                Elements elements = doc.select("section .minfo .content div[style]");
                List<String> contentList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    contentList.add(elements.get(i).text().trim());
                }
                // 图片地址
                List<String> imageList = new ArrayList<>();
                // 首先查看本地记录是否有该 id 对应的 url 图片保存
                List<ImgUrl> imgUrlList = imgUrlService.findByNewsId(id);
                if (imgUrlList.isEmpty()) {
                    Elements imgElements = doc.select("section .minfo .content div img");
                    for (int i = 0; i < imgElements.size(); i++) {
                        String imgUrl = "img/" + saveImage(imgElements.get(i).attr("src"));
                        imageList.add(localhost + imgUrl);
                        imgUrlService.insert(id, localhost + imgUrl);
                    }
                }
                for (ImgUrl imgUrl : imgUrlList) {
                    imageList.add(imgUrl.getUrl());
                }
                newsDto.setContent(contentList);
                newsDto.setImage(imageList);
                return newsDto;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存新闻详情的图片
     *
     * @return
     */
    private String saveImage(String imgUrl) {
        HttpGet getVerifyCode = new HttpGet(schoolHost + imgUrl);
        FileOutputStream fileOutputStream = null;
        String filename = imgUrl.split("/")[4];
        try {
            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(getVerifyCode);
            fileOutputStream = new FileOutputStream(new File(imagePath + filename));
            response.getEntity().writeTo(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileOutputStream != null;
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filename;
    }
}
