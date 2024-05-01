package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dao.MailNotificationPoRepository;
import com.blog.dto.MailNotificationDto;
import com.blog.mapper.MailNotificationPoMapper;
import com.blog.po.MailNotificationPo;
import com.blog.service.MailNotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailNotificationServiceImpl implements MailNotificationService {

    private final MailNotificationPoRepository mailNotificationPoRepository;
    @Override
    public void sendMailNotification(MailNotificationDto mailNotificationDto) {
        MailNotificationPo mailNotificationPo = MailNotificationPoMapper.INSTANCE.toPo(mailNotificationDto);
        mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
    }

    @Override
    public void sendMailNotification(List<MailNotificationDto> mailNotificationDtoList) {
        for (MailNotificationDto mailNotificationDto : mailNotificationDtoList) {
            MailNotificationPo mailNotificationPo = MailNotificationPoMapper.INSTANCE.toPo(mailNotificationDto);
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
        }
    }

    @Override
    public Page<MailNotificationDto> getAllMailNotification(String username,String name, String subject, String email, boolean isRead, int page, int size) {
        Specification<MailNotificationPo> specification = (root, query, criteriaBuilder) -> {
            if (username != null) {
                query.where(criteriaBuilder.equal(root.get("username"), username));
            }
            if (name != null) {
                query.where(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (subject != null) {
                query.where(criteriaBuilder.like(root.get("subject"), "%" + subject + "%"));
            }
            if (email != null) {
                query.where(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }
            if (isRead) {
                query.where(criteriaBuilder.equal(root.get("isRead"), isRead));
            }
            return query.getRestriction();
        };
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MailNotificationPo> mailNotificationPos = mailNotificationPoRepository.findAll(specification, pageRequest);
        return new PageImpl<>(MailNotificationPoMapper.INSTANCE.toDtoList(mailNotificationPos.getContent()), pageRequest, mailNotificationPos.getTotalElements());
    }

    @Override
    public String updateMailNotification(MailNotificationDto mailNotificationDto) {
        MailNotificationPo mailNotificationPo = mailNotificationPoRepository.findById(mailNotificationDto.getId()).orElseThrow(() -> new ResourceNotFoundException("找不到該筆資料"));
        mailNotificationPo.setRead(mailNotificationDto.getIsRead());
        mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
        return "更新狀態成功";
    }

    @Override
    public Long getMailNotificationCount() {
        return mailNotificationPoRepository.countByIsReadFalse();
    }
}
