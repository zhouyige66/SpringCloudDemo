package cn.roy.springcloud.mq.service;

import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/12 11:10
 * @Version: v1.0
 */
@Service
public interface ActiveMQSendService {

    void sendText2Queue(String queue, String msg);

    void sendText2Topic(String topic, String msg);

    void send2Queue(String queue, Object obj);

    void send2Top(String topic, Object obj);

}
