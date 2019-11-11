package cn.roy.springcloud.mq.receive;

import cn.roy.springcloud.mq.config.ActiveMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/11 13:36
 * @Version: v1.0
 */
@Component
public class MQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    // 使用JmsListener配置消费者监听的队列，其中name是接收到的消息
    @JmsListener(destination = ActiveMQConfig.QUEUE)
    @SendTo("SQueue") // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    public String handleMessage(String name) {
        logger.info("收到消息1：" + name);
        return "成功接受Name" + name;
    }

    @JmsListener(destination = ActiveMQConfig.TOPIC, containerFactory = "jmsListenerContainerTopic",concurrency = "1")
    public void handleMessage2(String name) {
        logger.info("收到消息2：" + name);
    }

    @JmsListener(destination = ActiveMQConfig.TOPIC, containerFactory = "jmsListenerContainerTopic",concurrency = "1")
    public void handleMessage3(String name) {
        logger.info("收到消息3：" + name);
    }

}
