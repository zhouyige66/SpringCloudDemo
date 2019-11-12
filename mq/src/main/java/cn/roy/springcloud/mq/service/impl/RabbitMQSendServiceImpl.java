package cn.roy.springcloud.mq.service.impl;

import cn.roy.springcloud.mq.service.RabbitMQSendService;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:20
 * @Version: v1.0
 */
public class RabbitMQSendServiceImpl implements RabbitMQSendService {
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Override
    public void sendText(String text) {
        rabbitMessagingTemplate.convertAndSend(text);
    }

}
