package cn.roy.springcloud.api2.service.call;

import cn.roy.springcloud.api2.fallback.RemoteCallServiceFallback;
import io.swagger.models.auth.In;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description: 远程调用，同时配置服务降级处理
 * @Author: Roy Z
 * @Date: 2019-04-25 11:25
 * @Version: v1.0
 */
@FeignClient(name = "api", fallback = RemoteCallServiceFallback.class)
public interface RemoteCallService {

    @GetMapping(value = "/test/timeOut/{time}")
    String timeOut(@PathVariable Integer time);

}
