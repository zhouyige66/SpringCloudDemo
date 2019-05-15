package cn.roy.springcloud.api.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @Description: 邮件发送工具
 * @Author: Roy Z
 * @Date: 2019-04-29 14:36
 * @Version: v1.0
 */
@Component
public class MailUtil {

    @Value("${spring.freemarker.template-loader-path}")
    private String templatePath;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine engine;

    public void sendSimpleMail() throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("798103175@qq.com");
        message.setTo("798103175@qq.com");
        message.setSubject("主题：简单邮件");
        message.setText("测试邮件内容");
        mailSender.send(message);
    }

    public void sendAttachmentsMail() throws Exception {
        // 有些邮件会带上附件，这个时候我们就需要使用MimeMessage来设置复杂一些的邮件内容
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("798103175@qq.com");
        helper.setTo("798103175@qq.com");
        helper.setSubject("主题：有附件");
        helper.setText("有附件的邮件");
        FileSystemResource file = new FileSystemResource(new File("weixin.jpg"));
        helper.addAttachment("附件-1.jpg", file);
        helper.addAttachment("附件-2.jpg", file);

        mailSender.send(mimeMessage);
    }

    public void sendInlineMail() throws Exception {
        // 有些邮件会带上静态资源，这个时候我们就需要使用MimeMessage来设置复杂一些的邮件内容
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("798103175@qq.com");
        helper.setTo("798103175@qq.com");
        helper.setSubject("主题：嵌入静态资源");
        helper.setText("<html><body><img src=\"cid:photo\" ></body></html>", true);
        FileSystemResource file = new FileSystemResource(new File("photo.jpg"));
        // 这里需要注意的是addInline函数中资源名称photo需要与正文中cid:photo对应起来
        helper.addInline("photo", file);

        mailSender.send(mimeMessage);
    }

    public void sendTemplateMail() throws Exception {
        //通常我们使用邮件发送服务的时候，都会有一些固定的场景，比如重置密码、注册确认等，我们会使用模板引擎来为各类邮件设置成模板，这样我们只需要在发送时去替换变化部分的参数即可。
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom("798103175@qq.com");
        helper.setTo("798103175@qq.com");
        helper.setSubject("主题：模板邮件");
        Map<String, Object> model = new HashedMap();
        model.put("username", "cakin");

        TemplateEngine templateEngine = new TemplateEngine();
        final Context ctx = new Context(Locale.CHINESE);
        ctx.setVariable("name", "kk20");
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        ctx.setVariable("imageResourceName", "c:\\dd.png"); // so that we can reference it from HTML
        final String htmlContent = templateEngine.process("html/email-inlineimage.html", ctx);
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }

    public void sendTemplateMail(String filePath) throws Exception {
        //通常我们使用邮件发送服务的时候，都会有一些固定的场景，比如重置密码、注册确认等，我们会使用模板引擎来为各类邮件设置成模板，这样我们只需要在发送时去替换变化部分的参数即可。
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo("377148428@qq.com");
        helper.setSubject("主题：模板邮件，带附件");
        File file = new File(filePath);
        helper.addAttachment(file.getName(), file);

        // 采用Thymeleaf方式
//        TemplateEngine templateEngine = new TemplateEngine();
//        final Context ctx = new Context(Locale.CHINESE);
//        ctx.setVariable("name", "kk20");
//        ctx.setVariable("subscriptionDate", new Date());
//        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
//        ctx.setVariable("imageResourceName", "c:\\dd.png"); // so that we can reference it from HTML
//        String content = templateEngine.process("html/email-inlineimage.html", ctx);

        // 采用freemarker方式
        Map<String, Object> model = new HashedMap();
        model.put("name", "Roy");
        model.put("password", "123456");
        model.put("msg", "注册成功过");
        String content = getTemplate("test.ftl", model);
        helper.setText(content, true);

        mailSender.send(mimeMessage);
    }

    @Autowired
    private Configuration configuration;

    public String getTemplate(String template, Map map) {
//        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
//        String path = MailUtil.class.getResource("/").getPath();
//        try {
//            cfg.setDirectoryForTemplateLoading(new File(path+"templates/freemarker/"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        cfg.setDefaultEncoding("UTF-8");
//        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//        cfg.setLogTemplateExceptions(false);
//        cfg.setWrapUncheckedExceptions(true);

        try {
            Template temp = configuration.getTemplate(template);
            StringWriter stringWriter = new StringWriter();
            temp.process(map, stringWriter);
            return stringWriter.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return null;
    }
}
