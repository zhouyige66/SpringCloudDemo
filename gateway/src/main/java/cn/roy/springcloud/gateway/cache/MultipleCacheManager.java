package cn.roy.springcloud.gateway.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;

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

    public MultipleCacheManager(EhCacheCacheManager ehCacheCacheManager, RedisCacheManager redisCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    public EhCacheCacheManager getEhCacheCacheManager() {
        return ehCacheCacheManager;
    }

    public void setEhCacheCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
    }

    public RedisCacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        EhCacheCache ehCacheCache = (EhCacheCache) ehCacheCacheManager.getCache(name);
        RedisCache redisCache = (RedisCache) redisCacheManager.getCache(name);
        MultipleCache cache = new MultipleCache(false, ehCacheCache, redisCache);
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new LinkedHashSet<>();
        names.addAll(ehCacheCacheManager.getCacheNames());
        names.addAll(redisCacheManager.getCacheNames());
        return Collections.unmodifiableSet(names);
    }

}
