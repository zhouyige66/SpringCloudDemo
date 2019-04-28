package cn.roy.springcloud.api2.controller;

import cn.roy.springcloud.api2.service.RemoteCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-04-25 11:00
 * @Version: v1.0
 */
@RestController
@RequestMapping("api2")
public class TestController {

    @Autowired
    RemoteCallService remoteCallService;

    @GetMapping("hello")
    public String hello(){
        return "I am api2";
    }

    @GetMapping("hello2")
    public String remoteCall(){
        return remoteCallService.getStringFromApi();
    }

}
