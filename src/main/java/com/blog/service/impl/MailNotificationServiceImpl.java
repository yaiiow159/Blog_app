package com.blog.service.impl;

import com.blog.dao.MailNotificationPoRepository;
import com.blog.dto.MailNotificationDto;
import com.blog.mapper.MailNotificationPoMapper;
import com.blog.po.MailNotificationPo;
import com.blog.service.MailNotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailNotificationServiceImpl implements MailNotificationService {

    private final MailNotificationPoRepository mailNotificationPoRepository;
    private static final Logger logger = LoggerFactory.getLogger(MailNotificationServiceImpl.class);
    @Override
    public void sendMailNotification(MailNotificationDto mailNotificationDto) {
        if(mailNotificationDto == null){
            throw new IllegalArgumentException("參數為空");
        }
        logger.debug("新增 郵件通知 {}", mailNotificationDto);
        MailNotificationPo mailNotificationPo = MailNotificationPoMapper.INSTANCE.toPo(mailNotificationDto);
        mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
    }

    @Override
    public void sendMailNotification(List<MailNotificationDto> mailNotificationDtoList){
        if(mailNotificationDtoList == null){
            throw new IllegalArgumentException("參數為空");
        }
        for (MailNotificationDto mailNotificationDto : mailNotificationDtoList) {
            logger.debug("新增 郵件通知 {}", mailNotificationDto);
            MailNotificationPo mailNotificationPo = MailNotificationPoMapper.INSTANCE.toPo(mailNotificationDto);
            mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
        }
    }

    @Override
    public Page<MailNotificationDto> getAllMailNotification(String username,String name, String subject, String email, boolean isRead, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<MailNotificationPo> specification = (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(username)) {
                query.where(criteriaBuilder.equal(root.get("username"), username));
            }
            if (StringUtils.hasText(name)) {
                query.where(criteriaBuilder.equal(root.get("name"), name));
            }
            if (StringUtils.hasText(subject)) {
                query.where(criteriaBuilder.equal(root.get("subject"), subject));
            }
            if (StringUtils.hasText(email)) {
                query.where(criteriaBuilder.equal(root.get("email"), email));
            }
            if (isRead) {
                query.where(criteriaBuilder.equal(root.get("isRead"), true));
            }
            return query.getRestriction();
        };

        Page<MailNotificationPo> mailNotificationPoPage = mailNotificationPoRepository.findAll(specification, pageable);
        return mailNotificationPoPage.map(MailNotificationPoMapper.INSTANCE::toDto);
    }

    @Override
    public void updateMailNotification(MailNotificationDto mailNotificationDto) throws EntityNotFoundException {
        MailNotificationPo mailNotificationPo = mailNotificationPoRepository.findById(mailNotificationDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該筆資料"));
        mailNotificationPo.setRead(mailNotificationDto.getIsRead());
        mailNotificationPoRepository.saveAndFlush(mailNotificationPo);
    }

    @Override
    public Long getMailNotificationCount() {
        return mailNotificationPoRepository.countByIsReadFalse();
    }

    @Override
    public MailNotificationDto queryNotification(Long id) throws EntityNotFoundException{
        return MailNotificationPoMapper.INSTANCE.toDto(mailNotificationPoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到該筆資料")));
    }
}
