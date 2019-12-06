package cn.roy.springcloud.api2.controller;

import cn.roy.springcloud.api2.service.call.RemoteCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("name")
    @ApiOperation(value = "查询用户名接口", notes = "功能：查询配置用户名称")
    @RefreshScope
    public String name() {
        return "从配置中心读取的名字为：" + userName;
    }

    @ApiOperation(value = "模拟服务处理请求接口", notes = "功能：模拟后台耗时处理")
    @GetMapping("timeOut/{time}")
    public String timeOut(@PathVariable Integer time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "睡眠时间：" + time;
    }

    @ApiOperation(value = "远程服务调用测试", notes = "功能：远程服务调用测试")
    @GetMapping("call/{time}")
    public String call(@PathVariable Integer time) {
        return remoteCallService.timeOut(time);
    }

}
