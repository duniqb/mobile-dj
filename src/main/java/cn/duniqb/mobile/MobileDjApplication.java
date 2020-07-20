package cn.duniqb.mobile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableScheduling
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
@MapperScan(basePackages = "cn.duniqb.mobile.dao")
public class MobileDjApplication {

    public static void main(String[] args) {

        SpringApplication.run(MobileDjApplication.class, args);
    }

}
