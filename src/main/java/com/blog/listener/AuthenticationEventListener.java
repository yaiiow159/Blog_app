package com.blog.listener;

import com.blog.dto.LoginHistoryDto;
import com.blog.service.LoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginHistoryService loginHistoryService;
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info("username: " + username);
        // 紀錄當前使用者登入時間以及其他訊息
        LoginHistoryDto loginHistoryDto = new LoginHistoryDto();
        loginHistoryDto.setUsername(username);
        loginHistoryDto.setLoginTimestamp(LocalDateTime.now());
        loginHistoryDto.setAction("login");

        HttpServletRequest request = getCurrentRequest();
        loginHistoryDto.setUserAgent(Objects.requireNonNull(request).getHeader("User-Agent"));
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            loginHistoryDto.setIpAddress(ipAddress);
        }
        loginHistoryService.addLog(loginHistoryDto);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}
