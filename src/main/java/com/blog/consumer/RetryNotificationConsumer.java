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
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetryNotificationConsumer {
    private final MailNotificationPoRepository mailNotificationPoRepository;
    private final JavaMailSender javaMailSender;
    private final UserPoRepository userPoRepository;
    private final UserReportPoRepository userReportPoRepository;
    private final CommentPoRepository commentPoRepository;
    private static final String EMAIL_SENDER = "TimmyChung";

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "email-notification-topic-retry", id = "email-notification-consumer-retry")
    public void retryEmailNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received email notification: {}", record.value());

        EmailNotification emailNotification = JSON.parseObject(record.value(), EmailNotification.class);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><p>").append(emailNotification.getMessage()).append("</p>");
        sb.append("<br> 收件人為: ").append(emailNotification.getSendTo()).append("</body></html>");

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

            ack.acknowledge();
            log.info("存儲 mq 重試對列 郵件訊息 成功");
        } catch (Exception e) {
            log.error("存儲 mq 重試對列 郵件訊息 失敗 原因: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "review-notification-topic-retry", id = "review-notification-consumer-retry")
    @Transactional
    public void getReviewCommentNotification(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Received review comment notification: {}", record.value());
        CommentDto commentDto = JSON.parseObject(record.value(), CommentDto.class);
        try {
            UserPo userPo = userPoRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new UsernameNotFoundException("找不到使用者資料"));
            CommentPo commentPo = commentPoRepository.findById(commentDto.getId()).orElseThrow(() -> new EntityNotFoundException("找不到該留言"));
            // 建立 通知訊息
            UserReportPo userReportPo = new UserReportPo();
            userReportPo.setUser(userPo);
            userReportPo.setComment(commentPo);
            userReportPo.setStatus(CommentReportEnum.PENDING.getStatus());
            userReportPo.setReason(commentDto.getReason());
            userReportPo.setReportTime(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            userReportPoRepository.saveAndFlush(userReportPo);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("寄送手機通知訊息 失敗 原因: {}", e.getMessage());
            throw new ValidateFailedException("寄送手機通知訊息 失敗 原因: " + e.getMessage());
        }
    }

}
