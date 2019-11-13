package cn.roy.springcloud.mq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/13 09:15
 * @Version: v1.0
 */
@Configuration
public class RabbitConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    /**********功能：注入四种类型的exchange**********/
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic");
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout");
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange("headers");
    }

    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    /**
     * 1.通过AMQP提供的事务机制实现（缺点：性能差）
     * 2.使用发送者确认模式实现
     */
    @Bean
    public void config() {
        RabbitTemplate rabbitTemplate = rabbitMessagingTemplate.getRabbitTemplate();
        rabbitTemplate.setConfirmCallback(
                (CorrelationData correlationData, boolean ack, String cause) -> {
                    logger.info("发送消息，收到确认消息：" + correlationData + ";" + ack + ";" + cause);
                }
        );
        rabbitTemplate.setReturnCallback(
                (Message message, int replyCode, String replyText, String exchange, String routingKey) -> {
                    logger.info("发送消息，收到返回消息：{};{};{};{};{}", message, replyCode, replyText, exchange, routingKey);
                }
        );
    }

}
