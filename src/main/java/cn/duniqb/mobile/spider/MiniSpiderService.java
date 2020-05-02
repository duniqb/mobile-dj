package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.tip.Tip;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * 小程序需要使用的爬虫服务
 *
 * @author duniqb
 */
@Service
public class MiniSpiderService {
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

        String url = "https://wis.qq.com/weather/common?source=pc&weather_type=observe%7Cindex%7Calarm%7Ctips&province=" + province + "&city=" + city;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")

                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Connection", "keep-alive")
                .addHeader("Host", "wis.qq.com")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "none")
                .addHeader("Sec-Fetch-User", "?1")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36")
                .build();

        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 200) {
                    Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
