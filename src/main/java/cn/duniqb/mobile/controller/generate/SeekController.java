package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.dao.ImgUrlDao;
import cn.duniqb.mobile.dto.seek.Seek;
import cn.duniqb.mobile.entity.ImgUrlEntity;
import cn.duniqb.mobile.entity.SeekEntity;
import cn.duniqb.mobile.entity.WxUserEntity;
import cn.duniqb.mobile.service.SeekService;
import cn.duniqb.mobile.service.WxUserService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import cn.duniqb.mobile.utils.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 失物招领
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-05-04 10:21:25
 */
@Api(tags = {"与失物招领相关的接口"})
@RestController
@RequestMapping("/seek")
public class SeekController {
    @Autowired
    private SeekService seekService;

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private ImgUrlDao imgUrlDao;

    @Autowired
    private WxUserService wxUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:seek:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = seekService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:seek:info")
    public R info(@PathVariable("id") Integer id) {
        SeekEntity seekEntity = seekService.getById(id);

        Seek seek = new Seek();

        // 复制已有属性
        BeanUtils.copyProperties(seekEntity, seek);

        // 查找该失物招领可能关联的图片
        Integer seekId = seekEntity.getId();
        QueryWrapper<ImgUrlEntity> queryWrapperImg = new QueryWrapper<>();
        queryWrapperImg.eq("article_id", seekId);
        List<ImgUrlEntity> imgUrlEntityList = imgUrlDao.selectList(queryWrapperImg);

        List<String> imgList = new ArrayList<>();
        for (ImgUrlEntity imgUrlEntity : imgUrlEntityList) {
            imgList.add(imgUrlEntity.getUrl());
        }
        seek.setImgUrlList(imgList);

        // 查找作者名
        QueryWrapper<WxUserEntity> queryWrapperName = new QueryWrapper<>();
        queryWrapperName.eq("openid", seekEntity.getOpenid());
        WxUserEntity wxUser = wxUserService.getOne(queryWrapperName);
        seek.setAuthor(wxUser.getNickname());

        return R.ok().put("seek", seek);
    }

    /**
     * 保存
     */
    @RequestMapping("/save/{sessionId}")
    // @RequiresPermissions("mobile:seek:save")
    public R save(@RequestBody SeekEntity seek, @PathVariable String sessionId) {

        String sessionIdValue = redisUtil.get(sessionId);
        if (sessionIdValue != null) {
            String openid = sessionIdValue.split(":")[0];
            seek.setOpenid(openid);
            seek.setStatus(0);
            seek.setTime(LocalDateTime.now());
            seek.setDate(LocalDate.now());
            int seekId = seekService.saveSeek(seek);

            return R.ok("失物招领保存成功").put("data", seekId);
        }
        return R.error(400, "失物招领保存失败");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:seek:update")
    public R update(@RequestBody SeekEntity seek) {
        seekService.updateById(seek);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:seek:delete")
    public R delete(@RequestBody Integer[] ids) {
        seekService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
