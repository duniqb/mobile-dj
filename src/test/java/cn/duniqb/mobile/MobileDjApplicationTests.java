package cn.duniqb.mobile;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootTest
class MobileDjApplicationTests {

    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    private ApplicationContext appCtx;

    /**
     * 密码
     */
    @Test
    public void test() {
        Environment environment = appCtx.getBean(Environment.class);

        // 首先获取配置文件里的原始明文信息
        String mysqlOriginPswd = environment.getProperty("spring.datasource.password");
        String redisOriginPswd = environment.getProperty("spring.redis.password");
        String rabbitOriginPswd = environment.getProperty("spring.rabbit.password");

        // 加密
        String mysqlEncryptedPswd = encrypt(mysqlOriginPswd);
        String redisEncryptedPswd = encrypt(redisOriginPswd);

        // 打印加密前后的结果对比
        System.out.println("MySQL原始明文密码为：" + mysqlOriginPswd);
        System.out.println("Redis原始明文密码为：" + redisOriginPswd);
        System.out.println("Rabbit原始明文密码为：" + rabbitOriginPswd);

        System.out.println("====================================");
        System.out.println("MySQL原始明文密码加密后的结果为：" + mysqlEncryptedPswd);
        System.out.println("Redis原始明文密码加密后的结果为：" + redisEncryptedPswd);
        System.out.println("Rabbit原始明文密码加密后的结果为：" + rabbitOriginPswd);

    }

    private String encrypt(String originPassord) {
        String encryptStr = stringEncryptor.encrypt(originPassord);
        return encryptStr;
    }

    private String decrypt(String encryptedPassword) {
        String decryptStr = stringEncryptor.decrypt(encryptedPassword);
        return decryptStr;
    }
}
