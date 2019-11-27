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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = "Test相关接口")
@DefaultProperties(defaultFallback = "defaultFallback")
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
    public ResultData name(HttpServletRequest request) {
        String baseInfo = request.getHeader("baseInfo");
        System.out.println("读取的baseInfo参数：" + baseInfo);

        SimpleMapDto simpleMapDto = new SimpleMapDto();
        simpleMapDto.add("name",userName);
        return ResultData.success(simpleMapDto);
    }

    @ApiOperation(value = "服务处理时间4S", notes = "功能：供其他服务调用接口")
    @GetMapping("time")
    public ResultData time() {
        try {
            Thread.sleep(4000);
            SimpleDto stringSimpleDto = new SimpleDto();
            stringSimpleDto.setValue("睡眠4秒");
            return ResultData.success(stringSimpleDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultData.serverError();
        }
    }

    @ApiOperation(value = "服务处理时间6S", notes = "功能：供其他服务调用接口")
    @GetMapping("timeOver")
    public ResultData timeOver() {
        try {
            Thread.sleep(6000);
            SimpleDto stringSimpleDto = new SimpleDto();
            stringSimpleDto.setValue("睡眠6秒");
            return ResultData.success(stringSimpleDto);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultData.serverError();
        }
    }

    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call")
    public String call() {
        return remoteCallService.callTimeFromApi2();
    }

    @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            })
    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call2")
    public String call2() {
        return remoteCallService.callTimeOverFromApi2();
    }

    //    HystrixCommandProperties中可查看属性含义
//    @HystrixCommand(
//            fallbackMethod = "buildFallbackLicenseList",
//            threadPoolKey = "licenseByOrgThreadPool",
//            threadPoolProperties = {
//                    @HystrixProperty(name = "coreSize",value="30"),
//                    @HystrixProperty(name="maxQueueSize", value="10")
//            }
//    )
    @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5500")
            })
    @ApiOperation(value = "测试远程调用接口", notes = "功能：测试远程调用")
    @GetMapping("call3")
    public String call3() {
        return remoteCallService.callTimeOverFromApi2();
    }

    private String fallback() {
        return "太拥挤了，请稍后再试";
    }

    private String defaultFallback() {
        return "默认太拥挤了，请稍后再试";
    }

}
