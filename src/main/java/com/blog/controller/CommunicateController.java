package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.dto.CommunicationDto;
import com.blog.exception.ValidateFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @NoResubmit(delaySecond = 3)
    @PostMapping("/contact")
    @Operation(summary = "聯繫我們", description = "聯繫我們", tags = {"聯繫相關功能"})
    public ResponseBody<String> communicate(@RequestParam("fromUser") String fromUser,
                                            @RequestParam("email") String email,
                                            @RequestParam("message") String content) {

        MimeMessage message = javaMailSender.createMimeMessage();
        final String recipientTo = "examyou076@gmail.com";
        try {
            message.setSubject("來自 " + fromUser + " 的聯繫通知");
            message.setFrom(fromUser);
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(recipientTo));
            message.setReplyTo(new Address[]{new InternetAddress(email)});
            message.setText("聯繫內容: " + content);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("寄送失敗 失敗原因 : {}", e.getMessage());
            throw new ValidateFailedException("寄送郵件失敗 失敗原因 : " + e.getMessage());
        }
        return new ResponseBody<>(true, "寄送成功",HttpStatus.OK);
    }
}
