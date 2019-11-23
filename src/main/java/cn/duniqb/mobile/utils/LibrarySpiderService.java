package cn.duniqb.mobile.utils;

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

        // 构造 POST 参数
        ArrayList<NameValuePair> postData = new ArrayList<>();
        postData.add(new BasicNameValuePair("content2", name));
        postData.add(new BasicNameValuePair("searchBooks", "检索"));
        postData.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwULLTExMzM4OTQyNTUPFgYeA3BhZwUCMzAeCGFsbENvdW50BQQxNTM5HgR3b3JkBQPlpb0WAgIBD2QWAgIFDxYCHgRUZXh0BYYnPGJyIC8+5oC76K6hOjE1MzkmbmJzcDsmbmJzcDs8YSBocmVmPSJqYXZhc2NyaXB0Ol9fZG9Qb3N0QmFjaygnZm4nLCdDOzE1Mzk7MTszMCcpIj7pppbpobU8L2E+Jm5ic3A7PGEgaHJlZj0iamF2YXNjcmlwdDpfX2RvUG9zdEJhY2soJ2ZuJywnQzsxNTM5OzE7MzAnKSI+5LiK6aG1PC9hPiZuYnNwOzImbmJzcDs8YSBocmVmPSJqYXZhc2NyaXB0Ol9fZG9Qb3N0QmFjaygnZm4nLCdDOzE1Mzk7MzszMCcpIj7kuIvpobU8L2E+Jm5ic3A7PGEgaHJlZj0iamF2YXNjcmlwdDpfX2RvUG9zdEJhY2soJ2ZuJywnQzsxNTM5OzUyOzMwJykiPuWwvumhtTwvYT48aHIgLz4zMS48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwODk0NDYiPuS4u+WKqOacjeWKoTrmpoLlv7XjgIHnu5PmnoTkuI7lrp7njrA8L2E+PGJyIC8+5byg5bCn5a2mLCDmlrnlrZjlpb3okZcs56eR5a2m5Ye654mI56S+PGJyIC8+VFAzOTMuNC82MDI1PGhyIC8+MzIuPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDc4MTc0Ij7kuobop6PkvaDnmoTku7flgLw6566h5aW96Ieq5bex55qE55+l6K+G5bm25LuO5Lit6I635YipPC9hPjxiciAvPijoi7Ep57Gz5YWLwrfmn6/mma4oTWlrZSBDb3BlKeiRlyznlLXlrZDlt6XkuJrlh7rniYjnpL48YnIgLz5DOTEyLjEvNDE3NDxociAvPjMzLjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA3MzI5MiI+5Lq655qE5LyY5Yq/OumAmui/h+abtOWlveeahOmBtOmAieS4juS4mue7qeaUueWWhOe7j+iQpeaIkOaenDwvYT48YnIgLz4o6IuxKeWGhee7tOWwlMK36LSd5oGpLCDmr5TlsJTCt+aiheS9qeiRlyznu4/mtY7nrqHnkIblh7rniYjnpL48YnIgLz5GMjcyLjkyLzYyMDA8aHIgLz4zNC48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNjY2OTciPuWImOawuOWlveWIm+mAoOi0ouWvjOeahDY2562W55WlPC9hPjxiciAvPui/n+WPjOaYjue8luiRlyzlvZPku6PkuJbnlYzlh7rniYjnpL48YnIgLz5GMjc5LjI0NS83Mjg4PGhyIC8+MzUuPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDg2NTExIj7liJvpgKDlpb3ov5A8L2E+PGJyIC8+KOe+jinpqazlhYvCt+exs+WwlOaWryhNYXJjIE15ZXJzKeiRlyzlm73pmYXmlofljJblh7rniYjlhazlj7g8YnIgLz5CODQ4LjQvODE4MDxociAvPjM2LjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA3ODE0NyI+5aW955yL5bCP6K+0PC9hPjxiciAvPuOAiuWMl+S6rOaWh+WtpuOAiyDnvJbovpHpg6jnvJYs54+g5rW35Ye654mI56S+PGJyIC8+STI0Ny43LzYzNDUoMSk8aHIgLz4zNy48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwMDQyOTIiPuWlveiOseWdnuWQjeeJh+mAj+inhjwvYT48YnIgLz7lkajpu47mmI7okZcs5bm/5Lic5Lq65rCR5Ye654mI56S+PGJyIC8+STEwNi4zMy82Mzk4PGhyIC8+MzguPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDY2NjQxIj7lpb3ojrHlnZ7njrDlnLrmiqXpgZM8L2E+PGJyIC8+5ZGo6buO5piO6JGXLOa1t+WNl+WHuueJiOekvjxiciAvPko5MDUuNzEyLzYzOTg8aHIgLz4zOS48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNjE4ODEiPuWunuWKoeaTjeS9nDrot5/miJHlrabpooblr7zmioDmnK88L2E+PGJyIC8+5ZGo5oyv5p6X77yM546L5oiQ546J77yM6Zm25reR6Imz5Li757yWLOS4reWbvee7j+a1juWHuueJiOekvjxiciAvPkM5MzMvNjM2OTxociAvPjQwLjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA1NzQ5MiI+5bCP55qE5piv576O5aW955qEPC9hPjxiciAvPlvoi7Fd6IiS6ams6LWr6JGXLOWVhuWKoeWNsOS5pummhjxiciAvPkYxMTIvODQ4NTxociAvPjQxLjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA4NjcwOSI+5byg5ZW45p6X56eY5LygOuS4iua1t+a7qeacgOWlveaWl+eahOWPmOiJsum+mTwvYT48YnIgLz7lj7jpqazng4jkurrokZcs5Lit5Zu95paH5Y+y5Ye654mI56S+PGJyIC8+SzgyOC45Lzg4OTk8aHIgLz40Mi48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNzA2NjAiPuW9k+S7o+aXpeaxieWPjOino+i+nuWFuDwvYT48YnIgLz7kuK3nlLDlub/pg47vvIzpgpPlkK/mmIznvJbokZcs5Lit5pel5Y+L5aW956CU56m256S+PGJyIC8+SDM2Ni82MTM5PGhyIC8+NDMuPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDU1NDM3Ij7lvZPlpb3oh6rlt7HnmoTigJzllabllabpmJ/igJ0tLS0t6Ieq5oiR5r+A5YqxPC9hPjxiciAvPuacseWwkeWNju+8jOm+muW5s+iRlyzkuIrmtbfotKLnu4/lpKflrablh7rniYjnpL48YnIgLz5ENDMyLjYzIC82NDg1PGhyIC8+NDQuPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDc2NzkwIj7mgI7moLflvZPlpb3lhazlj7jnu4/nkIY8L2E+PGJyIC8+6ZmI6bmk55qL5Li757yWLOeJqei1hOWHuueJiOekvjxiciAvPkYyNzIuOTEvNzE1MzxociAvPjQ1LjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA0OTU1NiI+5oCO5qC35b2T5aW955Sf5Lqn6LWE5paZ5L+h5omY5pyN5Yqh5ZGYPC9hPjxiciAvPuiNo+S6qOajo+iRlyznianotKjlh7rniYjnpL48YnIgLz5GNzIxLzkzNTA8aHIgLz40Ni48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNjk5MzEiPuaAjuagt+aJvuS4gOS4quWlveW3peS9nDwvYT48YnIgLz7lva3mlrDmnbDnvJbokZcs5rW35rSL5Ye654mI56S+PGJyIC8+QzkxMy4yLzcxNTM8aHIgLz40Ny48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNDg1ODMiPuaWsOWNjuekvuWlveeov+mAiS4xOTg35bm0PC9hPjxiciAvPuaWsOWNjuekvuaWsOmXu+eglOeptuaJgOe8lizmlrDljY7lh7rniYjnpL48YnIgLz5HMjE5LjE0LzU1ODU8aHIgLz40OC48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNzY2NTkiPuaWueS4juWchjrotaLlnKjmgbDliLDlpb3lpIQ8L2E+PGJyIC8+5LmQ5rqQ6JGXLOmHkeWfjuWHuueJiOekvjxiciAvPkI4NDguNC85MTI0PGhyIC8+NDkuPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDYzMDgyIj7ml6Dnur/nlLXniLHlpb3ogIXor7vmnKwu5LiK5Lit5LiL5YaMPC9hPjxiciAvPuacrOS5pue8luWGmee7hOe8luiRlyzkurrmsJHpgq7nlLXlh7rniYjnpL48YnIgLz5UTjgwLTQ5LzY4NjUoMSk8aHIgLz41MC48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNzYxNzMiPuacgOWlveeahOi+qeaKpDwvYT48YnIgLz4o576OKeiJvuS8psK35b636IKW5b6u6Iyo6JGXLOazleW+i+WHuueJiOekvjxiciAvPkQ5NzEuMjY1LzA1NDc8aHIgLz41MS48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNjY2NjgiPuatu+iAheeahOecvOedmzrplb/nr4fmgZDmgJblsI/or7Q8L2E+PGJyIC8+5L2Z5Lul6ZSu6JGXLOS4reWbveeUteW9seWHuueJiOekvjxiciAvPkkyNDcuNS8yNTIzPGhyIC8+NTIuPGEgaHJlZj0iU2hvd0Jvb2suYXNweD9pZD0wMDAwMDc0ODcxIj7msYLogYzlsLHkuJrmjIfljZc8L2E+PGJyIC8+5ZGo5Zu95pilLCDorrjlpb3kuIcsIOeOi+azouiRlyzlhpzmnZHor7vnianlh7rniYjnpL48YnIgLz5DOTEzLjIvNjMzNzxociAvPjUzLjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA3NzA1MyI+55S15a2Q5py65qKw5YWl6ZeoPC9hPjxiciAvPijml6Up5paw55S15rCU57yW6L6R6YOo57yWLOenkeWtpuWHuueJiOekvjxiciAvPlRILTM5LzUwNDY8aHIgLz41NC48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNjI3MDEiPueUteWtkOeIseWlveiAheWunueUqOaJi+WGjDwvYT48YnIgLz7pmYjlm73ljY7nvJYs5Lq65rCR6YKu55S15Ye654mI56S+PGJyIC8+VE4tNjIvNzEzNTxociAvPjU1LjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA3OTcxNyI+55S15a2Q55S16LevKOS4iik8L2E+PGJyIC8+KOaXpSkg6Zuo5a6r5aW95paH6JGXLOenkeWtpuWHuueJiOekvjxiciAvPlRONzEwLzIzNTQ8aHIgLz41Ni48YSBocmVmPSJTaG93Qm9vay5hc3B4P2lkPTAwMDAwNzk3MTgiPueUteWtkOeUtei3ryjkuIspPC9hPjxiciAvPijml6UpIOmbqOWuq+WlveaWh+iRlyznp5Hlrablh7rniYjnpL48YnIgLz5UVE43MTAvMjM1NDxociAvPjU3LjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA2MjM5NCI+55m+5oqY5LiN5oygOuWBmuWlveWIm+S4mueahOW/g+eQhuWHhuWkhzwvYT48YnIgLz7vvIjnvo7vvInln4Pov6rlhYvokZcs5aSp5rSl56eR5oqA57+76K+R5Ye654mI5YWs5Y+4PGJyIC8+RjI3OS43MTIvMDIwNDxociAvPjU4LjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA1MDEzOSI+57uP6JCl566h55CG5YWo6ZuGLjE3Oue+juWlveeahOaYjuWkqTwvYT48YnIgLz7mnb7kuIvlubjkuYvliqnokZcs5ZCN5Lq65Ye654mI5LqL5Lia6IKh5Lu95pyJ6ZmQ5YWs5Y+4PGJyIC8+RjQzMS4zNS84NTU2KDE2KTxociAvPjU5LjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA2MDQ1MSI+57uZ5Lq65aW95Y2w6LGh55qE6Ieq5oiR6KGo546w5pyvPC9hPjxiciAvPigp5aSa5rmW6L6J6JGXLOWkp+WxleWHuueJiOekvjxiciAvPkI4NDIvMDQ1NTxociAvPjYwLjxhIGhyZWY9IlNob3dCb29rLmFzcHg/aWQ9MDAwMDA3NTI3MiI+6Ziz5YWJ55S35a2pIOS9oOWHhuWkh+WlveS6huWQlz88L2E+PGJyIC8+546L5bGx57Gz5Li757yWLOenkeWtpuaKgOacr+aWh+eMruWHuueJiOekvjxiciAvPkc0NzkvNDA4ODxociAvPuaAu+iuoToxNTM5Jm5ic3A7Jm5ic3A7PGEgaHJlZj0iamF2YXNjcmlwdDpfX2RvUG9zdEJhY2soJ2ZuJywnQzsxNTM5OzE7MzAnKSI+6aaW6aG1PC9hPiZuYnNwOzxhIGhyZWY9ImphdmFzY3JpcHQ6X19kb1Bvc3RCYWNrKCdmbicsJ0M7MTUzOTsxOzMwJykiPuS4iumhtTwvYT4mbmJzcDsyJm5ic3A7PGEgaHJlZj0iamF2YXNjcmlwdDpfX2RvUG9zdEJhY2soJ2ZuJywnQzsxNTM5OzM7MzAnKSI+5LiL6aG1PC9hPiZuYnNwOzxhIGhyZWY9ImphdmFzY3JpcHQ6X19kb1Bvc3RCYWNrKCdmbicsJ0M7MTUzOTs1MjszMCcpIj7lsL7pobU8L2E+ZGRLJ2kwC6EVoMjySQTBtsBrHoNR8BTsFMlE+aAbD+x6bg=="));
        postData.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "B5EA1942"));

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

        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = null;
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
                list.add(split[i].split("、")[1].trim());
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
