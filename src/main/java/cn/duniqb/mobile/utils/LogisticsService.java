package cn.duniqb.mobile.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 与后勤相关的接口
 */
@Service
public class LogisticsService {
    /**
     * 查询校区 id，返回建筑 id的 url
     */
    @Value("${logistics.detail.distinctIdUrl}")
    private String distinctIdUrl;

    /**
     * 查询建筑 id，返回房间 id的 url
     */
    @Value("${logistics.detail.buildingIdUrl}")
    private String buildingIdUrl;

    /**
     * 查询房间 id，返回设备 id的 url
     */
    @Value("${logistics.detail.roomIdUrl}")
    private String roomIdUrl;

    /**
     * 查询设备 id，返回设备详情的 url
     */
    @Value("${logistics.detail.equipmentIdUrl}")
    private String equipmentIdUrl;

    /**
     * 故障报修 查询各项数据清单
     */
    public String detail(String id, String value) {
        String url = null;
        // 查询校区 id，返回建筑 id
        if ("distinctId".equals(id)) {
            url = distinctIdUrl;
        }
        // 查询建筑 id，返回房间 id
        else if ("buildingId".equals(id)) {
            url = buildingIdUrl;
        }
        // 查询房间 id，返回设备 id
        else if ("roomId".equals(id)) {
            url = roomIdUrl;
        }
        // 查询设备 id，返回设备详情
        else if ("equipmentId".equals(id)) {
            url = equipmentIdUrl;
        }

        HttpResponse response;
        HttpClient client = HttpClients.createDefault();

        try {
            ArrayList<NameValuePair> postData = new ArrayList<>();
            postData.add(new BasicNameValuePair(id, value));

            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(postData));

            post.setHeader("Accept", "application/json");
            post.setHeader("Accept-Encoding", "gzip, deflate, br");
            post.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            post.setHeader("Connection", "keep-alive");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            post.setHeader("Host", "nanqu.56team.com");
            post.setHeader("Origin", "https://nanqu.56team.com");
            post.setHeader("Sec-Fetch-Mode", "cors");
            post.setHeader("Sec-Fetch-Site", "same-origin");
            post.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
            post.setHeader("X-Requested-With", "XMLHttpRequest");

            response = client.execute(post);
            Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
            return doc.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
