package cn.roy.springcloud.mq.service.impl;

import cn.roy.springcloud.mq.service.ActiveMQSendService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    }

    @Override
    public void send2Top(String topic, Object obj) {
        jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(topic), obj);
    }
}
