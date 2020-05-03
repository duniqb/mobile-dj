package cn.duniqb.mobile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
public class MobileDjApplication {

    public static void main(String[] args) {

        SpringApplication.run(MobileDjApplication.class, args);
    }

//    /**
//     * http 重定向到 https
//     *
//     * @return
//     */
//    @Bean
//    public TomcatServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint constraint = new SecurityConstraint();
//                constraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                constraint.addCollection(collection);
//                context.addConstraint(constraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(httpConnector());
//        return tomcat;
//    }
//
//    @Bean
//    public Connector httpConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        // Connector 监听的 http 的端口号
//        connector.setPort(8080);
//        connector.setSecure(false);
//        // 监听到 http的端口号后转向到的 https 的端口号
//        connector.setRedirectPort(443);
//        return connector;
//    }
}
