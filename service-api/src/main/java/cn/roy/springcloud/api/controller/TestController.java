package cn.roy.springcloud.api.controller;

import cn.roy.springcloud.api.service.call.RemoteCallService;
import cn.roy.springcloud.base.dto.SimpleDto;
import cn.roy.springcloud.base.dto.SimpleMapDto;
import cn.roy.springcloud.base.http.ResultData;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
@DefaultProperties(defaultFallback = "defaultFallback")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Value("${server.port}")
    private int port;

    @Value("${zipkin.url}")
    private String zipkinUrl;

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
    public ResultData name(HttpServletRequest request) {
        String baseInfo = request.getHeader("baseInfo");
        System.out.println("读取的baseInfo参数：" + baseInfo);

        SimpleMapDto simpleMapDto = new SimpleMapDto();
        simpleMapDto.add("name", userName);
        simpleMapDto.add("port", port);
        simpleMapDto.add("zipkin", zipkinUrl);
        return ResultData.success(simpleMapDto);
    }

    @ApiOperation(value = "模拟服务处理请求接口", notes = "功能：模拟后台耗时处理")
    @GetMapping("timeout/{time}")
    public ResultData timeout(@PathVariable Integer time) {
        try {
            Thread.sleep(time);
            SimpleDto stringSimpleDto = new SimpleDto();
            stringSimpleDto.setValue("睡眠时间：" + time);
            return ResultData.success(stringSimpleDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultData.serverError();
        }
    }

    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call/{time}")
    public String call(@PathVariable Integer time) {
        return remoteCallService.timeout(time);
    }

    @HystrixCommand(
            fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            })
    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call2/{time}")
    public String call2(@PathVariable Integer time) {
        logger.info("command超时时间设置为：3000，请求时间：{}",time);
        return remoteCallService.timeout(time);
    }

    @HystrixCommand(
            fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4500")
            })
    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call3/{time}")
    public String call3(@PathVariable Integer time) {
        logger.info("command超时时间设置为：4500，请求时间：{}",time);
        return remoteCallService.timeout(time);
    }

    private String fallback(Integer time) {
        return "调用定制方法，服务熔断（降级了）";
    }

    private String defaultFallback(Integer time) {
        return "调用默认方法，服务熔断（降级了）";
    }

    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("session")
    public String sessionTest(HttpServletRequest request){
        return request.getSession().getId();
    }

    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("session2")
    public String sessionTest2(HttpServletRequest request){
        return request.getSession().getId();
    }
}
