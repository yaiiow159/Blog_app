package com.blog.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class KafkaSendReqHandler implements ProducerListener {
    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        log.info("recordMetadata 寄送成功 內容: {}", recordMetadata.toString());
    }
    @Override
    public void onError(ProducerRecord producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.error("生產者 寄送消息 : {} 出現異常 ,寄送失敗錯誤原因: {}", Objects.requireNonNull(recordMetadata) ,exception.getMessage());
    }
}
