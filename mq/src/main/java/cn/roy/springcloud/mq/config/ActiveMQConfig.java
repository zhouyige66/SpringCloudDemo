package cn.roy.springcloud.mq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/11 10:42
 * @Version: v1.0
 */
@Configuration
public class ActiveMQConfig {
    public final static String TOPIC = "springboot.topic.test";
    public final static String QUEUE = "springboot.queue.test";

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory();
    }

    /**********功能：topic模式**********/
    @Bean
    public Topic topic() {
        return new ActiveMQTopic(TOPIC);
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(true);//发布订阅模式
        factory.setSessionTransacted(true);
        factory.setConcurrency("5");
        return factory;
    }

    /**********功能：queue模式**********/
    @Bean
    public Queue queue() {
        return new ActiveMQQueue(QUEUE);
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

}
