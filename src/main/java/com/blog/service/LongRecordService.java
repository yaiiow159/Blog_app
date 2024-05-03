package com.blog.service;

import com.blog.dto.LoginHistoryDto;
import org.springframework.data.domain.Page;

public interface LongRecordService {
    Page<LoginHistoryDto> getLoginRecords(String username, String ipAddress, String action, Integer page, Integer pageSize);

    LoginHistoryDto getLoginRecord(Long id);
}
