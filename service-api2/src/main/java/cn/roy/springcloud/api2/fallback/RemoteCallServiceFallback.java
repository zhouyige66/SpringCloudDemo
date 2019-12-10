package cn.roy.springcloud.api2.fallback;

import cn.roy.springcloud.api2.service.call.RemoteCallService;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-06-18 13:09
 * @Version: v1.0
 */
@Component
public class RemoteCallServiceFallback implements RemoteCallService {

    @Override
    public String timeout(Integer time) {
        return "FeignClient调用远程服务超时，进行降级操作";

    }

}
