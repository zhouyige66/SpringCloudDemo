package cn.roy.springcloud.api;

import cn.roy.springcloud.api.datasource.SlaveDatasourcePropertyContainer;
import cn.roy.springcloud.api.intercepter.SQLInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-08-06 16:25
 * @Version: v1.0
 */
@Configuration
public class MyBatisConfig {

    @Autowired
    SlaveDatasourcePropertyContainer slaveDatasourcePropertyContainer;

    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                configuration.addInterceptor(new SQLInterceptor(slaveDatasourcePropertyContainer.getDatasource().size()-1));
            }
        };
    }

}
