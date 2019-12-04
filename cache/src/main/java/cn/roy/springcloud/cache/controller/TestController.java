package cn.roy.springcloud.cache.controller;

import cn.roy.springcloud.base.dto.SimpleDto;
import cn.roy.springcloud.base.http.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-11-21 14:38
 * @Version: v1.0
 */
@RestController
@RequestMapping("test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Cacheable(value = "user", key = "'name_'+#id")
    @GetMapping("user/{id}")
    public ResultData getUser(@PathVariable Integer id) {
        logger.info("调用本地方法");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SimpleDto dto = new SimpleDto();
        dto.setValue(String.valueOf(id));
        return ResultData.success(dto);
    }

}
