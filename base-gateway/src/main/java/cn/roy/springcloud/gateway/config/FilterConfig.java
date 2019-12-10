package cn.roy.springcloud.gateway.config;

import cn.roy.springcloud.gateway.filter.PostFilter;
import cn.roy.springcloud.gateway.filter.PreFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/22 10:19
 * @Version: v1.0
 */
@Configuration
public class FilterConfig {

    @Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }

    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }

}
