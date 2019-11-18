package cn.duniqb.mobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "cn.duniqb.mobile.mapper")
public class MobileDjApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileDjApplication.class, args);
    }

}
