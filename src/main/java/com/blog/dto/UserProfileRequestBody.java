package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestBody implements Serializable {

    @NotBlank(message = "名稱不得為空")
    @Schema(description = "名稱")
    private String name;

    @Schema(description = "電子郵件")
    @Email(message = "Email格式錯誤",regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")
    private String email;

    @Schema(description = "密碼")
    private String password;

    @Schema(description = "暱稱")
    private String nickName;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "電話")
    private String phoneNumber;
}
