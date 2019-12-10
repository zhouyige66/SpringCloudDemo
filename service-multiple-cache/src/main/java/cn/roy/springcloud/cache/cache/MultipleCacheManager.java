package cn.roy.springcloud.cache.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Description: 多级Cache Manager
 * @Author: Roy Z
 * @Date: 2019-11-20 14:36
 * @Version: v1.0
 */
public class MultipleCacheManager implements CacheManager {
    private EhCacheCacheManager ehCacheCacheManager;
    private RedisCacheManager redisCacheManager;

    public MultipleCacheManager(@NotNull EhCacheCacheManager ehCacheCacheManager,
                                @NotNull RedisCacheManager redisCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    public EhCacheCacheManager getEhCacheCacheManager() {
        return ehCacheCacheManager;
    }

    public RedisCacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        EhCacheCache ehCacheCache = (EhCacheCache) ehCacheCacheManager.getCache(name);
        Cache cache = redisCacheManager.getCache(name);
        RedisCache redisCache;
        if (cache instanceof RedisCache) {
            redisCache = (RedisCache) cache;
        } else {
            redisCache = (RedisCache) ((TransactionAwareCacheDecorator) cache).getTargetCache();
        }
        MultipleCache multipleCache = new MultipleCache(false, ehCacheCache, redisCache);
        return multipleCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new LinkedHashSet<>();
        names.addAll(ehCacheCacheManager.getCacheNames());
        names.addAll(redisCacheManager.getCacheNames());
        return Collections.unmodifiableSet(names);
    }

}
