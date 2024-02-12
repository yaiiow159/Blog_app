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
        mailNotificationPoRepository.save(mailNotificationPo);
    }

    @Override
    public void sendMailNotification(List<MailNotificationDto> mailNotificationDtoList) {
        for (MailNotificationDto mailNotificationDto : mailNotificationDtoList) {
            MailNotificationPo mailNotificationPo = MailNotificationPoMapper.INSTANCE.toPo(mailNotificationDto);
            mailNotificationPoRepository.save(mailNotificationPo);
        }
    }

    @Override
    public Page<MailNotificationDto> getAllMailNotification(String name, String subject, String email, boolean isRead, int page, int size, String sort, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sort);
        List<MailNotificationPo> mailNotificationPoList = mailNotificationPoRepository.findByNameOrContentOrEmailOrSubjectAndIsRead(name, email, subject, isRead);
        // 轉換成Page物件
        List<MailNotificationDto> mailNotificationDtoList = MailNotificationPoMapper.INSTANCE.toDtoList(mailNotificationPoList);
        return new PageImpl<>(mailNotificationDtoList, pageRequest, mailNotificationPoList.size());
    }

    @Override
    public String updateMailNotification(MailNotificationDto mailNotificationDto) {
        JSONObject jsonObject = new JSONObject();
        MailNotificationPo mailNotificationPo = mailNotificationPoRepository.findById(mailNotificationDto.getId()).orElseThrow(() -> new ResourceNotFoundException("找不到該筆資料"));
        mailNotificationPo.setRead(mailNotificationDto.getIsRead());
        mailNotificationPoRepository.save(mailNotificationPo);
        jsonObject.put("message", "更新成功");
        return jsonObject.toJSONString();
    }

    @Override
    public Long getMailNotificationCount() {
        return mailNotificationPoRepository.countByIsReadFalse();
    }
}
