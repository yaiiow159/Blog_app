package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SubscriptReqBody implements java.io.Serializable {
    @Schema(description = "使用者名稱", example = "test")
    @NotBlank(message = "使用者名稱不得為空")
    private String username;

    @Schema(description = "文章ID", example = "1")
    private Long postId;

    @Schema(description = "作者名稱", example = "test")
    @NotBlank(message = "作者名稱不得為空")
    private String authorName;

    @Schema(description = "電子郵件", example = "pXbqF@example.com")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$")
    private String email;
}
