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
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationConsumer {
    private static final String EMAIL_SENDER = "TimmyChung";
    private final JavaMailSender javaMailSender;
    private final MailNotificationPoRepository mailNotificationPoRepository;

    @KafkaListener(topics = "email-notification-topic", id = "email-notification-consumer")
    @RetryableTopic(attempts = "5", backoff = @Backoff(multiplier = 2, maxDelay = 3000L),
            include = SocketTimeoutException.class,dltTopicSuffix = "dlt",retryTopicSuffix = "retry")
    @Transactional(rollbackFor = Exception.class)
    public void getMailNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
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

        try {
            helper.setFrom(EMAIL_SENDER);
            helper.setSubject(emailNotification.getSubject());
            helper.setText(sb.toString(), true);
            helper.setTo(emailNotification.getEmailAddress());
            javaMailSender.send(message);
            // 寄送郵件成功後 紀錄郵件訊息至資料庫
            MailNotificationPo mailNotificationPo = new MailNotificationPo();
            mailNotificationPo.setEmail(emailNotification.getEmailAddress());
            mailNotificationPo.setSubject(emailNotification.getSubject());
            mailNotificationPo.setName(emailNotification.getSendTo());
            mailNotificationPo.setSendBy(emailNotification.getSendBy());
            mailNotificationPo.setAction(emailNotification.getOperation());
            mailNotificationPo.setContent(emailNotification.getMessage());
            mailNotificationPo.setSendTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            mailNotificationPo.setSend(true);
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("寄送郵件通知訊息 失敗 原因: {}", e.getMessage());
            throw new RuntimeException("寄送郵件通知訊息 失敗 原因: " + e.getMessage());
        }
    }

    // 寄送手機驗證碼通知訊息
    @KafkaListener(topics = "phone-notification-topic", id = "phone-notification-consumer")
    @RetryableTopic(attempts = "5", backoff = @Backoff(multiplier = 2, maxDelay = 3000L),
            include = SocketTimeoutException.class, dltTopicSuffix = "dlt",retryTopicSuffix = "retry")
    @Transactional(rollbackFor = Exception.class)
    public void getPhoneNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received phone notification: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<p style='color: #333; font-size: 18px; font-family: Arial, sans-serif;'>");
        sb.append("你的手機驗證碼為: <strong style='color: #f00; font-weight: bold'>").append(emailNotification.getMessage()).append("</strong>");
        sb.append("</p>");
        sb.append("</body></html>");

        try {
            helper.setFrom(EMAIL_SENDER);
            helper.setSubject(emailNotification.getSubject());
            helper.setText(sb.toString(), true);
            helper.setTo(emailNotification.getEmailAddress());
            javaMailSender.send(message);

            MailNotificationPo mailNotificationPo = new MailNotificationPo();
            mailNotificationPo.setEmail(emailNotification.getEmailAddress());
            mailNotificationPo.setSubject(emailNotification.getSubject());
            mailNotificationPo.setName(emailNotification.getSendTo());
            mailNotificationPo.setAction(emailNotification.getOperation());
            mailNotificationPo.setContent(emailNotification.getMessage());
            mailNotificationPo.setSendTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("寄送手機驗證碼通知訊息 失敗 原因: {}", e.getMessage());
            throw new RuntimeException("寄送手機通知訊息 失敗 原因: " + e.getMessage());
        }
    }
}
