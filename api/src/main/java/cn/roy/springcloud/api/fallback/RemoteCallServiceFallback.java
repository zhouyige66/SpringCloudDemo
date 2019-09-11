package cn.roy.springcloud.api.fallback;

import cn.roy.springcloud.api.service.call.RemoteCallService;
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
    public String callTimeFromApi2() {
        return "调用远程服务超时，进行降级操作";
    }

    @Override
    public String callTimeOverFromApi2() {
        return "调用远程服务超时，进行降级操作";
    }

}