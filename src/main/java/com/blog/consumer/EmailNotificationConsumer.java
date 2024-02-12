package com.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.blog.dao.MailNotificationPoRepository;
import com.blog.dto.EmailNotification;
import com.blog.po.MailNotificationPo;
import com.blog.service.MailNotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationConsumer {
    private static final String EMAIL_SENDER = "TimmyChung";
    private final JavaMailSender javaMailSender;
    private final MailNotificationPoRepository mailNotificationPoRepository;

    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer")
    public void getMailNotification(ConsumerRecord<String, String> record) {
        log.info("Received email notification: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<p style='color: #333; font-size: 18px; font-family: Arial, sans-serif;'>");
        sb.append("你的訂閱作者").append(emailNotification.getConsumer()).append(emailNotification.getAction()).append("：<strong>").append(emailNotification.getSubObject()).append("</strong>");
        sb.append("</p>");
        sb.append("<p style='color: #666; font-size: 14px; font-family: Arial, sans-serif;'>");
        sb.append("寄件內容：").append(emailNotification.getContent());
        sb.append("</p>");
        sb.append("<p style='color: #666; font-size: 14px; font-family: Arial, sans-serif;'>");
        sb.append("感謝你的訪問！");
        sb.append("</p>");
        sb.append("</body></html>");

        try {
            helper.setFrom(EMAIL_SENDER);
            helper.setSubject(emailNotification.getSubObject());
            helper.setText(sb.toString(), true);
            helper.setTo(emailNotification.getEmailAddress());
            javaMailSender.send(message);
            log.info("Sending email notification: {}", message);

            // 寄送郵件成功後 紀錄郵件訊息至資料庫
            MailNotificationPo mailNotificationPo = new MailNotificationPo();
            mailNotificationPo.setEmail(emailNotification.getEmailAddress());
            mailNotificationPo.setSubject(emailNotification.getSubObject());
            mailNotificationPo.setName(emailNotification.getConsumer());
            mailNotificationPo.setAction(emailNotification.getAction());
            mailNotificationPo.setContent(emailNotification.getContent());
            mailNotificationPo.setSendTime(LocalDateTime.now());
            mailNotificationPoRepository.save(mailNotificationPo);

        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }
    }

    // 寄送手機驗證碼通知訊息
    @KafkaListener(topics = "phone-notification-topic", groupId = "phone-notification-consumer")
    public void getPhoneNotification(ConsumerRecord<String, String> record) {
        log.info("Received phone notification: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<p style='color: #333; font-size: 18px; font-family: Arial, sans-serif;'>");
        sb.append("你的手機驗證碼為: <strong style='color: #f00; font-weight: bold'>").append(emailNotification.getContent()).append("</strong>");
        sb.append("</p>");
        sb.append("</body></html>");

        try {
            helper.setFrom(EMAIL_SENDER);
            helper.setSubject(emailNotification.getSubObject());
            helper.setText(sb.toString(), true);
            helper.setTo(emailNotification.getEmailAddress());
            javaMailSender.send(message);

            // 寄送郵件成功後 紀錄郵件訊息至資料庫
            MailNotificationPo mailNotificationPo = new MailNotificationPo();
            mailNotificationPo.setEmail(emailNotification.getEmailAddress());
            mailNotificationPo.setSubject(emailNotification.getSubObject());
            mailNotificationPo.setName(emailNotification.getConsumer());
            mailNotificationPo.setAction(emailNotification.getAction());
            mailNotificationPo.setContent(emailNotification.getContent());
            mailNotificationPo.setSendTime(LocalDateTime.now());
            mailNotificationPoRepository.save(mailNotificationPo);
            log.info("Sending email notification: {}", message);
        } catch (Exception e) {
            log.error("Failed to send phone notification: {}", e.getMessage());
        }
    }
}
