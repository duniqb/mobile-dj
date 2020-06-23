package cn.duniqb.mobile.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;

import java.io.IOException;
import java.util.Objects;

/**
 * @author duniqb <duniqb@qq.com>
 * @version v1.0.0
 * @date 2020/5/22 21:40
 * @since 1.8
 */
@Slf4j
public class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();

        System.out.println(TAG + "\n");
        System.out.println(TAG + "----------Start----------------");
        System.out.println(TAG + "| " + request.toString());

        String method = request.method();
        if ("POST".equals(method) || "post".equals(method)) {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < Objects.requireNonNull(body).size(); i++) {
                    sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                }
                sb.delete(sb.length(), sb.length());
                System.out.println(TAG + "| RequestParams:{" + sb.toString() + "}");
            }
        }
        System.out.println(TAG + "| Response:" + content);
        System.out.println(TAG + "----------End:" + duration + "毫秒----------");

        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(content, mediaType))
                .build();
    }
}
