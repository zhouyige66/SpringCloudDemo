package cn.roy.springcloud.mq.controller;

import cn.roy.springcloud.mq.service.ActiveMQSendService;
import cn.roy.springcloud.mq.service.RabbitMQSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    /**********功能：rabbit发送消息测试**********/

    @GetMapping("send5")
    public String send5() {
        rabbitMQSendService.send2Direct("direct", "key1", "发送主题信息，主题名：active_mq_topic");
        rabbitMQSendService.send2Topic("topic", "key.*", "发送主题信息，主题名：active_mq_topic");
        rabbitMQSendService.send2Fanout("fanout", "发送主题信息，主题名：active_mq_topic");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "roy");
        rabbitMQSendService.send2Headers("headers", map, "发送主题信息，主题名：active_mq_topic");

        return "success";
    }

    @GetMapping("send6")
    public String send6() {
        rabbitMQSendService.send2Topic("direct", "key.*", "发送主题信息，主题名：active_mq_topic");
        return "success";
    }
}
