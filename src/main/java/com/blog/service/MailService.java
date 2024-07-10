package com.blog.service;

import com.blog.dto.EmailNotification;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    CompletableFuture<Void> sendMailAsync(EmailNotification emailNotification);
}
