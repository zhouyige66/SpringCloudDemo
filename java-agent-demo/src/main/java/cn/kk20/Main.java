package cn.kk20;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020-01-09 13:38
 * @Version: v1.0
 */
@SpringBootApplication
@EnableSwagger2
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);

        // 多线程测试
        ExecutorService executorService = Executors.newWorkStealingPool();


    }

}
