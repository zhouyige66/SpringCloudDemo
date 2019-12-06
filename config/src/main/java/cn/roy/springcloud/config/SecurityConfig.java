package cn.roy.springcloud.config;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Description: 安全配置
 * @Author: Roy Z
 * @Date: 2019-12-04 10:36
 * @Version: v1.0
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        //ignore
        web.ignoring().antMatchers("/encrypt","/encrypt/*", "/health", "/decrypt","/refresh");
    }

}
