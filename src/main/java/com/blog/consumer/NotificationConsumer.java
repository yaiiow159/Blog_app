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
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private static final String EMAIL_SENDER = "TimmyChung";

    private final JavaMailSender javaMailSender;
    private final MailNotificationPoRepository mailNotificationPoRepository;

    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer", topicPartitions = @TopicPartition(topic = "email-notification-topic", partitions = {"0"}))
    @RetryableTopic(dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,timeout = "50000")
    @Transactional
    public void mailNotificationConsumer_partition1(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("接收到 email-notification-topic 訊息: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><p>").append(emailNotification.getMessage()).append("</p>");
        sb.append("<br> 收件人為: ").append(emailNotification.getSendTo()).append("</body></html>");

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
            log.error("處理郵件通知 失敗 原因: {}", e.getMessage());
            throw new RuntimeException("處理郵件通知 失敗 原因: " + e.getMessage());
        }
    }


    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer", topicPartitions = @TopicPartition(topic = "email-notification-topic", partitions = {"1"}))
    @RetryableTopic(dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,timeout = "50000")
    @Transactional
    public void mailNotificationConsumer_partition2(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("接收到 email-notification-topic 訊息: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><p>").append(emailNotification.getMessage()).append("</p>");
        sb.append("<br> 收件人為: ").append(emailNotification.getSendTo()).append("</body></html>");

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
            log.error("處理郵件通知 失敗 原因: {}", e.getMessage());
            throw new RuntimeException("處理郵件通知 失敗 原因: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer",
            topicPartitions = @TopicPartition(topic = "email-notification-topic", partitions = {"2"}))
    @RetryableTopic(dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,timeout = "50000")
    @Transactional
    public void mailNotificationConsumer_partition3(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("接收到 email-notification-topic 訊息: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><p>").append(emailNotification.getMessage()).append("</p>");
        sb.append("<br> 收件人為: ").append(emailNotification.getSendTo()).append("</body></html>");

        try {
            helper.setFrom(EMAIL_SENDER);
            helper.setSubject(emailNotification.getSubject());
            helper.setText(sb.toString(), true);
            helper.setTo(emailNotification.getEmailAddress());
            javaMailSender.send(message);
            MailNotificationPo mailNotificationPo = getMailNotificationPo(emailNotification);
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("處理郵件通知 失敗 原因: {}", e.getMessage());
            throw new RuntimeException("處理郵件通知 失敗 原因: " + e.getMessage());
        }
    }

    private static MailNotificationPo getMailNotificationPo(EmailNotification emailNotification) {
        MailNotificationPo mailNotificationPo = new MailNotificationPo();
        mailNotificationPo.setEmail(emailNotification.getEmailAddress());
        mailNotificationPo.setSubject(emailNotification.getSubject());
        mailNotificationPo.setName(emailNotification.getSendTo());
        mailNotificationPo.setSendBy(emailNotification.getSendBy());
        mailNotificationPo.setAction(emailNotification.getOperation());
        mailNotificationPo.setContent(emailNotification.getMessage());
        mailNotificationPo.setSend(true);
        return mailNotificationPo;
    }
}
