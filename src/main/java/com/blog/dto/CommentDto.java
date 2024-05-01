package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;


import java.io.Serializable;

/**
 * DTO for {@link com.blog.po.CommentPo}
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDto extends BaseDto implements Serializable {

    @NotBlank(message = "評論者名稱不得為空")
    @Schema(description = "評論者名稱",example = "alex",requiredMode = Schema.RequiredMode.REQUIRED)
    String name;

    @NotBlank(message = "評論內容不得為空")
    @Schema(description = "評論內容",example = "哈哈哈你好好笑",requiredMode = Schema.RequiredMode.REQUIRED)
    String content;

    @Schema(description = "文章id",example = "1")
    Long postId;

    @Schema(description = "檢舉理由",example = "不當行為")
    String reason;

    @Schema(description = "檢舉狀態",example = "true")
    Boolean isReport;
}