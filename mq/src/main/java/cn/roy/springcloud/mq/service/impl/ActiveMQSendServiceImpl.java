package cn.roy.springcloud.mq.service.impl;

import cn.roy.springcloud.mq.service.ActiveMQSendService;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.JMSException;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:11
 * @Version: v1.0
 */
@Service
public class ActiveMQSendServiceImpl implements ActiveMQSendService {

    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Override
    public void send2Queue(String queue, Object obj) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(queue), obj);

        jmsMessagingTemplate.getJmsTemplate().convertAndSend(new ActiveMQQueue(""), obj,
                new org.springframework.jms.core.MessagePostProcessor() {
                    @Override
                    public javax.jms.Message postProcessMessage(javax.jms.Message message) throws JMSException {
                        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 10 * 1000);
                        return message;
                    }
                });
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(""), obj, new MessagePostProcessor() {
            @Override
            public Message<?> postProcessMessage(Message<?> message) {
                return message;
            }
        });
    }

    @Override
    public void send2Top(String topic, Object obj) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(topic), obj);
    }
}
