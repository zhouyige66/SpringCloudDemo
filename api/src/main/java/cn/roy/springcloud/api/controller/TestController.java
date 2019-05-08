package cn.roy.springcloud.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-04-25 10:51
 * @Version: v1.0
 */
@RefreshScope
@RestController
@RequestMapping("test")
@Api(tags = "TestController相关接口")
public class TestController {

    @Value("${server.port}")
    private int port;

    @Value("${user.name}")
    private String userName;

    /**
     * ApiOperation使用说明
     * value:接口功能
     * notes:接口描述
     * tags:用于接口分类，相当于按tag分组
     */
    @ApiOperation(value = "我是hello接口",notes = "这是hello接口描述",tags = {"tag1","tag2"})
    @GetMapping("hello")
    public String hello() {
        return "I am api，端口：" + port + "，测试姓名：" + userName;
    }

    @ApiOperation(value = "我是name接口value",notes = "我是name接口notes",tags = {"tag1","tag3"})
    @GetMapping("hello2")
    public String getUserName() {
        return "I am api，端口：" + port + "，从配置中心读取的名字是：" + userName;
    }

}
