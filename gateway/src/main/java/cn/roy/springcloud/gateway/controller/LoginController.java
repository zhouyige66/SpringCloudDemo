package cn.roy.springcloud.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019-11-22 17:24
 * @Version: v1.0
 */
@RestController("auth")
public class LoginController {

    @PostMapping("login")
    public String login(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken authenticationToken = new UsernamePasswordToken(jsonObject.getString("username"),
                jsonObject.getString("password"));
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

}
