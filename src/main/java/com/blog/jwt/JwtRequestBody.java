package com.blog.jwt;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequestBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "帳號不得為空")
    @Schema(description = "使用者帳戶",example = "admin",requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密碼不得為空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密碼格式錯誤")
    @Schema(description = "使用者密碼",example = "admin1234",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "圖形驗證碼",maxLength = 4,requiredMode = Schema.RequiredMode.REQUIRED)
    @Length(max = 4)
    private String captchaCode;
}
