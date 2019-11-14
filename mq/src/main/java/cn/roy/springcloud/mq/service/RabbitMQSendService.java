package cn.roy.springcloud.mq.service;

import cn.roy.springcloud.mq.config.RabbitConfig;
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

    void send2Direct(RabbitConfig.DirectExchangeName direct, RabbitConfig.RoutingKey routingKey, Object object);

    void send2Topic(RabbitConfig.TopicExchangeName topic, String routingKey, Object object);

    void send2Fanout(RabbitConfig.FanoutExchangeName fanout, Object object);

    void send2Headers(RabbitConfig.HeadersExchangeName headers, @NotNull Map<String, Object> headersMap, Object object);

}
