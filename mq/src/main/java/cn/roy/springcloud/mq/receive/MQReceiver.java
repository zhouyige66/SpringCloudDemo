package cn.roy.springcloud.mq.receive;

import cn.roy.springcloud.mq.config.ActiveMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/11 13:36
 * @Version: v1.0
 */
@Component
public class MQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    // 使用JmsListener配置消费者监听的队列，其中msg是接收到的消息
//    @JmsListener(destination = ActiveMQConfig.QUEUE)
//    @SendTo("SQueue") // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
//    public String handleMessage(String msg) {
//        logger.info("收到消息：" + msg);
//        return msg;
//    }

    @JmsListener(destination = ActiveMQConfig.QUEUE)
    public void handleMessage(Message msg) {
        logger.info("收到消息1：" + msg);
        try {
            // 发送消费确认通知
            msg.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = ActiveMQConfig.TOPIC, containerFactory = "jmsListenerContainerTopic", concurrency = "1")
    public void handleMessage2(String msg) {
        logger.info("收到消息2：" + msg);
    }

    @JmsListener(destination = ActiveMQConfig.TOPIC, containerFactory = "jmsListenerContainerTopic", concurrency = "1")
    public void handleMessage3(String msg) {
        logger.info("收到消息3：" + msg);
    }

}
