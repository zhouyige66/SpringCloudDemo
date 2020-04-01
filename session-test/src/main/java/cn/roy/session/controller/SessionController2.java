package cn.roy.session.controller;

import cn.roy.session.service.PrintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("session2")
@Api(tags = "Session测试接口2")
public class SessionController2 {
    private static final Logger logger = LoggerFactory.getLogger(SessionController2.class);

    @Autowired
    PrintService printService;

    @ApiOperation(value = "获取sessionId", notes = "功能：获取调用接口的客户端传递的sessionId")
    @GetMapping("getId")
    public String sessionTest(HttpServletRequest request) {
        logger.info("调用了session2接口");
        printService.print("请求的sessionId为：" + request.getSession().getId());
        return request.getSession().getId();
    }

}
