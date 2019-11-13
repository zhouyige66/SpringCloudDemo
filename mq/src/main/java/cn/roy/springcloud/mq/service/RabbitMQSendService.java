package cn.roy.springcloud.mq.service;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:19
 * @Version: v1.0
 */
@Service
public interface RabbitMQSendService {

    void send2Direct(String exchange, String routingKey, Object object);

    void send2Topic(String exchange, String topic, Object object);

    void send2Fanout(String exchange, Object object);

    void send2Headers(String routingKey, @NotNull Map<String, Object> headers, Object object);

}
