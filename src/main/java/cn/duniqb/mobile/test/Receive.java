package cn.duniqb.mobile.test;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/5/2 14:56
 * @since 1.0
 */
@Component
@RabbitListener(queues = "dj")
public class Receive {

    @RabbitHandler
    public void receive(Map<String, Object> msg) {

        System.out.println("direct 收到消息：" + msg.toString());
    }
}
