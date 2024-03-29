package com.blog.dto;


import com.blog.enumClass.PostStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.springframework.beans.factory.annotation.Value;
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
    String content;

    @Schema(description = "文章圖片")
    MultipartFile multipartFile;

    @Schema(description = "文章圖片匯出")
    byte[] image;

    @NotBlank(message = "文章標題不得為空")
    @Schema(description = "文章標題",requiredMode = Schema.RequiredMode.REQUIRED)
    String title;

    @Schema(description = "文章描述")
    String description;

    @Schema(description = "文章分類id")
    String categoryId;

    @Schema(description = "文章分類名稱")
    @Valid
    List<CommentDto> comments;

    @Schema(description = "文章作者名稱")
    String authorName;

    @Schema(description = "文章作者電子郵件")
    String authorEmail;

    @Schema(description = "文章按讚數")
    Long likeCount;

    @Schema(description = "文章瀏覽數")
    Long viewCount;

    @Schema(description = "文章狀態")
    String status;

}