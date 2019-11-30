package cn.roy.springcloud.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/8 13:37
 * @Version: v1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }

}
