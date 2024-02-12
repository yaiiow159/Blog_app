package com.blog.dto;

import com.blog.po.SubscriptionPo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link SubscriptionPo}
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SubscriptionDto implements Serializable {
    @Schema(description = "訂閱者")
    private UserDto user;

    @Schema(description = "文章ID")
    private PostDto post;

    @NotBlank(message = "作者名稱不得為空")
    @Schema(description = "作者名稱")
    private String authorName;

    @NotBlank(message = "電子郵件不得為空")
    @Email(message = "電子郵件格式錯誤")
    @Schema(description = "電子郵件")
    private String email;
}