package cn.roy.springcloud.mq.service.impl;

import cn.roy.springcloud.mq.service.RabbitMQSendService;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:20
 * @Version: v1.0
 */
@Service
public class RabbitMQSendServiceImpl implements RabbitMQSendService {
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Override
    public void send2Direct(String exchange, String routingKey, Object object) {
        rabbitMessagingTemplate.convertAndSend(exchange, routingKey, object);
    }

    @Override
    public void send2Topic(String exchange, String topic, Object object) {
        rabbitMessagingTemplate.convertAndSend(exchange, topic, object);
    }

    @Override
    public void send2Fanout(String exchange, Object object) {
        rabbitMessagingTemplate.convertAndSend(exchange,"",object);
    }

    @Override
    public void send2Headers(String routingKey, @NotNull Map<String, Object> headers, Object object) {
        rabbitMessagingTemplate.convertAndSend(routingKey, object, headers);
    }

}
