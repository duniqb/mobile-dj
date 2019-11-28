package cn.duniqb.mobile.utils;

import cn.duniqb.mobile.domain.Tip;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 小程序需要使用的爬虫服务
 *
 * @author duniqb
 */
@Service
public class MiniSpiderService {
    /**
     * 查询校区 id，返回建筑 id的 url
     */
    @Value("${mini.spider.tipUrl}")
    private String tipUrl;

    /**
     * 提示信息，天气等
     *
     * @param province
     * @param city
     */
    public Tip tip(String province, String city) {
        HttpGet getTip = new HttpGet(tipUrl + province + "&city=" + city);

        getTip.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getTip.setHeader("Accept-Encoding", "gzip, deflate, br");
        getTip.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4,de-DE;q=0.2");
        getTip.setHeader("Connection", "keep-alive");
        getTip.setHeader("Host", "wis.qq.com");
        getTip.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0");

        HttpResponse response;
        HttpClient client = HttpClients.createDefault();

        try {
            response = client.execute(getTip);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("amp;", ""));
            Tip tips = new Tip();
            // 温度
            tips.setDegree(doc.text().split("degree")[1].split("\"")[2]);
            // 晴天
            tips.setWeather(doc.text().split("weather")[1].split("\"")[2]);
            // 风寒
            tips.setChill(doc.text().split("chill")[1].split("\"")[4]);
            // 化妆
            tips.setMakeup(doc.text().split("makeup")[1].split("\"")[4]);
            // 感冒
            tips.setClod(doc.text().split("cold")[1].split("\"")[4]);
            // 提示 两条
            tips.setTip1(doc.text().split("tips")[1].split("\"")[6]);
            tips.setTip2(doc.text().split("tips")[1].split("\"")[10]);
            return tips;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
