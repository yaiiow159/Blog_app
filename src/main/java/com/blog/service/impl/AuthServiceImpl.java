package com.blog.service.impl;

import com.blog.dao.UserPoRepository;
import com.blog.po.UserPo;
import com.blog.service.AuthService;
import com.blog.utils.UUIDUtil;
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

import java.util.Random;
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
        // 生成隨機四位數字 當作驗證碼
        Random random = new Random();
        int validateCode = random.nextInt(9000) + 1000;
        String validateCodeStr = String.valueOf(validateCode);
        // 將驗證碼存入redis
        //生成token當作 url令牌
        final String refreshToken = UUIDUtil.getUUID32();
        // 將refreshToken存入redis
        stringRedisTemplate.opsForValue().set(validateCodeStr, email, 10, TimeUnit.MINUTES);
        // refreshToken作為一次性令牌驗證
        stringRedisTemplate.opsForValue().set(refreshToken, email, 10, TimeUnit.MINUTES);
        // 發送郵件
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String content = "http://localhost:3030/resetPassword?token=" + refreshToken;
        InternetAddress address = new InternetAddress();
        address.setAddress(email);
        helper.setFrom("test123@example.com");
        helper.setTo(address);
        helper.setSubject("重置密碼");
        helper.setText("你的驗證碼為:" + validateCodeStr + ", 驗證碼時效為十分鐘" +
                "\n" + "<a href=" + content + " style='color:blue; text-decoration:none; font-size:20px border: 1px solid'>" + "點擊驗證" + "</a>");
        javaMailSender.send(message);
    }
}
