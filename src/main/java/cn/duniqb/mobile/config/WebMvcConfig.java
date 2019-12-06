package cn.duniqb.mobile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 设置验证码存放路径
     */
    @Value("${jw.verifyPath}")
    private String verifyPath;

    /**
     * 图片的存放路径
     */
    @Value("${news.imagePath}")
    private String imagePath;

    /**
     * 轮播图存放路径
     */
    @Value("${mini.slidePath}")
    private String slidePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 和页面有关的静态目录都放在项目的static目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/");
        // 其中OTA表示访问的前缀。"file:D:/OTA/"是文件真实的存储路径
        registry.addResourceHandler("/verify/**").addResourceLocations("file:" + verifyPath);
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + imagePath);
        registry.addResourceHandler("/slide/**").addResourceLocations("file:" + slidePath);
    }
}
