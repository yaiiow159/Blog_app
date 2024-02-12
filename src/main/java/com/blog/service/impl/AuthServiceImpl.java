package com.blog.service.impl;

import com.blog.dao.UserJpaRepository;
import com.blog.po.UserPo;
import com.blog.service.AuthService;
import com.blog.utils.UUIDUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JavaMailSender javaMailSender;

    @Override
    public void resetPassword(String token, String newPassword) {
        // 比對存放在redis中的token
        String email = stringRedisTemplate.opsForValue().get(token);
        UserPo userPo = userJpaRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("找不到使用者 " + email));
        userPo.setPassword(passwordEncoder.encode(newPassword));
        userJpaRepository.save(userPo);
        // 更新spring-security的數據
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPo.getUserName());
        // 生成新的usernamePasswordAuthenticationToken
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Override
    public void forgotPassword(String email) throws MessagingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        String data = email + System.currentTimeMillis();
        md.update(data.getBytes());
        md.update(randomBytes);
        byte[] hash = md.digest();
        String refreshToken = Base64.getEncoder().encodeToString(hash);
        // 存入redis
        stringRedisTemplate.opsForValue().set(refreshToken, email, 10, TimeUnit.MINUTES);
        // 發送郵件
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String content = "重製驗證信件 " + "令牌為:" + refreshToken;
        InternetAddress address = new InternetAddress();
        address.setAddress(email);
        helper.setFrom("test123@example.com");
        helper.setTo(address);
        helper.setSubject("重製驗證");
        helper.setText(content);
        javaMailSender.send(message);
    }
}
