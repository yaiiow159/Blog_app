package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptReqBody implements Serializable {

    @Email(message = "email格式錯誤")
    @Schema(description = "電子郵件",example = "pNlC9@example.com",requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "作者名稱",example = "John Doe")
    private String authorName;

    @Schema(description = "文章ID",example = "1")
    private Long postId;

    @Schema(description = "使用者名稱",example = "John Doe",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "使用者名稱不能為空")
    private String username;
}
