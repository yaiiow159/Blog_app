package com.blog.controller;

import com.alibaba.fastjson2.JSONObject;
import com.blog.dto.CommunicateRequest;
import com.blog.exception.ValidateFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 針對聯繫API進行控制
 */
@Tag(name = "聯繫相關功能", description = "聯繫相關功能API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/communicate")
@RestController
public class CommunicateController {
    private final JavaMailSender javaMailSender;
    @PostMapping("/contact")
    @Operation(summary = "聯繫我們", description = "聯繫我們", tags = {"聯繫相關功能"})
    public ResponseEntity<String> communicate(@Validated @RequestBody CommunicateRequest request) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setSubject("來自 " + request.getFromUser() + " 的聯繫通知");
            message.setFrom(request.getFromUser());
            final String recipientTo = "examyou076@gmail.com";
            message.setRecipients(MimeMessage.RecipientType.TO, recipientTo);
            message.setReplyTo(new Address[]{new InternetAddress(request.getEmail())});
            message.setText("聯繫內容: " + request.getContent());
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("寄送郵件發生錯誤", e);
            throw new ValidateFailedException("寄送郵件發生錯誤");
        }
        return ResponseEntity.ok("寄送成功");
    }
}
