package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.LikeBookEntity;
import cn.duniqb.mobile.service.LikeBookService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 收藏图书表
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与收藏图书相关的接口"})
@RestController
@RequestMapping("/likebook")
public class LikeBookController {
    @Autowired
    private LikeBookService likeBookService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 列表
     */
    @RequestMapping("/list/{sessionId}")
    // @RequiresPermissions("mobile:likebook:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("sessionId") String sessionId) {
        PageUtils page = likeBookService.queryPage(params);

        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            QueryWrapper<LikeBookEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", openid);
            queryWrapper.orderByDesc("time");
            
            List<LikeBookEntity> likeBookEntityList = likeBookService.list(queryWrapper);
            page.setList(likeBookEntityList);
            return R.ok("收藏列表查询成功").put("page", page);

        }
        return R.error(400, "收藏列表查询失败");
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{sessionId}")
    // @RequiresPermissions("mobile:likebook:info")
    public R info(@PathVariable("sessionId") String sessionId, @RequestParam String id) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];

            QueryWrapper<LikeBookEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", openid);
            queryWrapper.eq("book_id", id);
            LikeBookEntity likeBookEntity = likeBookService.getOne(queryWrapper);

            if (likeBookEntity != null) {
                return R.ok("已经收藏该图书").put("data", true);

            } else {
                return R.ok("没有收藏该图书").put("data", false);
            }
        }
        return R.error(400, "查询失败");
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("mobile:likebook:save")
    public R save(@RequestBody LikeBookEntity likeBook) {
        likeBookService.save(likeBook);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:likebook:update")
    public R update(@RequestBody LikeBookEntity likeBook, @RequestParam("sessionId") String sessionId) {
        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];

            QueryWrapper<LikeBookEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", openid).eq("book_id", likeBook.getBookId());
            LikeBookEntity likeBookEntity = likeBookService.getOne(queryWrapper);

            if (likeBookEntity != null) {
                likeBookService.remove(queryWrapper);
                return R.ok("取消收藏成功").put("data", false);
            } else {
                likeBook.setOpenid(openid);
                likeBook.setTime(new Date());
                boolean save = likeBookService.save(likeBook);
                return R.ok("收藏成功").put("data", true);
            }
        }
        return R.error(400, "修改失败");
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:likebook:delete")
    public R delete(@RequestBody String[] openids) {
        likeBookService.removeByIds(Arrays.asList(openids));

        return R.ok();
    }

}
