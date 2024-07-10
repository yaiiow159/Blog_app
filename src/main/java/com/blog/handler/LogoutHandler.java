package com.blog.handler;

import com.blog.dto.LoginHistoryDto;
import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtBlackListService;
import com.blog.service.LoginHistoryService;
import com.blog.utils.CacheUtil;
import com.blog.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogoutHandler extends SecurityContextLogoutHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtBlackListService jwtBlackListService;
    private final StringRedisTemplate stringRedisTemplate;
    private final LoginHistoryService loginHistoryService;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new ValidateFailedException("token缺失 或是 格式錯誤");
        }

        final String jwtToken = token.substring(7);
        if (StringUtils.containsWhitespace(jwtToken)) {
            throw new ValidateFailedException("token 不應該有空格");
        }

        String username = jwtTokenUtil.getUsername(jwtToken);
        log.info("登出使用者為:  " + username);

        if (StringUtils.hasText(jwtToken)) {
            try {
                // 清除缓存
                CacheUtil.remove(username);

                // 獲取最後一次登錄紀錄 並更新登出時間
                LoginHistoryDto loginHistoryDo = loginHistoryService.findLastLoginHistoryByUsername(username);
                loginHistoryDo.setAction("logout");
                loginHistoryDo.setLogoutTimestamp(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
                loginHistoryService.addLog(loginHistoryDo);

                // 将JWT添加到黑名单
                jwtBlackListService.addJwtToBlackList(jwtToken);

                // 删除Redis中的缓存
                stringRedisTemplate.delete(username);

                log.info("User " + username + "登出成功 登出時間點為:" + LocalDateTime.now(ZoneId.of("Asia/Taipei")));
            } catch (Exception e) {
                log.error("Error during logout process for user: " + username, e);
                throw new ValidateFailedException("再登出流程中出現異常 " + e.getMessage());
            }
        }
    }
}
