package cn.roy.springcloud.api2.controller;

import cn.roy.springcloud.api2.service.RemoteCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-04-25 11:00
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

    @Autowired
    RemoteCallService remoteCallService;

    @GetMapping("hello")
    @ApiOperation(value = "hello", notes = "功能：接口功能描述")
    public String hello() {
        return "I am api2，端口：" + port + "，姓名：" + userName;
    }

    @GetMapping("call")
    @ApiOperation(value = "remoteCall", notes = "功能：接口功能描述")
    public String remoteCall() {
        return "我是api2，我调用了api，返回：" + remoteCallService.getStringFromApi();
    }

}
