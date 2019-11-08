package cn.roy.springcloud.gateway.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/4 16:18
 * @Version: v1.0
 */
@Component
public class EhCacheUtil {

    @Resource(name = "EhCacheManager")
    private CacheManager ehCacheManager;

    public void addCache(String key,Object value){
//        CacheConfiguration configuration = new CacheConfiguration();
//        Cache cache = new Cache(configuration);
//        Element element = new Element(key, value);
//        cache.put(element);
//        ehCacheManager.addCache(cache);
    }

}
