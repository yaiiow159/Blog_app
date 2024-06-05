package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.blog.po.MailNotificationPo}
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailNotificationDto implements Serializable {
    @Schema(description = "主鍵", example = "1")
    Long id;

    @Schema(description = "姓名", example = "John")
    String name;

    @Schema(description = "電子郵件", example = "pXbqF@example.com")
    String email;

    @Schema(description = "內容", example = "Hello, World!")
    String content;

    @Schema(description = "主題", example = "Hello")
    String subject;

    @Schema(description = "發送時間", example = "2022-01-01T00:00:00")
    String sendTime;

    @Schema(description = "是否已讀取", example = "true")
    Boolean isRead;

    @Schema(description = "是否已發送", example = "true")
    Boolean isSend;
}