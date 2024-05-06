package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {
    @Schema(description = "頭像")
    private byte[] avatar;

    @Schema(description = "使用者名稱")
    private String username;

    @Schema(description = "信箱")
    private String email;

    @Schema(description = "密碼")
    private String password;

    @Schema(description = "暱稱")
    private String nickname;

    @Schema(description = "生日")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime birthday;

    @Schema(description = "地址")
    private String address;
}
