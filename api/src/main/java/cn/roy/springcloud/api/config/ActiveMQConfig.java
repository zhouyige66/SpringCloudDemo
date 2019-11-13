package cn.roy.springcloud.api.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import static javax.jms.JMSContext.AUTO_ACKNOWLEDGE;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/11 10:42
 * @Version: v1.0
 */
@Configuration
public class ActiveMQConfig {
    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory();
    }

    /**********功能：queue模式**********/
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionAcknowledgeMode(AUTO_ACKNOWLEDGE);
        return factory;
    }

    /**********功能：topic模式**********/
    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(true);//发布订阅模式
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(AUTO_ACKNOWLEDGE);
        factory.setConcurrency("5");
        return factory;
    }

}
