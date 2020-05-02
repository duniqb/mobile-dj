package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.repair.*;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 与后勤相关的接口
 */
@Service
public class RepairSpiderService {
    /**
     * 报修网站的主机
     */
    @Value("${repair.host}")
    private String repairHost;

    /**
     * 故障报修 查询各项数据清单
     */
    public String data(String id, String value) {
        String url = null;
        // 查询校区 id，返回建筑 id
        if ("distinctId".equals(id)) {
            url = repairHost + "/web/app/user/buildings.action";
        }
        // 查询建筑 id，返回房间 id
        else if ("buildingId".equals(id)) {
            url = repairHost + "/web/app/user/placeRoom.action";
        }
        // 查询房间 id，返回设备 id
        else if ("roomId".equals(id)) {
            url = repairHost + "/web/app/user/equipment.action";
        }
        // 查询设备 id，返回设备详情
        else if ("equipmentId".equals(id)) {
            url = repairHost + "/web/app/user/equipment/detail.action";
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add(id, value)
                .build();

        assert url != null;
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .addHeader("Host", "nanqu.56team.com")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                return JSON.toJSONString(doc.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据报修手机号查询报修列表
     *
     * @param userTel
     */
    public List<RepairDetail> list(String userTel) {
        String url = repairHost + "/web/app/user/select/all/maintenance.action";
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("userTel", userTel)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                // 重要的 Header
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Host", "nanqu.56team.com")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Sec-Fetch-User", "?1")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Elements elements = doc.select("div.page-current div.content>a");
                List<RepairDetail> list = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    RepairDetail repairDetail = new RepairDetail();
                    if (elements.get(i).select("form input[value]").last() != null) {
                        repairDetail.setShowEvaluate(true);
                    } else {
                        repairDetail.setShowEvaluate(false);
                    }
                    repairDetail.setPhone(userTel);
                    // 报修时间
                    repairDetail.setDate(elements.get(i).select("p.color-gray").text().split("\\.")[0]);
                    // 标题
                    repairDetail.setTitle(elements.get(i).select("a .card-content-inner>p[style]").last().text());
                    // 报修单号
                    repairDetail.setId(elements.get(i).select("div.color-gray").text().split(":")[1]);
                    // 提交状态
                    repairDetail.setState(elements.get(i).select("div.card-content div[style] div[style] p").text());
                    // 链接
                    repairDetail.setListNumber(elements.get(i).select("a").attr("href").split("/")[6].split("\\.")[0]);

                    list.add(repairDetail);
                }
                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 报修单详情
     */
    public RepairDetail detail(String listNumber) {
        String url = repairHost + "/web/app/user/select/oneMaintenance/" + listNumber + ".action";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element element = doc.select("div.page-current div.card-content-inner").first();

                RepairDetail repairDetail = new RepairDetail();
                repairDetail.setShowEvaluate(element.select("form input[value]").last() != null);
                repairDetail.setDate(element.select("p.color-gray").text().split("\\.")[0]);
                if (element.select("p[style]").get(0).text().contains("_")) {
                    repairDetail.setTitle(element.select("p[style]").first().text());
                } else {
                    repairDetail.setTitle(element.select("p[style]").get(2).text());
                }
                repairDetail.setRoom(element.select("div.card-content-inner>p[style]").last().text().split(" ")[1]);
                repairDetail.setDescription(element.select("div.card-content-inner>p[style]").last().text().split(" ")[2]);
                repairDetail.setId(element.select("div.color-gray").text().split(":")[1]);
                repairDetail.setState(element.select("div.card-content-inner>div[style] p").text());
                repairDetail.setListNumber(listNumber);
                List<TimeLine> timeLineList = new ArrayList<>();

                Elements timelineElement = doc.select("div.page-current div.card-content-inner").last().select("ul li");
                for (Element value : timelineElement) {
                    TimeLine timeLine = new TimeLine();
                    timeLine.setTime(value.select(".item-title").text().split("\\.")[0]);
                    timeLine.setComment(value.select(".item-after").text());
                    timeLineList.add(timeLine);
                }
                repairDetail.setTimeLineList(timeLineList);
                return repairDetail;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 最新通知
     */
    public Notice notice() {
        String url = repairHost + "/web/app/user/index.action";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element element = doc.select("div.bottom_inside a").first();

                Notice notice = new Notice();
                notice.setTitle(element.select(".bottom_inside_title").text());
                notice.setContent(element.select(".bottom_inside_art").text());
                notice.setDate(element.select(".inscription").text());

                return notice;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 最近维修数量
     */
    public List<Recent> recent() {
        String url = repairHost + "/web/app/user/my/maintenance/router.action";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Elements elements = doc.select("div.content>div.list-block>ul>li");
                List<Recent> list = new ArrayList<>();

                for (int i = 1; i < elements.size(); i++) {
                    Recent recent = new Recent();
                    recent.setArea(elements.get(i).select(".item-title").text());
                    String reported = elements.get(i).select(".item-after").text().split("/")[0].split("条")[0];
                    recent.setReported(reported);
                    String repaired = elements.get(i).select(".item-after").text().split("/")[1].split("条")[0];
                    recent.setRepaired(repaired);
                    recent.setPending(String.valueOf(Integer.parseInt(reported) - Integer.parseInt(repaired)));
                    list.add(recent);
                }
                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 发起报修
     *
     * @param phone
     * @param distinctId
     * @param buildingId
     * @param roomId
     * @param equipmentId
     * @param listDescription
     * @return
     */
    public Report report(String phone, String distinctId, String buildingId, String roomId, String equipmentId, String listDescription) {
        String url = repairHost + "/web/app/user/add/do.action";

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        builder.addFormDataPart("userTel", phone)
                .addFormDataPart("distinctId", distinctId)
                .addFormDataPart("buildingId", buildingId)
                .addFormDataPart("roomId", roomId)
                .addFormDataPart("equipmentId", equipmentId)
                .addFormDataPart("listDescription", listDescription).build();
        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                if (doc.text().contains("我们已经受理您的报修")) {
                    Report report = new Report();
                    Elements elements = doc.select(".content .card .card-content-inner .list-block ul li");
                    for (int i = 0; i < elements.size(); i++) {
                        if (elements.get(i).text().contains("报修单号")) {
                            report.setId(elements.get(i).select(".item-after").text());
                        } else if (elements.get(i).text().contains("报修时间")) {
                            report.setTime(elements.get(i).select(".item-after").text().split("\\.")[0]);
                        } else if (elements.get(i).text().contains("您的手机")) {
                            report.setPhone(elements.get(i).select(".item-after").text());
                        } else if (elements.get(i).text().contains("您所在校区的待办报修单数")) {
                            report.setPending(elements.get(i).select(".item-after").text().split("条")[0]);
                        } else if (elements.get(i).text().contains("您所在校区的在办报修单数")) {
                            report.setRepairing(elements.get(i).select(".item-after").text().split("条")[0]);
                        }
                    }
                    return report;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 评价
     */
    public Integer evaluate(String listNumber, String phone, String listScore, String listWord) {
        String url = repairHost + "/web/app/user/judgement/do.action";

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("listNumber", listNumber)
                .add("userTel", phone)
                .add("listScore", listScore)
                .add("listWord", listWord)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
