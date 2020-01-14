package cn.roy.springcloud.gateway.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * @Description: Shiro缓存
 * @Author: Roy Z
 * @Date: 2019-11-22 15:15
 * @Version: v1.0
 */
public class ShiroCache<K, V> implements Cache<K, V> {
    private Cache<K, V> ehCache;
    private org.springframework.cache.Cache redisCache;

    public ShiroCache(Cache<K, V> ehCache, org.springframework.cache.Cache redisCache) {
        this.ehCache = ehCache;
        this.redisCache = redisCache;
    }

    @Override
    public V get(K k) throws CacheException {
        if (null == k) {
            return null;
        }

        if (null != ehCache) {
            V v = ehCache.get(k);
            if (null != v) {
                return v;
            }
        }

        org.springframework.cache.Cache.ValueWrapper valueWrapper = redisCache.get(k);
        Object o = valueWrapper.get();
        if (null != o) {
            ehCache.put(k, (V) o);
        }
        return (V) o;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        redisCache.put(k,v);
        ehCache.put(k,v);
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        redisCache.evict(k);
        ehCache.remove(k);
        return null;
    }

    @Override
    public void clear() throws CacheException {
        redisCache.clear();
        ehCache.clear();
    }

    @Override
    public int size() {
        return ehCache.size();
    }

    @Override
    public Set<K> keys() {
        return ehCache.keys();
    }

    @Override
    public Collection<V> values() {
        return ehCache.values();
    }

}
