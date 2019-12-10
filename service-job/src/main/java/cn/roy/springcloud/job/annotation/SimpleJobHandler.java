package cn.roy.springcloud.job.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/8 13:33
 * @Version: v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SimpleJobHandler {

    String jobName() default "";

    String jobParameter() default "";

    String cron() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";
}
