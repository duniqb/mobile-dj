package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.BookDto;
import cn.duniqb.mobile.dto.profession.Item;
import cn.duniqb.mobile.dto.profession.ProfessionHotDto;
import cn.duniqb.mobile.utils.HttpUtils;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 爬取图书馆
 *
 * @author duniqb
 */
@Service
public class LibrarySpiderService {
    /**
     * 馆藏查询
     *
     * @param name
     * @return
     */
    public List<BookDto> query(String name) {
        // 先获取会变化的某项值
        String url = "http://wxlib.djtu.edu.cn/wx/search.aspx";
        String viewState = "";
        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                viewState = doc.select("body form input").get(0).attr("value");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 构造 POST 参数
        RequestBody requestBody = new FormBody.Builder()
                .add("content2", name)
                .add("searchBooks", "检索")
                .add("__VIEWSTATE", viewState)
                .add("__VIEWSTATEGENERATOR", "B5EA1942")
                .add("__EVENTARGUMENT", "")
                .add("__EVENTTARGET", "")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element div = doc.select("#form1 div").get(2);
                String[] split = div.toString().split("<hr>");
                List<BookDto> list = new ArrayList<>();

                for (int i = 1; i < split.length - 1; i++) {
                    BookDto bookDto = new BookDto();
                    bookDto.setCurNo(split[i].substring(0, 3).trim().replace(".", ""));

                    Document parse = Jsoup.parse(split[i]);
                    String bookUrl = parse.select("a").attr("href");
                    if (bookUrl.contains("id")) {
                        bookDto.setId(bookUrl.split("=")[1].trim());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图书详情
     *
     * @param id
     * @return
     */
    public BookDto show(String id) {
        String url = "http://wxlib.djtu.edu.cn/wx/ShowBook.aspx";

        Map<String, String> map = new HashMap<>();
        map.put("id", id);

        try (Response response = HttpUtils.get(url, map)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 热点图书
     *
     * @return
     */
    public List<BookDto> hot(String url) {
        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 学院列表
     */
    public ProfessionHotDto college() {
        String url = "http://wxlib.djtu.edu.cn/br/ReaderInstitute.aspx";

        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 专业/课程列表
     * major 为空则 http://wxlib.djtu.edu.cn/br/ReaderProfession.aspx?sq=材料科学
     * 否则 http://wxlib.djtu.edu.cn/br/ReaderFenLeiHao.aspx?zy=材料焊接&xy=材料科学
     */
    public ProfessionHotDto major(String college, String major) {
        String url = null;
        // 查询专业列表
        if (major == null && college != null) {
            url = "http://wxlib.djtu.edu.cn/br/ReaderProfession.aspx" + "?sq=" + college;
        } else if (major != null && college != null) {
            url = "http://wxlib.djtu.edu.cn/br/ReaderFenLeiHao.aspx" + "?zy=" + major + "&xy=" + college;
        }

        try (Response response = HttpUtils.get(url, null)) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
