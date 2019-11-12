package cn.roy.springcloud.mq.controller;

import cn.roy.springcloud.mq.config.ActiveMQConfig;
import cn.roy.springcloud.mq.service.ActiveMQSendService;
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

    @GetMapping("send")
    public String send() {
        activeMQSendService.send2Queue(ActiveMQConfig.QUEUE, "发送mq队列消息");

        return "success";
    }

    @GetMapping("send2")
    public String send2() {
        activeMQSendService.send2Top(ActiveMQConfig.TOPIC, "发送mq主题信息");

        return "success";
    }

}
