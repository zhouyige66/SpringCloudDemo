package cn.roy.springcloud.api2.service.call;

import cn.roy.springcloud.api2.fallback.RemoteCallServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description: 远程调用，同时配置服务降级处理
 * @Author: Roy Z
 * @Date: 2019-04-25 11:25
 * @Version: v1.0
 */
@FeignClient(name = "${server.name.api}", fallback = RemoteCallServiceFallback.class)
public interface RemoteCallService {

    @GetMapping(value = "/test/timeout/{time}")
    String timeout(@PathVariable Integer time);

}
