package com.blog.service;

import com.blog.dto.LoginHistoryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryService {
    void addLog(LoginHistoryDto loginHistoryDto);
    List<LoginHistoryDto> findLoginHistoryByUsername(String username);
    void deleteLoginHistoryByUsername(String username);

    void deleteLogBefore(LocalDateTime localDateTime);

    LoginHistoryDto findLastLoginHistoryByUsername(String username);
}
