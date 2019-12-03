package cn.roy.springcloud.api2.controller;

import cn.roy.springcloud.api2.service.call.RemoteCallService;
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
    @RefreshScope
    public String name() {
        return "从配置中心读取的名字为：" + userName;
    }

    @ApiOperation(value = "服务处理时间4S", notes = "功能：供其他服务调用接口")
    @GetMapping("time")
    public String time() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "睡眠4秒";
    }

    @ApiOperation(value = "服务处理时间6S", notes = "功能：供其他服务调用接口")
    @GetMapping("timeOver")
    public String timeOver() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "睡眠6秒";
    }

    @GetMapping("callTime")
    @ApiOperation(value = "远程接口调用，未超时", notes = "功能：远程调用测试接口")
    public String callTime() {
        return remoteCallService.callTimeFromApi();
    }

    @GetMapping("callTimeOver")
    @ApiOperation(value = "远程接口调用，超时", notes = "功能：远程调用测试接口")
    public String callTimeOver() {
        return remoteCallService.callTimeOverFromApi();
    }

}
