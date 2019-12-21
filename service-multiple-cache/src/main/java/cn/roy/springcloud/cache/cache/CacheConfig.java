package cn.roy.springcloud.cache.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean("cacheRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(connectionFactory());
        redisMessageListenerContainer.addMessageListener((Message message, byte[] pattern) -> {
            logger.info("收到过期消息：{}", message);
            //TODO 清除本地缓存
        }, new PatternTopic("__keyevent@*__:expired"));
        return redisMessageListenerContainer;
    }

    @Bean
    public void registerRedisKeyExpiredListener() {
        RedisMessageListenerContainer container = redisMessageListenerContainer();
        KeyExpirationEventMessageListener listener = new KeyExpirationEventMessageListener(container);
        listener.init();
        listener.setApplicationEventPublisher(applicationContext);
    }

    @EventListener(classes = RedisKeyExpiredEvent.class)
    public void listener(RedisKeyExpiredEvent expiredEvent) {
        logger.info("监听到过期事件：" + expiredEvent);
        String cacheName = expiredEvent.getKeyspace();
        String key = new String(expiredEvent.getId());
        logger.info("解析后数据：Keyspace={},id={}", cacheName, key);
        EhCacheCacheManager ehCacheCacheManager = ehCacheCacheManager();
        Cache cache = ehCacheCacheManager.getCache(cacheName);
        if (null != cache) {
            logger.info("清除EhCache中缓存");
            cache.evict(key);
        }
    }

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory());
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory())
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
