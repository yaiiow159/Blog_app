package com.blog.service.impl;

import com.blog.dao.UserPoRepository;
import com.blog.po.UserPo;
import com.blog.service.AuthService;
import com.blog.utils.UUIDUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserPoRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JavaMailSender javaMailSender;

    @Override
    public void resetPassword(String token, String newPassword) {
        // 比對存放在redis中的token
        String email = stringRedisTemplate.opsForValue().get(token);
        UserPo userPo = userJpaRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("找不到使用者 " + email));
        userPo.setPassword(passwordEncoder.encode(newPassword));
        userJpaRepository.saveAndFlush(userPo);
        // 更新spring-security的數據
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPo.getUserName());
        // 生成新的usernamePasswordAuthenticationToken
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Override
    public void forgotPassword(String email) throws MessagingException {
        UserPo userPo = userJpaRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("找不到該使用者對應的電子郵件" + email));

        String token = UUIDUtil.getUUID32();
        stringRedisTemplate.opsForValue().set(token, email, 10, TimeUnit.MINUTES);

        String url = "http://localhost:3030/api/v1/auth/reset?token=" + token;
        String content = "請點擊以下連結重置密碼: " + url;

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage());
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("重置密碼");
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(mimeMessageHelper.getMimeMessage());
    }
}
