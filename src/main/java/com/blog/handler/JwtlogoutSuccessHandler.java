package com.blog.handler;

import com.blog.exception.ValidateFailedException;
import com.blog.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtlogoutSuccessHandler implements LogoutSuccessHandler {
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final String token = request.getHeader("Authorization");
        String username = jwtTokenUtil.getUsername(token);
        log.info("username: " + username);

        if(StringUtils.containsWhitespace(token)){
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.JWT_AUTHENTICATION_ACCESS_ERROR);
        } else if (StringUtils.hasText(token)) {
            if(Boolean.TRUE.equals(jwtTokenUtil.validateToken(token))){
                // 從redis中清除該筆token
                stringRedisTemplate.opsForSet().remove(username, token);
                // 清除spring security context 上下文
                SecurityContextHolder.clearContext();
            }
        }
        log.info("logout success");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString("logout success"));
    }
}
