package cn.roy.springcloud.cache.cache;

import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.cache.RedisCache;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 多级Cache
 * @Author: Roy Z
 * @Date: 2019-11-20 14:36
 * @Version: v1.0
 */
public class MultipleCache extends AbstractValueAdaptingCache {

    private EhCacheCache ehCacheCache;
    private RedisCache redisCache;

    /**
     * Create an {@code AbstractValueAdaptingCache} with the given setting.
     *
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected MultipleCache(boolean allowNullValues, EhCacheCache ehCacheCache, RedisCache redisCache) {
        super(allowNullValues);
        this.ehCacheCache = ehCacheCache;
        this.redisCache = redisCache;
    }

    @Override
    protected Object lookup(Object key) {
        ValueWrapper valueWrapper = ehCacheCache.get(key);
        if (null != valueWrapper) {
            return valueWrapper.get();
        }

        ValueWrapper redisValueWrapper = redisCache.get(key);
        if (null != redisValueWrapper) {
            // 存入一级缓存
            ehCacheCache.put(key,redisValueWrapper);
            return redisValueWrapper.get();
        }

        return null;
    }

    @Override
    public String getName() {
        return "Multiple Cache";
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if (value != null) {
            return (T) value;
        }

        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            value = lookup(key);
            if(value != null) {
                return (T) value;
            }
            value = valueLoader.call();
            Object storeValue = toStoreValue(valueLoader.call());
            put(key, storeValue);
            return (T) value;
        } catch (Exception e) {
            try {
                Class<?> c = Class.forName("org.springframework.cache.Cache$ValueRetrievalException");
                Constructor<?> constructor = c.getConstructor(Object.class, Callable.class, Throwable.class);
                RuntimeException exception = (RuntimeException) constructor.newInstance(key, valueLoader, e.getCause());
                throw exception;
            } catch (Exception e1) {
                throw new IllegalStateException(e1);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(Object key, Object value) {
        redisCache.put(key,value);
        ehCacheCache.put(key,value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper ehValueWrapper = ehCacheCache.putIfAbsent(key, value);
        ValueWrapper redisValueWrapper = redisCache.putIfAbsent(key, value);
        return null;
    }

    @Override
    public void evict(Object key) {
        redisCache.evict(key);
        ehCacheCache.evict(key);
    }

    @Override
    public void clear() {
        redisCache.clear();
        ehCacheCache.clear();
    }

}
