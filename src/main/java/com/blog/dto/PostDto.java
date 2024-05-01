package com.blog.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;


/**
 * DTO for {@link com.blog.po.PostPo}
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class PostDto extends BaseDto implements Serializable {

    @NotBlank(message = "文章內文不得為空")
    @Schema(description = "文章內文",requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "文章圖片(請求)")
    private MultipartFile multipartFile;

    @Schema(description = "文章圖片(回傳)")
    private byte[] image;

    @Schema(description = "標籤")
    private String[] tagIds;

    @NotBlank(message = "文章標題不得為空")
    @Schema(description = "文章標題",requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "文章描述")
    private String description;

    @Schema(description = "文章分類id")
    @NotNull(message = "文章分類id不得為空")
    private Long categoryId;

    @Schema(description = "文章分類名稱")
    @Valid
    private List<CommentDto> comments;

    @Schema(description = "文章作者名稱")
    private String authorName;

    @Schema(description = "文章作者電子郵件")
    private String authorEmail;

    @Schema(description = "文章按讚數")
    private Long likeCount;

    @Schema(description = "文章瀏覽數")
    private Long viewCount;

    @Schema(description = "文章狀態")
    @NotBlank(message = "文章狀態不得為空")
    private String status;

}