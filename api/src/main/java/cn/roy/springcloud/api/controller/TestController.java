package cn.roy.springcloud.api.controller;

import cn.roy.springcloud.api.entity.User;
import cn.roy.springcloud.api.util.MailUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-04-25 10:51
 * @Version: v1.0
 */
@RefreshScope
@RestController
@RequestMapping("test")
@Api(tags = "TestController相关接口")
public class TestController {

    @Value("${server.port}")
    private int port;

    @Value("${user.name}")
    private String userName;

    @Value("#{'${mail.sendTo}'.split(',')}")
    private String[] sendTo;

    @Value("#{'${mail.ccTo}'.split(',')}")
    private String[] ccTo;

    @Autowired
    private MailUtil mailUtil;

    /**
     * ApiOperation使用说明
     * value:接口功能
     * notes:接口描述
     * tags:用于接口分类，相当于按tag分组
     */
    @ApiOperation(value = "接口名称", notes = "功能：接口功能描述", tags = {"tag1"})
    @PostMapping("hello")
    public String hello(@RequestBody User user) {
        Map<String, Object> map = new HashedMap<>();
        map.put("name", "kk20");
        return mailUtil.processThymeleafTemplateIntoString("entity.html", map);
    }

    @ApiOperation(value = "接口名称", notes = "功能：接口功能描述", tags = {"tag1"})
    @GetMapping("read")
    public String getUserName() {
        return "I am api，端口：" + port + "，从配置中心读取的名字是：" + userName;
    }

    @ApiOperation(value = "发送文本邮件", notes = "功能：发送文本邮件", tags = {"邮件"})
    @GetMapping("send")
    public String sendSimpleMail() {
        String content = "这是一封简单的测试邮件";
        try {
            mailUtil.sendSimpleMail(sendTo, ccTo, "测试普通文本邮件", content);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "发送失败";
        }
    }

    @ApiOperation(value = "发送附件邮件", notes = "功能：发送附件邮件", tags = {"邮件"})
    @GetMapping("send2")
    public String sendAttachmentsMail() {
        String filePath = "C:\\Users\\Roy Z Zhou\\Desktop\\log.xls";
        String filePath1 = "C:\\Users\\Roy Z Zhou\\Desktop\\1月.xlsm";
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        filePaths.add(filePath1);
        try {
            mailUtil.sendAttachmentsMail(sendTo, ccTo, "测试发送附件邮件", "", filePaths);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "发送失败";
        }
    }

    @ApiOperation(value = "发送模板邮件（带附件）", notes = "功能：发送模板邮件（带附件）", tags = {"邮件"})
    @GetMapping("send3")
    public String sendEmail() {
        String filePath = "C:\\Users\\Roy Z Zhou\\Desktop\\log.xls";
        String filePath1 = "C:\\Users\\Roy Z Zhou\\Desktop\\1月.xlsm";
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        filePaths.add(filePath1);
        Map<String, Object> model = new HashedMap();
        model.put("name", "Roy");
        model.put("password", "123456");
        model.put("age", 21);
        String content = mailUtil.processFreemarkerTemplateIntoString("test.ftl", model);
        try {
            mailUtil.sendTemplateMail(sendTo, ccTo, "测试模板邮件（带附件）", content, filePaths);
            return "发送成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "发送失败";
        }
    }
}
