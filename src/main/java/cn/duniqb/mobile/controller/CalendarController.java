package cn.duniqb.mobile.controller;

import cn.duniqb.mobile.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 与校历相关的接口
 *
 * @author duniqb
 */
@Api(value = "与校历相关的接口", tags = {"与校历相关的接口"})
@RestController
@RequestMapping("/api/v2/calendar/")
public class CalendarController {
    /**
     * 日历
     *
     * @return
     */
    @ApiOperation(value = "日历", notes = "日历的接口")
    @GetMapping("calendar")
    public R calendar() {
        Map<String, Integer> map = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        // 年度周次
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        map.put("weekOfYear", weekOfYear);

        // 教学周次
        map.put("weekOfTerm", null);
        if (weekOfYear >= 35 && weekOfYear <= 52) {
            map.put("weekOfTerm", weekOfYear - 35);
        } else if (weekOfYear >= 1 && weekOfYear <= 3) {
            map.put("weekOfTerm", weekOfYear + 17);
        }

        // 本学期第几天
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2019, Calendar.SEPTEMBER, 2);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2020, Calendar.JANUARY, 19);
        // 没到期末前计算
        map.put("dayOfTerm", null);
        if (calendar.getTimeInMillis() < calendar2.getTimeInMillis()) {
            map.put("dayOfTerm", (int) ((calendar.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000 / 60 / 60 / 24));
        }

        // 期末/寒假倒计时
        map.put("endOfTermDay", null);
        if (calendar.getTimeInMillis() < calendar2.getTimeInMillis()) {
            map.put("endOfTermDay", (int) ((calendar2.getTimeInMillis() - calendar.getTimeInMillis()) / 1000 / 60 / 60 / 24));
        }

        // 寒假日倒计时
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(2020, Calendar.JANUARY, 20);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2020, Calendar.FEBRUARY, 27);
        map.put("endOfVacationDay", null);
        if (calendar.getTimeInMillis() >= calendarStart.getTimeInMillis() && calendar.getTimeInMillis() <= calendarEnd.getTimeInMillis()) {
            map.put("endOfVacationDay", (int) ((calendarEnd.getTimeInMillis() - calendar.getTimeInMillis()) / 1000 / 60 / 60 / 24));
        }

        return R.ok().put("日历获取成功", map);
    }

    /**
     * 节假日提示
     *
     * @return
     */
    @ApiOperation(value = "节假日提示", notes = "节假日提示的接口")
    @GetMapping("festival")
    public R festival() {
        List<String> list = new ArrayList<>();

        // 平安夜
        Calendar calendarChristmasEveStart = Calendar.getInstance();
        calendarChristmasEveStart.set(2019, Calendar.DECEMBER, 24, 0, 0);
        Calendar calendarChristmasEveEnd = Calendar.getInstance();
        calendarChristmasEveEnd.set(2019, Calendar.DECEMBER, 24, 23, 59);
        if (System.currentTimeMillis() >= calendarChristmasEveStart.getTimeInMillis() && System.currentTimeMillis() <= calendarChristmasEveEnd.getTimeInMillis()) {
            list.add("平安夜快乐！");
            list.add("Happy Christmas Eve!");
            list.add("圣诞夜祝一切顺心如意！");
            list.add("平安夜，今天吃苹果了吗？");
        }

        // 圣诞节
        Calendar calendarChristmasStart = Calendar.getInstance();
        calendarChristmasStart.set(2019, Calendar.DECEMBER, 25, 0, 0);
        Calendar calendarChristmasEnd = Calendar.getInstance();
        calendarChristmasEnd.set(2019, Calendar.DECEMBER, 25, 23, 59);
        if (System.currentTimeMillis() >= calendarChristmasStart.getTimeInMillis() && System.currentTimeMillis() <= calendarChristmasEnd.getTimeInMillis()) {
            list.add("圣诞节快乐！");
            list.add("Merry Christmas!");
            list.add("祝你有个温馨的圣诞节！");
            list.add("愿圣诞的喜悦萦绕你心！");
        }

        // 元旦节
        Calendar calendarYearStart = Calendar.getInstance();
        calendarYearStart.set(2020, Calendar.JANUARY, 1, 0, 0);
        Calendar calendarYearEnd = Calendar.getInstance();
        calendarYearEnd.set(2020, Calendar.JANUARY, 1, 23, 59);
        if (System.currentTimeMillis() >= calendarYearStart.getTimeInMillis() && System.currentTimeMillis() <= calendarYearEnd.getTimeInMillis()) {
            list.add("Happy New Year's Day!");
            list.add("元旦节快乐！");
            list.add("新的一年要开始了，准备好了吗？");
        }

        // 除夕日
        Calendar calendarSpringEveStart = Calendar.getInstance();
        calendarSpringEveStart.set(2020, Calendar.JANUARY, 24, 0, 0);
        Calendar calendarSpringEveEnd = Calendar.getInstance();
        calendarSpringEveEnd.set(2020, Calendar.JANUARY, 24, 23, 59);
        if (System.currentTimeMillis() >= calendarSpringEveStart.getTimeInMillis() && System.currentTimeMillis() <= calendarSpringEveEnd.getTimeInMillis()) {
            list.add("Happy New Year's Eve!");
            list.add("除夕快乐！");
            list.add("猴年快要到了，今天和家人一起吃团圆饭呀！");
            list.add("爆竹声中一岁除，春风送暖入屠苏。");
        }

        // 春节
        Calendar calendarSpringStart = Calendar.getInstance();
        calendarSpringStart.set(2020, Calendar.JANUARY, 25, 0, 0);
        Calendar calendarSpringEnd = Calendar.getInstance();
        calendarSpringEnd.set(2020, Calendar.JANUARY, 27, 23, 59);
        if (System.currentTimeMillis() >= calendarSpringStart.getTimeInMillis() && System.currentTimeMillis() <= calendarSpringEnd.getTimeInMillis()) {
            list.add("Happy Spring Festival!");
            list.add("春节快乐！");
            list.add("热热闹闹庆团圆，欢欢喜喜过大年！");
            list.add("瑞雪迎春到，新年已来临！");
            list.add("鞭炮震天响，喜气满街巷！");
        }

        // 元宵节
        Calendar calendarLanternStart = Calendar.getInstance();
        calendarLanternStart.set(2020, Calendar.FEBRUARY, 8, 0, 0);
        Calendar calendarLanternEnd = Calendar.getInstance();
        calendarLanternEnd.set(2020, Calendar.FEBRUARY, 8, 23, 59);
        if (System.currentTimeMillis() >= calendarLanternStart.getTimeInMillis() && System.currentTimeMillis() <= calendarLanternEnd.getTimeInMillis()) {
            list.add("Happy Lantern Festival!");
            list.add("元宵节快乐！");
            list.add("正月十五月圆圆，万家灯火不夜天！");
            list.add("今天吃汤圆了吗？");
        }
        return R.ok().put("节假日提示成功", list);
    }
}
