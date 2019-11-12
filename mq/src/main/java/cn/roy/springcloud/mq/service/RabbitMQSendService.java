package cn.roy.springcloud.mq.service;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:19
 * @Version: v1.0
 */
public interface RabbitMQSendService {

    void sendText(String text);

}
