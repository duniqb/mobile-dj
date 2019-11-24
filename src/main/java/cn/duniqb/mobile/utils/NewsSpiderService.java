package cn.duniqb.mobile.utils;

import cn.duniqb.mobile.dto.news.NewsDto;
import cn.duniqb.mobile.dto.news.NewsList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    /**
     * 交大要闻的 url
     */
    @Value("${news.newsUrl}")
    private String newsUrl;

    /**
     * 综合报道的 url
     */
    @Value("${news.reportUrl}")
    private String reportUrl;

    /**
     * 通知公告的 url
     */
    @Value("${news.noticesUrl}")
    private String noticesUrl;

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
                    newsDto.setId(elements.get(i).select("a").attr("href").split("/")[2].split("\\.")[0]);
                    // 标题
                    newsDto.setTitle(elements.get(i).select("a").text());
                    // 日期：从列表中获取的
                    newsDto.setDate(elements.get(i).select(".sdate").text());
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
}
