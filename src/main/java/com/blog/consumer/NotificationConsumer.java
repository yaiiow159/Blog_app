package com.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.blog.dao.CommentPoRepository;
import com.blog.dao.MailNotificationPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dao.UserReportPoRepository;
import com.blog.dto.CommentDto;
import com.blog.dto.EmailNotification;
import com.blog.enumClass.CommentReport;
import com.blog.exception.ResourceNotFoundException;
import com.blog.po.CommentPo;
import com.blog.po.MailNotificationPo;
import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private static final String EMAIL_SENDER = "TimmyChung";

    private final JavaMailSender javaMailSender;
    private final MailNotificationPoRepository mailNotificationPoRepository;
    private final CommentPoRepository commentPoRepository;
    private final UserPoRepository userPoRepository;
    private final UserReportPoRepository userReportPoRepository;

    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer",
            topicPartitions = @TopicPartition(topic = "email-notification-topic", partitions = {"0"}))
    @Transactional
    public void mailNotificationConsumer_partition1(ConsumerRecord<String, String> record, Acknowledgment ack) {
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

    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer",
            topicPartitions = @TopicPartition(topic = "email-notification-topic", partitions = {"1"}))
    @Transactional
    public void mailNotificationConsumer_partition2(ConsumerRecord<String, String> record, Acknowledgment ack) {
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

    @KafkaListener(topics = "email-notification-topic", groupId = "email-notification-consumer",
            topicPartitions = @TopicPartition(topic = "email-notification-topic", partitions = {"2"}))
    @Transactional
    public void mailNotificationConsumer_partition3(ConsumerRecord<String, String> record, Acknowledgment ack) {
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

    @KafkaListener(topics = "review-notification-topic", groupId = "review-notification-consumer")
    @Transactional
    public void getReviewCommentNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received review comment notification: {}", record.value());
        CommentDto commentDto = JSON.parseObject(record.value(), CommentDto.class);
        try {
            UserPo userPo = userPoRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new UsernameNotFoundException("找不到使用者資料"));
            CommentPo commentPo = commentPoRepository.findById(commentDto.getId()).orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
            // 建立 通知訊息
            UserReportPo userReportPo = new UserReportPo();
            userReportPo.setUser(userPo);
            userReportPo.setComment(commentPo);
            userReportPo.setStatus(CommentReport.PENDING.getStatus());
            userReportPo.setReason(commentDto.getReason());
            userReportPo.setReportTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            userReportPoRepository.saveAndFlush(userReportPo);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("寄送手機通知訊息 失敗 原因: {}", e.getMessage());
            throw new RuntimeException("寄送手機通知訊息 失敗 原因: " + e.getMessage());
        }
    }
    @KafkaListener(topics = "phone-notification-topic", groupId = "phone-notification-consumer",
            topicPartitions = @TopicPartition(topic = "phone-notification-topic", partitions = {"0", "1", "2"}))
    @Transactional
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
