package cn.roy.springcloud.mq.receive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 13:16
 * @Version: v1.0
 */
@Component
public class RabbitMQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @RabbitHandler
    @RabbitListener
    public void handleMessage(){

    }

}
