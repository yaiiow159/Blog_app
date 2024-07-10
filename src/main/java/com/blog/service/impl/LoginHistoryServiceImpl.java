package com.blog.service.impl;

import com.blog.dao.LoginHistoryPoRepository;
import com.blog.dto.LoginHistoryDto;
import com.blog.mapper.LoginHistoryPoMapper;
import com.blog.po.LoginHistoryPo;
import com.blog.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryPoRepository loginHistoryPoRepository;
    @Override
    public void addLog(LoginHistoryDto loginHistoryDto) {
        LoginHistoryPo loginHistoryPo = LoginHistoryPoMapper.INSTANCE.toPo(loginHistoryDto);
        loginHistoryPoRepository.saveAndFlush(loginHistoryPo);
    }

    @Override
    public void deleteLogBefore(LocalDateTime localDateTime) {
        loginHistoryPoRepository.deleteByLoginTimestampBefore(localDateTime);
    }

    @Override
    public LoginHistoryDto findLastLoginHistoryByUsername(String username) {
        Optional<LoginHistoryPo> loginHistoryPo = loginHistoryPoRepository.findFirstByUsernameOrderByLoginTimestampDesc(username);
        return loginHistoryPo.map(LoginHistoryPoMapper.INSTANCE::toDto).orElse(null);
    }
}
