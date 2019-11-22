package cn.roy.springcloud.gateway.config;

import cn.roy.springcloud.gateway.cache.MultipleCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.time.Duration;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/22 10:20
 * @Version: v1.0
 */
@Configuration
public class CacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        // cookie名字
        defaultCookieSerializer.setCookieName("sessionId");
        // 不同子域时设置
        // defaultCookieSerializer.setDomainName("xxx.com");
        // 设置各web应用返回的cookiePath一致
        defaultCookieSerializer.setCookiePath("/");
        return defaultCookieSerializer;
    }

    @Bean("cacheRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener((Message message, byte[] pattern) -> {
            logger.info("收到过期消息：{}", message);
            //TODO 清除本地缓存
        }, new PatternTopic("__keyevent@*__:expired"));
//        redisMessageListenerContainer.addMessageListener((Message message, byte[] pattern) -> {
//            logger.info("收到keyspace消息：" + message);
//        }, new PatternTopic("__keyspace@*__:*"));

        return redisMessageListenerContainer;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(3)))
                .transactionAware()
                .build();
        return redisCacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(ehCacheManagerFactoryBean().getObject());
        return ehCacheCacheManager;
    }

    @Bean
    @Primary
    public CacheManager multipleCacheManager() {
        MultipleCacheManager multipleCacheManager = new MultipleCacheManager(ehCacheCacheManager(), redisCacheManager());
        return multipleCacheManager;
    }

}
