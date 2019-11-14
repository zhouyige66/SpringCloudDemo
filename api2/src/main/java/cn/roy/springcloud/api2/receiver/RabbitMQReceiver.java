package cn.roy.springcloud.api2.receiver;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 13:16
 * @Version: v1.0
 */
@Component
public class RabbitMQReceiver {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);

    @RabbitListener(queues = {"queue.direct", "queue.direct2"})
    public void handleMessage(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
        logger.info("收到文字消息：" + msg);
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.topic"),
            exchange = @Exchange(value = "roy.topic", type = ExchangeTypes.TOPIC),
            key = "topic.B.*"
    ))
    public void handleMessage2(Message message) {
        logger.info("收到消息：" + message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "custom.queue.fanout"),
            exchange = @Exchange(value = "roy.fanout", type = ExchangeTypes.FANOUT)
    ))
    public void handleMessage3(Message message) {
        logger.info("收到广播消息：" + message);
    }

}
