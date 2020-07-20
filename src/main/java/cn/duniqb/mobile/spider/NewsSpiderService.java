package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.news.News;
import cn.duniqb.mobile.dto.news.NewsList;
import cn.duniqb.mobile.entity.ImgUrlEntity;
import cn.duniqb.mobile.service.ImgUrlService;
import cn.duniqb.mobile.utils.HttpUtils;
import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 爬取新闻
 *
 * @author duniqb
 */
@Service
public class NewsSpiderService {

    @Autowired
    private ImgUrlService imgUrlService;

    @Resource
    private OSS ossClient;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;


    /**
     * 新闻列表
     *
     * @param type 1：交大要闻 2：综合报道 ，3：通知公告
     * @param page
     */
    public NewsList list(String type, String page) {
        String url = "";
        if ("1".equals(type)) {
            url = "http://www.djtu.edu.cn/News";
        } else if ("2".equals(type)) {
            url = "http://www.djtu.edu.cn/Report.html";
        } else if ("3".equals(type)) {
            url = "http://www.djtu.edu.cn/Notices.html";
        }

        Map<String, String> map = new HashMap<>();
        map.put("page", page);

        try (Response response = HttpUtils.get(url, map)) {
            if (response.code() == HttpStatus.OK.value()) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Elements elements = doc.select("body section .m .list li");
                NewsList newsList = new NewsList();
                List<News> list = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    News news = new News();
                    // 当前序号
                    news.setCurNo(i);
                    // 唯一 id
                    if (elements.get(i).select("a").attr("href").contains("html")) {
                        news.setId(elements.get(i).select("a").attr("href").split("/")[2].split("\\.")[0]);
                    }
                    // 标题
                    news.setTitle(elements.get(i).select("a").text());
                    // 日期：从列表中获取的
                    news.setDate(elements.get(i).select(".sdate").text());
                    news.setType(type);
                    list.add(news);
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
     * @param type 1：交大要闻 http://www.djtu.edu.cn/News，
     *             2：综合报道 http://www.djtu.edu.cn/Report，
     *             3：通知公告：http://www.djtu.edu.cn/Notices
     * @param id
     */
    public News detail(String type, Integer id) {
        String url = "";
        if ("1".equals(type)) {
            url = "http://www.djtu.edu.cn/News" + "/" + id + ".html";
        } else if ("2".equals(type)) {
            url = "http://www.djtu.edu.cn/Report" + "/" + id + ".html";
        } else if ("3".equals(type)) {
            url = "http://www.djtu.edu.cn/Notices" + "/" + id + ".html";
        }

        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                News news = new News();
                // 唯一 id
                news.setId(String.valueOf(id));
                // 标题
                news.setTitle(doc.select("section .minfo h1").text());
                // 新闻类型
                news.setType(type);

                String string = doc.select("section .minfo h5").text().toString();
                // 发布时间：从详情中获取的
                news.setTime(string.split(" ")[0] + " " + string.split(" ")[1]);
                // 浏览数
                news.setBrowse(string.split("浏览：")[1]);
                if (string.contains("来源")) {
                    // 来源
                    news.setFrom(string.split("来源：")[1].split("浏览：")[0].trim());
                } else {
                    news.setFrom("");
                }

                // 内容
                Elements elements = doc.select("section .minfo .content div[style]");
                List<String> contentList = new ArrayList<>();
                for (org.jsoup.nodes.Element element : elements) {
                    contentList.add(element.text().trim());
                }
                // 准备传输的图片地址
                List<String> imageList = new ArrayList<>();

                // 首先查看本地记录是否有该 id 对应的 url 图片保存
                QueryWrapper<ImgUrlEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("article_id", id);
                List<ImgUrlEntity> imgUrlList = imgUrlService.list(queryWrapper);

                // 本地没有记录，上传图片到 oss
                if (imgUrlList.isEmpty()) {
                    Elements imgElements = doc.select("section .minfo .content div img");
                    for (org.jsoup.nodes.Element imgElement : imgElements) {
                        // https://mobile-dj.oss-cn-beijing.aliyuncs.com/slide/1.jpg
                        // 实际可访问图片地址
                        String ossHost = "https://" + bucket + "." + endpoint + "/";
                        String imgName = saveImage(imgElement.attr("src"));
                        imageList.add(ossHost + imgName);

                        // 新图片信息保存到数据库
                        ImgUrlEntity imgUrlEntity = new ImgUrlEntity();
                        imgUrlEntity.setArticleId(id);
                        // 图片类型，0：新闻图片，1：文章图片，2：失物招领
                        imgUrlEntity.setImgType(0);
                        imgUrlEntity.setUrl(ossHost + imgName);
                        imgUrlService.save(imgUrlEntity);
                    }
                }

                news.setContent(contentList);
                news.setImage(imageList);
                return news;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存新闻详情的图片到 oss
     *
     * @return
     */
    private String saveImage(String imgUrl) throws IOException {
        String date = imgUrl.split("/")[3];
        String uuid = imgUrl.split("/")[4];
        String fileName = "news/" + date + "/" + uuid;

        // 上传网络流
        InputStream inputStream = new URL("http://www.djtu.edu.cn" + imgUrl).openStream();
        ossClient.putObject("mobile-dj", fileName, inputStream);

        return fileName;
    }
}
