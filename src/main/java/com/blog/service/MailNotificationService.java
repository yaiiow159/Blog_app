package com.blog.service;

import com.blog.dto.MailNotificationDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MailNotificationService {

    void sendMailNotification(MailNotificationDto mailNotificationDto);

    void sendMailNotification(List<MailNotificationDto> mailNotificationDtoList);

    Page<MailNotificationDto> getAllMailNotification(String username,String name, String subject, String email, boolean isRead, int page, int size);

    String updateMailNotification(MailNotificationDto mailNotificationDto);

    Long getMailNotificationCount();

    MailNotificationDto getMailNotification(Long id);
}
