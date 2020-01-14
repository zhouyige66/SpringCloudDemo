package cn.roy.springcloud.gateway.controller;

import cn.roy.springcloud.base.dto.SimpleDto;
import cn.roy.springcloud.base.dto.SimpleMapDto;
import cn.roy.springcloud.base.http.ResultData;
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

    @GetMapping("getUserById/{uid}")
    public ResultData getUser(HttpServletRequest request, @PathVariable String uid) {
        logger.debug("request中获取sessionId：{}", request.getSession().getId());
        Subject subject = SecurityUtils.getSubject();
        logger.debug("subject中获取sessionId：{}", subject.getSession().getId());

        try {
            int id = Integer.parseInt(uid);
            SimpleMapDto simpleMapDto = new SimpleMapDto();
            simpleMapDto.add("id", "userId_" + id)
                    .add("requestSessionId", request.getSession().getId())
                    .add("subjectSessionId", subject.getSession().getId());
            return ResultData.success(simpleMapDto);
        } catch (Exception e) {
            return ResultData.serverError("未找到指定用户");
        }
    }

}
