package cn.roy.springcloud.api.controller;

import cn.roy.springcloud.api.service.RemoteCallService;
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

    @Autowired
    private RemoteCallService remoteCallService;

    /**
     * ApiOperation使用说明
     * value:接口功能
     * notes:接口描述
     * tags:用于接口分类，相当于按tag分组
     */
    @ApiOperation(value = "用户名接口", notes = "功能：获取配置中心配置的用户名")
    @GetMapping("name")
    public String name() {
        return "I am api，从配置中心读取的名字是：" + userName;
    }

    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call")
    public String call() {
        return "I am api，调用api2返回结果：" + remoteCallService.getStringFromApi2();
    }

    @ApiOperation(value = "服务降级功能测试接口，未超时", notes = "功能：供其他服务调用接口")
    @GetMapping("time")
    public String time() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "I am api，返回数据：success";
    }


    @ApiOperation(value = "服务降级功能测试接口，超时测试", notes = "功能：供其他服务调用接口")
    @GetMapping("timeOver")
    public String timeOver() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "I am api，返回数据：success";
    }

}
