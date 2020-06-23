package cn.duniqb.mobile.jw;

import cn.duniqb.mobile.interceptor.LogInterceptor;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Objects;

/**
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/28 10:28
 * @since 1.8
 */
public class Login {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 教务主机 ip
     */
    @Value("${jw.host}")
    private String host;

    /**
     * Cookie 在 Redis 里的前缀
     */
    private static final String COOKIE = "COOKIE";

    private String login(String sessionId, String stuNo, String password, String verifyCode) {
        System.out.println(sessionId);
        // 拿到该用户的 Cookie
        String cookie =redisUtil.get(COOKIE + ":" + sessionId);
        System.out.println(cookie + "...");

        System.out.println("Redis 获取到的 cookie:" + cookie);
        System.out.println("stuNo:" + stuNo);
        System.out.println("password:" + password);
        System.out.println("verifyCode:" + verifyCode);
        System.out.println("sessionId:" + sessionId);

        String url = "http://" + host + "/academic/j_acegi_security_check";

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();
        FormBody requestBody = new FormBody.Builder()
                .add("j_username", stuNo)
                .add("j_password", password)
                .add("j_captcha", verifyCode)
                .build();

        // 构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Length", String.valueOf(requestBody.contentLength()))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cookie", cookie)
                .addHeader("Host", "jw.djtu.edu.cn")
                .addHeader("Origin", "http://jw.djtu.edu.cn")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                return doc.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Login login = new Login();
        login.login("SESSION_ID:e577b7ec45844b9facb03893c09c506f", "1821010431", "62052219950825133X", "2064");
    }
}
