package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ResetPasswordDto implements Serializable {

    @Schema(description = "驗證令牌")
    @NotBlank(message = "令牌不得為空")
    private String token;

    @Schema(description = "重製密碼")
    @NotBlank(message = "密碼不得為空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密碼不符合規定")
    private String newPassword;
}