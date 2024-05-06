package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestBody implements Serializable {

    @NotBlank(message = "名稱不得為空")
    @Schema(description = "名稱")
    private String name;

    @NotBlank(message = "電子郵件不得為空")
    @Schema(description = "電子郵件")
    private String email;

    @Schema(description = "密碼")
    private String password;

    @Schema(description = "圖片")
    private String avatar;

    @Schema(description = "圖片名稱")
    private String imageName;

    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime birthday;

    @Schema(description = "暱稱")
    private String nickName;

    @Schema(description = "地址")
    private String address;

}
