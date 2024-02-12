package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.blog.po.PostHistoryPo}
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostHistoryPoDto implements Serializable {
    @NotNull
    @Schema(description = "文章ID",example = "1",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;

    @Schema(description = "文章標題",example = "文章標題",requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "文章內容")
    private String content;

    @Schema(description = "作者名稱")
    private String authorName;

    @Schema(description = "作者電子郵件")
    private String authorEmail;

    @Schema(description = "創建時間")
    private LocalDateTime createTime;

    @Schema(description = "文章圖片")
    private byte[] image;
}