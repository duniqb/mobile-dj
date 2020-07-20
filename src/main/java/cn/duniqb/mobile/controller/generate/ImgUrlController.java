package cn.duniqb.mobile.controller.generate;

import cn.duniqb.mobile.entity.ImgUrlEntity;
import cn.duniqb.mobile.service.ImgUrlService;
import cn.duniqb.mobile.utils.PageUtils;
import cn.duniqb.mobile.utils.R;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * 图片表，存储在oss
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
@Api(tags = {"与图片存储相关的接口"})
@RestController
@RequestMapping("/imgurl")
public class ImgUrlController {
    @Autowired
    private ImgUrlService imgUrlService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("mobile:imgurl:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = imgUrlService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("mobile:imgurl:info")
    public R info(@PathVariable("id") Integer id) {
        ImgUrlEntity imgUrl = imgUrlService.getById(id);

        return R.ok().put("imgUrl", imgUrl);
    }

    /**
     * 保存
     */
    @RequestMapping("/save/{articleId}")
    // @RequiresPermissions("mobile:imgurl:save")
    public R save(@RequestBody List<String> imgList, @PathVariable("articleId") Integer articleId) {
        System.out.println("保存图片...");
        System.out.println("图片:" + Arrays.toString(imgList.toArray()));
        for (int i = 0; i < imgList.size(); i++) {
            ImgUrlEntity imgUrlEntity = new ImgUrlEntity();
            imgUrlEntity.setArticleId(articleId);
            imgUrlEntity.setUrl(imgList.get(i));
            imgUrlEntity.setImgType(1);
            imgUrlService.save(imgUrlEntity);
        }

        return R.ok("保存图片列表成功");
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("mobile:imgurl:update")
    public R update(@RequestBody ImgUrlEntity imgUrl) {
        imgUrlService.updateById(imgUrl);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("mobile:imgurl:delete")
    public R delete(@RequestBody Integer[] ids) {
        imgUrlService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
