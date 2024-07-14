package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {

    @Schema(description = "使用者id")
    private Long id;

    @Schema(description = "使用者名稱", example = "John Doe")
    private String username;

    @Schema(description = "信箱", example = "a@a.com")
    @Email(message = "Email格式錯誤",regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")
    private String email;

    @Schema(description = "密碼")
    private String password;

    @Schema(description = "暱稱", example = "John Doe")
    private String nickname;

    @Schema(description = "地址", example = "台北市中正區忠孝東路一段 10 號 3 樓")
    private String address;

    @Schema(description = "電話", example = "0912345678")
    private String phoneNumber;

    @Schema(description = "使用者頭像圖片")
    private String imageUrl;
}
