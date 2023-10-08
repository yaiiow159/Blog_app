package com.blog.filter;

import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtBlackListService;
import com.blog.jwt.JwtUserDetailService;
import com.blog.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class JwtAuthenticatePreFilter extends OncePerRequestFilter {

    private JwtUserDetailService jwtUserDetailService;
    private JwtTokenUtil jwtTokenUtil;

    private JwtBlackListService jwtBlackListService;

    private StringRedisTemplate redisTemplate;

    public JwtAuthenticatePreFilter(JwtBlackListService jwtBlackListService,StringRedisTemplate redisTemplate) {
        this.jwtBlackListService = jwtBlackListService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        // JWT Token 的形式為 Bearer token。 去掉 Bearer word 並得到 Token
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                username = jwtTokenUtil.getUsername(jwtToken);
                log.debug("username:" + username);
            } catch (IllegalArgumentException e) {
                logger.info("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.info("JWT Token has expired");
            } catch (ValidateFailedException e) {
                logger.info("JWT Token has expired, please login");
            }
        }
        // 驗證 token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = this.jwtUserDetailService.loadUserByUsername(username);
            System.out.println("userDetail:" + userDetails.getUsername());
            // 如果 token 有效，則將 Spring Security 配置為手動設置驗證 將user信息紀錄到Spring Security Context中
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken))) {
                log.debug("jwtToken:" + jwtToken);
                // 驗證該筆token是否被加入進redis中的黑名單中
                if(jwtBlackListService.isJwtInBlackList(jwtToken)){
                    throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.JWT_AUTHENTICATION_TOKEN_EXPIRED);
                }
                // 生成新的usernamePasswordAuthenticationToken
                var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 將請求驗證的資訊放進WebAuthenticationDetailsSource 如id地址 設備資訊等
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 將usernamePasswordAuthenticationToken放進SecurityContextHolder的上下文中 以便後續做授權以及認證判斷用
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                // 將token存入redis中保存
                redisTemplate.opsForValue().set(username, jwtToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
