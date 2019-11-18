package cn.duniqb.mobile.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SpiderService {



    /**
     * 获取个人信息
     *
     * @throws Exception
     */
    public void getInfo(HttpClient client) throws Exception {
        HttpResponse response = client.execute(new HttpGet("http://202.199.128.21/academic/showPersonalInfo.do"));
        Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));

        doc.setBaseUri("http://202.199.128.21/");
        Elements elements = doc.select("table.form tr");

        System.out.println("element: " + elements.isEmpty());

        for (Element element : elements) {
            Elements tit = element.select("th");
            Elements info = element.select("td");

            for (int i = 0; i < tit.size(); i++) {
                System.out.println(tit.get(i).text() + ": " + info.get(i).text());
            }
        }
    }

}
