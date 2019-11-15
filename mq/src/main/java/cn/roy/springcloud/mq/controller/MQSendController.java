package cn.roy.springcloud.mq.controller;

import cn.roy.springcloud.mq.config.RabbitConfig;
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

    /**********功能：rabbit发送消息测试**********/

    @GetMapping("sendDelay")
    public String sendDelayTest() {
        rabbitMQSendService.send2Delay("发送消息到延迟队列：内容1，过期时间：10s", 10 * 1000);
        try {
            Thread.sleep(5000);
            rabbitMQSendService.send2Direct(RabbitConfig.DirectExchangeName.DefaultName,
                    RabbitConfig.RoutingKey.DirectDelay, "发送消息到延迟队列：内容2，过期时间：0s");

            rabbitMQSendService.send2Delay("发送消息到延迟队列：内容3，过期时间：10s", 10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "操作成功";
    }

    @GetMapping("send5")
    public String send5() {
        rabbitMQSendService.send2Direct(RabbitConfig.DirectExchangeName.DefaultName, RabbitConfig.RoutingKey.DirectA,
                "发送direct信息给A");
        rabbitMQSendService.send2Direct(RabbitConfig.DirectExchangeName.DefaultName, RabbitConfig.RoutingKey.DirectB,
                "发送direct信息给B");
        rabbitMQSendService.send2Direct(RabbitConfig.DirectExchangeName.DefaultName, RabbitConfig.RoutingKey.DirectC,
                "发送direct信息给C");

//        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic",
//                "发送topic信息，routingKey=topic");
//        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic.A",
//                "发送topic信息，routingKey=topic.A");
//        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic.A.B",
//                "发送topic信息，routingKey=topic.A.B");
//        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic.goods",
//                "发送topic信息，routingKey=topic.goods");
//        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "order",
//                "发送topic信息，routingKey=order");
//
//        rabbitMQSendService.send2Fanout(RabbitConfig.FanoutExchangeName.DefaultName, "发送fanout消息");

        return "success";
    }

    @GetMapping("send6")
    public String send6() {
        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic",
                "发送topic信息，routingKey=topic");
        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic.A",
                "发送topic信息，routingKey=topic.A");
        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic.A.B",
                "发送topic信息，routingKey=topic.A.B");
        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "topic.goods",
                "发送topic信息，routingKey=topic.goods");
        rabbitMQSendService.send2Topic(RabbitConfig.TopicExchangeName.DefaultName, "order",
                "发送topic信息，routingKey=order");
//
//        rabbitMQSendService.send2Fanout(RabbitConfig.FanoutExchangeName.DefaultName, "发送fanout消息");

        return "success";
    }

    @GetMapping("send7")
    public String send7() {
        rabbitMQSendService.send2Fanout(RabbitConfig.FanoutExchangeName.DefaultName, "发送fanout消息");
        return "success";
    }

}
