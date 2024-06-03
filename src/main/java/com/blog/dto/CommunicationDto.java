package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunicationDto implements Serializable {
    @Schema(description = "姓名", example = "TimmyChung")
    private String fromUser;

    @Schema(description = "郵箱", example = "5HqKz@example.com")
    @Email(message = "郵箱格式錯誤")
    @NotBlank(message = "郵箱不得為空")
    private String email;

    @Schema(description = "聯繫內容", example = "Hello World")
    private String message;
}
