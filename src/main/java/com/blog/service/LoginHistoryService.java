package com.blog.service;

import com.blog.dto.LoginHistoryDto;

import java.time.LocalDateTime;

public interface LoginHistoryService {
    void addLog(LoginHistoryDto loginHistoryDto);

    void deleteLogBefore(LocalDateTime localDateTime);

    LoginHistoryDto findLastLoginHistoryByUsername(String username);
}
