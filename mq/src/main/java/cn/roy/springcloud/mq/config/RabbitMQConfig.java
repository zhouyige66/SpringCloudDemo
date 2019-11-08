package cn.roy.springcloud.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/6 16:06
 * @Version: v1.0
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue helloQueue() {
        return new Queue("hello");
    }

    @Bean
    public Queue userQueue() {
        return new Queue("user");
    }

    //===============以下是验证Topic Exchange的队列==========
    @Bean
    public Queue queueMessage() {
        return new Queue("topic.message");
    }

    @Bean
    public Queue queueMessages() {
        return new Queue("topic.messages");
    }

    //===============以下是验证Fanout Exchange的队列==========
    @Bean
    public Queue AMessage() {
        return new Queue("fanout.A");
    }

    @Bean
    public Queue BMessage() {
        return new Queue("fanout.B");
    }

    @Bean
    public Queue CMessage() {
        return new Queue("fanout.C");
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("exchange");
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanoutExchange");
    }

    /**
     * 将队列topic.message与exchange绑定，binding_key为topic.message,就是完全匹配
     *
     * @param queueMessage
     * @param exchange
     * @return
     */
    @Bean
    Binding bindingExchangeMessage(Queue queueMessage, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).with("topic.message");
    }

    /**
     * 将队列topic.messages与exchange绑定，binding_key为topic.#,模糊匹配
     *
     * @param queueMessages
     * @param exchange
     * @return
     */
    @Bean
    Binding bindingExchangeMessages(Queue queueMessages, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");
    }

    @Bean
    Binding bindingExchangeA(Queue AMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(AMessage).to(fanoutExchange);
    }

    @Bean
    Binding bindingExchangeB(Queue BMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(BMessage).to(fanoutExchange);
    }

    @Bean
    Binding bindingExchangeC(Queue CMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(CMessage).to(fanoutExchange);
    }

}
