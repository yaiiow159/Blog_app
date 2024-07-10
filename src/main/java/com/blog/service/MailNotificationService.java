package com.blog.service;

import com.blog.dto.MailNotificationDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MailNotificationService {

    void sendMailNotification(MailNotificationDto mailNotificationDto) throws Exception;

    void sendMailNotification(List<MailNotificationDto> mailNotificationDtoList) throws Exception;

    void updateMailNotification(MailNotificationDto mailNotificationDto) throws Exception;

    Page<MailNotificationDto> getAllMailNotification(String username,String name, String subject, String email, boolean isRead, int page, int size);

    Long getMailNotificationCount();

    MailNotificationDto queryNotification(Long id) throws EntityNotFoundException;
}
