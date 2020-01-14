package cn.roy.springcloud.gateway.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-11-22 17:24
 * @Version: v1.0
 */
@RestController
@RequestMapping("user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("getUserById/{id}")
    public String getUser(HttpServletRequest request, @PathVariable String id) {
        logger.debug("request中获取sessionId：{}", request.getSession().getId());
        Subject subject = SecurityUtils.getSubject();
        logger.debug("subject中获取sessionId：{}", subject.getSession().getId());
        return "user_" + id;
    }

}
