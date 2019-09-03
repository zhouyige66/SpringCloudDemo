package cn.roy.springcloud.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: 远程调用，同时配置服务降级处理
 * @Author: Roy Z
 * @Date: 2019-04-25 11:25
 * @Version: v1.0
 */
@FeignClient(name = "api2",fallback = RemoteCallServiceFallback.class)
public interface RemoteCallService {

    @GetMapping(value = "/hello/name")
    String getStringFromApi2();

}
