package cn.roy.springcloud.mq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.UUID;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/13 09:15
 * @Version: v1.0
 */
@Configuration
public class RabbitConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    public enum DirectExchangeName {
        DefaultName("roy.direct");

        private final String name;

        DirectExchangeName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum TopicExchangeName {
        DefaultName("roy.topic");

        private final String name;

        TopicExchangeName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum FanoutExchangeName {
        DefaultName("roy.fanout");

        private final String name;

        FanoutExchangeName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum HeadersExchangeName {
        DefaultName("roy.headers");

        private final String name;

        HeadersExchangeName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum RoutingKey {
        DirectA("direct.A"),
        DirectB("direct.B"),
        DirectC("direct.C"),
        TopicA("topic.A.*"),
        TopicB("topic.*"),
        TopicC("topic.#");

        private final String name;

        RoutingKey(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public interface QueueName {
        interface Direct {
            String direct = "queue.direct";
            String direct2 = "queue.direct2";
        }

        interface Topic {
            String topic = "queue.topic";
            String topic2 = "queue.topic2";
        }

        interface Fanout {
            String fanout = "queue.fanout";
            String fanout2 = "queue.fanout2";
        }

        interface Headers {
            String headers = "queue.headers";
            String headers2 = "queue.headers2";
        }

        String mix = "queue.mix";
    }

    /**********功能：注入queue**********/
    @Bean
    public Queue directQueue() {
        return new Queue(QueueName.Direct.direct);
    }

    @Bean
    public Queue directQueue2() {
        return new Queue(QueueName.Direct.direct2);
    }

    @Bean
    public Queue topicQueue() {
        return new Queue(QueueName.Topic.topic);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(QueueName.Topic.topic2);
    }

    @Bean
    public Queue fanoutQueue() {
        return new Queue(QueueName.Fanout.fanout);
    }

    @Bean
    public Queue fanoutQueue2() {
        return new Queue(QueueName.Fanout.fanout2);
    }

    @Bean
    public Queue headersQueue() {
        return new Queue(QueueName.Headers.headers);
    }

    @Bean
    public Queue headersQueue2() {
        return new Queue(QueueName.Headers.headers2);
    }

    @Bean
    public Queue mixQueue() {
        return new Queue(QueueName.mix);
    }

    /**********功能：注入四种类型的exchange**********/
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DirectExchangeName.DefaultName.name);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TopicExchangeName.DefaultName.name);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FanoutExchangeName.DefaultName.name);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HeadersExchangeName.DefaultName.name);
    }

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Bean
    public void register() {
        amqpAdmin.declareExchange(directExchange());
        amqpAdmin.declareExchange(topicExchange());
        amqpAdmin.declareExchange(fanoutExchange());
        amqpAdmin.declareExchange(headersExchange());

        amqpAdmin.declareQueue(directQueue());
        amqpAdmin.declareQueue(directQueue2());
        amqpAdmin.declareQueue(topicQueue());
        amqpAdmin.declareQueue(topicQueue2());
        amqpAdmin.declareQueue(fanoutQueue());
        amqpAdmin.declareQueue(fanoutQueue2());
        amqpAdmin.declareQueue(headersQueue());
        amqpAdmin.declareQueue(headersQueue2());
        amqpAdmin.declareQueue(mixQueue());

        Binding directBinding = BindingBuilder.bind(directQueue()).to(directExchange()).with(RoutingKey.DirectA.name);
        Binding directBinding2 = BindingBuilder.bind(directQueue2()).to(directExchange()).with(RoutingKey.DirectB.name);
        Binding directBinding3 = BindingBuilder.bind(mixQueue()).to(directExchange()).with(RoutingKey.DirectC.name);
        Binding topicBinding = BindingBuilder.bind(topicQueue()).to(topicExchange()).with(RoutingKey.TopicA.name);
        Binding topicBinding2 = BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(RoutingKey.TopicB.name);
        Binding topicBinding3 = BindingBuilder.bind(mixQueue()).to(topicExchange()).with(RoutingKey.TopicC.name);
        Binding fanoutBinding = BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
        Binding fanoutBinding2 = BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
        Binding fanoutBinding3 = BindingBuilder.bind(mixQueue()).to(fanoutExchange());
        Binding headersBinding = BindingBuilder.bind(headersQueue()).to(headersExchange()).where("header.A").exists();
        Binding headersBinding2 = BindingBuilder.bind(headersQueue2()).to(headersExchange()).where("header.B").exists();
        Binding headersBinding3 = BindingBuilder.bind(mixQueue()).to(headersExchange()).where("header.C").exists();

        amqpAdmin.declareBinding(directBinding);
        amqpAdmin.declareBinding(directBinding2);
        amqpAdmin.declareBinding(directBinding3);
        amqpAdmin.declareBinding(topicBinding);
        amqpAdmin.declareBinding(topicBinding2);
        amqpAdmin.declareBinding(topicBinding3);
        amqpAdmin.declareBinding(fanoutBinding);
        amqpAdmin.declareBinding(fanoutBinding2);
        amqpAdmin.declareBinding(fanoutBinding3);
        amqpAdmin.declareBinding(headersBinding);
        amqpAdmin.declareBinding(headersBinding2);
        amqpAdmin.declareBinding(headersBinding3);
    }

    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    /**
     * 1.通过AMQP提供的事务机制实现（缺点：性能差）
     * 2.使用发送者确认模式实现
     */
    @Bean
    public void config() {
        RabbitTemplate rabbitTemplate = rabbitMessagingTemplate.getRabbitTemplate();
        rabbitTemplate.setCorrelationDataPostProcessor(
                (Message message, CorrelationData correlationData) -> {
                    byte[] body = message.getBody();
                    String s = new String(body);
                    logger.info("发送的内容为：" + s);
                    Map<String, Object> headers = message.getMessageProperties().getHeaders();
                    logger.info("发送的消息的属性：{}", headers);
                    String id = UUID.randomUUID().toString();
                    logger.info("设置的确认ID：{}", id);
                    CorrelationData data = new CorrelationData(id);
                    return data;
                });
        rabbitTemplate.setConfirmCallback(
                (CorrelationData correlationData, boolean ack, String cause) -> {
                    logger.info("发送消息，收到确认结果：{},{}", ack, correlationData);
                });
        rabbitTemplate.setReturnCallback(
                (Message message, int replyCode, String replyText, String exchange, String routingKey) -> {
                    logger.info("发送消息，收到返回消息：{};{};{};{};{}", message, replyCode, replyText, exchange, routingKey);
                });
    }

}
