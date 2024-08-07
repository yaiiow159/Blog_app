package com.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.blog.dao.CommentPoRepository;
import com.blog.dao.MailNotificationPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dao.UserReportPoRepository;
import com.blog.dto.CommentDto;
import com.blog.dto.EmailNotification;
import com.blog.enumClass.CommentReportEnum;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.po.CommentPo;
import com.blog.po.MailNotificationPo;
import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class DlqEmailNotificationConsumer {

    private final MailNotificationPoRepository mailNotificationPoRepository;
    private final UserPoRepository userPoRepository;
    private final UserReportPoRepository userReportPoRepository;
    private final CommentPoRepository commentPoRepository;

    @Transactional
    @KafkaListener(topics = "email-notification-topic-dlq", id = "email-notification-consumer-dlq")
    @RetryableTopic(timeout = "10000")
    public void dlqEmailNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("死信對列郵寄通知 訊息內容: {}", record.value());
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

            ack.acknowledge();
            log.info("存儲 mq 死信對列 郵件訊息 成功");
        } catch (Exception e) {
            log.error("存儲 mq 死信對列 郵件訊息 失敗 原因: {}", e.getMessage());
            throw new ValidateFailedException("存儲 mq 死信對列 郵件訊息 失敗 原因: " + e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = "review-notification-topic-dlq", id = "review-notification-consumer-dlq")
    @RetryableTopic(timeout = "10000")
    public void getReviewCommentNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("留言審核死信對列 訊息內容: {}", record.value());
        CommentDto commentDto = JSON.parseObject(record.value(), CommentDto.class);
        try {
            UserPo userPo = userPoRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new UsernameNotFoundException("找不到使用者資料"));
            CommentPo commentPo = commentPoRepository.findById(commentDto.getId()).orElseThrow(() -> new EntityNotFoundException("找不到該留言"));
            // 建立 通知訊息
            UserReportPo userReportPo = new UserReportPo();
            userReportPo.setUser(userPo);
            userReportPo.setComment(commentPo);
            userReportPo.setStatus(CommentReportEnum.REJECT.getStatus());
            userReportPo.setReason(commentDto.getReason());
            userReportPo.setReportTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            userReportPoRepository.saveAndFlush(userReportPo);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("存儲 mq 死信對列 郵件訊息 失敗 原因: {}", e.getMessage());
            throw new ValidateFailedException("存儲 mq 死信對列 郵件訊息 失敗 原因: " + e.getMessage());
        }
    }
}
