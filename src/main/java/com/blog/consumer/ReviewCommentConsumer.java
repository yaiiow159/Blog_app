package com.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.blog.dao.CommentPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dao.UserReportPoRepository;
import com.blog.dto.CommentDto;
import com.blog.enumClass.CommentReportEnum;
import com.blog.exception.ValidateFailedException;
import com.blog.po.CommentPo;
import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCommentConsumer {

    private final UserPoRepository userPoRepository;
    private final UserReportPoRepository userReportPoRepository;
    private final CommentPoRepository commentPoRepository;

    @KafkaListener(topics = "review-notification-topic", groupId = "review-notification-consumer",
            topicPartitions = @TopicPartition(topic = "review-notification-topic", partitions = {"0"}))
    @RetryableTopic(dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR)
    @Transactional
    public void getReviewCommentPartition0(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("接收到 review-notification-topic 訊息: {}", record.value());
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
            log.error("處理審核通知失敗 原因: {}", e.getMessage());
            throw new RuntimeException("送審評論失敗 失敗 原因: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "review-notification-topic", groupId = "review-notification-consumer",
            topicPartitions = @TopicPartition(topic = "review-notification-topic", partitions = {"1"}))
    @RetryableTopic(dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR)
    @Transactional
    public void getReviewCommentPartition1(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("接收到 review-notification-topic 訊息: {}", record.value());
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
            log.error("處理審核通知失敗 原因: {}", e.getMessage());
            throw new RuntimeException("送審評論失敗 失敗 原因: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "review-notification-topic", groupId = "review-notification-consumer",
            topicPartitions = @TopicPartition(topic = "review-notification-topic", partitions = {"2"}))
    @RetryableTopic(dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR)
    @Transactional
    public void getReviewCommentPartition2(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("接收到 review-notification-topic 訊息: {}", record.value());
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
            log.error("處理審核通知失敗 原因: {}", e.getMessage());
            throw new ValidateFailedException("送審評論失敗 原因: " + e.getMessage());
        }
    }
}
