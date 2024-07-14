package com.blog.service.impl;

import com.blog.dto.EmailNotification;
import com.blog.producer.NotificationProducer;
import com.blog.service.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final NotificationProducer emailNotificationProducer;

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    /**
     * 寄送郵件 (交由 異步執行 )
     *
     * @param emailNotification 郵件通知資訊
     * @return void
     */
    @Override
    @Async("mailThreadPoolExecutor")
    public CompletableFuture<Void> sendMailAsync(EmailNotification emailNotification) {
        logger.debug("發送 郵件通知: {}", emailNotification.toString());
        emailNotificationProducer.sendMailNotification(emailNotification);
        return CompletableFuture.completedFuture(null);
    }
}
