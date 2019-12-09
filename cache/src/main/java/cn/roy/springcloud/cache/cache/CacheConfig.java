package cn.roy.springcloud.cache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

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
    private ApplicationContext applicationContext;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

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
    public void registerRedisKeyExpiredListener() {
        RedisMessageListenerContainer container = redisMessageListenerContainer();
        KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(container);
//        listener.init();
        listener.setApplicationEventPublisher(applicationContext);
    }

    @EventListener(classes = RedisKeyExpiredEvent.class)
    public void listener(RedisKeyExpiredEvent expiredEvent) {
        logger.info("监听到过期事件：" + expiredEvent);
        String cacheName = expiredEvent.getKeyspace();
        String key = new String(expiredEvent.getId());

        EhCacheCacheManager ehCacheCacheManager = ehCacheCacheManager();
        Cache cache = ehCacheCacheManager.getCache(cacheName);
        if (null != cache) {
            logger.info("清除EhCache中缓存");
            cache.evict(key);
        }
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
