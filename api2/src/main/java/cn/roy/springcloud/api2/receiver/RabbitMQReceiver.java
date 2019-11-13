package cn.roy.springcloud.api2.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 13:16
 * @Version: v1.0
 */
@Component
public class RabbitMQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @RabbitListener(queues = {"rabbit_mq_queue1","rabbit_mq_queue2"})
    public void handleMessage(String msg) {
        logger.info("收到文字消息：" + msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "topic1"),
            exchange = @Exchange(value = "exchange_topic",type = ExchangeTypes.TOPIC),
            key = "topic.#"))
    public void handleMessage(Message message) {
        logger.info("收到消息：" + message);
    }

}
