package cn.roy.springcloud.api2.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-04-25 11:25
 * @Version: v1.0
 */
@FeignClient(name = "api")
public interface RemoteCallService {

    @RequestMapping(value = "/test/hello",method = RequestMethod.GET)
    String getStringFromApi();

}
