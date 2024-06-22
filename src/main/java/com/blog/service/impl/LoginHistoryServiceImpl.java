package com.blog.service.impl;

import com.blog.dao.LoginHistoryPoRepository;
import com.blog.mapper.LoginHistoryPoMapper;
import com.blog.dto.LoginHistoryDto;
import com.blog.po.LoginHistoryPo;
import com.blog.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryPoRepository loginHistoryPoRepository;
    @Override
    public void addLog(LoginHistoryDto loginHistoryDto) {
        LoginHistoryPo loginHistoryPo = LoginHistoryPoMapper.INSTANCE.toPo(loginHistoryDto);
        loginHistoryPoRepository.saveAndFlush(loginHistoryPo);
    }

    @Override
    public List<LoginHistoryDto> findLoginHistoryByUsername(String username) {
        return loginHistoryPoRepository.findByUsername(username)
                .stream().map(LoginHistoryPoMapper.INSTANCE::toDto).toList();
    }

    @Override
    public void deleteLoginHistoryByUsername(String username) {
        loginHistoryPoRepository.deleteByUsername(username);
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
