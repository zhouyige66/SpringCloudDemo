package cn.roy.springcloud.api2.service;

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
    public String getStringFromApi() {
        return "fail";
    }

}
