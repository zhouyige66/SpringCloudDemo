package cn.kk20.action;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-04-25 10:51
 * @Version: v1.0
 */
@RestController
@RequestMapping("test")
@Api(tags = "TestController相关接口")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @ApiOperation(value = "session测试", notes = "功能：session测试")
    @GetMapping("session")
    public String sessionTest(HttpServletRequest request){
        return request.getSession().getId();
    }

    @ApiOperation(value = "session测试2", notes = "功能：session测试2")
    @GetMapping("session2")
    public String sessionTest2(org.apache.catalina.servlet4preview.http.HttpServletRequest request){
        return request.getSession().getId();
    }

}
