package com.blog.handler;

import com.blog.dto.LoginHistoryDto;
import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtBlackListService;
import com.blog.service.LoginHistoryService;
import com.blog.utils.CacheUtils;
import com.blog.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class LogoutHandler extends SecurityContextLogoutHandler {
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private JwtBlackListService jwtBlackListService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private LoginHistoryService loginHistoryService;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String token = request.getHeader("Authorization");
        final String jwtToken = token.substring(7);
        String username = jwtTokenUtil.getUsername(jwtToken);
        log.info("username: " + username);

        if(StringUtils.containsWhitespace(jwtToken)){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.JWT_AUTHENTICATION_ACCESS_ERROR);
        } else if (StringUtils.hasText(jwtToken)) {
            CacheUtils.remove(username);
            List<LoginHistoryDto> loginHistoryDos = loginHistoryService.findLoginHistoryByUsername(username);
            loginHistoryDos.get(0).setLogoutTimestamp(LocalDateTime.now());
            loginHistoryService.addLog(loginHistoryDos.get(0));
            jwtBlackListService.addJwtToBlackList(jwtToken);
            stringRedisTemplate.delete(username);
            log.info("logout success");
        }
    }
}
