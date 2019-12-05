package cn.duniqb.mobile.dto.jw;

import lombok.Data;

import java.util.List;

@Data
public class NoticeList {
    private String page;

    private String totalPage;

    private List<Notice> list;
}
