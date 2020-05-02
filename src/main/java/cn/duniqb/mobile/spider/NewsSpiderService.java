package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.news.NewsDto;
import cn.duniqb.mobile.dto.news.NewsList;
import cn.duniqb.mobile.entity.ImgUrlEntity;
import cn.duniqb.mobile.service.ImgUrlService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 爬取新闻
 *
 * @author duniqb
 */
@Service
public class NewsSpiderService {

    @Autowired
    private ImgUrlService imgUrlService;

    /**
     * 文章图片存放的本机文件夹
     */
    @Value("${local.image}")
    private String imagePath;

    /**
     * 本机 url，以供回传图片地址
     */
    @Value("${local.host}")
    private String localhost;

    /**
     * 新闻列表
     *
     * @param type 1：交大要闻 2：综合报道 ，3：通知公告
     * @param page
     */
    public NewsList list(String type, String page) {
        String url = "";
        if ("1".equals(type)) {
            url = "http://www.djtu.edu.cn/News" + "?page=" + page;
        } else if ("2".equals(type)) {
            url = "http://www.djtu.edu.cn/Report.html" + "?page=" + page;
        } else if ("3".equals(type)) {
            url = "http://www.djtu.edu.cn/Notices.html" + "?page=" + page;
        }

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
                Elements elements = doc.select("body section .m .list li");
                NewsList newsList = new NewsList();
                List<NewsDto> list = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    NewsDto newsDto = new NewsDto();
                    // 当前序号
                    newsDto.setCurNo(i);
                    // 唯一 id
                    if (elements.get(i).select("a").attr("href").contains("html")) {
                        newsDto.setId(elements.get(i).select("a").attr("href").split("/")[2].split("\\.")[0]);
                    }
                    // 标题
                    newsDto.setTitle(elements.get(i).select("a").text());
                    // 日期：从列表中获取的
                    newsDto.setDate(elements.get(i).select(".sdate").text());
                    newsDto.setType(type);
                    list.add(newsDto);
                }
                // 新闻类型
                newsList.setType(doc.select("section .totitle a").text().substring(0, 4));
                // 当前页数
                newsList.setPage(doc.select("section .pages ul .active").text());
                newsList.setList(list);
                newsList.setTotal(doc.select("section .pages span").text().split("有")[1].split("条")[0]);
                newsList.setTotalPage(doc.select("section .pages span").text().split("分")[1].split("页")[0]);
                return newsList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新闻详情
     *
     * @param type 1：交大要闻 http://www.djtu.edu.cn/News，2：综合报道 http://www.djtu.edu.cn/Report，3：通知公告：http://www.djtu.edu.cn/Notices
     * @param id
     */
    public NewsDto detail(String type, Integer id) {
        String url = null;
        if ("1".equals(type)) {
            url = "http://www.djtu.edu.cn/News" + "/" + id + ".html";
        } else if ("2".equals(type)) {
            url = "http://www.djtu.edu.cn/Report" + "/" + id + ".html";
        } else if ("3".equals(type)) {
            url = "http://www.djtu.edu.cn/Notices" + "/" + id + ".html";
        }

        OkHttpClient client = new OkHttpClient();
        assert url != null;
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
                NewsDto newsDto = new NewsDto();
                // 唯一 id
                newsDto.setId(String.valueOf(id));
                // 标题
                newsDto.setTitle(doc.select("section .minfo h1").text());
                // 新闻类型
                newsDto.setType(type);

                String string = doc.select("section .minfo h5").text().toString();
                // 发布时间：从详情中获取的
                newsDto.setTime(string.split(" ")[0] + " " + string.split(" ")[1]);
                // 浏览数
                newsDto.setBrowse(string.split("浏览：")[1]);
                if (string.contains("来源")) {
                    // 来源
                    newsDto.setFrom(string.split("来源：")[1].split("浏览：")[0].trim());
                } else {
                    newsDto.setFrom("");
                }

                // 内容
                Elements elements = doc.select("section .minfo .content div[style]");
                List<String> contentList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    contentList.add(elements.get(i).text().trim());
                }
                // 图片地址
                List<String> imageList = new ArrayList<>();
                // 首先查看本地记录是否有该 id 对应的 url 图片保存
                List<ImgUrlEntity> imgUrlList = imgUrlService.listById(String.valueOf(id));
                if (imgUrlList.isEmpty()) {
                    Elements imgElements = doc.select("section .minfo .content div img");
                    for (int i = 0; i < imgElements.size(); i++) {
                        String imgUrl = "img/" + saveImage(imgElements.get(i).attr("src"));
                        imageList.add(localhost + imgUrl);
                        ImgUrlEntity imgUrlEntity = new ImgUrlEntity();
                        imgUrlEntity.setArticleId(id);
                        imgUrlEntity.setId(id);
                        imgUrlEntity.setImgType(0);
                        imgUrlEntity.setUrl(localhost + imgUrl);

                        imgUrlService.save(imgUrlEntity);
                    }
                }

                newsDto.setContent(contentList);
                newsDto.setImage(imageList);
                return newsDto;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存新闻详情的图片 到 oss
     *
     * @return
     */
    private String saveImage(String imgUrl) {
//        HttpGet getVerifyCode = new HttpGet("http://www.djtu.edu.cn" + imgUrl);
//        FileOutputStream fileOutputStream = null;
//        String filename = imgUrl.split("/")[4];
//        try {
//            HttpClient client = HttpClients.createDefault();
//            HttpResponse response = client.execute(getVerifyCode);
//            fileOutputStream = new FileOutputStream(new File(imagePath + filename));
//            response.getEntity().writeTo(fileOutputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fileOutputStream != null) {
//                    fileOutputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return filename;
        return null;
    }
}
