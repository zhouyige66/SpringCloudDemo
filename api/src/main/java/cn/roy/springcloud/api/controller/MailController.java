package cn.roy.springcloud.api.controller;

import cn.roy.springcloud.api.util.MailUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 邮件控制器
 * @Author: Roy Z
 * @Date: 2019-06-19 13:32
 * @Version: v1.0
 */
@RestController
@RequestMapping("mail")
@Api(tags = "Mail相关接口")
public class MailController {

    @Value("#{'${custtom.mail.sendTo}'.split(',')}")
    private String[] sendTo;

    @Value("#{'${custtom.mail.ccTo}'.split(',')}")
    private String[] ccTo;

    @Autowired
    private MailUtil mailUtil;

    @ApiOperation(value = "发送文本邮件接口", notes = "功能：发送文本邮件")
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

    @ApiOperation(value = "发送附件邮件接口", notes = "功能：发送附件邮件")
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

    @ApiOperation(value = "发送模板邮件（带附件）接口", notes = "功能：发送模板邮件（带附件）")
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

    @ApiOperation(value = "模板接口", notes = "功能：获取模板数据")
    @PostMapping("template")
    public String template() {
        Map<String, Object> map = new HashedMap<>();
        map.put("name", "kk20");
        return mailUtil.processThymeleafTemplateIntoString("entity.html", map);
    }
}
