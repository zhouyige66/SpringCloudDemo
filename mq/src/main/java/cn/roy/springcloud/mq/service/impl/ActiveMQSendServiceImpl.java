package cn.roy.springcloud.mq.service.impl;

import cn.roy.springcloud.mq.service.ActiveMQSendService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:11
 * @Version: v1.0
 */
@Service
public class ActiveMQSendServiceImpl implements ActiveMQSendService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Override
    public void sendText2Queue(String queue, String msg) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(queue), msg);
    }

    @Override
    public void sendText2Topic(String topic, String msg) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(topic), msg);
    }

    @Override
    public void send2Queue(String queue, Object obj) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(queue), obj);
    }

    @Override
    public void send2Top(String topic, Object obj) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(topic), obj);
    }
}
