package cn.roy.springcloud.mq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MqApplication implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String name;

    @Value("${server.name.mq}")
    private String name2;

    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("读取的名字：" + name);
        System.out.println("读取的名字2：" + name2);
    }

}

