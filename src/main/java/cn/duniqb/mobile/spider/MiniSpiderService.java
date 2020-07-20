package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.mini.AccessToken;
import cn.duniqb.mobile.dto.mini.SecurityCheck;
import cn.duniqb.mobile.dto.tip.Tip;
import cn.duniqb.mobile.utils.HttpUtils;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 小程序需要使用的爬虫服务
 *
 * @author duniqb
 */
@Service
public class MiniSpiderService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 小程序配置
     */
    @Value("${mini.appId}")
    private String appId;

    /**
     * 小程序配置
     */
    @Value("${mini.secret}")
    private String secret;

    /**
     * AccessToken 在 Redis 里前缀
     */
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

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

    /**
     * 文本安全检查
     *
     * @param content
     * @param accessToken
     * @return
     */
    public SecurityCheck msgSecCheck(String content, String accessToken) {
        String url = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=" + accessToken;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        String json = "{\"content\": \"" + content + "\"}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=UTF-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == HttpStatus.OK.value()) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                String res = doc.select("html body").first().text();
                return JSON.parseObject(res, SecurityCheck.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取小程序全局唯一后台接口调用凭据
     *
     * @return
     */
    public AccessToken getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credential");
        map.put("appid", appId);
        map.put("secret", secret);

        try (Response response = HttpUtils.get(url, map)) {
            if (response.code() == HttpStatus.OK.value()) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                String json = doc.select("html body").first().text();
                return JSON.parseObject(json, AccessToken.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
