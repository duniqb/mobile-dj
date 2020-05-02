package cn.duniqb.mobile.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * 对 OKHttp 的封装，目的是减少代码中多余的请求头等
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/4/30 22:07
 * @since 1.0
 */
public class HttpUtils {
    static private OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder().build();
    }

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
                formParam.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(url + formParam)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        return okHttpClient.newCall(request).execute();
    }
}
