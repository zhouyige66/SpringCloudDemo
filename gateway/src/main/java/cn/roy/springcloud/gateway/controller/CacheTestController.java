package cn.roy.springcloud.gateway.controller;

import cn.roy.springcloud.common.base.SimpleMapDto;
import cn.roy.springcloud.common.http.ResultData;
import cn.roy.springcloud.gateway.util.EhCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/1 09:48
 * @Version: v1.0
 */
@RestController
public class CacheTestController {
    private static final Logger logger = LoggerFactory.getLogger(CacheTestController.class);

    @Autowired
    private EhCacheUtil ehCacheUtil;

    @GetMapping("getUser")
    @Cacheable(value = "cache", key = "'cache_'+#id")
    public ResultData getUser(@RequestParam String id) {
        logger.info("执行数据库查询数据");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SimpleMapDto simpleMapDto = new SimpleMapDto();
        simpleMapDto.add("name", "roy_" + id);
        simpleMapDto.add("id", id);
        simpleMapDto.add("com", "pwc");
        simpleMapDto.add("age", "28");

        // 换出数据
        ResultData success = ResultData.success(simpleMapDto);
        String key = getClass().getName().concat("").concat("");
        ehCacheUtil.addCache(key,success);
        return success;
    }

}
