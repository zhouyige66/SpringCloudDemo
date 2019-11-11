package cn.roy.springcloud.mq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/11 13:29
 * @Version: v1.0
 */
@RestController
public class MQSendController {

    @Resource
    private JmsMessagingTemplate jmsTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    @GetMapping("send")
    public String send(){
        jmsTemplate.convertAndSend(queue,"发送mq队列消息");

        return "success";
    }

    @GetMapping("send2")
    public String send2(){
        jmsTemplate.convertAndSend(topic,"发送mq主题信息");

        return "success";
    }

}
