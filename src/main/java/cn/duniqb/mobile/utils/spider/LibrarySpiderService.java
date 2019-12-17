package cn.duniqb.mobile.utils.spider;

import cn.duniqb.mobile.dto.BookDto;
import cn.duniqb.mobile.dto.profession.Item;
import cn.duniqb.mobile.dto.profession.ProfessionHotDto;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取图书馆
 *
 * @author duniqb
 */
@Service
public class LibrarySpiderService {

    /**
     * 发起馆藏查询的 url
     */
    @Value("${lib.searchUrl}")
    private String searchUrl;

    /**
     * 显示图书详情的 url
     */
    @Value("${lib.showBookUrl}")
    private String showBookUrl;

    /**
     * 学院列表的 url
     */
    @Value("${lib.collegeUrl}")
    private String collegeUrl;

    /**
     * 专业查询的 url
     */
    @Value("${lib.majorUrl}")
    private String majorUrl;

    /**
     * 学院+专业查询的 url
     */
    @Value("${lib.collegeMajorUrl}")
    private String collegeMajorUrl;

    /**
     * 馆藏查询
     *
     * @param name
     * @return
     */
    public List<BookDto> query(String name) {
        HttpClient client = HttpClients.createDefault();

        // 先获取会变化的某项值
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet("http://wxlib.djtu.edu.cn/wx/search.aspx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        String viewState = doc.select("body form input").get(0).attr("value");

        // 构造 POST 参数
        ArrayList<NameValuePair> postData = new ArrayList<>();
        postData.add(new BasicNameValuePair("content2", name));
        postData.add(new BasicNameValuePair("searchBooks", "检索"));
        postData.add(new BasicNameValuePair("__VIEWSTATE", viewState));
        postData.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "B5EA1942"));
        postData.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        postData.add(new BasicNameValuePair("__EVENTTARGET", ""));

        HttpPost post = new HttpPost(searchUrl);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        try {
            post.setEntity(new UrlEncodedFormEntity(postData, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Encoding", "gzip, deflate");
        post.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", "").replace("&gt;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Element div = doc.select("#form1 div").get(2);
        String[] split = div.toString().split("<hr>");
        List<BookDto> list = new ArrayList<>();

        for (int i = 1; i < split.length - 1; i++) {
            BookDto bookDto = new BookDto();
            bookDto.setCurNo(split[i].substring(0, 3).trim().replace(".", ""));

            Document parse = Jsoup.parse(split[i]);
            String url = parse.select("a").attr("href");
            if (url.contains("id")) {
                bookDto.setId(url.split("=")[1].trim());
            }
            bookDto.setBookName(parse.select("a").text());
            if (split[i].contains("<br>")) {
                String[] split1 = split[i].split("<br>");
                bookDto.setAuthor(split1[1].trim());
                bookDto.setIndex(split1[2].trim());
            }
            list.add(bookDto);
        }
        return list;
    }

    /**
     * 图书详情
     *
     * @param id
     * @return
     */
    public BookDto show(String id) {
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(showBookUrl + "?id=" + id));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Element element = doc.select("#form1 div").get(0);
        String[] split = element.toString().split("<br>");
        BookDto bookDto = new BookDto();
        // 复本情况
        List<String> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("MARC状态")) {
                bookDto.setType(split[i].split(">")[1].trim());
            } else if (split[i].contains("题名")) {
                bookDto.setBookName(split[i].split("：")[1].trim());
            } else if (split[i].contains("责任者")) {
                bookDto.setAuthor(split[i].split("：")[1].trim());
            } else if (split[i].contains("出版发行项")) {
                bookDto.setPublisher(split[i].split("：")[1].trim());
            } else if (split[i].contains("索书号")) {
                bookDto.setIndex(split[i].split("：")[1].trim());
            } else if (split[i].contains("提要文摘")) {
                bookDto.setSummary(split[i].split("：")[1].trim());
            } else if (split[i].contains("CALIS")) {
                bookDto.setCALIS(split[i].split("：")[1].trim());
            } else if (split[i].contains("ISBN")) {
                bookDto.setISBN(split[i].split("：")[1].trim());
            } else if (split[i].contains("可借") || split[i].contains("不详") || split[i].contains("留本")) {
                if (split[i].contains("、")) {
                    list.add(split[i].split("、")[1].trim());
                }
            }
        }
        bookDto.setId(id);
        bookDto.setStatus(list);
        return bookDto;
    }


    /**
     * 热点图书
     *
     * @return
     */
    public List<BookDto> hot(String url) {
        HttpClient client = HttpClients.createDefault();
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements elements = doc.select("#form1 article dd a");
        List<BookDto> bookDtoList = new ArrayList<>();
        for (Element element : elements) {
            BookDto bookDto = new BookDto();
            // 当前序号
            bookDto.setCurNo(element.text().split("\\.")[0]);

            // 热度
            String[] split = element.text().split("\\(");
            String last = split[split.length - 1];
            bookDto.setHot(last.replace(")", ""));

            // id
            String string = element.select("a").attr("href");
            if (string.contains("id")) {
                bookDto.setId(string.split("=")[1]);
            }

            // 书名
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 1; i < element.text().split("\\.").length; i++) {
                stringBuffer.append(element.text().split("\\.")[i]);
            }
            bookDto.setBookName(stringBuffer.toString().split("\\(")[0].replace(")", ""));

            bookDtoList.add(bookDto);
        }
        return bookDtoList;
    }

    /**
     * 学院列表
     */
    public ProfessionHotDto college() {
        HttpClient client = HttpClients.createDefault();
        String url = collegeUrl;
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements elements = doc.select("#form1 .cgal_nr ul li");
        ProfessionHotDto professionHotDto = new ProfessionHotDto();
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Item item = new Item();
            item.setCurNo(i);
            item.setName(elements.get(i).text());
            list.add(item);
        }
        professionHotDto.setTitle("院系列表");
        professionHotDto.setList(list);
        return professionHotDto;
    }

    /**
     * 专业/课程列表
     * major 为空则 http://wxlib.djtu.edu.cn/br/ReaderProfession.aspx?sq=材料科学
     * 否则 http://wxlib.djtu.edu.cn/br/ReaderFenLeiHao.aspx?zy=材料焊接&xy=材料科学
     */
    public ProfessionHotDto major(String college, String major) {
        HttpClient client = HttpClients.createDefault();
        String url = null;
        // 查询专业列表
        if (major == null && college != null) {
            url = majorUrl + "?sq=" + college;
        } else if (major != null && college != null) {
            url = collegeMajorUrl + "?zy=" + major + "&xy=" + college;
        }
        HttpResponse response = null;
        try {
            response = client.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            assert response != null;
            doc = Jsoup.parse(EntityUtils.toString(response.getEntity()).replace("&nbsp;", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        ProfessionHotDto professionHotDto = new ProfessionHotDto();
        List<Item> list = new ArrayList<>();
        Elements elements = doc.select("#form1 article dd a");
        for (int i = 0; i < elements.size(); i++) {
            Item item = new Item();
            item.setCurNo(i);
            item.setName(elements.get(i).text());
            String trim = elements.get(i).select("span").attr("title").trim();
            item.setSq("".equals(trim) ? null : trim);
            list.add(item);
        }
        professionHotDto.setTitle(doc.select("#form1 article dt a").text());
        professionHotDto.setList(list);
        return professionHotDto;
    }
}
