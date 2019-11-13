package cn.roy.springcloud.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    /**********功能：注入四种类型的exchange**********/
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("");
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("");
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("");
    }

    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange("");
    }

}
