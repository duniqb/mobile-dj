package cn.duniqb.mobile.spider;

import cn.duniqb.mobile.dto.Book;
import cn.duniqb.mobile.dto.profession.Item;
import cn.duniqb.mobile.dto.profession.ProfessionHot;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    public List<Book> query(String name) {
        // 先获取会变化的某项值
        String url = "http://wxlib.djtu.edu.cn/wx/search.aspx";
        String viewState = "";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                viewState = doc.select("body form input").get(2).attr("value");

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

        Request request2 = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .build();

        try (Response response = client.newCall(request2).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element div = doc.select("#form1 div").get(2);
                String[] split = div.toString().split("<hr>");
                List<Book> list = new ArrayList<>();

                for (int i = 1; i < split.length - 1; i++) {
                    Book book = new Book();
                    book.setCurNo(split[i].substring(0, 3).trim().replace(".", ""));

                    Document parse = Jsoup.parse(split[i]);
                    String bookUrl = parse.select("a").attr("href");
                    if (bookUrl.contains("id")) {
                        book.setId(bookUrl.split("=")[1].trim());
                    }
                    book.setBookName(parse.select("a").text());
                    if (split[i].contains("<br>")) {
                        String[] split1 = split[i].split("<br>");
                        book.setAuthor(split1[1].trim());
                        book.setIndex(split1[2].trim());
                    }
                    list.add(book);
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
    public Book show(String id) {
        String url = "http://wxlib.djtu.edu.cn/wx/ShowBook.aspx?id=" + id;
        OkHttpClient client = new OkHttpClient.Builder()
                // 超时时间
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Element element = doc.select("#form1 div").get(2);
                String[] split = element.toString().split("<br>");
                Book book = new Book();
                // 复本情况
                List<String> list = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    if (split[i].contains("MARC状态")) {
                        book.setType(split[i].split(">")[1].trim());
                    } else if (split[i].contains("题名")) {
                        book.setBookName(split[i].split("：")[1].trim());
                    } else if (split[i].contains("责任者")) {
                        book.setAuthor(split[i].split("：")[1].trim());
                    } else if (split[i].contains("出版发行项")) {
                        book.setPublisher(split[i].split("：")[1].trim());
                    } else if (split[i].contains("索书号")) {
                        book.setIndex(split[i].split("：")[1].trim());
                    } else if (split[i].contains("提要文摘")) {
                        book.setSummary(split[i].split("：")[1].trim());
                    } else if (split[i].contains("CALIS")) {
                        book.setCALIS(split[i].split("：")[1].trim());
                    } else if (split[i].contains("ISBN")) {
                        book.setISBN(split[i].split("：")[1].trim());
                    } else if (split[i].contains("可借") || split[i].contains("不详") || split[i].contains("留本")) {
                        if (split[i].contains("、")) {
                            list.add(split[i].split("、")[1].trim());
                        }
                    }
                }
                book.setId(id);
                book.setStatus(list);
                return book;
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
    public List<Book> hot(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Elements elements = doc.select("#form1 article dd a");
                List<Book> bookList = new ArrayList<>();
                for (Element element : elements) {
                    Book book = new Book();
                    // 当前序号
                    book.setCurNo(element.text().split("\\.")[0]);

                    // 热度
                    String[] split = element.text().split("\\(");
                    String last = split[split.length - 1];
                    book.setHot(last.replace(")", ""));

                    // id
                    String string = element.select("a").attr("href");
                    if (string.contains("id")) {
                        book.setId(string.split("=")[1]);
                    }

                    // 书名
                    StringBuilder stringBuffer = new StringBuilder();
                    for (int i = 1; i < element.text().split("\\.").length; i++) {
                        stringBuffer.append(element.text().split("\\.")[i]);
                    }
                    book.setBookName(stringBuffer.toString().split("\\(")[0].replace(")", ""));

                    bookList.add(book);
                }
                return bookList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 学院列表
     */
    public ProfessionHot college() {
        String url = "http://wxlib.djtu.edu.cn/br/ReaderInstitute.aspx";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                Elements elements = doc.select("#form1 .cgal_nr ul li");
                ProfessionHot professionHot = new ProfessionHot();
                List<Item> list = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    Item item = new Item();
                    item.setCurNo(i);
                    item.setName(elements.get(i).text());
                    list.add(item);
                }
                professionHot.setTitle("院系列表");
                professionHot.setList(list);
                return professionHot;
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
    public ProfessionHot major(String college, String major) {
        String url = null;
        // 查询专业列表
        if (major == null && college != null) {
            url = "http://wxlib.djtu.edu.cn/br/ReaderProfession.aspx" + "?sq=" + college;
        } else if (major != null && college != null) {
            url = "http://wxlib.djtu.edu.cn/br/ReaderFenLeiHao.aspx" + "?zy=" + major + "&xy=" + college;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).string().replace("&nbsp;", "").replace("amp;", ""));
                ProfessionHot professionHot = new ProfessionHot();
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
                professionHot.setTitle(doc.select("#form1 article dt a").text());
                professionHot.setList(list);
                return professionHot;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
