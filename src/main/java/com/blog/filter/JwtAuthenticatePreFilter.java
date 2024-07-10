package com.blog.filter;

import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtBlackListService;
import com.blog.jwt.JwtUserDetailService;
import com.blog.utils.JwtTokenUtil;
import com.blog.utils.ThreadLocalUtil;
import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticatePreFilter extends OncePerRequestFilter {

    private final JwtUserDetailService jwtUserDetailService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtBlackListService jwtBlackListService;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final String token = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        // JWT Token 的形式為 Bearer token。 去掉 Bearer word 並得到 Token
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                username = jwtTokenUtil.getUsername(jwtToken);
                logger.info("username:" + username);
            } catch (IllegalArgumentException e) {
                logger.info("無法取得token");
            } catch (ExpiredJwtException e) {
                logger.info("JWT 令牌已經過期");
            } catch (ValidateFailedException e) {
                logger.info("JWT 令牌驗證失敗");
            }
        }
        // 驗證 token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = this.jwtUserDetailService.loadUserByUsername(username);
            log.info("userDetails:" + userDetails);
            // 如果 token 有效，則將 Spring Security 配置為手動設置驗證 將user信息紀錄到Spring Security Context中
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken))) {
                log.debug("jwtToken:" + jwtToken);
                // 驗證該筆token是否被加入進redis中的黑名單中
                if(jwtBlackListService.isJwtInBlackList(jwtToken)) {
                    throw new ValidateFailedException("JWT令牌已失效，請重新登入");
                }
                var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                redisTemplate.opsForValue().set(username, jwtToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
