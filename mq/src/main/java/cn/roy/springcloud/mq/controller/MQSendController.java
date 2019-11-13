package cn.roy.springcloud.mq.controller;

import cn.roy.springcloud.mq.service.ActiveMQSendService;
import cn.roy.springcloud.mq.service.RabbitMQSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/11 13:29
 * @Version: v1.0
 */
@RestController
public class MQSendController {

    @Autowired
    private ActiveMQSendService activeMQSendService;
    @Autowired
    private RabbitMQSendService rabbitMQSendService;

    @GetMapping("send")
    public String send() {
        activeMQSendService.send2Queue("active_mq_queue", "发送队列消息，队列名：active_mq_queue");

        return "success";
    }

    @GetMapping("send2")
    public String send2() {
        activeMQSendService.send2Queue("active_mq_topic", "发送队列信息，队列名：active_mq_topic");

        return "success";
    }

    @GetMapping("send3")
    public String send3() {
        activeMQSendService.send2Top("active_mq_queue", "发送主题信息，主题名：active_mq_queue");

        return "success";
    }

    @GetMapping("send4")
    public String send4() {
        activeMQSendService.send2Top("active_mq_topic", "发送主题信息，主题名：active_mq_topic");

        return "success";
    }

}
