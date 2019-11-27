package cn.roy.springcloud.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }

    @Bean
    public RmiRegistryFactoryBean rmiRegistryFactoryBean(){
        RmiRegistryFactoryBean factoryBean = new RmiRegistryFactoryBean();
//        factoryBean.setPort();
        factoryBean.setAlwaysCreate(true);
        return factoryBean;
    }

    @Bean
    public ConnectorServerFactoryBean connectorServerFactoryBean(){
        ConnectorServerFactoryBean factoryBean = new ConnectorServerFactoryBean();
//        factoryBean.setServiceUrl();
        return factoryBean;
    }

}
