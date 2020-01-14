//package cn.roy.springcloud.gateway.shiro;
//
//import org.apache.shiro.cache.Cache;
//import org.apache.shiro.cache.CacheException;
//import org.apache.shiro.cache.CacheManager;
//import org.apache.shiro.cache.ehcache.EhCacheManager;
//import org.springframework.data.redis.cache.RedisCacheManager;
//
///**
// * @Description: Shiro缓存管理器
// * @Author: Roy Z
// * @Date: 2019-11-22 14:35
// * @Version: v1.0
// */
//public class ShiroCacheManager implements CacheManager {
//
//    private EhCacheManager ehCacheManager;
//    private RedisCacheManager redisCacheManager;
//
//    public ShiroCacheManager(EhCacheManager ehCacheManager, RedisCacheManager redisCacheManager) {
//        this.ehCacheManager = ehCacheManager;
//        this.redisCacheManager = redisCacheManager;
//    }
//
//    @Override
//    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
//        Cache<K, V> ehCache = ehCacheManager.getCache(s);
//        org.springframework.cache.Cache redisCache = redisCacheManager.getCache(s);
//        ShiroCache shiroCache = new ShiroCache(ehCache,redisCache);
//        return shiroCache;
//    }
//
//}
