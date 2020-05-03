package cn.duniqb.mobile.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 对 OKHttp 的封装，目的是减少代码中多余的请求头等
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/4/30 22:07
 * @since 1.0
 */
@Slf4j
public class HttpUtils {
    /**
     * 封装 get 方法
     *
     * @param url
     * @param getParam
     * @return
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> getParam) throws IOException {
        StringBuilder formParam = new StringBuilder("?");
        if (getParam != null) {
            for (Map.Entry<String, String> entry : getParam.entrySet()) {
                formParam.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 超时时间
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS).build();

        log.info("请求的 URL:" + url + formParam);

        Request request = new Request.Builder()
                .url(url + formParam)

                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        return okHttpClient.newCall(request).execute();
    }

    /**
     * 封装 post 方法
     *
     * @param url
     * @param requestBody
     * @return
     * @throws IOException
     */
    public static Response post(String url, RequestBody requestBody) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 超时时间
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS).build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)

                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        return okHttpClient.newCall(request).execute();

    }
}
