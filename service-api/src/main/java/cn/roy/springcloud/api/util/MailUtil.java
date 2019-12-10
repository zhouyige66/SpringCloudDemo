package cn.roy.springcloud.api.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;
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

    // 解决中文乱码问题
    static{
        System.setProperty("mail.mime.splitlongparameters", "false");
    }

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Configuration configuration;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    /**
     * 发送简单的纯文本邮件
     *
     * @param to
     * @param ccTo
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String[] to, String[] ccTo, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setCc(ccTo);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    /**
     * 发送带附件的邮件
     *
     * @param to
     * @param ccTo
     * @param subject
     * @param content
     * @param filePaths
     */
    public void sendAttachmentsMail(String[] to, String[] ccTo, String subject, String content, List<String> filePaths) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setCc(ccTo);
            helper.setSubject(subject);
            helper.setText(content);

            for (String filePath : filePaths) {
                File file = new File(filePath);
                helper.addAttachment(file.getName(), file);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送富文本邮件（contentIds的大小与filePaths的大小要相等，且一一对应）
     *
     * @param to
     * @param ccTo
     * @param subject
     * @param content    富文本的邮件，比如<html><body><img src="cid:photo" ></body></html>
     * @param contentIds
     * @param filePaths
     */
    public void sendInlineMail(String[] to, String[] ccTo, String subject, String content, String[] contentIds,
                               String[] filePaths) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setCc(ccTo);
            helper.setSubject(subject);
            helper.setText(content, true);
            int size = contentIds.length;
            for (int i = 0; i < size; i++) {
                // 假如helper.setText("<html><body><img src=\"cid:photo\" ></body></html>", true);
                // 这里需要注意的是addInline函数中资源名称photo需要与正文中cid:photo对应起来
                // helper.addInline("photo", new File("photo.png"));
                helper.addInline(contentIds[i], new File(filePaths[i]));
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送模板邮件，需要调用者自己完成模板值匹配
     *
     * @param to
     * @param ccTo
     * @param subject
     * @param content
     * @param filePaths
     */
    public void sendTemplateMail(String[] to, String[] ccTo, String subject, String content, List<String> filePaths) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setCc(ccTo);
            helper.setSubject(subject);
            helper.setText(content, true);
            for (String filePath : filePaths) {
                File file = new File(filePath);
                helper.addAttachment("中文附件-"+file.getName(), file);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向Thymeleaf模板中填入数据
     *
     * @param templateName
     * @param map
     * @return
     */
    public String processThymeleafTemplateIntoString(String templateName, Map<String, Object> map) {
        final Context ctx = new Context(Locale.CHINESE);
        for (String key : map.keySet()) {
            ctx.setVariable(key, map.get(key));
        }
        final String htmlContent = springTemplateEngine.process(templateName, ctx);
        return htmlContent;
    }

    /**
     * 向Freemarker模板中填入数据
     *
     * @param templateName
     * @param map
     * @return
     */
    public String processFreemarkerTemplateIntoString(String templateName, Map map) {
        try {
            Template template = configuration.getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return null;
    }
}
