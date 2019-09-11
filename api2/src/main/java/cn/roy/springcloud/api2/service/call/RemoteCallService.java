package cn.roy.springcloud.api2.service.call;

import cn.roy.springcloud.api2.fallback.RemoteCallServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
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

    @RequestMapping(value = "/test/time", method = RequestMethod.GET)
    String callTimeFromApi();

    @RequestMapping(value = "/test/timeOver", method = RequestMethod.GET)
    String callTimeOverFromApi();

}