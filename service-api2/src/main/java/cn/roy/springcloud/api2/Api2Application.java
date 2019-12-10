package cn.roy.springcloud.api2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableRabbit
public class Api2Application extends SpringBootServletInitializer implements CommandLineRunner, ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(Api2Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Api2Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Api2Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("---CommandLineRunner---");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("---ApplicationRunner---");
    }

}
