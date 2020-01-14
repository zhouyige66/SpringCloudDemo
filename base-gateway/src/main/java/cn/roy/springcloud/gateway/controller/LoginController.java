package cn.roy.springcloud.gateway.controller;

import cn.roy.springcloud.base.dto.SimpleDto;
import cn.roy.springcloud.base.http.ResultData;
import cn.roy.springcloud.gateway.model.User;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-11-22 17:24
 * @Version: v1.0
 */
@RestController
@RequestMapping("auth")
@DefaultProperties(defaultFallback = "fallback")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("login")
    public ResultData login(@RequestBody User user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken authenticationToken = new UsernamePasswordToken(user.getUserName(),
                user.getPassword());

        logger.debug("subject中获取的sessionId=={}", subject.getSession().getId());
        try {
            subject.login(authenticationToken);
            return ResultData.success(new SimpleDto().setValue("登录成功"));
        } catch (AuthenticationException e) {
            return ResultData.fail(500, "用户名或密码错误");
        }
    }

    @GetMapping("logout")
    public ResultData login() {
        return ResultData.fail(401, "请重新登录");
    }

    private String fallback(@Nullable Object obj) {
        return "调用默认降级方法";
    }

}
