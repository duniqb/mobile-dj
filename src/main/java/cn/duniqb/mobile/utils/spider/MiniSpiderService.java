package cn.duniqb.mobile.utils.spider;

import cn.duniqb.mobile.dto.Tip;
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
        if ("undefined".equals(province) || "undefined".equals(city)) {
            return null;
        }
        HttpGet getTip = new HttpGet(tipUrl + province + "&city=" + city);
        System.out.println(tipUrl + province + "&city=" + city);
        getTip.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        getTip.setHeader("Accept-Encoding", "gzip, deflate, br");
        getTip.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        getTip.setHeader("Cache-Control", "max-age=0");
        getTip.setHeader("Connection", "keep-alive");
        getTip.setHeader("Host", "wis.qq.com");
        getTip.setHeader("Sec-Fetch-Mode", "navigate");
        getTip.setHeader("Sec-Fetch-Site", "none");
        getTip.setHeader("Sec-Fetch-User", "?1");
        getTip.setHeader("Upgrade-Insecure-Requests", "1");
        getTip.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");

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
            String string = doc.text().split("tips")[1];
            if (string.contains("forecast")) {
                tips.setTip1(string.split("\"")[6]);
                String str = doc.text().split("tips")[1].split("observe")[1];
                tips.setTip2(str.split("\"")[4]);
                tips.setTip3(str.split("\"")[8]);
            } else {
                tips.setTip1(doc.text().split("tips")[1].split("\"")[6]);
                tips.setTip2(doc.text().split("tips")[1].split("\"")[10]);
            }
            return tips;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
