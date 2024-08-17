package com.blog.producer;

import com.alibaba.fastjson2.JSON;
import com.blog.dto.CommentDto;
import com.blog.dto.EmailNotification;
import com.blog.handler.KafkaSendReqHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaSendReqHandler kafkaSendReqHandler;
    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);

    public void sendMailNotification(EmailNotification emailNotification) {
        kafkaTemplate.setProducerListener(kafkaSendReqHandler);
        logger.info("發送 郵件通知: {}", emailNotification.toString());
        kafkaTemplate.send("email-notification-topic", JSON.toJSONString(emailNotification));
    }

    public void sendReviewNotification(CommentDto commentDto) {
        kafkaTemplate.setProducerListener(kafkaSendReqHandler);
        logger.info("發送評論審核通知: {}" , commentDto.toString());
        kafkaTemplate.send("review-notification-topic", JSON.toJSONString(commentDto));
    }
}

