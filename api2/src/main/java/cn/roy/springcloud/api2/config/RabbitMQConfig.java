package cn.roy.springcloud.api2.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/6 16:06
 * @Version: v1.0
 */
@Configuration
public class RabbitMQConfig {

    // 配置该解析器，程序走自动确认消费
//    @Bean
//    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);// 手动确认
        return factory;
    }

}
