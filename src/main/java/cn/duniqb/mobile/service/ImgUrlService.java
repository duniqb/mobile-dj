package cn.duniqb.mobile.service;

import cn.duniqb.mobile.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.duniqb.mobile.entity.ImgUrlEntity;

import java.util.List;
import java.util.Map;

/**
 * 图片表，存储在oss
 *
 * @author duniqb
 * @email duniqb@qq.com
 * @date 2020-04-30 19:36:16
 */
public interface ImgUrlService extends IService<ImgUrlEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ImgUrlEntity> listById(String id);
}

