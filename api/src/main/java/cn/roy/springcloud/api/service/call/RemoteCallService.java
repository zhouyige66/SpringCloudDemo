package cn.roy.springcloud.api.service.call;

import cn.roy.springcloud.api.fallback.RemoteCallServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description: 远程调用，同时配置服务降级处理
 * @Author: Roy Z
 * @Date: 2019-04-25 11:25
 * @Version: v1.0
 */
@FeignClient(name = "api2", fallback = RemoteCallServiceFallback.class)
public interface RemoteCallService {

    @GetMapping(value = "/test/timeOut/{time}")
    String timeOut(@PathVariable Integer time);

}
