package cn.roy.springcloud.gateway.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/10/30 13:55
 * @Version: v1.0
 */
@Configuration
@Order(1)
public class EhCacheConfig {

    @Resource(name = "mainCacheManager")
    CacheManager manager;

    /**
     * EhCacheManagerFactoryBean缓存管理器，默认的为EhCacheCacheManager
     * Spring分别通过CacheManager.create()和new CacheManager方式来创建一个ehcache工厂
     * 一个EhCacheManagerFactoryBean创建完成, 也就代表着一个CacheManager
     */
    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean(name = "EhCacheManager")
    public net.sf.ehcache.CacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean) {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(bean.getObject());
        CompositeCacheManager cacheManager = (CompositeCacheManager) manager;
        cacheManager.setCacheManagers(Arrays.asList(ehCacheCacheManager));
        return ehCacheCacheManager.getCacheManager();
    }

}




