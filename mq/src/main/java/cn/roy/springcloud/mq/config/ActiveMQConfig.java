package cn.roy.springcloud.mq.config;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.ConnectionFactory;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/13 10:53
 * @Version: v1.0
 */
@Configuration
public class ActiveMQConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        return new PooledConnectionFactory();
    }

    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(){
        return new JmsMessagingTemplate(connectionFactory());
    }

}
