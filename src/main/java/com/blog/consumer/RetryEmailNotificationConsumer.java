package com.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.blog.dao.MailNotificationPoRepository;
import com.blog.dto.EmailNotification;
import com.blog.po.MailNotificationPo;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetryEmailNotificationConsumer {
    private final MailNotificationPoRepository mailNotificationPoRepository;
    private final JavaMailSender javaMailSender;
    private static final String EMAIL_SENDER = "TimmyChung";

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "email-notification-topic-retry", id = "email-notification-consumer-retry")
    public void retryEmailNotification(ConsumerRecord<String, String> record) {
        log.info("Received email notification: {}", record.value());

        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<p style='color: #333; font-size: 18px; font-family: Arial, sans-serif;'>");
        sb.append("你的訂閱作者").append(emailNotification.getSendTo()).append(emailNotification.getOperation()).append("：<strong>").append(emailNotification.getSubject()).append("</strong>");
        sb.append("</p>");
        sb.append("<p style='color: #666; font-size: 14px; font-family: Arial, sans-serif;'>");
        sb.append("寄件內容：").append(emailNotification.getMessage());
        sb.append("</p>");
        sb.append("<p style='color: #666; font-size: 14px; font-family: Arial, sans-serif;'>");
        sb.append("感謝你的訪問！");
        sb.append("</p>");
        sb.append("</body></html>");
        // 將 emailNotification 寫入資料庫
        try {
            MailNotificationPo mailNotificationPo = new MailNotificationPo();
            mailNotificationPo.setEmail(emailNotification.getEmailAddress());
            mailNotificationPo.setSubject(emailNotification.getSubject());
            mailNotificationPo.setName(emailNotification.getSendTo());
            mailNotificationPo.setSendBy(emailNotification.getSendBy());
            mailNotificationPo.setSendTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            mailNotificationPo.setSend(true);
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);

            helper.setFrom(EMAIL_SENDER);
            helper.setSubject(emailNotification.getSubject());
            helper.setText(sb.toString(), true);
            helper.setTo(emailNotification.getEmailAddress());
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("存儲 mq 重試對列 郵件訊息 失敗 原因: {}", e.getMessage());
        }
    }
}
