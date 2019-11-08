package cn.roy.springcloud.gateway.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.time.Duration;
import java.util.Arrays;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/10/30 10:23
 * @Version: v1.0
 */
@Configuration
@Order(2)
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60) //注解，开启redis集中session管理
public class RedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private RedisConnectionFactory factory;

    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
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

    /**********功能：基础存储类型操作**********/
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    /**********功能：缓存相关**********/
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        // cookie名字
        defaultCookieSerializer.setCookieName("sessionId");
        //不同子域时设置
        // defaultCookieSerializer.setDomainName("xxx.com");
        // 设置各web应用返回的cookiePath一致
        defaultCookieSerializer.setCookiePath("/");
        return defaultCookieSerializer;
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("mainCacheManager") CacheManager manager) {
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(3)))
                .transactionAware()
                .build();
        CompositeCacheManager cacheManager = (CompositeCacheManager) manager;
        cacheManager.setCacheManagers(Arrays.asList(redisCacheManager));
        return redisCacheManager;
    }

    @Bean("cacheRedisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(factory);
        redisMessageListenerContainer.addMessageListener((Message message, byte[] pattern) -> {
            logger.info("收到过期消息：{}", message);
            // 清除本地缓存

        }, new PatternTopic("__keyevent@*__:expired"));
//        redisMessageListenerContainer.addMessageListener((Message message, byte[] pattern) -> {
//            logger.info("收到keyspace消息：" + message);
//        }, new PatternTopic("__keyspace@*__:*"));

        return redisMessageListenerContainer;
    }

}
