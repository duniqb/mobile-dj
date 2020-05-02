package cn.duniqb.mobile.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rabbit 配置
 *
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/5/2 15:49
 * @since 1.0
 */
@Configuration
public class RabbitConfig {
    /**
     * 队列起名
     *
     * @return
     */
    @Bean
    public Queue testDirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。

        return new Queue("dj", true);
    }

    /**
     * Direct交换机起名
     *
     * @return
     */
    @Bean
    DirectExchange testDirectExchange() {

        return new DirectExchange("dj", true, false);
    }


    /**
     * 将队列和交换机绑定, 并设置用于匹配键：dj
     *
     * @return
     */
    @Bean
    Binding bindingDirect() {

        return BindingBuilder.bind(testDirectQueue())
                .to(testDirectExchange()).with("dj");
    }

    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }
}
