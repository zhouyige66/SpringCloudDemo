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
@RequestMapping("hello")
@Api(tags = "HelloController相关接口")
public class HelloController {

    @Value("${server.port}")
    private int port;

    @Value("${user.name}")
    private String userName;

    @Autowired
    RemoteCallService remoteCallService;

    @GetMapping("name")
    @ApiOperation(value = "查询用户名接口", notes = "功能：查询配置用户名称")
    public String name() {
        return "I am api2，从配置中心读取的名字为：" + userName;
    }

    @GetMapping("call")
    @ApiOperation(value = "远程调用测试接口", notes = "功能：远程调用测试接口")
    public String call() {
        return "I am api2，调用api返回数据：" + remoteCallService.getStringFromApi();
    }

    @GetMapping("timeOver")
    @ApiOperation(value = "远程调用测试接口", notes = "功能：远程调用测试接口")
    public String timeOver() {
        return "I am api2，调用api返回数据：" + remoteCallService.getStringFromApi2();
    }

}
