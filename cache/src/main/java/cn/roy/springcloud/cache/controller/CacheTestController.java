package cn.roy.springcloud.cache.controller;

import cn.roy.springcloud.cache.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/1 09:48
 * @Version: v1.0
 */
@RestController
public class CacheTestController {
    private static final Logger logger = LoggerFactory.getLogger(CacheTestController.class);

    @Cacheable(value = "user",key = "'name_'+#id")
    @GetMapping("user/{id}")
    public User getUser(@PathVariable Integer id) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User(id, "name_" + id);
    }

}
