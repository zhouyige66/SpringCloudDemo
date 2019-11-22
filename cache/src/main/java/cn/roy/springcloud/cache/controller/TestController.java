package cn.roy.springcloud.cache.controller;

import cn.roy.springcloud.cache.bean.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-11-21 14:38
 * @Version: v1.0
 */
@RestController
public class TestController {

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
