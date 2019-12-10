package cn.roy.springcloud.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableJms
public class ApiApplication extends SpringBootServletInitializer implements CommandLineRunner, ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApiApplication.class);
    }

    @Override
    public void run(String... args) {
        logger.info("---CommandLineRunner---");
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("---ApplicationRunner---");
    }

}
