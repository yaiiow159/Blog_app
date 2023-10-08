package com.blog.jwt;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequestBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotNull
    @Schema(description = "使用者帳戶",example = "admin",requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_-]{6,16}$",message = "密碼格式錯誤")
    @Schema(description = "使用者密碼",example = "admin1234",requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",message = "電子郵件格式錯誤")
            @Schema(description = "電子郵件地址",example = "admin1234@example.com")
    private String email;

    @Schema(description = "驗證碼")
    private String capchaCode;

    @Schema(description = "驗證碼token (UUID32)")
    private String token;
}
