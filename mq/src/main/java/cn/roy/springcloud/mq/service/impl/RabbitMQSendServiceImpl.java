package cn.roy.springcloud.mq.service.impl;

import cn.roy.springcloud.mq.config.RabbitConfig;
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
    public void send2Direct(RabbitConfig.DirectExchangeName direct, RabbitConfig.RoutingKey routingKey, Object object) {
        rabbitMessagingTemplate.convertAndSend(direct.getName(), routingKey.getName(), object);
    }

    @Override
    public void send2Topic(RabbitConfig.TopicExchangeName topic, String routingKey, Object object) {
        rabbitMessagingTemplate.convertAndSend(topic.getName(), routingKey, object);
    }

    @Override
    public void send2Fanout(RabbitConfig.FanoutExchangeName fanout, Object object) {
        rabbitMessagingTemplate.convertAndSend(fanout.getName(), "", object);
    }

    @Override
    public void send2Headers(RabbitConfig.HeadersExchangeName headers, @NotNull Map<String, Object> headersMap, Object object) {
        rabbitMessagingTemplate.convertAndSend(headers.getName(), "", object, headersMap);
    }

}
