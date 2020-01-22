package cn.roy.springcloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    public static void main(String[] args) {
        /**
         * 测试agent类替换功能
         */
        System.out.println("执行main方法");
        User user = new User("roy");
        user.print();
        user.print("hello,");

        SpringApplication.run(EurekaApplication.class, args);
    }

}
