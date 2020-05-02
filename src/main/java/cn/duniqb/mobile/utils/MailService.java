package cn.duniqb.mobile.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @author duniqb <duniqb@qq.com>
 * @version V1.0.0
 * @date 2020/4/23 11:55
 * @since 1.0
 */
@Slf4j
@Component
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

//    public void send(String to, String subject, Bomb bomb) {
//        Context context = new Context();
//        context.setVariable("bomb", bomb);
//        String emailContent = templateEngine.process("mail", context);
//
//        MimeMessage message = javaMailSender.createMimeMessage();
//
//        MimeMessageHelper helper;
//        try {
//            helper = new MimeMessageHelper(message, true);
//            helper.setFrom(from);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(emailContent, true);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        javaMailSender.send(message);
//    }

}
