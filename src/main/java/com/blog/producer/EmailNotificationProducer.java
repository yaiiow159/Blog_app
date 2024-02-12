package com.blog.producer;

import com.alibaba.fastjson2.JSON;
import com.blog.dto.EmailNotification;
import com.blog.handler.KafkaSendReqHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaSendReqHandler kafkaSendReqHandler;

    public void sendMailNotification(EmailNotification emailNotification) {
        kafkaTemplate.setProducerListener(kafkaSendReqHandler);
        log.info("Sending email notification: {}", emailNotification.toString());
        kafkaTemplate.send("email-notification-topic", JSON.toJSONString(emailNotification));
    }

    // 寄送手機驗證碼通知訊息
    public void sendPhoneNotification(EmailNotification emailNotification) {
        kafkaTemplate.setProducerListener(kafkaSendReqHandler);
        log.info("Sending phone notification: {}", emailNotification.toString());
        kafkaTemplate.send("phone-notification-topic", JSON.toJSONString(emailNotification));
    }
}

