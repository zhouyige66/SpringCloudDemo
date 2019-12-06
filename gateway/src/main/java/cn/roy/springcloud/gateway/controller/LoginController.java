package cn.roy.springcloud.gateway.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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

    @PostMapping("login")
    public String login(@RequestBody User user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken authenticationToken = new UsernamePasswordToken(user.getUserName(),
                user.getPassword());
        try {
            subject.login(authenticationToken);
            return "success";
        } catch (AuthenticationException e) {
            return e.getMessage();
        }
    }

    @GetMapping("user/{id}")
    public String getUser(@PathVariable String id) {
        return "user_" + id;
    }

    @GetMapping("test")
    public String test(@RequestParam String txt) {
        return "hello " + txt;
    }

    public static final class User {
        private String userName;
        private String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private String fallback(@Nullable Object obj) {
        return "调用默认降级方法";
    }

}
