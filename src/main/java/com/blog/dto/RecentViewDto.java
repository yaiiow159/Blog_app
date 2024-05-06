package com.blog.dto;

import com.blog.po.RecentViewPo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link RecentViewPo}
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class RecentViewDto implements Serializable {

    @Schema(description = "id", example = "1")
    Long id;

    @Schema(description = "使用者名稱", example = "John Doe")
    @NotBlank(message = "使用者名稱不可為空")
    String userName;

    @Schema(description = "文章主鍵", example = "1")
    @NotNull(message = "文章主鍵不可為空")
    Long postId;

    @Schema(description = "文章標題", example = "文章標題")
    @NotBlank(message = "文章標題不可為空")
    String title;

    @Schema(description = "創建時間", example = "2022-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",shape = JsonFormat.Shape.STRING)
    LocalDateTime createTime;

    @Schema(description = "使用者ID")
    Long userId;
}