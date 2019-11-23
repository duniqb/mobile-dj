package cn.duniqb.mobile.utils;

import cn.duniqb.mobile.dto.CardInfo;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 爬取一卡通
 *
 * @author duniqb
 */
@Service
public class CardService {

    /**
     * 获取卡信息的 url
     */
    @Value("${card.infoUrl}")
    private String infoUrl;

    /**
     * 校园卡管理-基本信息
     *
     * @throws Exception
     */
    public CardInfo info(CookieStore cookieStore) {
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpGet httpGet = new HttpGet(infoUrl);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
        httpGet.setHeader("Host", "ykt.djtu.edu.cn");

        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            if (response != null) {
                doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements elements = doc.select("body .userInfoR p");

        CardInfo cardInfo = new CardInfo();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            String[] split = elements.get(i).toString().split("<em>");
            stringBuilder.append(elements.get(i).toString().split("<em>")[1].split("</em>")[0].trim()).append(",")
                    .append(split.length < 3 ? split[1].split("</em>")[0].trim() : split[2].split("</em>")[0].trim()).append(",");
        }
        String[] split = stringBuilder.toString().split(",");
        for (int i = 0; i < split.length; i++) {
            cardInfo.setName(split[0]);
            cardInfo.setStuNo(split[1]);
            cardInfo.setId(split[2]);
            cardInfo.setBalance(split[3]);
            cardInfo.setTransition(split[4]);
            cardInfo.setLossState(split[6]);
            cardInfo.setFrozen(split[7]);
        }
        return cardInfo;
    }
}
