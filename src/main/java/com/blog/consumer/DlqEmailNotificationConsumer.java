package com.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.blog.dao.MailNotificationPoRepository;
import com.blog.dto.EmailNotification;
import com.blog.po.MailNotificationPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class DlqEmailNotificationConsumer {
    private final MailNotificationPoRepository mailNotificationPoRepository;

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "email-notification-topic-dlq", id = "email-notification-consumer-dlq")
    public void dlqEmailNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received email notification: {}", record.value());
        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);
        // 將 emailNotification 寫入資料庫
        try {
            MailNotificationPo mailNotificationPo = new MailNotificationPo();
            mailNotificationPo.setEmail(emailNotification.getEmailAddress());
            mailNotificationPo.setSubject(emailNotification.getSubject());
            mailNotificationPo.setName(emailNotification.getSendTo());
            mailNotificationPo.setSendBy(emailNotification.getSendBy());
            mailNotificationPo.setSendTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            mailNotificationPo.setSend(false);
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
        } catch (Exception e) {
            log.error("存儲 mq 死信對列 郵件訊息 失敗 原因: {}", e.getMessage());
        }
    }
}
